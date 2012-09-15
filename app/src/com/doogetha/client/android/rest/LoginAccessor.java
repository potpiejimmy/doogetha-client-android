package com.doogetha.client.android.rest;

import de.potpiejimmy.util.RestResourceAccessor;

public class LoginAccessor extends RestResourceAccessor<String, String> {
	public LoginAccessor(String url) {
		super(url, String.class, String.class);
	}
}
