package fr.yncrea.m1_s1project_android.models;

import java.util.ArrayList;

public class Generator {
    private ArrayList<Channel> channelList = new ArrayList<>();

    public ArrayList<Channel> getChannelList() {
        return channelList;
    }

    public void setChannelList(final ArrayList<Channel> channelList) {
        this.channelList = channelList;
    }
}
