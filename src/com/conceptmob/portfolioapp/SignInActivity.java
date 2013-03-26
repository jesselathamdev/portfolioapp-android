package com.conceptmob.portfolioapp;

import org.apache.http.HttpResponse;

import com.conceptmob.core.BaseActivity;
import com.conceptmob.core.communication.RestClient;
import com.conceptmob.core.communication.SimpleHttpResponse;
import com.conceptmob.portfolioapp.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
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
        
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        
//        StrictMode.ThreadPolicy policy = new StrictMode.
//        ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy); 
        
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
                       
                       new LoginTask(SignInActivity.this).execute(email, password);
                       
                       // startActivity(new Intent(v.getContext(), SimpleActivity.class));
                       // SignInActivity.this.finish();
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
        
        private Activity activity;
        private Context context;
        private ProgressDialog progress;
        
        public LoginTask(Activity activity) {
            this.activity = activity;
            context = activity;
            progress = new ProgressDialog(context); 
        }
        
        protected void onPreExecute() {
            this.progress.setMessage("Signing in");
            this.progress.show();
        }
        
        @Override
        protected void onPostExecute(SimpleHttpResponse response) {
            Log.i("PortfolioApp", "onPostExecute started");
            
            if (this.progress.isShowing()) {
                this.progress.dismiss();
            }
            
            Log.i("PortfolioApp", "API Success: " + response.getSuccess());
            Log.i("PortfolioApp", "API Status Code: " + response.getStatusCode());            
            Log.i("PortfolioApp", "API Content: " + response.getContent());
            
//            if (restResponse.getSuccess()) {
//                Toast.makeText(getApplicationContext(), "Access token success: ", Toast.LENGTH_LONG).show();
//            } else {
//                Toast.makeText(getApplicationContext(), "Access token failure", Toast.LENGTH_LONG).show();
//            }            
        }
        
        protected SimpleHttpResponse doInBackground(String... params) {
            Log.i("PortfolioApp", "doInBackground started");
            RestClient client = new RestClient("http://10.0.2.2:8000/api/v2/");
            SimpleHttpResponse response = client.get("auth/token/create");
            
            return response;
        }
    }
}
