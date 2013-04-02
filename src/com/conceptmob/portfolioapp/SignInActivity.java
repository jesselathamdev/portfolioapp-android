package com.conceptmob.portfolioapp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.conceptmob.core.communication.SimpleServerResponse;
import com.conceptmob.core.utils.PreferencesSingleton;
import com.conceptmob.portfolioapp.R;
import com.conceptmob.portfolioapp.core.BaseApplication;
import com.conceptmob.portfolioapp.data.AuthTokenContainer;

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


public class SignInActivity extends Activity {
    
    private BaseApplication app;    
    private EditText etEmail;
    private EditText etPassword;
    private Button btnSignIn;
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // pull app instance details from BaseApplication
        app = (BaseApplication)this.getApplication();
        
        Log.i(app.TAG, "ACTIVITY: Loaded Sign In");
        
        // set up the associated layout
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
    
    
    private class LoginTask extends AsyncTask<String, Void, SimpleServerResponse> {
        
        private HttpClient httpClient;
        private Exception e = null;
        private Activity activity;
        private Context context;        
        private ProgressDialog progress;
        
        
        protected LoginTask(Activity activity) {
            this.context = activity;
            progress = new ProgressDialog(this.context);
            httpClient = app.getHttpClient();
        }
        
        
        protected void onPreExecute() {
            progress.setMessage("Signing in");
            progress.show();
        }
        
        
        protected SimpleServerResponse doInBackground(String... args) {
            Log.i(app.TAG, "doInBackground started for Login");
            
            SimpleServerResponse serverResponse = null;
            
            try {                
                // build the encoded parameters required for the request
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("email", args[0]));
                params.add(new BasicNameValuePair("password", args[1]));
                params.add(new BasicNameValuePair("identifier", PreferencesSingleton.getInstance().getPreference("identifier", "")));
                
                // build the request
                HttpPost httpRequest = new HttpPost(app.BASE_URL + "auth/token/create");                
                HttpResponse httpResponse = null;                
                HttpEntity httpEntity = null;
                
                // include the extra request parameters for post
                httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                
                // Log a few details
                Log.i(app.TAG, "Executing HTTP request (Login)");
                
                // execute request and handle return response, returning it a custom server response object (due to potentially long running EntityUtils)
                httpResponse = httpClient.execute(httpRequest);
                serverResponse = new SimpleServerResponse();
                serverResponse.setStatusCode(httpResponse.getStatusLine().getStatusCode());
                httpEntity = httpResponse.getEntity();
                serverResponse.setContent(EntityUtils.toString(httpEntity));
                httpEntity.consumeContent();             
                serverResponse.setSuccess(true);
                
            } catch (UnsupportedEncodingException e) {
                this.e = e;
                // covers UrlEncodedFormEntity issues
                e.printStackTrace();
            } catch (IOException e) {
                this.e = e;
                // covers:
                // ClientProtocolException
                // ConnectTimeoutException
                // ConnectionPoolTimeoutException
                // SocketTimeoutException
                e.printStackTrace();
            } catch (Exception e) {
                this.e = e;
                // covers all others
                e.printStackTrace();
            } 
            
            return serverResponse;
        }
        
        
        @Override
        protected void onPostExecute(SimpleServerResponse serverResponse) {
            if (progress.isShowing()) {
                progress.dismiss();
            }
            
            // check to see if an exception came back, if not, carry on
            if (e == null) {                
                // check the success of the request, do we have a response?
                if (serverResponse != null) {
                    // pull out details from the response
                    int statusCode = serverResponse.getStatusCode();
                    String content = serverResponse.getContent();
                    
                    switch (statusCode) {
                        case 201:
                            Log.i(app.TAG, "HTTP 201: Auth Token creation successful");
                            
                            // splash a toast up to the user
                            Toast.makeText(this.context, "Sign in successful", Toast.LENGTH_LONG).show();
                            
                            // parse the response for the auth token just received, could just pull this out for simplicity and speed and use a few JSONOjbect calls...
                            // all we're really doing is getting the token value out in order to store
                            ObjectMapper mapper = new ObjectMapper();
                            AuthTokenContainer authTokenContainer = null;
                            
                            try {
                                authTokenContainer = mapper.readValue(content, AuthTokenContainer.class);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            
                            // store the auth token that was just received
                            PreferencesSingleton.getInstance().setPreference("authToken", authTokenContainer.response.token);
                            
                            Intent intent = new Intent(this.context, PortfoliosActivity.class);
                            intent.putExtra("authTokenResponse", content);
                            this.context.startActivity(intent);
                            SignInActivity.this.finish();
                            
                            break;
                        case 401:
                            Log.i(app.TAG, "HTTP 401: Auth Token creation unsuccessful");
                            
                            break;
                        default:
                            Log.e(app.TAG, "There was an error contacting the server.");
                            Toast.makeText(getApplicationContext(), "An error occurred while attempting to sign in.  Please try again.", Toast.LENGTH_LONG).show();
                    }
                    
                } else {
                    Log.e(app.TAG, "There was an unexpected error.");
                }
            } else {
                Log.e(app.TAG, "Error in doInBackground task");
                e.printStackTrace();
            }
        }
    }
}
