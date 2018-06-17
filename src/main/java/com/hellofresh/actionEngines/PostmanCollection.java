package com.hellofresh.actionEngines;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.hellofresh.utilities.ApiPOJO;

public class PostmanCollection {
	
	public static String post_body;

	
	public static List<ApiPOJO> apiUrlHeaders(String collection_path) throws Exception{	
		
		BufferedReader br = new BufferedReader(new FileReader(collection_path));
		try{		
		String collection_json ="";
		StringBuffer sb = new StringBuffer();
		while((collection_json=br.readLine())!=null){
			sb.append(collection_json);			
		}
		
		JSONObject js = new JSONObject(sb.toString());			
		List<ApiPOJO> apiList = new ArrayList<ApiPOJO>();
	
		if(js.has("item")&&js.getJSONArray("item").length()!=0){		
			for(int i=0;i<js.getJSONArray("item").length();i++){				
				ApiPOJO call = new ApiPOJO();
				call.setName(js.getJSONArray("item").getJSONObject(i).getString("name"));
				JSONObject requestObject = js.getJSONArray("item").getJSONObject(i).getJSONObject("request");
				String method = requestObject.getString("method");
				String jsonUrl=requestObject.getString("url");
				call.setUrl(requestObject.getString("url"));			
				call.setMethod(method);
				
				
				if(method.equalsIgnoreCase("POST")){
					if(requestObject.getJSONObject("body").has("raw")){
					post_body = requestObject.getJSONObject("body").getString("raw");
					}
					call.setBody(post_body);
				}				
				apiList.add(call);
			}			
			}
			return apiList;	
		}		
		finally{
			br.close();
		}						
	}
}
