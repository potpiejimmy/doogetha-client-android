package com.doogetha.client.android;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.doogetha.client.android.uitasks.DoogethaFriendsSyncTask;
import com.doogetha.client.android.uitasks.DoogethaFriendsSyncTaskCallback;
import com.doogetha.client.util.ContactsUtils;
import com.doogetha.client.util.SlideListActivity;
import com.doogetha.client.util.Utils;

import de.letsdoo.server.vo.UserVo;
import de.letsdoo.server.vo.UsersVo;
import de.potpiejimmy.util.AsyncUITask;
import de.potpiejimmy.util.DroidLib;

public class DoogethaFriendsActivity extends SlideListActivity implements OnItemClickListener, OnClickListener, DoogethaFriendsSyncTaskCallback {

    private ArrayAdapter<UserVo> data = null;
	
	private ImageButton syncButton = null;
	private ImageButton addButton = null;
	private TextView numFriendsLabel = null;

	private Map<String,Boolean> currentSelection = new HashMap<String,Boolean>();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.doogetha_friends);
        
    	Button buttonok = (Button) findViewById(R.id.editok);
    	Button buttoncancel = (Button) findViewById(R.id.editcancel);
        this.syncButton = (ImageButton)findViewById(R.id.syncbutton);
        this.addButton = (ImageButton)findViewById(R.id.addbutton);
        this.numFriendsLabel = (TextView)findViewById(R.id.numFriendsLabel);
        
        syncButton.setOnClickListener(this);
        addButton.setOnClickListener(this);
    	buttonok.setOnClickListener(this);
    	buttoncancel.setOnClickListener(this);
    	
    	List<UserVo> users = this.getCurrentFriendsList();
    	
    	this.data = new ArrayAdapter<UserVo>(this, R.layout.doogetha_friend_item, users) {
    		@Override
    		public View getView(int position, View convertView, ViewGroup viewGroup) {
    			if (convertView == null) {
    	            LayoutInflater inflater = (LayoutInflater) getContext()
    	                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	            convertView = inflater.inflate(R.layout.doogetha_friend_item, null);
    	        }
    			UserVo user = getItem(position);
    			TextView displayName = (TextView) convertView.findViewById(R.id.participantname);
    			displayName.setText(ContactsUtils.userDisplayName(Utils.getApp(DoogethaFriendsActivity.this), user));
    			TextView email = (TextView) convertView.findViewById(R.id.participantemail);
    			email.setText(user.getEmail());
    			CheckedTextView checkBox = (CheckedTextView)convertView.findViewById(R.id.item_checkbox);
    			checkBox.setChecked(currentSelection.get(user.getEmail()) != null);
    	        return convertView;
    		}
    	};
    	this.setListAdapter(data);
    	
    	getListView().setTextFilterEnabled(true);
    	getListView().setOnItemClickListener(this);
    	//registerForContextMenu(getListView());
    	
    	updateUI();
    }
    
    protected void updateUI()
    {
    	numFriendsLabel.setText(data.getCount() + " " + getString(R.string.friends));
    }
    
    protected List<UserVo> getCurrentFriendsList() {
    	Collection<UserVo> friends = Utils.getApp(this).getDoogethaFriends().getUsers();
    	List<UserVo> users = new ArrayList<UserVo>();
    	if (friends != null) {
    		for (UserVo user : friends) users.add(user);
    	}
    	return users;
    }
    
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		UserVo user = data.getItem(position);
		if (currentSelection.get(user.getEmail()) != null)
			currentSelection.remove(user.getEmail());
		else
			currentSelection.put(user.getEmail(), true);
		data.notifyDataSetChanged();
	}
	
    protected void add() {
		final EditText input = new EditText(this);
		new AlertDialog.Builder(this)
	    .setMessage("E-Mail-Adresse eingeben")
	    .setView(input)
	    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int whichButton) {
	        	checkAddUser(input.getText().toString().trim());
	        }
	    }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int whichButton) {
	        }
	    }).show();
    }

    protected void checkAddUser(String email) {
    	if (email == null) return;
    	email = email.trim();
    	
    	if (!Utils.checkValidMailAddress(email)) {
    		DroidLib.toast(this, getString(R.string.email_address_invalid) + ": " + email);
    		return;
    	}
    	
    	boolean found = false;
    	for (int i=0; i<data.getCount(); i++)
    		if (data.getItem(i).getEmail().equalsIgnoreCase(email)) {found = true; break;}
    	if (found) {
    		DroidLib.toast(this, getString(R.string.already_added));
    		return;
    	}
    		
    	// now check against server:
    	new CheckUserTask(email).go(getString(R.string.address_being_verified));
    }
    
	protected void addUser(String email) {
    	Letsdoo app = Utils.getApp(this);
        UserVo newUser = new UserVo();
        newUser.setEmail(email);
        newUser.setState(0); /* unconfirmed/new */
        ContactsUtils.fillUserInfo(this.getContentResolver(), newUser);
        currentSelection.put(email, true);
        
        // manually add a new entry to doogetha friends list (in alphabetical order):
        UsersVo doogethaFriends = app.getDoogethaFriends();
        Collection<UserVo> users = doogethaFriends.getUsers();
        List<UserVo> newUsers = users != null ? new ArrayList<UserVo>(users) : new ArrayList<UserVo>();
        boolean added = false;
        for (int i=0; i<newUsers.size(); i++) {
        	String n1 = ContactsUtils.userDisplayName(app, newUsers.get(i)).toLowerCase(Locale.getDefault());
        	String n2 = ContactsUtils.userDisplayName(app, newUser).toLowerCase(Locale.getDefault());
        	if (n1.compareTo(n2) > 0) {
        		newUsers.add(i, newUser);
        		added = true;
        		break;
        	}
        }
        if (!added) newUsers.add(newUser);
        doogethaFriends.setUsers(newUsers);
        app.setDoogethaFriends(doogethaFriends);

        updateFriendsList();
    }
    
    protected void finishOk()
    {
    	Intent returnValue = new Intent();
    	UsersVo selectedUsers = new UsersVo();
    	List<UserVo> users = new ArrayList<UserVo>();
    	for (String email : currentSelection.keySet()) {
    		for (int i=0; i<data.getCount(); i++) {
    			if (data.getItem(i).getEmail().equals(email))
    				users.add(data.getItem(i));
    		}
    	}
    	selectedUsers.setUsers(users);
    	returnValue.putExtra("selectedUsers", selectedUsers);
    	setResult(RESULT_OK, returnValue);
    	finish();
    }
    
    protected void finishCancel()
    {
    	setResult(RESULT_CANCELED);
    	finish();
    }
    
	public void onClick(View view) {
		switch (view.getId())
		{
		case R.id.addbutton:
			add();
			break;
		case R.id.syncbutton:
			new DoogethaFriendsSyncTask(this, this).go(getString(R.string.doogethafriends_synchronizing));
			break;
		case R.id.editok:
			finishOk();
			break;
		case R.id.editcancel:
			finishCancel();
			break;
		}
	}

	public void friendListSynced() {
		updateFriendsList();
	}
	
	protected void updateFriendsList() {
		data.clear();
		for (UserVo user : getCurrentFriendsList())
			data.add(user);
		data.notifyDataSetChanged();
		updateUI();
	}

	protected class CheckUserTask extends AsyncUITask<UserVo>
	{
		private String email = null;
		
		public CheckUserTask(String email) {
			super(DoogethaFriendsActivity.this);
			this.email = email;
		}

		@Override
		public UserVo doTask() throws Throwable {
			return Utils.getApp(DoogethaFriendsActivity.this).getUsersAccessor().getItem(Utils.md5Hex(email));
		}

		@Override
		public void doneFail(Throwable throwable) {
			DroidLib.alert(DoogethaFriendsActivity.this, email, "Unter dieser Adressse ist noch kein Benutzer registriert. Wollen Sie die Adresse dennoch hinzufügen und eine Einladung an diese Adresse verschicken?", getString(R.string.ok), getString(R.string.cancel), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					addUser(email);
				}
			});
		}

		@Override
		public void doneOk(UserVo result) {
			addUser(result.getEmail());
		}
	}
}
