package com.doogetha.client.android;

import java.util.ArrayList;
import java.util.List;

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

import com.doogetha.client.util.ContactsUtils;
import com.doogetha.client.util.SlideListActivity;
import com.doogetha.client.util.Utils;

import de.letsdoo.server.vo.UserVo;

public class DoogethaFriendsActivity extends SlideListActivity implements OnItemClickListener, OnClickListener {

    private ArrayAdapter<SelectableUser> data = null;
	
	private ImageButton addButton = null;
	
	protected static class SelectableUser
	{
		public UserVo user = null;
		public boolean selected = false;
		
		public SelectableUser(UserVo user, boolean selected)
		{
			this.user = user;
			this.selected = selected;
		}
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.doogetha_friends);
        
    	Button buttonok = (Button) findViewById(R.id.editok);
    	Button buttoncancel = (Button) findViewById(R.id.editcancel);
        this.addButton = (ImageButton)findViewById(R.id.addbutton);
        
        addButton.setOnClickListener(this);
    	buttonok.setOnClickListener(this);
    	buttoncancel.setOnClickListener(this);
    	
    	List<SelectableUser> users = new ArrayList<SelectableUser>();
    	UserVo user = new UserVo();
    	user.setEmail("thorsten@potpiejimmy.de");
    	user.setFirstname("Thorsten");
    	users.add(new SelectableUser(user, false));
    	user = new UserVo();
    	user.setEmail("kerstin_nicklaus@web.de");
    	user.setFirstname("Kerstin");
    	users.add(new SelectableUser(user, false));
    	
    	this.data = new ArrayAdapter<SelectableUser>(this, R.layout.doogetha_friend_item, users) {
    		@Override
    		public View getView(int position, View convertView, ViewGroup viewGroup) {
    			if (convertView == null) {
    	            LayoutInflater inflater = (LayoutInflater) getContext()
    	                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	            convertView = inflater.inflate(R.layout.doogetha_friend_item, null);
    	        }
    			SelectableUser user = getItem(position);
    			TextView displayName = (TextView) convertView.findViewById(R.id.participantname);
    			displayName.setText(ContactsUtils.userDisplayName(Utils.getApp(DoogethaFriendsActivity.this), user.user));
    			TextView email = (TextView) convertView.findViewById(R.id.participantemail);
    			email.setText(user.user.getEmail());
    			CheckedTextView checkBox = (CheckedTextView)convertView.findViewById(R.id.item_checkbox);
    			checkBox.setChecked(user.selected);
    	        return convertView;
    		}
    	};
    	this.setListAdapter(data);
    	
    	getListView().setTextFilterEnabled(true);
    	getListView().setOnItemClickListener(this);
    	//registerForContextMenu(getListView());
    }
    
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		SelectableUser user = data.getItem(position);
		user.selected = user.selected ^ true;
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
		case R.id.addbutton:
			
			break;
		case R.id.editok:
			finishOk();
			break;
		case R.id.editcancel:
			finishCancel();
			break;
		}
	}
}
