package com.smart.uci;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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

@Path("/savesunpath")
public class SaveSunpath {
	
	private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	@GET
	@Path("/name={name}")
	@Produces(MediaType.TEXT_HTML)
	public String SaveSunpath(@PathParam("name")final String name){
		
		readSunpath();
		
		return "SUCCESS";
		
	}
	
	private void readSunpath(){
		Integer[] data = new Integer[2000];
		String date = "";
		HashMap<Integer,Integer> hmap = new HashMap<Integer,Integer>();
		try {///Users/shinjiung/djangotest/server/test.csv
			String path = SampleREST.class.getResource("").getPath();
			System.out.println(path);
			File file = new File(path + "sunpathtest.txt");
			
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = "";
			
			
			
			while ((line = br.readLine()) != null) {
				
				int i=0;
				int id=1;
				int start=0;
				int end=0;
				String[] token = line.split(";");
				date = token[0];
				for(int j=1 ; j<token.length; j++){
					if(!token[j].equals("--"))
					{
						
						if(token[j-1].equals("--") && !token[j].equals("--"))
							start = (int) Float.parseFloat(token[j+1]);
							//System.out.println(start);
						if(token[j+1].equals("--") && !token[j].equals("--"))
							end = (int) Float.parseFloat(token[j]);
							//System.out.println(end);
							 
							 
						data[i++] = (int) Float.parseFloat(token[j]);
					}
				}
				
				int size =0;
				
				Integer[][] database = new Integer[2][300];
				int dataIndex=0;
				//for(int x=1;x<start;x++)
					//putFunction(date,0,x,id++);
				for(int x=1;x<data.length-2;){
					if(x%2==1){
						if(data[x] == data[x+2])
						{	
							x+=2;
						}
						else{
							database[0][dataIndex]=data[x];
							database[1][dataIndex++]=data[x-1];
							//putFunction(date,data[x-1],data[x],id++);
							System.out.println(data[x] +" : "+data[x-1]);
							x++;
							size++;
						}
						
					}
					else{
						x++;
					}
				}
				System.out.println(start);
				System.out.println(end);
				System.out.println(database[0].length);//
				System.out.println(size);
				
				
				hmap = fillData(database,size);
				Map<Integer,Integer> tm = new TreeMap<Integer,Integer>(hmap);
				  
				Iterator<Integer> iteratorKey = tm.keySet( ).iterator( );
				while( iteratorKey.hasNext()) {
					 
		            int key = iteratorKey.next();
		            int value = tm.get( key );
		            
		            putFunction(date,value, key,id++);
		        }
				
				//for(int x=end+1;x<=360;x++)
					//putFunction(date,0,x,id++);
				
				
				
					
			}
			
			for(int j=0;j<data.length;j++)
			{
				if(data[j]==null);
				//System.out.println(data[j]);
				

			}
			br.close();
			
			//saveShadow(data,tablename);
			
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private HashMap<Integer,Integer> fillData(Integer[][] database, int size){
		HashMap<Integer,Integer> temp = new HashMap<Integer,Integer>();
		int count=1;
		while(count <database[0][0]){
			temp.put(count++,0);
		}
		for(int i=0;i<size-2;i++)
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
	private void putFunction(String table,int degree, int azimuth,int id){
		//DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity ent = new Entity(table,id);
		ent.setProperty("Degree", degree);
		ent.setProperty("Azimuth", azimuth);
		datastore.put(ent);
		
	}
}
