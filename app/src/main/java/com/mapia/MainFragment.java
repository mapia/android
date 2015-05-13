package com.mapia;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.mapia.asne.core.SocialNetworkManager;
import com.mapia.asne.core.listener.OnLoginCompleteListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainFragment extends Fragment implements SocialNetworkManager.OnInitializationCompleteListener, OnLoginCompleteListener {
    public static com.mapia.asne.core.SocialNetworkManager mSocialNetworkManager;
    /**
     * SocialNetwork Ids in ASNE:
     * 1 - Twitter
     * 2 - Instagram
     * 3 - Google Plus
     * 4 - Facebook
     * 5 - Foursquare
     * 6 - Odnoklassniki
     */
    private Button facebook;
    private Button twitter;
    private Button instagram;
    private Button googleplus;

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(com.mapia.R.layout.main_fragment, container, false);
        ((SNSActivity)getActivity()).getSupportActionBar().setTitle(com.mapia.R.string.app_name);
        // init buttons and set Listener
        facebook = (Button) rootView.findViewById(com.mapia.R.id.facebook);
        facebook.setOnClickListener(loginClick);
        twitter = (Button) rootView.findViewById(com.mapia.R.id.twitter);
        twitter.setOnClickListener(loginClick);
        instagram = (Button) rootView.findViewById(com.mapia.R.id.instagram);
        instagram.setOnClickListener(loginClick);
        googleplus = (Button) rootView.findViewById(com.mapia.R.id.googleplus);
        googleplus.setOnClickListener(loginClick);

        //Get Keys for initiate SocialNetworks
        String TWITTER_CONSUMER_KEY = getActivity().getString(com.mapia.R.string.twitter_consumer_key);
        String TWITTER_CONSUMER_SECRET = getActivity().getString(com.mapia.R.string.twitter_consumer_secret);
        String TWITTER_CALLBACK_URL = "http://54.65.32.198:8081/map";
        String instagram_CLIENT_ID = getActivity().getString(com.mapia.R.string.instagram_client_id);
        String instagram_CLIENT_SECRET = getActivity().getString(com.mapia.R.string.instagram_client_secret);
        String instagram_CALLBACK_URL = "http://54.65.32.198:8081/map";

        //Chose permissions
        ArrayList<String> fbScope = new ArrayList<String>();
        fbScope.addAll(Arrays.asList("public_profile, email, user_friends"));
//       String instagramScope = "r_basicprofile+r_fullprofile+rw_nus+r_network+w_messages+r_emailaddress+r_contactinfo";
        String instagramScope = "comments+relationships+likes";

        //Use manager to manage SocialNetworks
        mSocialNetworkManager = (SocialNetworkManager) getFragmentManager().findFragmentByTag(SNSActivity.SOCIAL_NETWORK_TAG);

        //Check if manager exist
        if (mSocialNetworkManager == null) {
            mSocialNetworkManager = new com.mapia.asne.core.SocialNetworkManager();

            //Init and add to manager FacebookSocialNetwork
            com.mapia.asne.facebook.FacebookSocialNetwork fbNetwork = new com.mapia.asne.facebook.FacebookSocialNetwork(this, fbScope);
            mSocialNetworkManager.addSocialNetwork(fbNetwork);

            //Init and add to manager TwitterSocialNetwork
            com.mapia.asne.twitter.TwitterSocialNetwork twNetwork = new com.mapia.asne.twitter.TwitterSocialNetwork(this, TWITTER_CONSUMER_KEY, TWITTER_CONSUMER_SECRET, TWITTER_CALLBACK_URL);
            mSocialNetworkManager.addSocialNetwork(twNetwork);

//            //Init and add to manager instagramSocialNetwork
            com.mapia.asne.instagram.InstagramSocialNetwork liNetwork = new com.mapia.asne.instagram.InstagramSocialNetwork(this, instagram_CLIENT_ID, instagram_CLIENT_SECRET, instagram_CALLBACK_URL, instagramScope);
            mSocialNetworkManager.addSocialNetwork(liNetwork);

            //Init and add to manager instagramSocialNetwork
//            GooglePlusSocialNetwork gpNetwork = new GooglePlusSocialNetwork(this);
//            mSocialNetworkManager.addSocialNetwork(gpNetwork);

            //Initiate every network from mSocialNetworkManager
            getFragmentManager().beginTransaction().add(mSocialNetworkManager, SNSActivity.SOCIAL_NETWORK_TAG).commit();
            mSocialNetworkManager.setOnInitializationCompleteListener(this);
        } else {
            //if manager exist - get and setup login only for initialized SocialNetworks
            if(!mSocialNetworkManager.getInitializedSocialNetworks().isEmpty()) {
                List<com.mapia.asne.core.SocialNetwork> socialNetworks = mSocialNetworkManager.getInitializedSocialNetworks();
                for (com.mapia.asne.core.SocialNetwork socialNetwork : socialNetworks) {
                    socialNetwork.setOnLoginCompleteListener(this);
                    initSocialNetwork(socialNetwork);
                }
            }
        }
        return rootView;
    }

    private void initSocialNetwork(com.mapia.asne.core.SocialNetwork socialNetwork){
        if(socialNetwork.isConnected()){
            switch (socialNetwork.getID()){
                case com.mapia.asne.facebook.FacebookSocialNetwork.ID:
                    facebook.setText("Show Facebook profile");
                    break;
                case com.mapia.asne.twitter.TwitterSocialNetwork.ID:
                    twitter.setText("Show Twitter profile");
                    break;
                case com.mapia.asne.instagram.InstagramSocialNetwork.ID:
                    instagram.setText("Show instagram profile");
                    break;
//                case GooglePlusSocialNetwork.ID:
//                    googleplus.setText("Show GooglePlus profile");
//                    break;
            }
        }
    }



    @Override
    public void onSocialNetworkManagerInitialized() {
        //when init SocialNetworks - get and setup login only for initialized SocialNetworks
        for (com.mapia.asne.core.SocialNetwork socialNetwork : mSocialNetworkManager.getInitializedSocialNetworks()) {
            socialNetwork.setOnLoginCompleteListener(this);
            initSocialNetwork(socialNetwork);
        }
    }

    //Login listener

    private View.OnClickListener loginClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int networkId = 0;
            switch (view.getId()){
                case com.mapia.R.id.facebook:
                    networkId = com.mapia.asne.facebook.FacebookSocialNetwork.ID;
                    break;
                case com.mapia.R.id.twitter:
                    networkId = com.mapia.asne.twitter.TwitterSocialNetwork.ID;
                    break;
                case com.mapia.R.id.instagram:
                    networkId = com.mapia.asne.instagram.InstagramSocialNetwork.ID;
                    break;
//                case R.id.googleplus:
//                    networkId = GooglePlusSocialNetwork.ID;
//                    break;
            }
            com.mapia.asne.core.SocialNetwork socialNetwork = mSocialNetworkManager.getSocialNetwork(networkId);
            if(!socialNetwork.isConnected()) {
                if(networkId != 0) {
                    socialNetwork.requestLogin();
                    SNSActivity.showProgress("Loading social person");
                } else {
                    Toast.makeText(getActivity(), "Wrong networkId", Toast.LENGTH_LONG).show();
                }
            } else {
                startProfile(socialNetwork.getID());
            }
        }
    };

    @Override
    public void onLoginSuccess(int networkId) {
        SNSActivity.hideProgress();
        startProfile(networkId);
    }

    @Override
    public void onError(int networkId, String requestID, String errorMessage, Object data) {
        SNSActivity.hideProgress();
        Toast.makeText(getActivity(), "ERROR: " + errorMessage, Toast.LENGTH_LONG).show();
    }

    private void startProfile(int networkId){
        ProfileFragment profile = ProfileFragment.newInstannce(networkId);
        getActivity().getSupportFragmentManager().beginTransaction()
                .addToBackStack("profile")
                .replace(com.mapia.R.id.container, profile)
                .commit();
    }
}