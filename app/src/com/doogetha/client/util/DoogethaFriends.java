package com.doogetha.client.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import com.doogetha.client.android.Letsdoo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.letsdoo.server.vo.UserVo;
import de.letsdoo.server.vo.UsersVo;

public class DoogethaFriends
{
	private Letsdoo app = null;
	private List<UserVo> friends = null;
	private Gson gson = null;
	
	public static class FriendsListComparator implements Comparator<UserVo>
	{
		private Letsdoo app = null;
		public FriendsListComparator(Letsdoo app) {
			this.app = app;
		}
		public int compare(UserVo lhs, UserVo rhs) {
			String n1 = ContactsUtils.userDisplayName(app, lhs).toLowerCase(Locale.getDefault()); 
			String n2 = ContactsUtils.userDisplayName(app, rhs).toLowerCase(Locale.getDefault()); 
			return n1.compareToIgnoreCase(n2);
		}
	}
	
	public DoogethaFriends(Letsdoo app)
	{
		this.app = app;
		this.gson = new GsonBuilder().create();
		
		load();
	}
	
	protected void load()
	{
		String res = app.getPreferences().getString("doogethaFriends", null);
		UsersVo usersVo = res != null ? gson.fromJson(res, UsersVo.class) : new UsersVo();
    	Collection<UserVo> flist = usersVo.getUsers();
    	friends = new ArrayList<UserVo>();
    	if (flist != null) {
    		for (UserVo user : flist) friends.add(user);
    	}
	}
	
	public void save()
	{
		UsersVo users = new UsersVo();
		users.setUsers(friends);
		app.getPreferences().edit().putString("doogethaFriends", gson.toJson(users)).commit();
	}
	
	public List<UserVo> getFriends()
	{
		return friends;
	}

	public void setFriends(List<UserVo> friends)
	{
		this.friends = friends;
	}
	
	public void addFriend(UserVo friend)
	{
		if (friend.getEmail().equalsIgnoreCase(app.getEmail())) return; // don't add myself
		
		for (UserVo f : friends)
			if (f.getEmail().equalsIgnoreCase(friend.getEmail())) return; // don't add duplicates
		
        // manually add a new entry to doogetha friends list (in alphabetical order):
        boolean added = false;
        FriendsListComparator comparator = new FriendsListComparator(app);
        for (int i=0; i<friends.size(); i++) {
        	if (comparator.compare(friends.get(i), friend) > 0) {
        		friends.add(i, friend);
        		added = true;
        		break;
        	}
        }
        if (!added) friends.add(friend);
	}
	
	public void removeFriend(UserVo friend)
	{
		for (UserVo f : friends) {
			if (f.getEmail().equalsIgnoreCase(friend.getEmail())) {
				friends.remove(f);
				break;
			}
		}
	}
}
