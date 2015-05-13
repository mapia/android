package com.mapia.map;

import com.google.android.gms.maps.model.LatLng;
import com.mapia.R;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class MapActivity extends FragmentActivity implements OnClickListener {

	int currentFragmentIndex;

	public static LatLng currentLatlng;
	public static LatLng cameraLatlng = new LatLng(0,0);
	public static float cameraZoom = 15;
	public static ImageButton imgBtnNav, imgBtnSearch;
	public static TextView txtMapName;
	Button btn1, btn2, btn3, btn4;
	MapPrivateFragment mapPrivateFragment = null;
	MapPublicFragment mapPublicFragment = null;
	MapFollowFragment mapFollowFragment = null;
	MapGroupFragment mapGroupFragment = null;
	Fragment lastFragment = null;

	@TargetApi(11)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		ActionBar mActionBar = getActionBar();
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setDisplayShowTitleEnabled(false);
		LayoutInflater mInflater = LayoutInflater.from(this);
		View mCustomView = mInflater.inflate(R.layout.actionbar_activity_map, null);

		imgBtnNav = (ImageButton)mCustomView.findViewById(R.id.actBtnNavi);
		imgBtnSearch = (ImageButton)mCustomView.findViewById(R.id.actBtnSearch);
		txtMapName = (TextView)mCustomView.findViewById(R.id.actTxtMapName);

		mActionBar.setCustomView(mCustomView);
		mActionBar.setDisplayShowCustomEnabled(true);

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