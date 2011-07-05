package de.potpiejimmy.util;

/**
 * Utility class to access resources of a RESTful web service.
 * <p/>
 * A RESTful web service is usually accessed using a base-URL:
 * 
 * <code>http://myhost/service/resources/<entityName>s/
 * 
 * which will fetch a collection of available <entityName> entities,
 * whereas the URL extended by an entity's ID:
 * 
 * <code>http://myhost/service/resources/<entityName>s/<id>/
 * 
 * is used to access a single specific entity.
 * 
 * On the base URL, you perform HTTP GET to retrieve a collection of items
 * and HTTP POST to insert a new item.
 * 
 * On the extended URL using the entity's ID, you perform HTTP GET,
 * PUT or DELETE to read, update or delete a specific item, respectively.
 * <p/>
 * This class is designed to simplify the access for all of the above.
 * To do this, you need to specify the collection's class type and the
 * single entity's class type. The collection's class type has to be
 * designed to hold everything that's returned from the base URL, the
 * entity's class type should reflect the fields of a single entity.
 * 
 * @author Thorsten Liese
 */
public class RestResourceAccessor<CT, ET>
{
	private String baseUrl = null;
	private JsonWebRequest requester = null;
	
	private Class<CT> collectionType = null;
	private Class<ET> entityType = null;
	
	public RestResourceAccessor(String baseUrl, Class<CT> collectionType, Class<ET> entityType)
	{
		if (baseUrl == null) throw new IllegalArgumentException("baseUrl is null");
		
		this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
		this.requester = new JsonWebRequest();
		
		this.collectionType = collectionType;
		this.entityType = entityType;
	}
	
	/**
	 * Returns the WebRequest object used to handle all HTTP
	 * requests - can be used to set additional properties for
	 * the web requests.
	 * @return WebRequest
	 */
	public WebRequest getWebRequest()
	{
		return this.requester;
	}
	
	public CT getItems() throws Exception
	{
		return requester.getObject(baseUrl, collectionType);
	}
	
	public void insertItem(ET item) throws Exception
	{
		requester.postObject(baseUrl, item);
	}
	
	public ET insertItemWithResult(ET item) throws Exception
	{
		return requester.postObjectWithResult(baseUrl, item, entityType);
	}
	
	public ET getItem(String id) throws Exception
	{
		return requester.getObject(baseUrl+id, entityType);
	}
	
	public void updateItem(String id, ET item) throws Exception
	{
		requester.putObject(baseUrl + id, item);
	}
	
	public void deleteItem(String id) throws Exception
	{
		requester.deleteObject(baseUrl + id);
	}
	
	public ET getItem(long id) throws Exception
	{
		return getItem(""+id);
	}
	
	public void updateItem(long id, ET item) throws Exception
	{
		updateItem(""+id, item);
	}
	
	public void deleteItem(long id) throws Exception
	{
		deleteItem(""+id);
	}
}
