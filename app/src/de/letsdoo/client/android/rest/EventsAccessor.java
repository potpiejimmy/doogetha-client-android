package de.letsdoo.client.android.rest;

import de.letsdoo.client.entity.Event;
import de.letsdoo.client.entity.Events;
import de.potpiejimmy.util.RestResourceAccessor;

public class EventsAccessor extends RestResourceAccessor<Events, Event> {
	public EventsAccessor(String url) {
		super(url, Events.class, Event.class);
	}
}
