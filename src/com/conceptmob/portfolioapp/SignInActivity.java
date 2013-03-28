package com.conceptmob.portfolioapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.conceptmob.core.communication.RestClient;
import com.conceptmob.core.communication.SimpleHttpResponse;
import com.conceptmob.core.utils.PreferencesSingleton;
import com.conceptmob.portfolioapp.R;
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
//        etEmail.setText("user1@conceptmob.com");
//        etPassword.setText("access");
        
        btnSignIn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {     
               String email = etEmail.getText().toString();
               String password = etPassword.getText().toString();
               
               if ((email.length() != 0) || (password.length() != 0)) {
                   if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                       
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
        
        public LoginTask(Activity activity) {
            this.context = activity;
            progress = new ProgressDialog(this.context); 
        }
        
        protected void onPreExecute() {
            progress.setMessage("Signing in");
            progress.show();
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
                            
                            Intent intent = new Intent(this.context, SimpleActivity.class);
                            intent.putExtra("authTokenResponse", response.getContent());
                            this.context.startActivity(intent);
                            SignInActivity.this.finish();
                            
                            break;
                        case 401:
                            Log.i("PortfolioApp", "LOGIN: Unsuccessful");
                            
                            break;
                    }
                    
//                    ObjectMapper mapper = new ObjectMapper();
//                    
//                    // handle the response, if it was a successful login or unauthorized
//                    int statusCode = response.getStatusCode();
//                    switch (statusCode) {
//                        case 201:
//                            Log.i("PortfolioApp", "LOGIN: Successful");
//                            
//                            AuthTokenContainer authTokenContainer = null;
//                            try {
//                                authTokenContainer = mapper.readValue(response.getContent(), AuthTokenContainer.class);
//                                Log.i("PortfolioApp", Integer.toString(authTokenContainer.response.meta.statusCode));
//                            } catch (JsonParseException e1) {
//                                // TODO Auto-generated catch block
//                                e1.printStackTrace();
//                            } catch (JsonMappingException e1) {
//                                // TODO Auto-generated catch block
//                                e1.printStackTrace();
//                            } catch (IOException e1) {
//                                // TODO Auto-generated catch block
//                                e1.printStackTrace();
//                            }
//                            
//                            // verify that the contents of the response match the header from the server
//                            if (authTokenContainer.response.meta.statusCode == 201) {
//                                Log.i("PortfolioApp", "inside statusCode test");
//                                Toast.makeText(this.context, "Sign in successful", Toast.LENGTH_LONG).show();
//                                
//                                Intent intent = new Intent(this.context, SimpleActivity.class);
//                                Bundle extras = new Bundle();
//                                extras.putSerializable("user", authTokenContainer);
//                                intent.putExtras(extras);
//                                this.context.startActivity(intent);
//                                SignInActivity.this.finish();
//                            }
//                            
//                            break;
//                        case 401:
//                            Log.i("PortfolioApp", "LOGIN: Unsuccessful");
//                            
//                            ErrorContainer errorContainer = null;
//                            try {
//                                errorContainer = mapper.readValue(response.getContent(), ErrorContainer.class);
//                                Log.i("PortfolioApp", "Jackson: " + errorContainer.toString());
//                            } catch (JsonParseException e) {
//                                // TODO Auto-generated catch block
//                                e.printStackTrace();
//                            } catch (JsonMappingException e) {
//                                // TODO Auto-generated catch block
//                                e.printStackTrace();
//                            } catch (IOException e) {
//                                // TODO Auto-generated catch block
//                                e.printStackTrace();
//                            }
//                            
//                            // verify that the contents of the response match the header from the server
//                            if (errorContainer.response.meta.statusCode == 401) {
//                                Toast.makeText(this.context, "An error occurred while trying to authenticate.", Toast.LENGTH_LONG).show();
//                            }
//                            
//                            break;
//                    }
                                        
                } else {
                    Toast.makeText(getApplicationContext(), "An error occurred while attempting to sign in.  Please try again.", Toast.LENGTH_LONG).show();
                }
            } else {
                Log.e("PortfolioApp", "Error in doInBackground task");
                e.printStackTrace();
            }
        }
        
        protected SimpleHttpResponse doInBackground(String... args) {
            Log.i("PortfolioApp", "doInBackground started");
            
            try {
                RestClient client = new RestClient("http://portfolioapp.conceptmob.com/api/v2/");
                
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("email", args[0]));
                params.add(new BasicNameValuePair("password", args[1]));
                params.add(new BasicNameValuePair("identifier", PreferencesSingleton.getInstance().getPreference("identifier", "")));
                
                SimpleHttpResponse response = client.post("auth/token/create", params);
                
                return response;
            } catch (Exception e) {
                this.e = e;
            }
           
            return null;
        }
    }
}
