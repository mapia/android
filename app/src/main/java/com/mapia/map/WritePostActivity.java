package com.mapia.map;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.mapia.R;
import com.mapia.s3.Util;
import com.mapia.s3.network.TransferController;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class WritePostActivity extends Activity implements View.OnClickListener {
    final int REQ_CODE_SELECT_IMAGE=100;
    LatLng latlng;
    EditText edtComment;
    Button actBtnSubmit, actBtnCancel;
    Button actBtnAddImage, actBtnAddVideo;
    private ArrayList<String> mFileNameList = new ArrayList<String>();


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
        actBtnAddImage = (Button)findViewById(R.id.actBtnAddImage);
        actBtnAddVideo = (Button)findViewById(R.id.actBtnAddVideo);

        actBtnCancel.setOnClickListener(this);
        actBtnSubmit.setOnClickListener(this);
        actBtnAddImage.setOnClickListener(this);
        actBtnAddVideo.setOnClickListener(this);

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
                if(mFileNameList.size() > 0)
                    intent.putExtra("filelist",mFileNameList);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.actBtnCancel:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.actBtnAddImage:
                Intent intentImage = new Intent(Intent.ACTION_GET_CONTENT);
                intentImage.setType("image/*");
                startActivityForResult(intentImage, REQ_CODE_SELECT_IMAGE); //REQ_CODE_SELECT_IMAGE);
                break;
            case R.id.actBtnAddVideo:
                Intent intentVideo = new Intent(Intent.ACTION_GET_CONTENT);
                intentVideo.setType("video/*");
                startActivityForResult(intentVideo, 0);
                break;

        }
    }

    //선택한 이미지 데이터 받기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        Toast.makeText(getBaseContext(), "resultCode : " + resultCode, Toast.LENGTH_SHORT).show();
        if(requestCode == REQ_CODE_SELECT_IMAGE)
        {
            if(resultCode==Activity.RESULT_OK && data != null)
            {

                Uri uri = data.getData();
                if (uri != null) {
                    TransferController.upload(this, uri);

                    String uriString = uri.toString();
                    mFileNameList.add(Util.getFileName(uriString));
                    Log.i("file", uriString);
                    Log.i("file", mFileNameList.toString());
                }

                try {
                    //Uri에서 이미지 이름을 얻어온다.
                    //String name_Str = getImageNameToUri(data.getData());

                    //이미지 데이터를 비트맵으로 받아온다.
                    Bitmap image_bitmap 	= MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    ImageView image = (ImageView)findViewById(R.id.imageView1);

                    //배치해놓은 ImageView에 set
                    image.getLayoutParams().height = 200;
                    image.setImageBitmap(image_bitmap);


                    //Toast.makeText(getBaseContext(), "name_Str : "+name_Str , Toast.LENGTH_SHORT).show();

                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

}
