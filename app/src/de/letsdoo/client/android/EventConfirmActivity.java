package de.letsdoo.client.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import de.letsdoo.client.android.rest.EventsAccessor;
import de.letsdoo.client.util.ContactsUtils;
import de.letsdoo.client.util.Utils;
import de.letsdoo.server.vo.EventVo;
import de.letsdoo.server.vo.UserVo;
import de.potpiejimmy.util.AsyncUITask;

public class EventConfirmActivity extends Activity implements OnClickListener {

	private EventVo event = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.eventconfirm);
    	
    	this.event = (EventVo)getIntent().getExtras().get("event");
    	
		TextView eventdatetime = (TextView) findViewById(R.id.eventconfirmdatetime);
		eventdatetime.setText(event.getEventtime() != null ? Utils.formatDateTime(event.getEventtime()) : "");
		TextView eventconfirmtitle = (TextView) findViewById(R.id.eventconfirmtitle);
		eventconfirmtitle.setText(event.getName());
		TextView eventconfirmdescription = (TextView) findViewById(R.id.eventconfirmdescription);
		eventconfirmdescription.setText(event.getDescription());

		Button confirmbutton1 = (Button) findViewById(R.id.eventconfirmbutton1);
		Button confirmbutton2 = (Button) findViewById(R.id.eventconfirmbutton2);
		
		confirmbutton1.setOnClickListener(this);
		confirmbutton2.setOnClickListener(this);
		
		eventconfirmdescription.setText(event.getDescription());
		
		TextView activityconfirmtitle = (TextView) findViewById(R.id.activityconfirmtitle);
        activityconfirmtitle.setText(Utils.getActivityTitle(this, event));
    	
    	LinearLayout list = (LinearLayout)findViewById(R.id.participantsconfirmlist);
    	for (UserVo user : event.getUsers()) {
			ContactsUtils.fillUserInfo(this, user);
				
			View vi = getLayoutInflater().inflate(R.layout.participant_confirm_item, null);
			TextView name = (TextView) vi.findViewById(R.id.participantname);
			name.setText(ContactsUtils.userDisplayName(this, user));
			TextView email = (TextView) vi.findViewById(R.id.participantemail);
			email.setText(user.getEmail());
			ImageView icon = (ImageView) vi.findViewById(R.id.participantconfirmiconview);
			Utils.setIconForConfirmState(icon, user);
			list.addView(vi);
		}

    }

	public void onClick(View v) {
		switch (v.getId())
		{
			case R.id.eventconfirmbutton1:
				confirm(1);
				break;
			case R.id.eventconfirmbutton2:
				confirm(2);
				break;
		}
	}
	
	protected void confirm(int state) {
		new Confirmer(state).go("Speichern...");
	}
	
    protected void finishOk()
    {
    	setResult(RESULT_OK);
    	finish();
    }
    
	protected class Confirmer extends AsyncUITask<String>
	{
		private int state = 0;
		
		public Confirmer(int state) {
			super(EventConfirmActivity.this);
			this.state = state;
		}
		
		public String doTask() throws Throwable
		{
			EventsAccessor ea = Utils.getApp(EventConfirmActivity.this).getEventsAccessor();
			ea.getWebRequest().setParam("confirm", ""+state);
			ea.getItem(event.getId());
    		return "Gespeichert";
		}
		
		public void doneOk(String result)
		{
    		Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
    		finishOk();
		}

		public void doneFail(Throwable throwable)
		{
    		Toast.makeText(getApplicationContext(), throwable.toString(), Toast.LENGTH_SHORT).show();
		}
	}

}
