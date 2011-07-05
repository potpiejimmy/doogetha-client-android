package de.potpiejimmy.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.util.Base64;
import android.util.Log;

public class WebRequest implements ResponseHandler<String>
{
	private final static String CHAR_ENCODING = "UTF-8";
	
    private List <NameValuePair> params;
    private List <NameValuePair> headers;
    private String contentType = null;
 
    private int responseCode;
    private String message;
 
    private String response;
    
    private HttpClient client = null;
 
    public String getResponse()
    {
        return response;
    }
 
    public String getErrorMessage()
    {
        return message;
    }
 
    public int getResponseCode()
    {
        return responseCode;
    }
 
    public WebRequest()
    {
        params = new ArrayList<NameValuePair>();
        headers = new ArrayList<NameValuePair>();

        HttpParams httpParams = new BasicHttpParams();
    	HttpConnectionParams.setSoTimeout(httpParams, 5000);
    	
    	SchemeRegistry schemeRegistry = new SchemeRegistry ();

    	schemeRegistry.register (new Scheme ("http",
    	    PlainSocketFactory.getSocketFactory (), 80));
    	try {
	    	schemeRegistry.register (new Scheme ("https",
	    	    new CustomSSLSocketFactory (), 443));
    	} catch (Exception ex) {
    		throw new RuntimeException("Could not register CustomSSLSocketFactory", ex);
    	}
    	
        client = new DefaultHttpClient(new ThreadSafeClientConnManager (
        		httpParams, schemeRegistry), httpParams);
    }
 
    public void addParam(String name, String value)
    {
        params.add(new BasicNameValuePair(name, value));
    }
 
    public void addHeader(String name, String value)
    {
        headers.add(new BasicNameValuePair(name, value));
    }
 
    public String getContentType()
    {
		return contentType;
	}

	public void setContentType(String contentType)
	{
		this.contentType = contentType;
	}

	public String get(String url) throws Exception
    {
        //add parameters
        StringBuffer combinedParams = new StringBuffer();
        if (!params.isEmpty())
        {
            combinedParams.append('?');
            for (NameValuePair p : params)
            {
                String paramString = p.getName() + "=" + URLEncoder.encode(p.getValue(), CHAR_ENCODING);
                if (combinedParams.length() > 1) combinedParams.append('&');
                combinedParams.append(paramString);
            }
        }
 
       HttpGet request = new HttpGet(url + combinedParams);
 
       //add headers
       for(NameValuePair h : headers) {
            request.addHeader(h.getName(), h.getValue());
            if (h.getName().equals("Authorization")) {
            	String creds = h.getValue().substring(6);
            	String credentials = new String(Base64.decode(creds, Base64.DEFAULT));
            	Log.v("XXXXXX MYWEBREQUEST", "LOGGING IN USING " + credentials);
            }
       }
       
       return executeRequest(request);
    }

	public String put(String url, String msg) throws Exception
	{
		return putImpl(new HttpPut(url), msg);
	}
	
	public String post(String url, String msg) throws Exception
	{
		return putImpl(new HttpPost(url), msg);
	}
	
	protected String putImpl(HttpEntityEnclosingRequestBase request, String msg) throws Exception
	{
        //add headers
        for(NameValuePair h : headers)
            request.addHeader(h.getName(), h.getValue());

        StringEntity entity = new StringEntity(msg, CHAR_ENCODING);
    	if (contentType != null)
    		entity.setContentType(contentType);
    	request.setEntity(entity);
 
        return executeRequest(request);
    }
 
	public String delete(String url) throws Exception
	{
		HttpDelete request = new HttpDelete(url);
 
        return executeRequest(request);
    }
 
    protected String executeRequest(HttpUriRequest request) throws Exception
    {
    	final int tries = 10;
    	for (int i=0; i<tries; i++) {
    		try {
    	    	return client.execute(request, this);
    		} catch (Exception e) {
    			if (i==tries-1) throw e;
    		}
    	}
    	return null;
    }
 
    public static String streamToString(InputStream is) throws IOException, UnsupportedEncodingException
    {
    	byte[] buf = new byte[1024];
    	int read = 0;
    	ByteArrayOutputStream data = new ByteArrayOutputStream();
    	
        try 
        {
	    	while ((read = is.read(buf)) > 0)
	    		data.write(buf, 0, read);
        }
        finally
        {
            try {is.close();} catch (IOException e) {}
        }
        String result = new String(data.toByteArray(), CHAR_ENCODING);
        return result;
    }

    public String handleResponse(HttpResponse response) throws IOException, UnsupportedEncodingException
    {
        responseCode = response.getStatusLine().getStatusCode();
        message = response.getStatusLine().getReasonPhrase();
 
        HttpEntity entity = response.getEntity();
 
        if (entity != null)
            return streamToString(entity.getContent());
        return null;
    }
}