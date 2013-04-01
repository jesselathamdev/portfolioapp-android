package com.conceptmob.portfolioapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.conceptmob.core.communication.RestClient;
import com.conceptmob.core.communication.SimpleHttpResponse;
import com.conceptmob.core.utils.PreferencesSingleton;
import com.conceptmob.portfolioapp.R;
import com.conceptmob.portfolioapp.core.BaseActivity;
import com.conceptmob.portfolioapp.data.AuthTokenContainer;
import com.conceptmob.portfolioapp.data.ErrorContainer;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class SignInActivity extends BaseActivity {
    
    private EditText etEmail;
    private EditText etPassword;
    private Button btnSignIn;
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
        Log.i(getAppName(), "ACTIVITY: Loaded Sign In");
        
        // set up the activity and associated layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        
        // get references to controls in layout
        etEmail = (EditText)findViewById(R.id.etSignInEmailAddress);
        etPassword = (EditText)findViewById(R.id.etSignInPassword);
        btnSignIn = (Button)findViewById(R.id.btnSignInSubmit);
        
        // helpers, remove for prod
        etEmail.setText("user1@conceptmob.com");
        etPassword.setText("access");
        
        btnSignIn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {     
               String email = etEmail.getText().toString();
               String password = etPassword.getText().toString();
               
               if ((email.length() != 0) || (password.length() != 0)) {
                   if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                       
                       // do the main work in an asynctask
                       new LoginTask(SignInActivity.this).execute(email, password);
                       
                   } else {
                       Toast.makeText(getApplicationContext(), "Invalid Email Address format", Toast.LENGTH_LONG).show();
                   }                       
               } else {
                   Toast.makeText(getApplicationContext(), "Email Address and/or Password Required", Toast.LENGTH_LONG).show();
               } 
           }
        });
    }
    
    
    private class LoginTask extends AsyncTask<String, Void, SimpleHttpResponse> {
        
        private Exception e = null;
        private Activity activity;
        private Context context;        
        private ProgressDialog progress;
        
        protected LoginTask(Activity activity) {
            this.context = activity;
            progress = new ProgressDialog(this.context); 
        }
        
        
        protected void onPreExecute() {
            progress.setMessage("Signing in");
            progress.show();
        }
        
        
        protected SimpleHttpResponse doInBackground(String... args) {
            Log.i("PortfolioApp", "doInBackground started for Login");
            
            try {
                RestClient client = new RestClient("http://10.0.2.2:8000/api/v2/");
                
                // put the parameters together required for the post
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("email", args[0]));
                params.add(new BasicNameValuePair("password", args[1]));
                params.add(new BasicNameValuePair("identifier", PreferencesSingleton.getInstance().getPreference("identifier", "")));
                
                return client.post("auth/token/create", params);
            } catch (Exception e) {
                this.e = e;
            }
           
            return null;
        }
        
        
        @Override
        protected void onPostExecute(SimpleHttpResponse response) {
            if (progress.isShowing()) {
                progress.dismiss();
            }
            
            if (e == null) {
                
                // check the success of the request
                Boolean success = response.getSuccess();
                
                if (success) {
                    int statusCode = response.getStatusCode();
                    switch (statusCode) {
                        case 201:
                            Log.i("PortfolioApp", "LOGIN: Successful");
                            Toast.makeText(this.context, "Sign in successful", Toast.LENGTH_LONG).show();
                            
                            // parse the response for the auth token just received
                            String authToken = "";
                            ObjectMapper mapper = new ObjectMapper();
                            AuthTokenContainer authTokenContainer = null;
                            try {
                                authTokenContainer = mapper.readValue(response.getContent(), AuthTokenContainer.class);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            
                            // store the auth token that was just received
                            PreferencesSingleton.getInstance().setPreference("authToken", authTokenContainer.response.token);
                            
                            Intent intent = new Intent(this.context, PortfoliosActivity.class);
                            intent.putExtra("authTokenResponse", response.getContent());
                            this.context.startActivity(intent);
                            SignInActivity.this.finish();
                            
                            break;
                        case 401:
                            Log.i("PortfolioApp", "LOGIN: Unsuccessful");
                            
                            break;
                    }
                    
                } else {
                    Toast.makeText(getApplicationContext(), "An error occurred while attempting to sign in.  Please try again.", Toast.LENGTH_LONG).show();
                }
            } else {
                Log.e("PortfolioApp", "Error in doInBackground task");
                e.printStackTrace();
            }
        }
    }
}
