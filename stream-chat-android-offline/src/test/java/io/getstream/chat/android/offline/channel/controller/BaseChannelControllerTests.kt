package io.getstream.chat.android.offline.channel.controller

import androidx.annotation.CallSuper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.channel.ChannelClient
import io.getstream.chat.android.core.ExperimentalStreamChatApi
import io.getstream.chat.android.offline.ChatDomainImpl
import io.getstream.chat.android.offline.channel.ChannelController
import io.getstream.chat.android.offline.experimental.channel.logic.ChannelLogic
import io.getstream.chat.android.offline.experimental.channel.state.ChannelMutableState
import io.getstream.chat.android.offline.message.MessageSendingService
import io.getstream.chat.android.offline.randomUser
import io.getstream.chat.android.offline.repository.RepositoryFacade
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.jupiter.api.BeforeEach

internal open class BaseChannelControllerTests {
    protected val channelType = "channelType"
    protected val channelId = "channelId"
    protected val cid: String
        get() = "$channelType:$channelId"
    protected lateinit var sut: ChannelController
    protected lateinit var chatClient: ChatClient
    protected lateinit var chatDomainImpl: ChatDomainImpl
    protected lateinit var channelClient: ChannelClient
    protected lateinit var repos: RepositoryFacade
    protected lateinit var messageSendingService: MessageSendingService

    @OptIn(ExperimentalStreamChatApi::class)
    @ExperimentalCoroutinesApi
    @BeforeEach
    @CallSuper
    open fun before() {
        repos = mock()
        channelClient = mock()
        messageSendingService = mock()
        chatClient = mock {
            on { channel(channelType, channelId) } doReturn channelClient
            on { channel(any()) } doReturn channelClient
        }
        chatDomainImpl = mock {
            on(it.appContext) doReturn mock()
            on { scope } doReturn TestCoroutineScope()
            on { repos } doReturn repos
        }
        val mutableState =
            ChannelMutableState(channelType, channelId, chatDomainImpl.scope, MutableStateFlow(randomUser()))
        sut = ChannelController(
            mutableState,
            ChannelLogic(mutableState, chatDomainImpl),
            chatClient,
            chatDomainImpl,
        )
    }
}
