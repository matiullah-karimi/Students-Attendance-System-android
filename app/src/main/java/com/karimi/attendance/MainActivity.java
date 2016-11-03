package com.karimi.attendance;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    private Button login_button;
    private EditText email_edittext;
    private EditText password_edittext;
    private SessionManager sessionManager;
    private ProgressDialog pDialog;
    private TextView errorText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        sessionManager = new SessionManager(this);

        email_edittext = (EditText) findViewById(R.id.email_field);
        password_edittext = (EditText) findViewById(R.id.password_field);
        login_button = (Button) findViewById(R.id.btn_login);
        errorText = (TextView) findViewById(R.id.error);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isNetWorkAvailable()) {
                    Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_LONG).show();
                } else {

                    pDialog = new ProgressDialog(MainActivity.this);
                    pDialog.setMessage("Attempting login...");
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(true);
                    pDialog.show();

                    final String email = email_edittext.getText().toString();
                    String password = password_edittext.getText().toString();

                    Log.d("yes", "worked");

                    JobClient client = new JobClient();
                    client.getLogin(email, password, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);

                            Log.d("response", response.toString());
                            pDialog.hide();

                            if (response.has("token")) {
                                try {
                                    String token = response.getString("token");
                                    sessionManager.createLoginSession(email, token);

                                    Intent intent = new Intent(MainActivity.this, Classes.class);
                                    startActivity(intent);
                                    finish();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                            pDialog.hide();

                            try {
                                String error = errorResponse.getString("error");
                                if (error.equals("invalid_credentials")){

                                    errorText.setText("Invalid Credentials");
                                }
                                else {
                                    errorText.setText("Something went wrong, please try again ");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });


    }

    public boolean isNetWorkAvailable(){
        boolean isNetworkAvailable = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()){
            isNetworkAvailable = true;
        }
        return isNetworkAvailable;
    }
}
