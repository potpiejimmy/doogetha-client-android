package de.letsdoo.client.android.rest;

import de.potpiejimmy.util.RestResourceAccessor;

public class RegisterAccessor extends RestResourceAccessor<String, String> {
	public RegisterAccessor(String url) {
		super(url, String.class, String.class);
	}
}
