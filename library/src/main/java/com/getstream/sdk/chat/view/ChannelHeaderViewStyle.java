package com.getstream.sdk.chat.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.getstream.sdk.chat.R;
import com.getstream.sdk.chat.utils.BaseStyle;

public class ChannelHeaderViewStyle extends BaseStyle {
    private int channelTitleTextSize;
    private int lastActiveTextSize;
    // Color
    private int channelTitleTextColor;
    private int lastActiveTextColor;
    // Style
    private int channelTitleTextStyle;
    private int lastActiveTextStyle;

    private boolean showBackButton;

    public ChannelHeaderViewStyle(Context c, AttributeSet attrs) {
        // parse the attributes
        setContext(c);
        TypedArray a = this.getContext().obtainStyledAttributes(attrs,
                R.styleable.ChannelHeaderView, 0, 0);
        // Channel Title
        channelTitleTextSize = (int) a.getDimension(R.styleable.ChannelHeaderView_channelTitleTextSize, getDimension(R.dimen.stream_channel_header_initials));
        channelTitleTextColor = a.getColor(R.styleable.ChannelHeaderView_channelTitleTextColor, getColor(R.color.stream_channel_initials));
        channelTitleTextStyle = a.getInt(R.styleable.ChannelHeaderView_channelTitleTextStyle, Typeface.BOLD);
        // Last Active
        lastActiveTextSize = (int) a.getDimension(R.styleable.ChannelHeaderView_lastActiveTextSize, getDimension(R.dimen.stream_channel_preview_date));
        lastActiveTextColor = a.getColor(R.styleable.ChannelHeaderView_lastActiveTextColor, getColor(R.color.gray_dark));
        lastActiveTextStyle = a.getInt(R.styleable.ChannelHeaderView_lastActiveTextStyle, Typeface.NORMAL);
        // Back Button Show/Hide
        showBackButton = a.getBoolean(R.styleable.ChannelHeaderView_showBackButton, false);
        // Avatar
        avatarWidth = a.getDimensionPixelSize(R.styleable.ChannelHeaderView_avatarWidth, getDimension(R.dimen.stream_channel_avatar_width));
        avatarHeight = a.getDimensionPixelSize(R.styleable.ChannelHeaderView_avatarHeight, getDimension(R.dimen.stream_channel_avatar_height));

        avatarBorderWidth = a.getDimensionPixelSize(R.styleable.ChannelHeaderView_avatarBorderWidth, getDimension(R.dimen.stream_channel_avatar_border_width));
        avatarBorderColor = a.getColor(R.styleable.ChannelHeaderView_avatarBorderColor, Color.WHITE);
        avatarBackGroundColor = a.getColor(R.styleable.ChannelHeaderView_avatarBackGroundColor, getColor(R.color.stream_gray_dark));

        avatarInitialTextSize = a.getDimensionPixelSize(R.styleable.ChannelHeaderView_avatarTextSize, getDimension(R.dimen.stream_channel_initials));
        avatarInitialTextColor = a.getColor(R.styleable.ChannelHeaderView_avatarTextColor, Color.WHITE);
        avatarInitialTextStyle = a.getInt(R.styleable.ChannelHeaderView_avatarTextStyle, Typeface.BOLD);

        a.recycle();
    }

    public int getChannelTitleTextSize() {
        return channelTitleTextSize;
    }

    public int getChannelTitleTextColor() {
        return channelTitleTextColor;
    }

    public int getChannelTitleTextStyle() {
        return channelTitleTextStyle;
    }

    public int getLastActiveTextSize() {
        return lastActiveTextSize;
    }

    public int getLastActiveTextColor() {
        return lastActiveTextColor;
    }

    public int getLastActiveTextStyle() {
        return lastActiveTextStyle;
    }

    public boolean isShowBackButton() {
        return showBackButton;
    }

}
