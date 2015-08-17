package com.smart.uci;

import java.io.UnsupportedEncodingException;
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

@Path("/getinfo")
public class GetInfo {
	@GET
	@Path("/placename={placename}/azimuth={azimuth}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getInfo(@PathParam("placename")final String placename, @PathParam("azimuth")final String azimuth) throws JSONException, UnsupportedEncodingException{
		//variable for save shadow data 
		
		
		//connect with database
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		//make query
		int azi = Integer.parseInt(azimuth);
		Filter findByDegree =
				
				new FilterPredicate("Azimuth",
						FilterOperator.EQUAL,
						azi);

		int[] shadowInfo = new int[5];
		for(int i=1;i<6;i++) {
			Query q = new Query(placename+i).setFilter(findByDegree);
			PreparedQuery pq = datastore.prepare(q);
			Entity result = pq.asSingleEntity();
			shadowInfo[i-1] = Integer.parseInt(result.getProperty("Degree").toString());
			System.out.println(shadowInfo[i-1]);
		}
		
		int standard = 0;
		Query q = new Query("2015-01-04").setFilter(findByDegree);
		PreparedQuery pq = datastore.prepare(q);
		Entity result = pq.asSingleEntity();
		standard = Integer.parseInt(result.getProperty("Degree").toString());
		System.out.println(standard);
		
		int effect = 0;
		for(int i=0; i< 5 ; i++) { 
			if(shadowInfo[i] > standard)
				 effect++;
		}
		
		Integer rateOfShadow = 0;
		Integer rateOfSun = 0;
		if(effect != 0) {
			rateOfShadow = ((effect*100)/5 + (effect*100)%5);
			rateOfSun = 100 - rateOfShadow;
		}
		else {
			rateOfSun = 100;
		}
		
		JSONObject jsonroot = new JSONObject();
		JSONArray jsonList = new JSONArray();
		JSONObject jsontmp = new JSONObject();
		jsontmp.put("effected_by_shadow", rateOfShadow.toString()+"%" );
		jsontmp.put("effected_by_sun", rateOfSun.toString()+"%");
		jsonList.put(jsontmp);
		jsonroot.put("effect", jsonList);
		String sending = new String(jsonroot.toString().getBytes(), "EUC-KR");
		System.out.println(jsonroot.toString());
		return Response.status(200).entity(sending).build();
	}
	
}
