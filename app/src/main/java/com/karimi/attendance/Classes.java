package com.karimi.attendance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class Classes extends AppCompatActivity {

    private Spinner classes;
    private Button next;
    private SessionManager sessionManager;
    private HashMap<String,String> spinnerMap;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classes);

        sessionManager = new SessionManager(this);
        HashMap<String, String> user_token = sessionManager.getUserDetails();
        String token = user_token.get(sessionManager.KEY_TOKEN);

        classes = (Spinner) findViewById(R.id.classes_spinner);
        next = (Button) findViewById(R.id.btn_next);

        pDialog = new ProgressDialog(Classes.this);
        pDialog.setMessage("Your Classes are Loading....");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        JobClient client = new JobClient();
        client.getClasses(token, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                pDialog.hide();

                Log.d("response", response.toString());

                try {
                    JSONArray jsonArray = response.getJSONArray("classes");
                    String[] classes_array = new String[jsonArray.length()];
                     spinnerMap = new HashMap<String, String>();

                    for (int i=0; i<jsonArray.length(); i++)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String name = jsonObject.getString("name");
                        String id = jsonObject.getString("id");

                        spinnerMap.put(name, id);
                        classes_array[i] = name;
                    }

                    ArrayAdapter<String> adapter =new ArrayAdapter<String>(Classes.this,android.R.layout.simple_spinner_item, classes_array);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    classes.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.d("Classes", "onFailure called");
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = classes.getSelectedItem().toString();
                String id = spinnerMap.get(name);
                Log.d("class_id", id);

                Intent intent = new Intent(Classes.this, Subjects.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

    }
}
