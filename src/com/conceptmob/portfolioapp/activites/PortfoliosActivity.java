package com.conceptmob.portfolioapp.activites;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.conceptmob.portfolioapp.adapters.PortfoliosListAdapter;
import com.conceptmob.portfolioapp.base.BaseActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class PortfoliosActivity extends BaseActivity {
    
    private String authToken;
    private String identifier;
    private ListView lvPortfolios;
    private TextView tvPortfoliosLastUpdated;
    
    
    // ###################################################################################################################
    // onCreate
    // ###################################################################################################################
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.i(app.TAG, "ACTIVITY: Loaded Portfolios activity.");
        
        // set up the associated layout
        setContentView(R.layout.activity_portfolios);
        
        initActionBar();        
        initPreferences();
        initViewReferences();
        
		// make sure that there's a valid authToken
		if (authToken != null) {
            // do the main work in an asynctask		    
		    
		    getPortfolios();
		
		    lvPortfolios.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    
                    HashMap<String, String> map = (HashMap<String, String>)lvPortfolios.getItemAtPosition(position); // map.get("portfolio_id");
                    
                    Intent intent = new Intent(getApplicationContext(), PortfolioHoldingsActivity.class);
                    intent.putExtra("portfolio_id", map.get("portfolio_id"));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);                   
                }
		        
		    });
		}
    }
    
    
    // ###################################################################################################################
    // initPreferences
    // ###################################################################################################################
    
    private void initPreferences() {
        // fetch the preferences from the common singleton store
        authToken = PreferencesSingleton.getInstance().getPreference("authToken", null);
        identifier = PreferencesSingleton.getInstance().getPreference("identifier", null);        
    }
    
    
    // ###################################################################################################################
    // initViewReferences
    // ###################################################################################################################
    
    private void initViewReferences() {
        lvPortfolios = (ListView)findViewById(R.id.lvPortfolioList);  
        tvPortfoliosLastUpdated = (TextView)findViewById(R.id.tvPortfoliosLastUpdated);
    }
    
    
    // ###################################################################################################################
    // getPortfolios
    // ###################################################################################################################
    
    public void getPortfolios() {
        new PortfolioTask(PortfoliosActivity.this).execute(authToken, identifier);
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
	            
                Log.i(app.TAG, "Executing HTTP request (GET Portfolios)");
                
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
                
                Log.i(app.TAG, "Completed HTTP request (GET Portfolios)");
                
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
                            
                            // parse the JSON object and pull out the values that are of interest to us
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
                        ListAdapter adapter = new PortfoliosListAdapter(PortfoliosActivity.this, portfoliosList);
                        
                        lvPortfolios.setAdapter(adapter);
                        
                        // update the date and time last updated at the bottom of the screen
                        Date date = new Date();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM d, yyyy h:mma zzz");
                        
                        tvPortfoliosLastUpdated.setText("Last updated " + simpleDateFormat.format(date));
                        
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
                getPortfolios();
                
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}