package de.letsdoo.client.android;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import de.letsdoo.client.entity.Event;
import de.letsdoo.client.entity.User;
import de.letsdoo.client.util.ContactsUtils;
import de.potpiejimmy.util.DroidLib;

public class ParticipantsActivity extends ListActivity implements OnItemClickListener, OnClickListener {

    public static final int PICK_CONTACT    = 1;

    private ArrayAdapter<User> data = null;
	
	private ImageButton addButton = null;
	
	private Event event = null;
	
	private String[] currentMailSelection = null;
	
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
    	
    	this.event = (Event)getIntent().getExtras().get("event");
    	
    	for (User user : event.getUsers())
    		ContactsUtils.fillUserInfo(this, user);
    	
    	this.data = new ArrayAdapter<User>(this, R.layout.participant_item, new ArrayList<User>(Arrays.asList(event.getUsers()))) {
    		@Override
    		public View getView(int position, View convertView, ViewGroup viewGroup) {
    			if (convertView == null) {
    	            LayoutInflater inflater = (LayoutInflater) getContext()
    	                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	            convertView = inflater.inflate(R.layout.participant_item, null);
    	        }
    			User user = getItem(position);
    			TextView displayName = (TextView) convertView.findViewById(R.id.participantname);
    			displayName.setText(ContactsUtils.userDisplayName(ParticipantsActivity.this, user));
    			TextView email = (TextView) convertView.findViewById(R.id.participantemail);
    			email.setText(user.getEmail());
    	        return convertView;
    		}
    	};
    	this.setListAdapter(data);
    	
    	getListView().setTextFilterEnabled(true);
    	getListView().setOnItemClickListener(this);
    	registerForContextMenu(getListView());
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
      super.onCreateContextMenu(menu, v, menuInfo);
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.context, menu);
    }
    
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	}

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
            super.onActivityResult(reqCode, resultCode, data);

            switch (reqCode) {
                    case (PICK_CONTACT):
                            if (resultCode == Activity.RESULT_OK) {
                                    Uri contactData = data.getData();
                                    
                                    Cursor c =  managedQuery(contactData, null, null, null, null);      
                                    if (c.moveToFirst()) {
                                    	String id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                                    	List<String> emails = ContactsUtils.fetchEmails(this, id);
                                    	if (emails.size()==0) {
                                    		// No email found
                                    		DroidLib.toast(this, "Keine E-Mail-Adressen für diesen Kontakt gefunden");
                                    	} else if (emails.size()==1) {
                                    		// Single email address found - add it.
                                    		addUser(emails.get(0));
                                    	} else {
                                    		// Multiple email addresses found - create dialog
                                    		this.currentMailSelection = emails.toArray(new String[emails.size()]);
                                    		DroidLib.alert(this, "E-Mail-Adresse wählen", this.currentMailSelection, new DialogInterface.OnClickListener() {
                                    		    public void onClick(DialogInterface dialog, int item) {
                                    		        addUser(currentMailSelection[item]);
                                    		    }
                                    		});
                                    	}
                                    }
                                    c.close();
                            }
                            break;
            }
    }
    
    protected void addUser(String email) {
    	boolean found = false;
    	for (int i=0; i<data.getCount(); i++)
    		if (data.getItem(i).getEmail().equalsIgnoreCase(email)) {found = true; break;}
    	if (found) {
    		DroidLib.toast(this, "Bereits hinzugefügt");
    		return;
    	}
    		
        User newUser = new User();
        newUser.setEmail(email);
        ContactsUtils.fillUserInfo(this, newUser);
        this.data.add(newUser);
    }
    
    protected void add() {
    	  Intent intentContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI); 
    	  startActivityForResult(intentContact, PICK_CONTACT);
	}
	
    protected void finishOk()
    {
    	Intent returnValue = new Intent();
    	List<User> users = new ArrayList<User>();
    	for (int i=0; i<data.getCount(); i++)
    		users.add(data.getItem(i));
    	event.setUsers(users.toArray(new User[users.size()]));
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
