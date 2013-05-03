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
import com.conceptmob.portfolioapp.adapters.PortfolioHoldingsListAdapter;
import com.conceptmob.portfolioapp.adapters.PortfolioListAdapter;
import com.conceptmob.portfolioapp.core.BaseActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class PortfolioHoldingsActivity extends BaseActivity {
    
    private String authToken;
    private String identifier;
    private String portfolioId;
    private ListView lvPortfolioHoldings;
    
    
    // ###################################################################################################################
    // onCreate
    // ###################################################################################################################
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.i(app.TAG, "ACTIVITY: Loaded Portfolio Holdings activity.");
        
        // set up the associated layout
        setContentView(R.layout.activity_portfolio_holdings);
        
        initActionBar();
        
		// try and pick up the authToken from shared preferences
		authToken = PreferencesSingleton.getInstance().getPreference("authToken", null);
		identifier = PreferencesSingleton.getInstance().getPreference("identifier", null);
		
		// get the portfolio containing the holdings in question from the previous activity
		Bundle extras = getIntent().getExtras();
        if (extras != null) {
            portfolioId = extras.getString("portfolio_id");
        }
		
		lvPortfolioHoldings = (ListView)findViewById(R.id.lvPortfolioHoldings);
		
		// make sure that there's a valid authToken
		if (authToken != null) {
            // do the main work in an asynctask		    
		    
		    getPortfolioHoldings(authToken, identifier, portfolioId);
		
		    lvPortfolioHoldings.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    Toast.makeText(getBaseContext(), "Selected: " + position, Toast.LENGTH_SHORT).show();
                }
		        
		    });
		}
    }
    
    
    // ###################################################################################################################
    // getPortfolioHoldings
    // ###################################################################################################################
    
    public void getPortfolioHoldings(String authToken, String identifier, String portfolio_id) {
        new PortfolioHoldingsTask(PortfolioHoldingsActivity.this).execute(authToken, identifier, portfolio_id);
    }
    
    
    // ###################################################################################################################
    // AsyncTask
    // ###################################################################################################################
    
	private class PortfolioHoldingsTask extends AsyncTask<String, Void, SimpleServerResponse> {
	    
	    private HttpClient httpClient;
	    private Exception e = null;
        private Activity activity;
        private Context context; 
        private ProgressDialog progress;
	    
        
        // constructor ###################################################################################################################
        
	    protected PortfolioHoldingsTask(Activity activity) {
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
	        Log.i(app.TAG, "doInBackground started for Portfolio Holdings");
	    
	        SimpleServerResponse serverResponse = null;
	        
	        try {
	            // build the encoded parameters required for the request
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("token", args[0]));
                params.add(new BasicNameValuePair("identifier", args[1]));
                                
                String paramAuthString = URLEncodedUtils.format(params, HTTP.UTF_8);
	            
                // get the portfolio id from the args
                String pId = args[2];
                
	            HttpGet httpRequest = new HttpGet(app.BASE_URL + "portfolios/" + pId + "/holdings?" + paramAuthString);	            
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
                        Log.i(app.TAG, "HTTP 200: Portfolio Holdings retrieved successfully");
                        
                        ArrayList<HashMap<String, String>> portfolioHoldingsList = new ArrayList<HashMap<String, String>>();
                        
                        try {
                            JSONObject json = new JSONObject(content);
                            JSONArray portfolioHoldings = json.getJSONObject("response").getJSONArray("portfolio_holdings");
                            
                            // parse the JSON object and pull out the values that are of interest to us
                            for (int i = 0; i < portfolioHoldings.length(); i++) {
                                HashMap<String, String> map = new HashMap<String, String>();
                                JSONObject p = portfolioHoldings.getJSONObject(i);
                                map.put("id", String.valueOf(i));
                                map.put("portfolio_id", p.getString("portfolio_id".toString()));
                                map.put("stock_name", p.getString("stock_name"));
                                map.put("last_price", p.getString("last_price"));
                                map.put("total_quantity", p.getString("total_quantity"));
                                
                                portfolioHoldingsList.add(map);
                            }           
                        } catch (JSONException e) {
                            Log.e("log_tag", "Error parsing portfolio holding JSON: " + e.toString());
                        }
                        
                        Log.i(app.TAG, "Processed JSON");
                        
                        // get a reference to the layout which describes an item row and populates it as required
                        ListAdapter adapter = new PortfolioHoldingsListAdapter(PortfolioHoldingsActivity.this, portfolioHoldingsList);
                        
                        lvPortfolioHoldings.setAdapter(adapter);
                        
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
                getPortfolioHoldings(authToken, identifier, portfolioId);
                
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}