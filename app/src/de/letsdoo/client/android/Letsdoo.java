package de.letsdoo.client.android;

import android.app.Application;
import de.letsdoo.client.entity.Event;
import de.letsdoo.client.entity.Events;
import de.potpiejimmy.util.RestResourceAccessor;

public class Letsdoo extends Application {
	
	//public final static String URL = "http://www.potpiejimmy.de/letsdoo/res/events/";
	public final static String URL = "http://deffm949:8089/letsdoo/res/events/";
	
	private RestResourceAccessor<Events, Event> req = null;
	
	@Override
	public void onCreate() {
        req = new RestResourceAccessor<Events, Event>(URL, Events.class, Event.class);
    	req.getWebRequest().addParam("max", "255");
	}
	
	public RestResourceAccessor<Events, Event> getRestAccessor() {
		return req;
	}
}
