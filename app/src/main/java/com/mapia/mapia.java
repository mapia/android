package com.mapia;

import android.app.Application;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;


/**
 * Created by daehyun on 2015. 4. 2..
 */
public class mapia extends Application {

    @Override
    public void onCreate() {
        AsyncHttpClient client = new AsyncHttpClient();

        client.get("api.mapeen.com", new AsyncHttpResponseHandler(){
            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
        super.onCreate();
    }


}
