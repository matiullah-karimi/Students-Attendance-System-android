package com.karimi.attendance;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class Students extends AppCompatActivity {

    private ListView listView;
    private SessionManager sessionManager;
    private ProgressDialog pDialog;
    private ArrayList<StudentDetails> studentDetailsArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students);


        final String class_id = getIntent().getStringExtra("class_id");
        final String subject_id = getIntent().getStringExtra("subject_id");

        sessionManager = new SessionManager(this);
        HashMap<String, String> user_token = sessionManager.getUserDetails();
        final String token = user_token.get(sessionManager.KEY_TOKEN);

        pDialog = new ProgressDialog(Students.this);
        pDialog.setMessage("Students are Loading....");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        listView = (ListView) findViewById(R.id.listView);

        final JobClient client = new JobClient();
        client.getStudents(token, class_id, new JsonHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                Log.d("response", response.toString());
                pDialog.hide();

                try {
                    JSONArray jsonArray = response.getJSONArray("students");

                    String[] students_array = new String[jsonArray.length()];

                    studentDetailsArrayList = new ArrayList<StudentDetails>();
                    Log.d("students_array", jsonArray.toString());

                    for (int i=0; i<jsonArray.length(); i++)
                    {
                        JSONObject second = jsonArray.getJSONObject(i);
                        String name = second.getString("name");
                        String id = second.getString("id");

                        studentDetailsArrayList.add(new StudentDetails(name, id));
                        students_array[i] = name;
                    }

                    ArrayAdapter<String> adapter =new ArrayAdapter<String>(Students.this,android.R.layout.simple_list_item_multiple_choice, students_array);
                    listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    listView.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selected = "";
                int status =0;
                String id = "";
                int count = listView.getCount();
                HashMap<String, HashMap<String, String>> results = new HashMap<String, HashMap<String, String>>();
                HashMap<String, String> attendance = new HashMap<String, String>();

                SparseBooleanArray sparseBooleanArray = listView.getCheckedItemPositions();
                for(int i = 0; i < count; i++){
                    if(sparseBooleanArray.get(i)) {
                        status = 1;
                        selected = listView.getItemAtPosition(i).toString();
                        if (selected.equals(studentDetailsArrayList.get(i).getName())){
                            id = studentDetailsArrayList.get(i).getId();
                        }
                        attendance.put(id, Integer.toString(status));
                    }
                    else {
                        status = 0;
                        id = studentDetailsArrayList.get(i).getId();
                        attendance.put(id, Integer.toString(status));
                    }
                }
                results.put("results", attendance);

                Log.d("results", results.toString());

                client.sendResult(results, class_id, subject_id, token, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.d("saveResult", response.toString());
                        super.onSuccess(statusCode, headers, response);

                        Toast.makeText(Students.this, "Succesfully Submitted!!!", Toast.LENGTH_LONG).show();
                    }


                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);

                        Log.d("step2", errorResponse.toString());
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);

                        if (statusCode == 200)
                        {
                            Log.d("step1", responseString);
                        }
                    }


                });


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
