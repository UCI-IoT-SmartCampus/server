package com.smart.uci;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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

@Path("/sample")
public class SampleREST {
	
	private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	@GET
	@Path("/insert/name={name}")
	@Produces(MediaType.TEXT_HTML)
	public String hello(@PathParam("name")final String name){
		//putFunction(12,123);
		//putFunction(23,234);
		//putFunction(34,345);
		
		//if("insert".equals(name)){
		//	String[][] data = putShadow();
		//	String result="";
		
			//for(int j=0;j<data[1].length;j++){
			//	if(data[1][j]!=null) {
			//		putFunction("gaga" ,(int) Float.parseFloat((data[2][j])), (int) Float.parseFloat(data[1][j]));
			//	}
			//}
		readShadowCsv(name);
		
		return "SUCCESS";
		
		
		
		//return "FALSE";
		//return "J";
	}
	
	//@GET
	//@Path("/getdata/name={name}")
	//@Produces(MediaType.TEXT_HTML)
	//public String getData(@PathParam("name")final String name){
		
		//retuun 
		
		
	//}
	
	
	private void saveShadow(Integer[][] data,String tablename){
		
		HashMap<Integer,Integer> hmap = new HashMap<Integer,Integer>();
		for(int i=0; i< data[1].length; i++) {
			if(data[1][i]!=null){
				hmap.put(data[1][i], data[2][i]);
			}
			
		}
		System.out.println("test1");
		/*
		
		Map<Integer, Integer> map = new TreeMap<Integer, Integer>(hmap); 
		Set set2 = map.entrySet();
		
		Iterator iterator2 = set2.iterator();
        while(iterator2.hasNext()) {
        	System.out.println("test2");
             Map.Entry me2 = (Map.Entry)iterator2.next();
             int key = (int) me2.getKey();
             int value = (int) me2.getValue();
             putFunction( tablename,key, value);
             
        }
        */
		TreeMap<Integer,Integer> tm = new TreeMap<Integer,Integer>(hmap);
		  
		Iterator<Integer> iteratorKey = tm.keySet( ).iterator( );
		int id =1;
		while( iteratorKey.hasNext()) {
			 
            int key = iteratorKey.next();
            int value = tm.get( key );
            System.out.println(key+" : "+value );
            
            putFunction( tablename,value, key,id++);
        }
		
	}
	
	private void readShadowCsv(String tablename){
		Integer[][] data = new Integer[3][360];
		try {///Users/shinjiung/djangotest/server/test.csv
			String path = SampleREST.class.getResource("").getPath();
			System.out.println(path);
			File csv = new File(path + "test.csv");
			
			BufferedReader br = new BufferedReader(new FileReader(csv));
			String line = "";
			
			int i=0;
			while ((line = br.readLine()) != null) {
				String[] token = line.split(",");
				for(int j=0 ; j<token.length; j++){
					data[i][j] = (int) Float.parseFloat(token[j]);
				}
				i++;	
			}
			br.close();
			
			saveShadow(data,tablename);
			
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void putFunction(String table,int degree, int azimuth,int id){
		//DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity ent = new Entity(table,id);
		ent.setProperty("Degree", degree);
		ent.setProperty("Azimuth", azimuth);
		datastore.put(ent);
		
	}
	/*
	@GET
	@Path("/getdata/placename={placename}")
	@Produces(MediaType.APPLICATION_JSON)
	private Response getFunction(@PathParam("placename")final String placename) throws JSONException, UnsupportedEncodingException{
		//variable for save shadow data 
		int azimuth=0;
		int degree=0;
		
		//connect with database
		//DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
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
		ArrayList<Map<Integer,Integer>> selectlist = new ArrayList<Map<Integer,Integer>>();
		
		Map<Integer,Integer> dbmap = null;
		
		//read database after excute query
		for (Entity result : pq.asIterable()) {
			//log.severe((String) (result.getProperty("Azimuth")));
			azimuth =  (int) result.getProperty("Azimuth");
			degree = (int) result.getProperty("Degree");
			
			dbmap = new HashMap<Integer,Integer>();
			dbmap.put(azimuth, degree);
			selectlist.add(dbmap);

			System.out.println(azimuth + " " + degree );
		}
		
		JSONArray jsonList = new JSONArray();
		JSONObject jsontmp = null;
		for(Map<Integer,Integer> selectone : selectlist){
			Set<Integer> key = selectone.keySet();
			for(Iterator<Integer> iterator = key.iterator();iterator.hasNext();){
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

	}*/
	
}
