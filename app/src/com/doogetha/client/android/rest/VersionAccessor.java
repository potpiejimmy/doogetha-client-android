package com.doogetha.client.android.rest;

import de.potpiejimmy.util.RestResourceAccessor;

public class VersionAccessor extends RestResourceAccessor<String, String> {
	public VersionAccessor(String url) {
		super(url, String.class, String.class);
	}
}
