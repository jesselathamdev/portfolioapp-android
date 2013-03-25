package com.conceptmob.portfolioapp;

import com.conceptmob.core.BaseActivity;
import com.conceptmob.portfolioapp.R;
import com.conceptmob.portfolioapp.utils.Actions;

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
                       
                       // DO API STUFF HERE!
                       // IF SUCCESSFUL THEN DO THE fOLLOWING
                       // wrap 
                       // Actions actions = new Actions();
                       // actions.getToken(email, password, "12345");
                       new Login().execute(email, password);
                       
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
    
    private class Login extends AsyncTask<String, String, String> {
        protected String doInBackground(String... params) {
            Actions actions = new Actions();
            actions.getToken(params[0], params[1]);
            
            return "";
        }
        
        protected void onPostExecute(String response) {
            Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
        }
    }
}
