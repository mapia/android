package com.mapia;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URI;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;


public class RestRequestHelper {

    private static RestRequestHelper instance;

    private RestCookieManager cookieManager;
    private RestAdapter restAdapter;
    private RestRequest restRequest;

    public static RestRequestHelper newInstance() {
        if (instance == null) {
            instance = new RestRequestHelper();
        }
        return instance;
    }

    public RestRequestHelper() {
        cookieManager = new RestCookieManager();
        CookieHandler.setDefault(cookieManager);

        restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://54.65.32.198:8080")
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        // 쿠키 설정
                        request.addHeader("Cookie", cookieManager.getCurrentCookie());
                    }
                })
                .build();

        restRequest = restAdapter.create(RestRequest.class);

    }

    public interface RestRequest {

        @POST("/auth/signup")
        @Headers({ "Content-Type: application/json;charset=UTF-8"})
        void signUp(@Body UserJsonRequest userJO,
                    Callback<JsonObject> callback);

//        @FormUrlEncoded
        @POST("/auth/login")
        @Headers({ "Content-Type: application/json;charset=UTF-8"})
        void login(@Body UserJsonRequest userJO,
                   Callback<JsonObject> callback);

        @POST("/post")
        @Headers({ "Content-Type: application/json;charset=UTF-8"})
        void posts(@Body PostJsonRequest postJO,
                   Callback<JsonObject> callback);

        @GET("/auth/login")
        void login(Callback<JsonObject> callback);

        @GET("/auth/profile")
        void profile(Callback<JsonObject> callback);

        @GET("/post")
        void posts(@Query("map-type") String mapType,
                   @Query("center-latitude")double lat, @Query("center-longitude")double lng,
                   @Query("map-level")float level, Callback<JsonObject> callback);
        @GET("/post?type={map-type}&(group={group-id})&lat={center-latitude}&lng={center-longitude}&level={map-level}")
        void posts(@Query("map-type") String mapType, @Query("group-id") int groupID,
                   @Query("center-latitude")double lat, @Query("center-longitude")double lng,
                   @Query("map-level")float level, Callback<JsonObject> callback);

    }

    public void signUp(String id, String password, Callback<JsonObject> callback) {
        UserJsonRequest userJO = null;
        //Since GSON make string as json format while wrapping key 'nameValuePairs' ,
        //그래서 그냥 JSONObject 안쓰고 JsonObject 사용함
        try {
            userJO = new UserJsonRequest(id, password);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        restRequest.signUp(userJO, callback);
    }

    public void login(Callback<JsonObject> callback) {
        restRequest.login(callback);
    }

    public void login(String id, String password, Callback<JsonObject> callback) {
        UserJsonRequest userJO = null;
        //Since GSON make string as json format while wrapping key 'nameValuePairs' ,
        //그래서 그냥 JSONObject 안쓰고 JsonObject 사용함
        try {
            userJO = new UserJsonRequest(id, password);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        restRequest.login(userJO, callback);
    }

    public void posts(String content, LatLng latlng, Callback<JsonObject> callback){
        PostJsonRequest postJO = null;
        try{
            postJO = new PostJsonRequest(content, latlng);
        } catch(Exception e){
            e.printStackTrace();
        }
        restRequest.posts(postJO, callback);
    }

    public void profile(Callback<JsonObject> callback) {
        restRequest.profile(callback);
    }

    public void posts(String mapType, double lat, double lng, float level, Callback<JsonObject> callback){
        restRequest.posts(mapType, lat, lng, level, callback);
    }
    public void posts(String mapType, int groupID, double lat, double lng, float level, Callback<JsonObject> callback){
        restRequest.posts(mapType, groupID, lat, lng, level, callback);
    }

}

class RestCookieManager extends CookieManager {

    private String currentCookie;

    @Override
    public void put(URI uri, Map<String, List<String>> stringListMap) throws IOException {
        super.put(uri, stringListMap);
        if (stringListMap != null && stringListMap.get("Set-Cookie") != null)
            for (String string : stringListMap.get("Set-Cookie")) {
                if (string.contains("session")) {
                    currentCookie = string;
                }
            }
    }

    public String getCurrentCookie() {
        return currentCookie;
    }
}

class PostJsonRequest{
    final String content;
    final double lat, lng;

    PostJsonRequest(String content, LatLng latlng){
        this.content = content;
        this.lat = latlng.latitude;
        this.lng = latlng.longitude;
    }
}

class UserJsonRequest {
    final String username;
    final String password;

    UserJsonRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}