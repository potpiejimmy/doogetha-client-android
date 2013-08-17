package com.doogetha.client.android.rest;

import de.letsdoo.server.vo.VersionVo;
import de.potpiejimmy.util.RestResourceAccessor;

public class VersionAccessor extends RestResourceAccessor<VersionVo, VersionVo> {
	public VersionAccessor(String url) {
		super(url, VersionVo.class, VersionVo.class);
	}
}
