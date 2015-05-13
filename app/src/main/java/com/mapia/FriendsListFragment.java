package com.mapia;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.mapia.sns.SocialPerson;
import com.mapia.asne.core.SocialNetwork;
import com.mapia.asne.core.listener.OnRequestGetFriendsCompleteListener;

import java.util.ArrayList;

public class FriendsListFragment extends Fragment implements OnRequestGetFriendsCompleteListener {

    private static final String NETWORK_ID = "NETWORK_ID";
    private ListView listView;

    public static FriendsListFragment newInstannce(int id) {
        FriendsListFragment fragment = new FriendsListFragment();
        Bundle args = new Bundle();
        args.putInt(NETWORK_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    public FriendsListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        int networkId = getArguments().containsKey(NETWORK_ID) ? getArguments().getInt(NETWORK_ID) : 0;

        View rootView = inflater.inflate(R.layout.friends_list_fragment, container, false);
        listView = (ListView) rootView.findViewById(R.id.list);

        SocialNetwork socialNetwork = MainFragment.mSocialNetworkManager.getSocialNetwork(networkId);
        socialNetwork.setOnRequestGetFriendsCompleteListener(this);
        socialNetwork.requestGetFriends();
        SNSActivity.showProgress("Loading friends");

        return rootView;
    }

    @Override
    public void OnGetFriendsIdComplete(int id, String[] friendsID) {
        ((SNSActivity)getActivity()).getSupportActionBar().setTitle(friendsID.length + " Friends");
    }

    @Override
    public void OnGetFriendsComplete(int networkID, ArrayList<SocialPerson> socialPersons) {
        SNSActivity.hideProgress();
        FriendsListAdapter adapter = new FriendsListAdapter(getActivity(), socialPersons, networkID);
        listView.setAdapter(adapter);
    }

    @Override
    public void onError(int networkId, String requestID, String errorMessage, Object data) {
        SNSActivity.hideProgress();
        Toast.makeText(getActivity(), "ERROR: " + errorMessage, Toast.LENGTH_LONG).show();
    }
}
