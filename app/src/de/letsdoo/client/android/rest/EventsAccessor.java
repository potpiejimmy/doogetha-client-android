package de.letsdoo.client.android.rest;

import de.letsdoo.client.entity.EventVo;
import de.letsdoo.client.entity.EventsVo;
import de.potpiejimmy.util.RestResourceAccessor;

public class EventsAccessor extends RestResourceAccessor<EventsVo, EventVo> {
	public EventsAccessor(String url) {
		super(url, EventsVo.class, EventVo.class);
	}
}
