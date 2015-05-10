package com.mapia.s3;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.amazonaws.services.s3.AmazonS3Client;
import com.mapia.R;
import com.mapia.s3.models.TransferModel;
import com.mapia.s3.network.TransferController;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by daehyun on 2015. 5. 10..
 */
public class S3Activity extends Activity {
    private boolean exists = false;
    private boolean checked = false;
    private static final String TAG = "S3Activity";
    private static final int REFRESH_DELAY = 500;

    private Timer mTimer;
    private LinearLayout mLayout;
    private TransferModel[] mModels = new TransferModel[0];

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.s3_main);

        mLayout = (LinearLayout) findViewById(R.id.transfers_layout);
        new CheckBucketExists().execute();
        findViewById(R.id.create_bucket).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                new CreateBucket().execute();
            }
        });
        findViewById(R.id.delete_bucket).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                new DeleteBucket().execute();
            }
        });
        findViewById(R.id.download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checked) {
                    Toast.makeText(getApplicationContext(), "Please wait a moment...",
                            Toast.LENGTH_SHORT).show();
                }
                else if (!exists) {
                    Toast.makeText(getApplicationContext(), "You must first create the bucket",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(
                            S3Activity.this, DownloadActivity.class);
                    startActivity(intent);
                }
            }
        });

        findViewById(R.id.upload_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checked) {
                    Toast.makeText(getApplicationContext(), "Please wait a moment...",
                            Toast.LENGTH_SHORT).show();
                }
                else if (!exists) {
                    Toast.makeText(getApplicationContext(), "You must first create the bucket",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, 0);
                }
            }
        });

        findViewById(R.id.upload_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checked) {
                    Toast.makeText(getApplicationContext(), "Please wait a moment...",
                            Toast.LENGTH_SHORT).show();
                }
                else if (!exists) {
                    Toast.makeText(getApplicationContext(), "You must first create the bucket",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("video/*");
                    startActivityForResult(intent, 0);
                }
            }
        });

        // make timer that will refresh all the transfer views
        mTimer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                S3Activity.this.runOnUiThread(new Runnable() {
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

    /*
     * When we get a Uri back from the gallery, upload the associated
     * image/video
     */
    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        if (resCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                TransferController.upload(this, uri);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();
        syncModels();
    }

    @Override
    public void onStop() {
        super.onStop();
        mTimer.purge();
    }

    /* makes sure that we are up to date on the transfers */
    public void syncModels() {
        TransferModel[] models = TransferModel.getAllTransfers();
        if (mModels.length != models.length) {
            // add the transfers we haven't seen yet
            for (int i = mModels.length; i < models.length; i++) {
                mLayout.addView(new TransferView(this, models[i]), 0);
            }
            mModels = models;
        }
    }

    public class CreateBucket extends AsyncTask<Object, Void, Boolean> {

        @Override
        public Boolean doInBackground(Object... params) {
            AmazonS3Client sS3Client = Util.getS3Client(getApplicationContext());
            if (!Util.doesBucketExist()) {
                Util.createBucket();
                return true;
            }
            return false;
        }

        @Override
        public void onPostExecute(Boolean result) {
            if (!result) {
                Toast.makeText(getApplicationContext(), "Bucket already exists", Toast.LENGTH_SHORT)
                        .show();
            }
            else {
                Toast.makeText(getApplicationContext(), "Bucket successfully created!",
                        Toast.LENGTH_SHORT).show();
            }
            exists = true;
        }
    }

    public class CheckBucketExists extends AsyncTask<Object, Void, Boolean> {

        @Override
        public Boolean doInBackground(Object... params) {
            AmazonS3Client sS3Client = Util.getS3Client(getApplicationContext());
            return Util.doesBucketExist();
        }

        @Override
        public void onPostExecute(Boolean result) {
            checked = true;
            exists = result;
        }
    }

    protected class DeleteBucket extends AsyncTask<Object, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Object... params) {
            AmazonS3Client sS3Client = Util.getS3Client(getApplicationContext());
            if (Util.doesBucketExist()) {
                Util.deleteBucket();
                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) {
                Toast.makeText(getApplicationContext(), "Bucket does not exist", Toast.LENGTH_SHORT)
                        .show();
            }
            else {
                Toast.makeText(getApplicationContext(), "Bucket successfully deleted!",
                        Toast.LENGTH_SHORT).show();
            }
            exists = false;
        }
    }
}
