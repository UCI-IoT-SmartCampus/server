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




@Path("/getdata")
public class Getdata {
	@GET
	@Path("/placename={placename}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFunction(@PathParam("placename")final String placename) throws JSONException, UnsupportedEncodingException{
		//variable for save shadow data 
		
		
		//connect with database
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		//make query
		int min =0;
		Filter findByDegree =
				new FilterPredicate("Azimuth",
						FilterOperator.GREATER_THAN_OR_EQUAL,
						min);

		Query q = new Query(placename).setFilter(findByDegree);
		
		
		
		//query excute
		PreparedQuery pq = datastore.prepare(q);
		
		
		//prepare for make json object
		JSONObject jsonroot = new JSONObject();
		ArrayList<Map<String,String>> selectlist = new ArrayList<Map<String,String>>();
		
		Map<String,String> dbmap = null;
		
		//read database after excute query
		for (Entity result : pq.asIterable()) {
			//log.severe((String) (result.getProperty("Azimuth")));
			String azimuth =  result.getProperty("Azimuth").toString();
			String degree = result.getProperty("Degree").toString();
			System.out.print(azimuth+" ");
			System.out.println(degree);
			
			dbmap = new HashMap<String,String>();
			dbmap.put(azimuth, degree);
			selectlist.add(dbmap);

			System.out.println(azimuth + " " + degree );
		}
		
		JSONArray jsonList = new JSONArray();
		JSONObject jsontmp = null;
		for(Map<String, String> selectone : selectlist){
			Set<String> key = selectone.keySet();
			for(Iterator<String> iterator = key.iterator();iterator.hasNext();){
				String tempkey = iterator.next().toString();
				String tempvalue = selectone.get(tempkey).toString();
				jsontmp=new JSONObject();
				try {
					jsontmp.put(tempkey, tempvalue);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			jsonList.put(jsontmp);
		}
		jsonroot.put("data", jsonList);
		String sending = new String(jsonroot.toString().getBytes(), "EUC-KR");
		System.out.println(jsonroot.toString());
		return Response.status(200).entity(sending).build();

	}
}
