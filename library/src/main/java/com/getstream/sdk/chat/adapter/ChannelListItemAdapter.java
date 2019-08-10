package com.getstream.sdk.chat.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.getstream.sdk.chat.R;
import com.getstream.sdk.chat.model.Channel;
import com.getstream.sdk.chat.rest.response.ChannelState;
import com.getstream.sdk.chat.view.ChannelListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChannelListItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final String TAG = ChannelListItemAdapter.class.getSimpleName();
    private Context context;
    private List<Channel> channels; // cached list of channels
    public String filter;
    private View.OnClickListener clickListener;
    private String className;
    private int itemLayoutId;
    private ChannelListView.Style style;
    private View.OnLongClickListener longClickListener;

    public ChannelListItemAdapter(Context context, List<Channel> channels,
                                  String className, int itemLayoutId,
                                  View.OnClickListener clickListener, View.OnLongClickListener longClickListener) {
        this.context = context;
        this.channels = channels;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
        this.className = className;
        this.itemLayoutId = itemLayoutId;

    }

    // TODO: review this. Good to forward to other constructor but I have no idea what the default is for clickListener and longClickListener
    public ChannelListItemAdapter(Context context, int itemLayoutId) {
        this(context, new ArrayList<>(), "", itemLayoutId, null, null);
    }

    public void SetOnClickListener(View.OnClickListener l ) {
        clickListener = l;
    }

    public void setStyle(ChannelListView.Style s) {
        style = s;
    }

    public void deleteChannel(Channel channel) {
        int index = 0;
        for (Channel c : channels) {
            if (c.getCid() == channel.getCid()) {
                channels.remove(index);
                notifyItemChanged(index);
            }
            index += 1;
        }
    }

    public void upsertChannel(Channel channel) {
        // try to remove
        this.deleteChannel(channel);
        // always add to the top of the list...
        channels.add(0, channel);
    }

    public void replaceChannels(List<Channel> channelList) {
        channels = channelList;

        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        try {
            Class tempClass = Class.forName(className);
            Class[] cArg = new Class[2];
            cArg[0] = int.class;
            cArg[1] = ViewGroup.class;
            Object obj = tempClass.getDeclaredConstructor(cArg).newInstance(itemLayoutId, parent);
            if (obj instanceof BaseChannelListItemViewHolder) {
                return ((BaseChannelListItemViewHolder) obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ChannelListItemViewHolder(R.layout.list_item_channel, parent, style);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChannelState channelState = channels.get(position).getChannelState();
        ((BaseChannelListItemViewHolder) holder).bind(this.context, channelState, position, clickListener, longClickListener);
    }

    @Override
    public int getItemCount() {
        return channels.size();
    }


}
