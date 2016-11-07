package com.karimi.attendance;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

/**
 * Created by Matiullah Karimi on 4/26/2016.
 */
public class JobClient {
    Context context;

    private static final String API_BASE_URL = "http://172.30.10.153:8080/api/teacher";
    private AsyncHttpClient client;

    public JobClient() {
        this.client = new AsyncHttpClient();
    }

    private String getApiUrl(String relativeUrl) {
        return API_BASE_URL + relativeUrl;
    }

    private String getApiUrl() {
        return API_BASE_URL;
    }



    // login the user
    public void getLogin(String username,String password,JsonHttpResponseHandler handler){

            HashMap<String,String> params= new HashMap<String, String>();
            params.put("email",username.toString());
            params.put("password",password.toString());
            RequestParams param = new RequestParams(params);

            String url = getApiUrl("/login");
            client.post(url,param,handler);
    }

    // Method for accessing classes
    public void getClasses(String token, JsonHttpResponseHandler handler) {

        String url = getApiUrl("?token="+token);
        client.get(url , handler);
    }

    public void getSubjects(String token, String id, JsonHttpResponseHandler handler)
    {
        String url = getApiUrl("/classSubjects/" + id + "?token=" + token);

        client.get(url,handler);
    }

    public void getStudents(String token, String class_id, JsonHttpResponseHandler handler)
    {
        String url = getApiUrl("/classStudents/" + class_id + "?token=" + token);

        client.get(url, handler);
    }

    public void sendResult(HashMap<String, HashMap<String, String>> results, String class_id, String subject_id, String token, JsonHttpResponseHandler handler)
    {
        JSONObject object = new JSONObject(results);
        ByteArrayEntity entity = null;
        try {
            entity = new ByteArrayEntity(object.toString().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

        String url = getApiUrl("/saveResult/" + class_id  + "/" + subject_id + "?token="+token);
        client.post(context,url, entity, "application/json", handler);
    }



}
