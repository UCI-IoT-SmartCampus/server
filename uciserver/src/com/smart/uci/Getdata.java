package com.smart.uci;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;










@Path("/getdata")
public class Getdata {
	
	
	@GET
	@Path("/placename={placename}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFunction(@PathParam("placename")final String placename) throws JSONException, UnsupportedEncodingException{
		//variable for save shadow data 
		
		
		//connect with database
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		//Arr for compare degree
		Integer[] shadow = new Integer[359];
		Integer[] sunpath = new Integer[359];
		
		//make query
		int min =0;
		Filter findByDegree =
				new FilterPredicate("Azimuth",
						FilterOperator.GREATER_THAN,
						min);

		Query q = new Query(placename).setFilter(findByDegree);
		
		
		
		//query excute
		PreparedQuery pq = datastore.prepare(q);
		
		
		//prepare for make json object
		JSONObject jsonroot = new JSONObject();
		JSONArray jsonList = new JSONArray();
		jsonList = makegraphdata(pq,shadow);
		jsonroot.put("shadow", jsonList);
	
		
		q = new Query("2015-01-04").setFilter(findByDegree);
		
		
		
		//query excute
		pq = datastore.prepare(q);
		
		
		//prepare for make json object
		//jsonroot = new JSONObject();
		jsonList = makegraphdata(pq,sunpath);
		jsonroot.put("sunpath", jsonList);
		
		jsonList = calculateTime(shadow, sunpath);
		jsonroot.put("time", jsonList);
		for(int j=0;j<359;j++)
			System.out.println(sunpath[j] +" , " + shadow[j]);

		
		
		
		
		
		
		//jsonroot.put("shadow", jsonList);
		//jsonroot.put("sunpath", jsonList2);
		String sending = new String(jsonroot.toString().getBytes(), "EUC-KR");
		System.out.println(jsonroot.toString());
		
		return Response.status(200).entity(sending).build();

	}
	
		
	private JSONArray makegraphdata(PreparedQuery pq, Integer[] arr){
		ArrayList<Map<String,String>> selectlist = new ArrayList<Map<String,String>>();
		
		Map<String,String> dbmap = null;
		
		//read database after excute query
		
		for (Entity result : pq.asIterable()) {
			//log.severe((String) (result.getProperty("Azimuth")));
			String azimuth =  result.getProperty("Azimuth").toString();
			String degree = result.getProperty("Degree").toString();
			
			
			dbmap = new HashMap<String,String>();
			dbmap.put(azimuth, degree);
			selectlist.add(dbmap);

			//System.out.println(azimuth + " " + degree );
		}
		int i=0;
		JSONArray jsonList = new JSONArray();
		JSONObject jsontmp = null;
		for(Map<String, String> selectone : selectlist){
			Set<String> key = selectone.keySet();
			for(Iterator<String> iterator = key.iterator();iterator.hasNext();){
				String tempkey = iterator.next().toString();
				String tempvalue = selectone.get(tempkey).toString();
				arr[i++] =  Integer.parseInt(tempvalue);
				jsontmp=new JSONObject();
				try {
					jsontmp.put("azimuth", tempkey);
					
					jsontmp.put("degree", tempvalue);
					jsonList.put(jsontmp);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			//jsonList2.put(jsonList);
		}
		return jsonList;
	}
	
	private JSONArray calculateTime(Integer[] shadow, Integer[] sunpath) throws JSONException {
		JSONArray jsonList = new JSONArray();
		int[] upper = new int[30];
		int[] under = new int[30];
		
		int upIndex =0, underIndex = 0;
		for(int i=1; i<359 ; i++) {
			if(sunpath[i]>shadow[i] && sunpath[i-1]<shadow[i-1])
				upper[upIndex++] = i;
			if(sunpath[i]<shadow[i] && sunpath[i-1]>shadow[i-1])
				under[underIndex++] = i;
		}
		
		for(int i=0; i< 30 ; i++)
			System.out.println(upper[i] + " , "+under[i]);
		
		int index = 0;
		String fullsuntime = "";
		String shadowtime = "";
		shadowtime += "0~"+upper[0] + ", " ;
		
		while (upper[index] > 0 && upper[index]!=0 ){
			
			fullsuntime = fullsuntime + upper[index] + "~" + under[index]+ ", ";
		 
			shadowtime = shadowtime + under[index] + "~" + upper[++index] + ", ";
			
			
		}
		shadowtime += under[underIndex] + "~359";
		
		JSONObject jsontmp = new JSONObject();
		jsontmp.put("fullsun", fullsuntime);
		jsontmp.put("shadow", shadowtime);
		jsonList.put(jsontmp);
		
		System.out.println(fullsuntime);
		System.out.println(shadowtime);
		
		return jsonList;
	}
}

