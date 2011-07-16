package de.letsdoo.client.util;

import java.util.ArrayList;
import java.util.List;

import de.letsdoo.client.entity.User;

import android.app.Activity;
import android.database.Cursor;
import android.provider.ContactsContract;

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
	public static void fillUserInfo(Activity activity, User user) {
    	Cursor c = activity.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.DATA + "=?", new String[] {user.getEmail()}, null);
    	if (c.moveToFirst()) {
    		user.setLastname(c.getString(c.getColumnIndex(ContactsContract.Data.DISPLAY_NAME)));
    	}
    	c.close();
    }

	/**
	 * Chooses the appropriate display name for the given user object
	 */
	public static String userDisplayName(Activity activity, User user) {
		if (user.getEmail().equalsIgnoreCase(Utils.getApp(activity).getEmail()))
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
}
