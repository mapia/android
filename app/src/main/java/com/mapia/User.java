package com.mapia;

        import com.google.android.gms.maps.model.LatLng;

import java.sql.Date;
import java.sql.Timestamp;

public class User {

    char[] userName, gender;
    int userID, userPWHashed, profilePicID;


    Date birth;
    LatLng homeAddress;
    Timestamp firstTimeStamp;
    Timestamp recentTimeStamp;
    User[] follower, following;

    public User() {
        // TODO Auto-generated constructor stub
    }

}
