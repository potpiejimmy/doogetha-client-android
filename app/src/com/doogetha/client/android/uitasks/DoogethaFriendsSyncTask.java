package com.doogetha.client.android.uitasks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.widget.Toast;

import com.doogetha.client.android.Letsdoo;
import com.doogetha.client.util.ContactsUtils;
import com.doogetha.client.util.DoogethaFriends;
import com.doogetha.client.util.Utils;

import de.letsdoo.server.vo.UserVo;
import de.letsdoo.server.vo.UsersVo;
import de.potpiejimmy.util.AsyncUITask;
import de.potpiejimmy.util.JsonWebRequest;

public class DoogethaFriendsSyncTask  extends AsyncUITask<UsersVo> {
	private Activity activity = null;
	private DoogethaFriendsSyncTaskCallback callback = null;
	
	public DoogethaFriendsSyncTask(Activity activity, DoogethaFriendsSyncTaskCallback callback) 
	{
		super(activity);
		this.activity = activity;
		this.callback = callback;
	}
	
	public UsersVo doTask() throws Throwable
	{
		Letsdoo app = Utils.getApp(activity);
		
		// maps email hash strings to email addresses:
		Map<String,String> userMap = new HashMap<String,String>();

		// get the current list:
		List<UserVo> currentUsers = app.getDoogethaFriends().getFriends();
		// put all current users in the hash list:
		if (currentUsers != null)
			for (UserVo user : currentUsers) userMap.put(Utils.md5Base64(user.getEmail()), user.getEmail());
		
		// also put all mail addresses from the address book in the map:
		try {
			List<String> addressBookMails = ContactsUtils.fetchEmails(activity, null);
			for (String mail : addressBookMails) 
				if (!mail.equalsIgnoreCase(app.getEmail()))
					userMap.put(Utils.md5Base64(mail), mail);
		} catch (Exception ex) {
			// for now, just ignore
		}
		
		StringBuilder hashes = new StringBuilder();
		for (String hash : userMap.keySet()) {
			if (hashes.length() > 0) hashes.append(',');
			hashes.append(hash);
		}
		
		// now send comma separated list of hashes to server:
		JsonWebRequest req = (JsonWebRequest)app.getUsersAccessor().getWebRequest();
		
		Collection<UserVo> syncedUsers = req.convertResult(req.post(app.getUsersAccessor().getBaseUrl(), hashes.toString()), UsersVo.class).getUsers();
		
		// fetch display names from address book:
		for (UserVo friend : syncedUsers)
			ContactsUtils.fillUserInfo(activity.getContentResolver(), friend);
		
		UserVo[] sortedFriends = syncedUsers.toArray(new UserVo[syncedUsers.size()]);
		Arrays.sort(sortedFriends, new DoogethaFriends.FriendsListComparator(app));
		
		currentUsers = new ArrayList<UserVo>(Arrays.asList(sortedFriends)); // must be modifiable
		UsersVo newUsers = new UsersVo();
		newUsers.setUsers(currentUsers);
		
		return newUsers;
	}
	
	public void doneOk(UsersVo users)
	{
		// store back:
		DoogethaFriends friends = Utils.getApp(activity).getDoogethaFriends(); 
		friends.setFriends((List<UserVo>)users.getUsers());
		friends.save();
		
		callback.friendListSynced();
	}
	
	public void doneFail(Throwable throwable) 
	{
		Toast.makeText(activity, "Synchronization failed.", Toast.LENGTH_SHORT).show();
	}
}
