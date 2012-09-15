package com.doogetha.client.android.rest;

import de.letsdoo.server.vo.EventVo;
import de.letsdoo.server.vo.EventsVo;
import de.potpiejimmy.util.RestResourceAccessor;

public class EventsAccessor extends RestResourceAccessor<EventsVo, EventVo> {
	public EventsAccessor(String url) {
		super(url, EventsVo.class, EventVo.class);
	}
}
