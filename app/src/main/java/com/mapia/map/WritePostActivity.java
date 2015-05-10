package com.mapia.map;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.mapia.R;
import com.mapia.s3.S3Activity;
import com.mapia.s3.TransferView;
import com.mapia.s3.Util;
import com.mapia.s3.models.TransferModel;
import com.mapia.s3.network.TransferController;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class WritePostActivity extends S3Activity implements View.OnClickListener {

    private boolean exists = false;
    private boolean checked = false;
    //s3 flag
    private ArrayList<String> mFileNameList = new ArrayList<String>();
    LatLng latlng;
    EditText edtComment;
    Button btnSubmit, btnCancel, btnImage, btnVideo;
    final int REQ_CODE_SELECT_IMAGE=100;
    private static final int REFRESH_DELAY = 500;

    private Timer mTimer;
    private LinearLayout mLayout;
    private TransferModel[] mModels = new TransferModel[0];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);

        Intent intent = getIntent();
        latlng = (LatLng)intent.getParcelableExtra("latlng");

        edtComment = (EditText)findViewById(R.id.txtPostComment);
        btnSubmit = (Button)findViewById(R.id.btnSubmit);
        btnCancel = (Button)findViewById(R.id.btnCancel);
        btnImage = (Button)findViewById(R.id.btnImage);
        btnVideo = (Button)findViewById(R.id.btnVideo);
        btnSubmit.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnImage.setOnClickListener(this);
        btnVideo.setOnClickListener(this);


        mLayout = (LinearLayout) findViewById(R.id.transfers_layout);

        new CheckBucketExists().execute();

        // make timer that will refresh all the transfer views
        mTimer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                WritePostActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        syncModels();
                        for (int i = 0; i < mLayout.getChildCount(); i++) {
                            ((TransferView) mLayout.getChildAt(i)).refresh();
                        }
                    }
                });
            }
        };
        mTimer.schedule(task, 0, REFRESH_DELAY);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btnVideo:
                Intent intentVideo = new Intent(Intent.ACTION_GET_CONTENT);
                intentVideo.setType("video/*");
                startActivityForResult(intentVideo, 0);
                break;
            case R.id.btnImage:   //버튼 클릭 시 기본 Gallery로 이동
                Intent intentImage = new Intent(Intent.ACTION_GET_CONTENT);
                intentImage.setType("image/*");
                startActivityForResult(intentImage, REQ_CODE_SELECT_IMAGE); //REQ_CODE_SELECT_IMAGE);
                break;
            case R.id.btnSubmit:

//                Intent i = new Intent(WritePostActivity.this, S3Activity.class);
//                startActivity(i);

                Intent intent = new Intent();
                intent.putExtra("comment",edtComment.getText().toString());
                intent.putExtra("latlng",latlng);
                intent.putExtra("filelist",mFileNameList);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.btnCancel:
                setResult(RESULT_CANCELED);
                finish();
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


    //URL에서 파일명 추출
    public String getImageNameToUri(Uri data)
    {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        String imgPath = cursor.getString(column_index);
        String imgName = imgPath.substring(imgPath.lastIndexOf("/")+1);

        return imgName;
    }
}
