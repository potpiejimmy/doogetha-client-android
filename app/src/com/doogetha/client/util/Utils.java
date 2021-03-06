package com.doogetha.client.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.util.Base64;
import android.widget.ImageView;

import com.doogetha.client.android.Letsdoo;
import com.doogetha.client.android.R;

import de.letsdoo.server.vo.EventCommentVo;
import de.letsdoo.server.vo.EventVo;
import de.letsdoo.server.vo.SurveyItemVo;
import de.letsdoo.server.vo.SurveyVo;
import de.letsdoo.server.vo.UserVo;

/**
 * Static helper methods for Letsdoo app
 */
public class Utils {
	
	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("EEEEEE,\ndd MMM yyyy", Locale.getDefault());
	public static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.getDefault());

	public static Letsdoo getApp(Activity activity) {
		return (Letsdoo)activity.getApplication();
	}
	
    public static void xor(byte[] result, byte[] xor) {
        if (result == null || xor == null || result.length != xor.length)
            throw new IllegalArgumentException();
        
        for (int i=0; i<result.length; i++)
            result[i] ^= xor[i];
    }
    
    public static String xorHex(String a, String b) {
        byte[] result = hexToBytes(a);
        byte[] xor = hexToBytes(b);
        xor(result, xor);
        return bytesToHex(result);
    }
    
    public static byte[] hexToBytes(String hex) {
        byte[] result = new byte[hex.length()/2];
        for (int i=0; i<result.length; i++)
            result[i] = (byte)Short.parseShort(hex.substring(i*2, i*2+2), 16);
        return result;
    }
    
    public static String bytesToHex(byte[] digest) {
        char[] digits = "0123456789abcdef".toCharArray();
        StringBuilder sb = new StringBuilder(digest.length * 2);
        for (byte b : digest) {
            int bi = b & 0xff;
            sb.append(digits[bi >> 4]);
            sb.append(digits[bi & 0xf]);
        }
        return sb.toString();
    }
    
    public static String formatDateTime(long millis) {
    	Calendar cal = Calendar.getInstance();
    	cal.setTimeInMillis(millis);
    	StringBuilder stb = new StringBuilder();
    	stb.append(DATE_FORMAT.format(cal.getTime()));
    	if (cal.get(Calendar.HOUR_OF_DAY)!=0 || cal.get(Calendar.MINUTE)!=0) {
    		stb.append(' ');
    		stb.append(TIME_FORMAT.format(cal.getTime()));
    	}
    	return stb.toString();
    }
    
    public static String formatCommentSubline(Letsdoo app, EventCommentVo comment) {
		String userName = ContactsUtils.userDisplayName(app, comment.getUser());
		String timeLabel = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(comment.getCreated());
		return userName + " (" + timeLabel + ")";
    }
    
    public static String formatSurveyItem(SurveyVo survey, SurveyItemVo item) {
    	switch (survey.getType()) {
    		case 0: /* generic survey */
    			return item.getName();
    		case 1: /* date picker survey */
    			return Utils.DATE_FORMAT.format(new Date(Long.parseLong(item.getName())));
    		case 2: /* date and time picker survey */
    			Date d = new Date(Long.parseLong(item.getName()));
    			return Utils.DATE_FORMAT.format(d) + " " + Utils.TIME_FORMAT.format(d);
    	}
    	return null;
    }

   public static void setIconForConfirmState(ImageView view, UserVo user) {
		switch (user.getState()) {
			case 0: /* new / unconfirmed */
				view.setImageResource(R.drawable.dot_gray);
				break;
			case 1: /* confirmed */
				view.setImageResource(R.drawable.dot_green);
				break;
			case 2: /* denied */
				view.setImageResource(R.drawable.dot_red);
				break;
			default:
				view.setImageDrawable(null);
		}
    }
    
	public static boolean hasOpenSurveys(EventVo event) {
		boolean hasOpenSurveys = false;
		if (event.getSurveys() != null) {
			for (SurveyVo s : event.getSurveys())
				if (s.getState() == 0) hasOpenSurveys = true;
		}
		return hasOpenSurveys;
	}
	
    public static boolean isMyself(Activity activity, UserVo user) {
    	return Utils.getApp(activity).getEmail().equalsIgnoreCase(user.getEmail());
    }
    
    public static String getActivityTitle(Activity activity, EventVo event) {
        boolean myOwn = Utils.isMyself(activity, event.getOwner());
        if (!myOwn) ContactsUtils.fillUserInfo(activity.getContentResolver(), event.getOwner());
        return myOwn ? activity.getString(R.string.myactivity) : activity.getString(R.string.activityof) + " " + ContactsUtils.userDisplayName(Utils.getApp(activity), event.getOwner());
    }
    
    public static boolean checkValidMailAddress(String email) {
    	if (email == null) return false;
    	String[] parts = email.split("@");
    	return (parts.length==2 &&
    			parts[0].trim().length()>0 &&
    			parts[1].trim().length()>0);
    }

    public static byte[] md5(String input) {
    	try {
    		MessageDigest digest = MessageDigest.getInstance("MD5");
    		return digest.digest(input.getBytes());
    	} catch (NoSuchAlgorithmException nsae) {
    		throw new RuntimeException(nsae);
    	}
    }
    
    public static String md5Base64(String input) {
		return Base64.encodeToString(md5(input), Base64.NO_WRAP);
    }

    public static String md5Hex(String input) {
		return Utils.bytesToHex(md5(input));
    }
}
