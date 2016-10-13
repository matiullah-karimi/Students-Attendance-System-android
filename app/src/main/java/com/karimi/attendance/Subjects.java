package com.karimi.attendance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class Subjects extends AppCompatActivity {
    private Spinner subjects;
    private Button next;
    private SessionManager sessionManager;
    private HashMap<String,String> spinnerMap;
    private String class_id;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjects);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sessionManager = new SessionManager(this);
        HashMap<String, String> user_token = sessionManager.getUserDetails();
        String token = user_token.get(sessionManager.KEY_TOKEN);

        class_id = getIntent().getStringExtra("id");
        Log.d("class_id", class_id);

        subjects = (Spinner) findViewById(R.id.subjects_spinner);
        next = (Button) findViewById(R.id.btn_next);

        pDialog = new ProgressDialog(Subjects.this);
        pDialog.setMessage("Subjects are Loading....");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        JobClient client = new JobClient();

        client.getSubjects(token, class_id, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                Log.d("response", response.toString());
                pDialog.hide();

                try {
                    JSONArray jsonArray = response.getJSONArray("teacherSubjects");

                    String[] subjects_array = new String[jsonArray.length()];
                    spinnerMap = new HashMap<String, String>();

                    Log.d("subjects_array", jsonArray.toString());

                    for (int i=0; i<jsonArray.length(); i++)
                    {
                        JSONObject sencond = jsonArray.getJSONArray(i).getJSONObject(0);
                        String name = sencond.getString("name");
                        String id = sencond.getString("id");

                        spinnerMap.put(name, id);
                        subjects_array[i] = name;
                    }

                    ArrayAdapter<String> adapter =new ArrayAdapter<String>(Subjects.this,android.R.layout.simple_spinner_item, subjects_array);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    subjects.setAdapter(adapter);
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
            public void onClick(View view) {

                String name = subjects.getSelectedItem().toString();
                String subject_id = spinnerMap.get(name);

                Intent intent = new Intent(Subjects.this, Students.class);
                intent.putExtra("subject_id", subject_id);
                intent.putExtra("class_id", class_id);
                startActivity(intent);
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
