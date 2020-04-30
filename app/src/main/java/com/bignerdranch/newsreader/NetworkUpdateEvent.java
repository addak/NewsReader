package com.bignerdranch.newsreader;

public class NetworkUpdateEvent {
    public boolean isNetworkAvailable;

    public NetworkUpdateEvent(boolean status){
        isNetworkAvailable = status;
    }
}
