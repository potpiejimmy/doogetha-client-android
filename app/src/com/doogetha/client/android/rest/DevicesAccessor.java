package com.doogetha.client.android.rest;

import de.potpiejimmy.util.RestResourceAccessor;

public class DevicesAccessor extends RestResourceAccessor<String, String> {
	public DevicesAccessor(String url) {
		super(url, String.class, String.class);
	}
}
