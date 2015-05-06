package com.mapia.map;

import com.google.android.gms.maps.model.LatLng;
import com.mapia.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MapActivity extends FragmentActivity implements OnClickListener {

	int currentFragmentIndex;

	public static LatLng currentLatlng;
	public static LatLng cameraLatlng = new LatLng(0,0);
	public static float cameraZoom = 15;
	Button btn1, btn2, btn3, btn4;
	MapPrivateFragment mapPrivateFragment = null;
	MapPublicFragment mapPublicFragment = null;
	MapFollowFragment mapFollowFragment = null;
	MapGroupFragment mapGroupFragment = null;
	Fragment lastFragment = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		btn1 = (Button)findViewById(R.id.btn_Fragment1);
		btn2 = (Button)findViewById(R.id.btn_Fragment2);
		btn3 = (Button)findViewById(R.id.btn_Fragment3);
		btn4 = (Button)findViewById(R.id.btn_Fragment4);
		btn1.setOnClickListener(this);
		btn2.setOnClickListener(this);
		btn3.setOnClickListener(this);
		btn4.setOnClickListener(this);
		
		currentFragmentIndex = 1;
		
		fragmentReplace(currentFragmentIndex);
	}

	private void fragmentReplace(int newFragmentIndex) {
		Fragment newFragment = null;
		newFragment = getFragment(newFragmentIndex);
		if(newFragment == lastFragment) return;
		lastFragment = newFragment;
		
		final android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.mapShowFragment, newFragment);
		transaction.commit();
	}

	private Fragment getFragment(int index) {
		Fragment newFragment = null;
		switch(index){
			case 1:
				if(mapPrivateFragment==null) mapPrivateFragment = new MapPrivateFragment();
				newFragment = mapPrivateFragment;
				break;
			case 2:
				if(mapPublicFragment==null) mapPublicFragment = new MapPublicFragment();
				newFragment = mapPublicFragment;
				break;
			case 3:
				if(mapFollowFragment==null) mapFollowFragment = new MapFollowFragment();
				newFragment = mapFollowFragment;
				break;
			case 4:
				if(mapGroupFragment==null) mapGroupFragment = new MapGroupFragment();
				newFragment = mapGroupFragment;
				break;
			default:
				break;
		}
		return newFragment;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_Fragment1:
			currentFragmentIndex = 1;
			break;
		case R.id.btn_Fragment2:
			currentFragmentIndex = 2;
			break;
		case R.id.btn_Fragment3:
			currentFragmentIndex = 3;
			break;
		case R.id.btn_Fragment4:
			currentFragmentIndex = 4;
			break;
		}
		fragmentReplace(currentFragmentIndex);
	}
}