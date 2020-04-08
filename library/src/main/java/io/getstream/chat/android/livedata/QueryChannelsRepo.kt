package io.getstream.chat.android.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.events.ChatEvent
import io.getstream.chat.android.client.events.NotificationAddedToChannelEvent
import io.getstream.chat.android.client.logger.ChatLogger
import io.getstream.chat.android.client.models.Channel
import io.getstream.chat.android.livedata.entity.QueryChannelsEntity
import io.getstream.chat.android.livedata.requests.QueryChannelsPaginationRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/**
 * The StreamQueryChannelRepository is a small helper to show a list of channels
 *
 * - queryChannelRepo.channels a livedata object with the list of channels. this list
 * updates whenever a new message is added to one of the channels, the read state changes for members of these channels,
 * messages are edited or updated, or the channel is updated.
 */
class QueryChannelsRepo(var query: QueryChannelsEntity, var client: ChatClient, var repo: ChatRepo) {
    /**
     * A livedata object with the channels matching this query.
     */

    private val _channels = MutableLiveData<MutableMap<String, Channel>>()
    var channels: LiveData<List<Channel>> = Transformations.map(_channels) {it.values.toList()}

    private val logger = ChatLogger.get("QueryChannelsRepo")

    private val _loading = MutableLiveData<Boolean>(false)
    val loading : LiveData<Boolean> = _loading

    private val _loadingMore = MutableLiveData<Boolean>(false)
    val loadingMore : LiveData<Boolean> = _loadingMore

    fun loadMore(limit: Int = 30) {
        GlobalScope.launch(Dispatchers.IO) {
            val request = loadMoreRequest(limit)
            runQuery(request)
        }
    }

    fun loadMoreRequest(limit: Int = 30, messageLimit: Int = 10): QueryChannelsPaginationRequest {
        val channels = _channels.value ?: mutableMapOf()
        var request = QueryChannelsPaginationRequest().withLimit(limit).withOffset(channels.size).withMessages(messageLimit)

        return request
    }


    // TODO 1.1: handleMessageNotification should be configurable
    fun handleMessageNotification(event: NotificationAddedToChannelEvent) {
        event.channel?.let {
            addChannels(listOf(it))
        }
    }

    fun handleEvent(event: ChatEvent) {
        if (event is NotificationAddedToChannelEvent) {
            handleMessageNotification(event)
        }
        // TODO abstrac tthis check somewhere
        if (event.cid != null && event.cid!!.isNotBlank() && event.cid != "*") {
            // update the info for that channel from the channel repo
            val channel = repo.channel(event.cid!!).toChannel()
            updateChannel(channel)
        }
    }

    fun updateChannel(c: Channel) {
        val copy = _channels.value ?: mutableMapOf()
        copy[c.id] = c
        _channels.postValue(copy)
    }

    /**
     * Run the given queryChannels request and update the channels livedata object
     */
    fun query(request: QueryChannelsPaginationRequest) {
        GlobalScope.launch(Dispatchers.IO) {
            runQuery(request)
        }
    }
    suspend fun runQuery(pagination: QueryChannelsPaginationRequest) {
        val loader = if(pagination.isFirstPage()) {_loading} else {
            _loadingMore
        }
        loader.postValue(true)
        // start by getting the query results from offline storage
        val request = pagination.toQueryChannelsRequest(query.filter, query.sort, repo.userPresence)
        var queryEntity = repo.selectQuery(query.id)
        // TODO: add logging here
        if (queryEntity != null) {
            val channels = repo.selectAndEnrichChannels(query.channelCIDs)
            for (c in channels) {
                val channelRepo = repo.channel(c)
                channelRepo.updateLiveDataFromChannel(c)
            }
            if (pagination.isFirstPage()) {
                setChannels(channels)
            } else {
                addChannels(channels)
            }
        }
        val online = repo.isOnline()
        if (online) {
            // next run the actual query
            val response = client.queryChannels(request).execute()
            queryEntity = queryEntity ?: QueryChannelsEntity(query.filter, query.sort)

            if (response.isSuccess) {
                // store the results in the database
                val channelsResponse = response.data()
                // update the results stored in the db
                // TODO: Figure out why tests didn't catch this
                if (pagination.isFirstPage()) {
                    val cids = channelsResponse.map{it.cid}
                    queryEntity.channelCIDs = cids.toMutableList()
                    repo.insertQuery(queryEntity)
                }

                // initialize channel repos for all of these channels
                for (c in channelsResponse) {
                    val channelRepo = repo.channel(c)
                    channelRepo.updateLiveDataFromChannel(c)
                }



                repo.storeStateForChannels(channelsResponse)

                if (pagination.isFirstPage()) {
                    setChannels(channelsResponse)
                } else {
                    addChannels(channelsResponse)
                }
            } else {
                repo.addError(response.error())
            }
        }
        loader.postValue(false)
    }

    private fun addChannels(channelsResponse: List<Channel>) {
        val copy = _channels.value ?: mutableMapOf()

        val missingChannels = channelsResponse.filterNot { it.cid in copy }.map { repo.channel(it.cid).toChannel() }
        for (channel in missingChannels) {
            copy[channel.cid] = channel
        }
        _channels.postValue(copy)
    }

    private fun setChannels(channelsResponse: List<Channel>) {
        val channels = channelsResponse.map { repo.channel(it.cid).toChannel() }
        _channels.postValue(channels.associateBy { it.cid }.toMutableMap())
    }
}