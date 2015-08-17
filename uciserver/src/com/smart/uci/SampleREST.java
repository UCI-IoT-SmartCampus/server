package com.smart.uci;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

@Path("/sample")
public class SampleREST {
	
	private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	@GET
	@Path("/insert/name={name}")
	@Produces(MediaType.TEXT_HTML)
	public String hello(@PathParam("name")final String name){

		readShadowCsv(name);
		
		return "SUCCESS";
	}
	
	
	@GET
	@Path("/insert/pows={pows}")
	@Produces(MediaType.TEXT_HTML)
	public String putEnergy(@PathParam("pows")final String pows){
		
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat timeformat = new SimpleDateFormat("yyyyMMddHHmm");
		putEnergy(dateformat.format(calendar.getTime()), timeformat.format(calendar.getTime()),pows);
		
		return "SUCCESS";
	}
	
	
	
	private void saveShadow(Integer[][] data,String tablename){
		int reallength=0;
		HashMap<Integer,Integer> hmap = new HashMap<Integer,Integer>();
		HashMap<Integer,Integer> temp = new HashMap<Integer,Integer>();
		for(int i=0; i< data[1].length; i++) {
			if(data[1][i]!=null){
				hmap.put(data[1][i], data[2][i]);
			}
			else
				reallength = i;
			
		}
		
		Map<Integer,Integer> tm = new TreeMap<Integer,Integer>(hmap);
		  
		Iterator<Integer> iteratorKey = tm.keySet( ).iterator( );
		Integer[][] database = new Integer[2][tm.size()];
		int size=0;
		while( iteratorKey.hasNext()) {
			 
            int key = iteratorKey.next();
            int value = tm.get( key );
            database[0][size]=key;
            database[1][size++]=value;
            
            //System.out.println(key+" : "+value );
            
            //putFunction( tablename,value, key,id++);
        }
		
		temp = fillData(database,size);
		
		
		tm = new TreeMap<Integer,Integer>(temp);
		  
		iteratorKey = tm.keySet( ).iterator( );
		
		int id =1;
		while( iteratorKey.hasNext()) {
			
            
			int key = iteratorKey.next();
            int value = tm.get( key );
            
            
            System.out.println(key+" : "+value );
            
            putFunction( tablename,value, key,id++);
        }		
		
	}
	
	private HashMap<Integer,Integer> fillData(Integer[][] database, int size){
		HashMap<Integer,Integer> temp = new HashMap<Integer,Integer>();
		int count=1;
		while(count <database[0][0]){
			temp.put(count++,0);
		}
		for(int i=0;i<size-1;i++)
		{
			if(database[0][i+1] - database[0][i] != 1 ){
				if(database[0][i+1] - database[0][i] < 10){
					temp.put(database[0][i], database[1][i]);
					
					for(int j=1;j<database[0][i+1]-database[0][i];j++){
						if(database[1][i+1] - database[1][i]!=0) {
							temp.put(database[0][i]+j,database[1][i]+j*((database[1][i+1]-database[1][i])/(database[0][i+1]-database[0][i])));
						}
						else {
							temp.put(database[0][i]+j, database[1][i]);
						}
					}
				}
				else {
					for(int j=0;j<database[0][i+1]-database[0][i];j++){
						temp.put(database[0][i]+j,0);
					}
				}
			}
			else
				temp.put(database[0][i], database[1][i]);
		}
		
		for(int i=temp.size() ; i<360 ; i++)
			temp.put(i, 0);
		
		return temp;
		
	}
	
	private void readShadowCsv(String tablename){
		Integer[][] data = new Integer[3][1500];
		try {///Users/shinjiung/djangotest/server/test.csv
			
				
			
			String path = SampleREST.class.getResource("").getPath();
			System.out.println(path);
			File csv = new File(path + "javacity4.csv");
			
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
			
			saveShadow(data,"javacity4");
			
			
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
	private void putEnergy(String table,String time, String power){
		//DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity ent = new Entity(table,time);
		ent.setProperty("Power", power);
		//ent.setProperty("Azimuth", azimuth);
		datastore.put(ent);	
	}	
}
