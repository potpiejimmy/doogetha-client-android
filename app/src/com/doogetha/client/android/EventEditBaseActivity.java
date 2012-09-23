package com.doogetha.client.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.doogetha.client.util.SlideActivity;

import de.letsdoo.server.vo.EventVo;
import de.potpiejimmy.util.DroidLib;

public class EventEditBaseActivity extends SlideActivity implements OnClickListener {
	
	private EventVo event = null;

	private EditText activityname = null;
	private EditText activitydescription = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.eventedit_base);
        
    	Button buttonok = (Button) findViewById(R.id.editok);
    	Button buttoncancel = (Button) findViewById(R.id.editcancel);
    	this.activityname = (EditText) findViewById(R.id.activityname);
    	this.activitydescription = (EditText) findViewById(R.id.activitydescription);
    	
    	buttonok.setOnClickListener(this);
    	buttoncancel.setOnClickListener(this);

    	event = (EventVo)getIntent().getExtras().get("event");
        
        activityname.setText(event.getName());
        activitydescription.setText(event.getDescription());
    }

    protected void validateAndSave() {
    	if (activityname.getText().toString().trim().length() == 0) {
    		DroidLib.toast(this, getString(R.string.please_enter_activity_name));
    		return;
    	}
    	event.setName(activityname.getText().toString());
    	event.setDescription(activitydescription.getText().toString());
    	finishOk();
    }
    
    protected void finishOk()
    {
    	Intent returnValue = new Intent();
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
		case R.id.editok:
			validateAndSave();
			break;
		case R.id.editcancel:
			finishCancel();
			break;
		}
	}
}
