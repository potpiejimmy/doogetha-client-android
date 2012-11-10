package com.doogetha.client.util;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.doogetha.client.android.Letsdoo;

import de.letsdoo.server.vo.EventVo;
import de.letsdoo.server.vo.UserVo;

public class ContactsUtils {
	
	/**
	 * Fetches all email addresses from the address book for the given contact id
	 */
	public static List<String> fetchEmails(Activity activity, String contactId) {
    	Cursor emailCur = activity.getContentResolver().query( 
    			ContactsContract.CommonDataKinds.Email.CONTENT_URI, 
    			null,
    			ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", 
    			new String[]{contactId}, null); 
    	List<String> result = new ArrayList<String>();
		while (emailCur.moveToNext()) { 
		    String email = emailCur.getString(
	                      emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
	 	    //String emailType = emailCur.getString(
	        //              emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
	 	    result.add(email);
	 	} 
	 	emailCur.close();
	 	return result;
	}

	/**
	 * Fetches information for the given user identified by his or her email address and fills in
	 * the appropriate fields in the User object.
	 * @param user a user object to be filled - email must be provided
	 */
	public static void fillUserInfo(ContentResolver contentResolver, UserVo user) {
    	Cursor c = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.DATA + "=?", new String[] {user.getEmail()}, null);
    	while (c.moveToNext()) {
    		String displayName = c.getString(c.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
    		user.setLastname(displayName);
    		// found something that is not the mail address itself - break. otherwise continue:
    		if (!user.getEmail().equals(displayName)) break; 
    	}
    	c.close();
    }

	/**
	 * Chooses the appropriate display name for the given user object
	 */
	public static String userDisplayName(Letsdoo app, UserVo user) {
		if (user.getEmail().equalsIgnoreCase(app.getEmail()))
			return "Ich" ; // XXX

		StringBuilder stb = new StringBuilder();
    	if (user.getFirstname() != null && user.getFirstname().trim().length() > 0)
    		stb.append(user.getFirstname() + " ");
    	if (user.getLastname() != null && user.getLastname().trim().length() > 0)
    		stb.append(user.getLastname());
    	if (stb.length() == 0)
    		stb.append(user.getEmail());
    	return stb.toString();
	}
	
	/**
	 * Returns a comma-separated list of participant names for the given event
	 */
	public static String participantNames(Letsdoo app, EventVo event) {
		StringBuilder stb = new StringBuilder();
		for (UserVo user : event.getUsers()) {
			if (user.getEmail().equalsIgnoreCase(app.getEmail())) continue; // skip myself
			fillUserInfo(app.getContentResolver(), user); // resolve name from address book
			if (stb.length() > 0) stb.append(", ");
			stb.append(userDisplayName(app, user));
		}
		if (stb.length()>0) return stb.toString();
		else return null; // no participants except myself - return null
	}
}
