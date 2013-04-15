package com.conceptmob.portfolioapp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
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
import com.conceptmob.portfolioapp.adapters.PortfolioListAdapter;
import com.conceptmob.portfolioapp.core.BaseApplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class PortfoliosActivity extends ListActivity
{
    private BaseApplication app;
    private String authToken;
    private String identifier;
    ListView lv;
    
    
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
		
		lv = getListView();
        lv.setTextFilterEnabled(true);
		
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
        private ProgressDialog progress;
	    
        
        // constructor ###################################################################################################################
        
	    protected PortfolioTask(Activity activity) {
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
                        Log.i(app.TAG, "HTTP 200: Portfolios retrieved successfully");
                        
                        ArrayList<HashMap<String, String>> portfoliosList = new ArrayList<HashMap<String, String>>();
                        
                        try {
                            JSONObject json = new JSONObject(content);
                            JSONArray portfolios = json.getJSONObject("response").getJSONArray("portfolios");
                            
                            for (int i = 0; i < portfolios.length(); i++) {
                                HashMap<String, String> map = new HashMap<String, String>();
                                JSONObject p = portfolios.getJSONObject(i);
                                map.put("id", String.valueOf(i));
                                map.put("portfolio_id", p.getString("id".toString()));
                                map.put("name", p.getString("name"));
                                map.put("book_value", p.getString("book_value"));
                                map.put("market_value", p.getString("market_value"));
                                map.put("net_gain_dollar", p.getString("net_gain_dollar"));                                
                                map.put("net_gain_percent", p.getString("net_gain_percent"));
                                
                                portfoliosList.add(map);
                            }           
                        } catch (JSONException e) {
                            Log.e("log_tag", "Error parsing portfolio JSON: " + e.toString());
                        }
                        
                        Log.i(app.TAG, "Processed JSON");
                        
                        // get a reference to the layout which describes an item row and populates it as required
                        ListAdapter adapter = new PortfolioListAdapter(PortfoliosActivity.this, 
                                portfoliosList, 
                                R.layout.actiivty_portfolios_list_item, 
                                new String[] {"name", "book_value", "market_value", "net_gain_dollar", "net_gain_percent", "id"}, 
                                new int[] {R.id.portfolio_item_name, R.id.portfolio_item_book_value, R.id.portfolio_item_market_value, R.id.portfolio_item_net_gain_dollar, R.id.portfolio_item_net_gain_percent, R.id.portfolio_item_id});
                        
                        PortfoliosActivity.this.setListAdapter(adapter);
                        
                        lv.setOnItemClickListener(new OnItemClickListener() {
                        
                            @Override
                            public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                                Toast.makeText(PortfoliosActivity.this.getApplicationContext(), "Clicked: " + id + " at position: " + position, Toast.LENGTH_SHORT).show();
                            }                            
                        });
                        
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