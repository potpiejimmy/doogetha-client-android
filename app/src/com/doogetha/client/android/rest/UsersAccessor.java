package de.letsdoo.client.android.rest;

import de.letsdoo.server.vo.UserVo;
import de.letsdoo.server.vo.UsersVo;
import de.potpiejimmy.util.RestResourceAccessor;

public class UsersAccessor extends RestResourceAccessor<UsersVo, UserVo> {
	public UsersAccessor(String url) {
		super(url, UsersVo.class, UserVo.class);
	}
}
