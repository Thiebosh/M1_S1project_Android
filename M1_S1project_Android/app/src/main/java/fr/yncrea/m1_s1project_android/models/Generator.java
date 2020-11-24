package fr.yncrea.m1_s1project_android.models;

import java.util.ArrayList;

public class Generator {
    private ArrayList<Channel> channelList;

    public ArrayList<Channel> getChannelList() {
        return channelList;
    }

    public void setChannelList(ArrayList<Channel> channelList) {
        this.channelList = channelList;
    }

    public void setInit(ArrayList<Channel> channelList) {
        setChannelList(channelList);
    }
}
