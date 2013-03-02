package com.socialshare.http;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.LinkedHashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import com.socialshare.util.SS_Constants;
import com.socialshare.util.SS_Log;
import com.socialshare.util.SS_Util;


/**
 * This class contains the HTTP methods used by this application
 * @author Midhu
 */
public class SS_HttpOperations {
	private final String LOG_TAG = "Social Share";
	private String API_PATH = "";
	
	private InputStream prepareConnection(LinkedHashMap<String, String> parameters) throws Exception {
		String param = SS_Util.encodeUrl(parameters);
		URL url = new URL(API_PATH + "?" + param);
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url.toURI());
		
		SS_Log.i(LOG_TAG, "URL : " + url.toString());
		int nResponse = 0;
		HttpResponse response = null;
		try {
			response = httpClient.execute(httpGet);
			nResponse = response.getStatusLine().getStatusCode();
		} catch (UnknownHostException e) {
			SS_Log.printStackTrace(e);
			throw new UnknownHostException();
		} catch (Exception e) {
			SS_Log.printStackTrace(e);
			if (e.getMessage().equalsIgnoreCase("Write error: I/O error during system call, Broken pipe")) {
				nResponse = -1;
			}
		}
		try {
			if (nResponse == HttpURLConnection.HTTP_OK || nResponse == HttpURLConnection.HTTP_BAD_REQUEST) {
				return response.getEntity().getContent();
			} else if (nResponse == -1) {
				return prepareConnection(parameters);
			}
		} catch (Exception e) {
			SS_Log.printStackTrace(e);
		}
		return null;
	}
	
	public JSONObject getNewsData(String newsFeedURL, int pageNumber) throws Exception {
		API_PATH = SS_Constants.API_YQL_BASE;
		String yql = "select * from rss where url=\"" + newsFeedURL + "\"" 
				+ " limit "+ SS_Constants.SIZE_RESULT 
				+ " offset " + ((pageNumber * SS_Constants.SIZE_RESULT) + 1);
		String env = "store://datatables.org/alltableswithkeys";
		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
		
		parameters.put("q", yql);
		parameters.put("format", "json");
		parameters.put("env", env);
		
		String response = SS_Util.read(prepareConnection(parameters));
		return SS_Util.parseJson(response);
	}
	
	//TODO: Sample Request Method
	public JSONObject getJSONFromURL(String keyword) throws Exception {
		API_PATH = SS_Constants.API_BASE;
		LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
		
		parameters.put("param1", keyword);
		parameters.put("param2", "value2");
		
		String response = SS_Util.read(prepareConnection(parameters));
		return SS_Util.parseJson(response);
	}
}
