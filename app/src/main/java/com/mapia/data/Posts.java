package com.mapia.data;

import com.google.android.gms.maps.model.LatLng;

import java.sql.Timestamp;

/**
 * Created by JahyunKim on 15. 5. 3..
 */
public class Posts {
    Comments comment;
    Photos photos;
    Movies movie;
    LocTag locTag;
    UserTag userTag;
    Groups group;
    Events event;
    String userID, content, sns;
    Timestamp timeStamp;
    LatLng latLng;
    public Posts(){
    }
}