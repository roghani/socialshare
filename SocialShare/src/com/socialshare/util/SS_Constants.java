package com.socialshare.util;

import android.content.Context;


/**
 * Constants and other Values used by this Application.
 * @author Midhu
 */
public class SS_Constants {
	public static final String API_BASE		 			= ""; //TODO Update API_BASE
	public static Context CurrentActiveContext			= null;
	
	public static final String 	API_YQL_BASE			= "http://query.yahooapis.com/v1/public/yql";
	
	public static final int SIZE_RESULT					= 20;
	
	public static final String 	FB_APP_ID 				= "463929193672057";
	public static final String 	FB_GRAPH_BASE 			= "https://graph.facebook.com/";
	public static final String[] 	FB_PERMISSIONS 		= new String[] 	{
																				"offline_access", 
																				"read_stream",
																				"publish_stream"
																			};
}
