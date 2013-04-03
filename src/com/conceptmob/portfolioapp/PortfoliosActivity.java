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

import com.conceptmob.core.communication.SimpleServerResponse;
import com.conceptmob.core.utils.PreferencesSingleton;
import com.conceptmob.portfolioapp.R;
import com.conceptmob.portfolioapp.core.BaseApplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;


public class PortfoliosActivity extends ListActivity
{
    private BaseApplication app;
    private String authToken;
    private String identifier;
    
    
    // ###################################################################################################################
    // onCreate
    // ###################################################################################################################
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		
        // pull app instance details from BaseApplication
        app = (BaseApplication)this.getApplication();
        
        Log.i(app.TAG, "SCREEN: Loaded Portfolio activity.");
        
		// try and pick up the authToken from shared preferences
		authToken = PreferencesSingleton.getInstance().getPreference("authToken", null);
		identifier = PreferencesSingleton.getInstance().getPreference("identifier", null);
		
		// make sure that there's a valid authToken
		if (authToken != null) {
            // do the main work in an asynctask		    
		    new PortfolioTask(PortfoliosActivity.this).execute(authToken, identifier);
		}
    }
	
	
    // ###################################################################################################################
    // AsyncTask
    // ###################################################################################################################
    
	private class PortfolioTask extends AsyncTask<String, Void, SimpleServerResponse> {
	    
	    private HttpClient httpClient;
	    private Exception e = null;
        private Activity activity;
        private Context context; 
	    
	    
	    protected PortfolioTask(Activity activity) {
            this.context = activity;
	        httpClient = app.getHttpClient();
	    }
	    
	    
	    protected void onPreExecute() {}
	    
	    
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
	            
	            HttpGet httpRequest = new HttpGet(app.BASE_URL + "portfolios?" + paramString);	            
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
	    
	    
	    @Override 
	    protected void onPostExecute(SimpleServerResponse serverResponse) {
	        
	        // check to see if an exception came back and check the success of the request, do we have a response?
            if (e == null && serverResponse != null) {
                // pull out details from the response
                int statusCode = serverResponse.getStatusCode();
                String content = serverResponse.getContent();
                
                Log.i(app.TAG, "Status Code: " + Integer.toString(statusCode));
                Log.i(app.TAG, "Response: " + content);
                
                switch (statusCode) {
                    case 200:
                        Log.i(app.TAG, "HTTP 200: Portfolios retrieved successfully");
                        
                        ArrayList<HashMap<String, String>> portfoliosList = new ArrayList<HashMap<String, String>>();
                        
                        try {
                            JSONObject json = new JSONObject(content);
                            JSONArray portfolios = json.getJSONObject("response").getJSONArray("portfolios");
                            
                            for (int i = 0; i < portfolios.length(); i++) {
                                HashMap<String, String> map = new HashMap<String, String>();
                                JSONObject p = portfolios.getJSONObject(i);
                                map.put("id", String.valueOf(i));
                                map.put("name", p.getString("name"));
                                map.put("portfolio_id", p.getString("id".toString()));
                                portfoliosList.add(map);
                            }           
                        } catch (JSONException e) {
                            Log.e("log_tag", "Error parsing portfolio JSON: " + e.toString());
                        }
                        
                        Log.i(app.TAG, "Processed JSON");
                        
                        ListAdapter adapter = new SimpleAdapter(PortfoliosActivity.this, 
                                portfoliosList, 
                                R.layout.list_portfolios, 
                                new String[] { "name", "portfolio_id" }, 
                                new int[] { R.id.item_title, R.id.item_subtitle });
                        PortfoliosActivity.this.setListAdapter(adapter);
                        
                        final ListView lv = getListView();
                        lv.setTextFilterEnabled(true);
                        
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.menu_settings:
	            // Settings code:
	            // 1. Launch new activity
	            
	            return true;
	        case R.id.menu_signout:
	            // Sign Out code:
	            // 1. Destroy auth token on server (do later)
	            // 2. Remove auth token from shared preferences
	            // 3. Close out this activity and launch Sign In page
	            
	            // clear out auth token
	            PreferencesSingleton.getInstance().setPreference("authToken", null);
	            
	            // start activity
	            startActivity(new Intent(PortfoliosActivity.this, SignInActivity.class));
	            PortfoliosActivity.this.finish();
	            
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	
}