package com.mapia.map;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mapia.R;
import com.mapia.RestRequestHelper;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MapPublicFragment extends MapFragment {
	
	Button btnLocCurrent;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		this.type_num = 2;
		LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout relLayout = (RelativeLayout)inflater.inflate(R.layout.overview_map_public, null);
		RelativeLayout.LayoutParams paramrel = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
		getActivity().getWindow().addContentView(relLayout, paramrel);
		Toast.makeText(getActivity().getApplicationContext(), "PublicFragment", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		for(int i=0;i<markerDatas.size();i++){
			markerDatas.get(i).marker.remove();
		}
		markerDatas.clear();
		getMarker();
		drawMarker(markerDatas);
	}

	private void getMarker() {
		final ArrayList<MarkerData> markerList = new ArrayList<MarkerData>();
		try {
			RestRequestHelper requestHelper = RestRequestHelper.newInstance();

			requestHelper.posts(new Callback<JsonArray>() {
				@Override
				public void success(JsonArray jsonArray, Response response) {
					Toast.makeText(getActivity(),"Public 글 읽어오기 성공".toString(), Toast.LENGTH_LONG).show();
					for(int i=0;i<jsonArray.size();i++){
						JsonObject jsonObject = (JsonObject)jsonArray.get(i);
						MarkerData markerData = new MarkerData(new LatLng(jsonObject.get("lat").getAsDouble(),jsonObject.get("lng").getAsDouble()));
						markerList.add(markerData);
					}
					markerDatas = markerList;
					drawMarker(markerDatas);
				}

				@Override
				public void failure(RetrofitError error) {
					Toast.makeText(getActivity(),"글 읽어오기실패".toString(), Toast.LENGTH_LONG).show();
					error.printStackTrace();
				}
			},"private",this.cameraLatlng.latitude, this.cameraLatlng.longitude, this.cameraZoom);
		}  catch (Exception e) {
			e.printStackTrace();
		}
	}
}
