package com.mapia.map;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.sql.Timestamp;

class MarkerData{
    boolean isNew;
	LatLng location;
    String letter;
    Timestamp time;

	Marker marker;

	public MarkerData(LatLng location){
		super();
		this.location = location;
	}

    public MarkerData(LatLng location, Timestamp time, String letter){
        super();
        this.location = location;
        this.time = time;
        this.letter = letter;
    }

    public MarkerData(LatLng location, String letter){
        super();
        this.location = location;
        this.letter = letter;
    }
}
