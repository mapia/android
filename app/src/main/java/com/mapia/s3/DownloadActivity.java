package com.mapia.s3;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.mapia.R;
import com.mapia.s3.network.TransferController;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

/**
 * Created by daehyun on 2015. 5. 10..
 */
public class DownloadActivity extends Activity {
    private ListView mList;
    private AmazonS3Client mClient;
    private ObjectAdapter mAdapter;
    // keeps track of the objects the user has selected
    private HashSet<S3ObjectSummary> mSelectedObjects =
            new HashSet<S3ObjectSummary>();
    private Button mRefreshButton;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.s3_download);

        // initialize the client
        mClient = Util.getS3Client(DownloadActivity.this);
        mList = (ListView) findViewById(R.id.list);

        mAdapter = new ObjectAdapter(this);
        mList.setOnItemClickListener(new ItemClickListener());
        mList.setAdapter(mAdapter);

        mRefreshButton = (Button) findViewById(R.id.refresh);
        findViewById(R.id.download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // download all the objects that were selected
                String[] keys = new String[mSelectedObjects.size()];
                int i = 0;
                for (S3ObjectSummary obj : mSelectedObjects) {
                    keys[i] = obj.getKey();
                    i++;
                }
                TransferController.download(DownloadActivity.this, keys);
                finish();
            }
        });

        findViewById(R.id.refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RefreshTask().execute();
            }
        });

        new RefreshTask().execute();
    }


    private class RefreshTask extends AsyncTask<Void, Void, List<S3ObjectSummary>> {
        @Override
        protected void onPreExecute() {
            mRefreshButton.setEnabled(false);
            mRefreshButton.setText(R.string.refreshing);
        }

        @Override
        protected List<S3ObjectSummary> doInBackground(Void... params) {
            // get all the objects in bucket
            return mClient.listObjects(Constants.BUCKET_NAME.toLowerCase(Locale.US),
                    Util.getPrefix(
                            DownloadActivity.this)).getObjectSummaries();
        }

        @Override
        protected void onPostExecute(List<S3ObjectSummary> objects) {
            // now that we have all the keys, add them all to the adapter
            mAdapter.clear();
            mAdapter.addAll(objects);
            mSelectedObjects.clear();
            mRefreshButton.setEnabled(true);
            mRefreshButton.setText(R.string.refresh);
        }
    }

    /*
     * This lets the user click on anywhere in the row instead of just the
     * checkbox to select the files to download
     */
    private class ItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int pos,
                                long id) {
            S3ObjectSummary item = mAdapter.getItem(pos);
            boolean checked = false;
            // try removing, if it wasn't there add
            if (!mSelectedObjects.remove(item)) {
                mSelectedObjects.add(item);
                checked = true;
            }
            ((ObjectAdapter.ViewHolder) view.getTag()).checkbox.setChecked(
                    checked);
        }
    }

    /* Adapter for all the S3 objects */
    private class ObjectAdapter extends ArrayAdapter<S3ObjectSummary> {
        public ObjectAdapter(Context context) {
            super(context, R.layout.s3_bucket_row);
        }

        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.s3_bucket_row, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            S3ObjectSummary summary = getItem(pos);
            holder.checkbox.setChecked(mSelectedObjects.contains(summary));
            holder.key.setText(Util.getFileName(summary.getKey()));
            holder.size.setText(String.valueOf(summary.getSize()));
            return convertView;
        }

        public void addAll(Collection<? extends S3ObjectSummary> collection) {
            for (S3ObjectSummary obj : collection) {
                // if statement removes the "folder" from showing up
                if (!obj.getKey().equals(Util.getPrefix(DownloadActivity.this)))
                {
                    add(obj);
                }
            }
        }

        private class ViewHolder {
            private CheckBox checkbox;
            private TextView key;
            private TextView size;

            private ViewHolder(View view) {
                checkbox = (CheckBox) view.findViewById(R.id.checkbox);
                key = (TextView) view.findViewById(R.id.key);
                size = (TextView) view.findViewById(R.id.size);
            }
        }
    }
}
