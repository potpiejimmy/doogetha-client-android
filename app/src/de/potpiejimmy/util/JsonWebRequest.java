package de.potpiejimmy.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonWebRequest extends WebRequest
{
	private Gson gson = null;

	public JsonWebRequest()
	{
		this.gson = new GsonBuilder().create();
		
    	setContentType("application/json; charset=utf-8");
    	addHeader("Accept", "application/json");
	}

	public <T> T getObject(String url, Class<T> type) throws Exception
	{
		return gson.fromJson(super.get(url), type);
	}
	
	public void postObject(String url, Object object) throws Exception
	{
		super.post(url, gson.toJson(object));
	}

	public void putObject(String url, Object object) throws Exception
	{
		super.put(url, gson.toJson(object));
	}

	public void deleteObject(String url) throws Exception
	{
		super.delete(url);
	}
}
