package com.conceptmob.portfolioapp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.conceptmob.core.communication.SimpleServerResponse;
import com.conceptmob.core.utils.PreferencesSingleton;
import com.conceptmob.portfolioapp.R;
import com.conceptmob.portfolioapp.adapters.PortfolioListAdapter;
import com.conceptmob.portfolioapp.adapters.TransactionActivityListAdapter;
import com.conceptmob.portfolioapp.core.BaseActivity;


public class TransactionActivityActivity extends BaseActivity {
  
    private String authToken;
    private String identifier;
    private ListView lvActivity;
    
    
    // ###################################################################################################################
    // onCreate
    // ###################################################################################################################
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(app.TAG, "ACTIVITY: Loaded Transaction Activity activity.");
        
        // set up the associated layout
        setContentView(R.layout.activity_transaction_activity);
        
        initActionBar();
        
        // try and pick up the authToken from shared preferences
        authToken = PreferencesSingleton.getInstance().getPreference("authToken", null);
        identifier = PreferencesSingleton.getInstance().getPreference("identifier", null);
        
        lvActivity = (ListView)findViewById(R.id.lvTransactionActivityList);
        
        // make sure that there's a valid authToken
        if (authToken != null) {
            // do the main work in an asynctask         
            
            getTransactionActivity();
        
            lvActivity.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    Toast.makeText(getBaseContext(), "Selected: " + position, Toast.LENGTH_SHORT).show();
                }
                
            });
        }

    }
    
    
    // ###################################################################################################################
    // getTransactionActivity
    // ###################################################################################################################
    
    public void getTransactionActivity() {
        new TransactionActivityTask(TransactionActivityActivity.this).execute(authToken, identifier);
    }
    
    
    // ###################################################################################################################
    // AsyncTask
    // ###################################################################################################################
    
    private class TransactionActivityTask extends AsyncTask<String, Void, SimpleServerResponse> {
        
        private HttpClient httpClient;
        private Exception e = null;
        private Activity activity;
        private Context context; 
        private ProgressDialog progress;
        
        
        // constructor ###################################################################################################################
        
        protected TransactionActivityTask(Activity activity) {
            this.context = activity;
            progress = new ProgressDialog(this.context);
            httpClient = app.getHttpClient();
        }
        
        
        // onPreExecute ###################################################################################################################
        
        protected void onPreExecute() {
            progress.setMessage("Reticulating splines...");
            progress.show();
        }
        
        
        // doInBackground ###################################################################################################################
        
        @Override
        protected SimpleServerResponse doInBackground(String... args) {
            Log.i(app.TAG, "doInBackground started for Portfolios");
        
            SimpleServerResponse serverResponse = null;
            
            try {
                // build the encoded parameters required for the request
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("token", args[0]));
                params.add(new BasicNameValuePair("identifier", args[1]));
                
                String paramString = URLEncodedUtils.format(params, HTTP.UTF_8);
                
                HttpGet httpRequest = new HttpGet(app.BASE_URL + "activity?" + paramString);              
                HttpResponse httpResponse = null;
                HttpEntity httpEntity = null;
                
                // Log a few details
                Log.i(app.TAG, "Executing HTTP request (Login)");
                
                // execute request and handle return response, returning it a custom server response object (due to potentially long running EntityUtils)
                httpResponse = httpClient.execute(httpRequest);
                serverResponse = new SimpleServerResponse();
                serverResponse.setStatusCode(httpResponse.getStatusLine().getStatusCode());
                httpEntity = httpResponse.getEntity();
                serverResponse.setContent(EntityUtils.toString(httpEntity));                
                if (httpEntity != null) {
                    httpEntity.consumeContent();
                }
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
        
        
        // onPostExecute ###################################################################################################################
        
        @Override 
        protected void onPostExecute(SimpleServerResponse serverResponse) {
            if (progress.isShowing()) {
                progress.dismiss();
            }
            
            // check to see if an exception came back and check the success of the request, do we have a response?
            if (e == null && serverResponse != null) {
                // pull out details from the response
                int statusCode = serverResponse.getStatusCode();
                String content = serverResponse.getContent();
                
                Log.i(app.TAG, "Status Code: " + Integer.toString(statusCode));
                
                switch (statusCode) {
                    case 200:
                        Log.i(app.TAG, "HTTP 200: Transaction activity retrieved successfully");
                        
                        ArrayList<HashMap<String, String>> activityList = new ArrayList<HashMap<String, String>>();
                        
                        try {
                            JSONObject json = new JSONObject(content);
                            JSONArray activity = json.getJSONObject("response").getJSONArray("activity");
                            
                            // parse the JSON object and pull out the values that are of interest to us
                            for (int i = 0; i < activity.length(); i++) {
                                HashMap<String, String> map = new HashMap<String, String>();
                                JSONObject a = activity.getJSONObject(i);
                                map.put("id", String.valueOf(i));
                                map.put("portfolio_id", a.getString("id".toString()));
                                map.put("date_transacted", a.getString("date_transacted"));
                                map.put("name", a.getString("name"));
                                map.put("value", a.getString("value"));
                                map.put("quantity", a.getString("quantity"));
                                
                                activityList.add(map);
                            }           
                        } catch (JSONException e) {
                            Log.e("log_tag", "Error parsing transaction activity JSON: " + e.toString());
                        }
                        
                        Log.i(app.TAG, "Processed JSON");
                        
                        // get a reference to the layout which describes an item row and populates it as required
                        ListAdapter adapter = new TransactionActivityListAdapter(TransactionActivityActivity.this, activityList);
                        
                        lvActivity.setAdapter(adapter);
                        
                        break;
                    case 401:
                        Log.i(app.TAG, "HTTP 401: Authentication unsuccessful");
                        
                        break;
                    default:
                        Log.e(app.TAG, "There was an error contacting the server.");
                        Toast.makeText(getApplicationContext(), "An error occurred while processing your request.  Please try again.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    
    
    // ###################################################################################################################
    // Menus
    // ###################################################################################################################
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                getTransactionActivity();
                
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
