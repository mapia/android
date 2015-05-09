package com.mapia.map;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;
import com.mapia.R;

public class WritePostActivity extends Activity implements View.OnClickListener {

    LatLng latlng;
    EditText edtComment;
    Button btnSubmit, btnCancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);

        Intent intent = getIntent();
        latlng = (LatLng)intent.getParcelableExtra("latlng");

        edtComment = (EditText)findViewById(R.id.txtPostComment);
        btnSubmit = (Button)findViewById(R.id.btnSubmit);
        btnCancel = (Button)findViewById(R.id.btnCancel);
        btnSubmit.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btnSubmit:
                Intent intent = new Intent();
                intent.putExtra("comment",edtComment.getText().toString());
                intent.putExtra("latlng",latlng);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.btnCancel:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
    }
}
