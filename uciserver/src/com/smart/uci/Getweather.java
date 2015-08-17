package com.smart.uci;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@Path("/getweather")
public class Getweather {
	@GET
	@Path("/requestweather={name}")
	@Produces(MediaType.TEXT_HTML)
	public String SendRequest(@PathParam("name")final String name) throws IOException, ParseException {
		
	BufferedReader rd;
	OutputStreamWriter wr;
	String result="";
	String weather="";

	URL url = new URL("http://api.openweathermap.org/data/2.5/weather?zip=92617,us");
	URLConnection conn = url.openConnection();
	conn.setDoOutput(true);
	wr = new OutputStreamWriter(conn.getOutputStream());
	wr.flush();
   
	// Get the response
	rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	String line;
	result = rd.readLine();
	//return result;
	    JSONParser jsonParser = new JSONParser();
	    //JSON데이터를 넣어 JSON Object 로 만들어 준다.
	    JSONObject jsonObject = (JSONObject) jsonParser.parse(result);
	    //books의 배열을 추출
	    JSONObject newobj = (JSONObject) jsonObject.get("clouds");
 
 
 
	    //for(int i=0; i<bookInfoArray.size(); i++){
 
	        return weather = (String) newobj.get("all").toString();
 
	    //}
	    
    	/*
    	//Object obj = JSONValue.parse(result);
		
		JSONObject jsonobj = new JSONObject(result);
		JSONArray arr = (JSONArray) jsonobj.get("clouds");
		for(int i=0;i<arr.length();i++) {
			JSONObject insideObject = null;
			insideObject = arr.getJSONObject(i);
			
			weather = (String) insideObject.get("all"); 
		}
		*/
		
		
    	
    		    	
	//}
	//catch (Exception e) {
     //   	System.out.println(e.toString());
      //  	return result;
    //	}

	//}
	//
	
	
	
	
	}
}
