package com.mapia.map;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.mapia.R;

public class WritePostActivity extends Activity implements View.OnClickListener {

    LatLng latlng;
    EditText edtComment;
    Button actBtnSubmit, actBtnCancel;

    @TargetApi(11)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);

        ActionBar mActionBar = getActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.actionbar_activity_write_post, null);

        actBtnCancel = (Button)mCustomView.findViewById(R.id.actBtnCancel);
        actBtnSubmit = (Button)mCustomView.findViewById(R.id.actBtnSubmit);
        actBtnCancel.setOnClickListener(this);
        actBtnSubmit.setOnClickListener(this);

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);

        Intent intent = getIntent();
        latlng = (LatLng)intent.getParcelableExtra("latlng");

        edtComment = (EditText)findViewById(R.id.txtPostComment);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.actBtnSubmit:
                Intent intent = new Intent();
                intent.putExtra("comment",edtComment.getText().toString());
                intent.putExtra("latlng",latlng);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.actBtnCancel:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
    }
}
