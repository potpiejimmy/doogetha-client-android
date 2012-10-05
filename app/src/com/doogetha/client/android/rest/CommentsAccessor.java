package com.doogetha.client.android.rest;

import de.letsdoo.server.vo.EventCommentVo;
import de.letsdoo.server.vo.EventCommentsVo;
import de.potpiejimmy.util.RestResourceAccessor;

public class CommentsAccessor extends RestResourceAccessor<EventCommentsVo, EventCommentVo> {
	
	private String urlPrefix = null;
	
	public CommentsAccessor(String url) {
		super(url, EventCommentsVo.class, EventCommentVo.class);
		this.urlPrefix = getBaseUrl();
	}
	
	public void setCurrentBaseId(int eventId) {
		setBaseUrl(urlPrefix + eventId);
	}
}
