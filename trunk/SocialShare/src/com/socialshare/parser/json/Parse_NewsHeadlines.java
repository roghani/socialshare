package com.socialshare.parser.json;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.socialshare.datatypes.DT_NewsHeadlines;
import com.socialshare.util.SS_Log;

/**
 * @author Midhu
 */
public class Parse_NewsHeadlines {

	/** Parse a json object to an {@link ArrayList} of {@link DT_NewsHeadlines} **/
	public static ArrayList<DT_NewsHeadlines> parse(JSONObject jsonObject) {
		ArrayList<DT_NewsHeadlines> newsList = new ArrayList<DT_NewsHeadlines>();
		try {
			JSONObject query = jsonObject.optJSONObject("query");
			JSONObject results = query.optJSONObject("results");

			JSONArray newsArray = results.optJSONArray("item");
			if (newsArray != null) {
				for (int i = 0; i < newsArray.length(); i++) {
					JSONObject news = newsArray.optJSONObject(i);
					newsList.add(getNewsItem(news));
				}
			}
		} catch (Exception e) {
			SS_Log.printStackTrace(e);
		}
		return newsList;
	}

	private static DT_NewsHeadlines getNewsItem(JSONObject resultObject) {
		DT_NewsHeadlines newsInfo = new DT_NewsHeadlines();
		newsInfo.title = resultObject.optString("title");
		newsInfo.description = resultObject.optString("description");
		newsInfo.pubDate = resultObject.optString("pubDate");
		newsInfo.link = resultObject.optString("link");
		return newsInfo;
	}
}
