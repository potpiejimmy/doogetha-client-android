package com.doogetha.client.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.doogetha.client.android.Letsdoo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.letsdoo.server.vo.UserVo;
import de.letsdoo.server.vo.UsersVo;

public class DoogethaFriends
{
	private Letsdoo app = null;
	
	private List<UserVo> friends = null;
	private Map<String,UserVo> lookupMap = null;
	
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
    	int capacity = (flist != null) ? flist.size() : 10;
    	friends = new ArrayList<UserVo>(capacity);
    	lookupMap = new HashMap<String,UserVo>(capacity);
    	if (flist != null) {
    		for (UserVo user : flist) {
    			friends.add(user);
    			lookupMap.put(user.getEmail(), user);
    		}
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
		
		lookupMap.clear();
		for (UserVo friend : friends)
			lookupMap.put(friend.getEmail(), friend);
	}
	
	public void addFriend(UserVo friend)
	{
		if (friend.getEmail().equalsIgnoreCase(app.getEmail())) return; // don't add myself
		
		if (lookupMap.containsKey(friend.getEmail())) return; // don't add duplicates
		
		// new friend: fetch display name from address book
		ContactsUtils.fillUserInfo(app.getContentResolver(), friend);
		
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
        lookupMap.put(friend.getEmail(), friend);
	}
	
	public void removeFriend(UserVo friend)
	{
		for (UserVo f : friends) {
			if (f.getEmail().equals(friend.getEmail())) {
				friends.remove(f);
				lookupMap.remove(f.getEmail());
				break;
			}
		}
	}
	
	public UserVo resolveUserInfo(UserVo user)
	{
		UserVo resolved = lookupMap.get(user.getEmail());
		if (resolved != null) {
			// adapt the user name from friend list:
			user.setFirstname(resolved.getFirstname());
			user.setLastname(resolved.getLastname());
		}
		return user;
	}
}
