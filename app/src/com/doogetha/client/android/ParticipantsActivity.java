package com.doogetha.client.android;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.doogetha.client.util.ContactsUtils;
import com.doogetha.client.util.SlideListActivity;
import com.doogetha.client.util.Utils;

import de.letsdoo.server.vo.EventVo;
import de.letsdoo.server.vo.UserVo;
import de.letsdoo.server.vo.UsersVo;

public class ParticipantsActivity extends SlideListActivity implements OnItemClickListener, OnClickListener {

    public static final int PICK_CONTACT    = 1;

    private ArrayAdapter<UserVo> data = null;
	
	private ImageButton addButton = null;
	
	private EventVo event = null;
	
//	private String[] currentMailSelection = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.participants);
        
    	Button buttonok = (Button) findViewById(R.id.editok);
    	Button buttoncancel = (Button) findViewById(R.id.editcancel);
        this.addButton = (ImageButton)findViewById(R.id.addbutton);
        
        addButton.setOnClickListener(this);
    	buttonok.setOnClickListener(this);
    	buttoncancel.setOnClickListener(this);
    	
    	this.event = (EventVo)getIntent().getExtras().get("event");
    	
    	for (UserVo user : event.getUsers())
    		ContactsUtils.fillUserInfo(this.getContentResolver(), user);
    	
    	this.data = new ArrayAdapter<UserVo>(this, R.layout.participant_item, new ArrayList<UserVo>(Arrays.asList(event.getUsers()))) {
    		@Override
    		public View getView(int position, View convertView, ViewGroup viewGroup) {
    			if (convertView == null) {
    	            LayoutInflater inflater = (LayoutInflater) getContext()
    	                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	            convertView = inflater.inflate(R.layout.participant_item, null);
    	        }
    			UserVo user = getItem(position);
    			TextView displayName = (TextView) convertView.findViewById(R.id.participantname);
    			displayName.setText(ContactsUtils.userDisplayName(Utils.getApp(ParticipantsActivity.this), user));
    			TextView email = (TextView) convertView.findViewById(R.id.participantemail);
    			email.setText(user.getEmail());
    	        return convertView;
    		}
    	};
    	this.setListAdapter(data);
    	
    	//getListView().setTextFilterEnabled(true);
    	getListView().setOnItemClickListener(this);
    	registerForContextMenu(getListView());
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
      super.onCreateContextMenu(menu, v, menuInfo);
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.participants_edit_context, menu);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
      AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
      switch (item.getItemId()) {
	      case R.id.deleteitem:
	    	UserVo user = data.getItem(info.position);
	    	if (Utils.isMyself(this, user))
	    		Toast.makeText(getApplicationContext(), "Du kannst nicht dich selbst entfernen", Toast.LENGTH_SHORT).show();
	    	else
	    		data.remove(user);
	        return true;
	      default:
	        return super.onContextItemSelected(item);
      }
    }
    
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	}

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent resultData) {
            super.onActivityResult(reqCode, resultCode, resultData);

//            switch (reqCode) {
//                    case (PICK_CONTACT):
//                            if (resultCode == Activity.RESULT_OK) {
//                                    Uri contactData = data.getData();
//                                    
//                                    Cursor c =  managedQuery(contactData, null, null, null, null);      
//                                    if (c.moveToFirst()) {
//                                    	String id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
//                                    	List<String> emails = ContactsUtils.fetchEmails(this, id);
//                                    	if (emails.size()==0) {
//                                    		// No email found
//                                    		DroidLib.toast(this, "Keine E-Mail-Adressen f�r diesen Kontakt gefunden");
//                                    	} else if (emails.size()==1) {
//                                    		// Single email address found - add it.
//                                    		checkAddUser(emails.get(0));
//                                    	} else {
//                                    		// Multiple email addresses found - create dialog
//                                    		this.currentMailSelection = emails.toArray(new String[emails.size()]);
//                                    		DroidLib.alert(this, "E-Mail-Adresse w�hlen", this.currentMailSelection, new DialogInterface.OnClickListener() {
//                                    		    public void onClick(DialogInterface dialog, int item) {
//                                    		    	checkAddUser(currentMailSelection[item]);
//                                    		    }
//                                    		});
//                                    	}
//                                    }
//                                    c.close();
//                            }
//                            break;
//            }
            
        // returned from DoogethaFriendsActivity:
        if (resultData != null && resultData.getExtras() != null) {
	        UsersVo selectedUsers = (UsersVo)resultData.getExtras().get("selectedUsers");
	        if (selectedUsers != null &&
	            selectedUsers.getUsers() != null) {
	        	for (UserVo user : selectedUsers.getUsers()) {
	        		boolean found = false;
	        		for (int i=0; i<data.getCount(); i++) {
	        			if (data.getItem(i).getEmail().equalsIgnoreCase(user.getEmail())) {
	        				found = true;
	        				break;
	        			}
	        		}
	        		if (!found) data.add(user);
	        	}
	        	data.notifyDataSetChanged();
	        }
        }
    }
    
    protected void add() {
//    	DroidLib.alert(this, "Mailadresse ausw�hlen...", new String[] {"aus Kontaktliste", "aus Doogetha-Liste", "manuell eingeben"}, new android.content.DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int which) {
//				switch (which) {
//					case 0: 
//			    	  Intent intentContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI); 
//			    	  startActivityForResult(intentContact, PICK_CONTACT);
//			    	  break;
//					case 1:
//					  DroidLib.alert(ParticipantsActivity.this, "E-Mail-Adresse w�hlen", Utils.getApp(ParticipantsActivity.this).getKnownAddresses(),  new android.content.DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog, int which) {
//							checkAddUser(Utils.getApp(ParticipantsActivity.this).getKnownAddresses()[which]);
//						}
//					  });
					  Intent intentDoogethaList = new Intent(getApplicationContext(), DoogethaFriendsActivity.class);
					  startActivityForResult(intentDoogethaList, 0);
//					  break;
//					case 2:
//						final EditText input = new EditText(ParticipantsActivity.this);
//						new AlertDialog.Builder(ParticipantsActivity.this)
//					    .setMessage("E-Mail-Adresse eingeben")
//					    .setView(input)
//					    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
//					        public void onClick(DialogInterface dialog, int whichButton) {
//					        	checkAddUser(input.getText().toString().trim());
//					        }
//					    }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
//					        public void onClick(DialogInterface dialog, int whichButton) {
//					        }
//					    }).show();
//
//				}
//			}
//    	});
//    	
	}
	
    protected void finishOk()
    {
    	Intent returnValue = new Intent();
    	List<UserVo> users = new ArrayList<UserVo>();
    	for (int i=0; i<data.getCount(); i++)
    		users.add(data.getItem(i));
    	event.setUsers(users.toArray(new UserVo[users.size()]));
    	returnValue.putExtra("event", event);
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
		case R.id.editok:
			finishOk();
			break;
		case R.id.editcancel:
			finishCancel();
			break;
		}
	}
}
