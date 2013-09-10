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
    	setHeader("Accept", "application/json");
	}

	public <T> T getObject(String url, Class<T> type) throws Exception
	{
		return convertResult(super.get(url), type);
	}
	
	public void postObject(String url, Object object) throws Exception
	{
		super.post(url, gson.toJson(object));
	}

	public <T> T postObjectWithResult(String url, Object object, Class<T> resultType) throws Exception
	{
		return convertResult(super.post(url, gson.toJson(object)), resultType);
	}

	public void putObject(String url, Object object) throws Exception
	{
		super.put(url, gson.toJson(object));
	}

	public <T> T putObjectWithResult(String url, Object object, Class<T> resultType) throws Exception
	{
		return convertResult(super.put(url, gson.toJson(object)), resultType);
	}

	public void deleteObject(String url) throws Exception
	{
		super.delete(url);
	}
	
	public <T> T convertResult(String result, Class<T> type) {
		try {
			return gson.fromJson(result, type);
		} catch (Exception ex) {
			throw new RuntimeException("Could not parse: " + result, ex);
		}
	}
}
