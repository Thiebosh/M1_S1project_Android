package fr.yncrea.m1_s1project_android.models;

import java.util.ArrayList;

public class Generator {
    public static final String ATTRIBUTE_CHANNEL_LIST = "channelList";

    private ArrayList<Channel> channelList = new ArrayList<>();

    public ArrayList<Channel> getChannelList() {
        return channelList;
    }

    public Generator setChannelList(final ArrayList<Channel> channelList) {
        this.channelList = channelList;
        return this;
    }

    public Generator setAllChannelActive(final boolean active) {
        for (Channel channel : channelList) channel.setActive(active);
        return this;
    }

    public Channel getChannel(final int id) {
        return channelList.get(id);
    }
}
