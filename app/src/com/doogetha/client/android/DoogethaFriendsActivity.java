package com.doogetha.client.android;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
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
import android.widget.ImageButton;
import android.widget.TextView;

import com.doogetha.client.android.uitasks.DoogethaFriendsSyncTask;
import com.doogetha.client.android.uitasks.DoogethaFriendsSyncTaskCallback;
import com.doogetha.client.util.ContactsUtils;
import com.doogetha.client.util.SlideListActivity;
import com.doogetha.client.util.Utils;

import de.letsdoo.server.vo.UserVo;

public class DoogethaFriendsActivity extends SlideListActivity implements OnItemClickListener, OnClickListener, DoogethaFriendsSyncTaskCallback {

    private ArrayAdapter<UserVo> data = null;
	
	private ImageButton syncButton = null;

	private Map<String,Boolean> currentSelection = new HashMap<String,Boolean>();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.doogetha_friends);
        
    	Button buttonok = (Button) findViewById(R.id.editok);
    	Button buttoncancel = (Button) findViewById(R.id.editcancel);
        this.syncButton = (ImageButton)findViewById(R.id.syncbutton);
        
        syncButton.setOnClickListener(this);
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

    protected void finishOk()
    {
    	setResult(RESULT_OK);
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
		data.clear();
		for (UserVo user : getCurrentFriendsList())
			data.add(user);
		data.notifyDataSetChanged();
	}
}
