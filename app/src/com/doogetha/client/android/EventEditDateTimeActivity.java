package com.doogetha.client.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import de.letsdoo.server.vo.EventVo;

public class EventEditDateTimeActivity extends AbstractEventEditDateTimeActivity implements OnClickListener {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.eventedit_datetime);
        
    	Button buttonok = (Button) findViewById(R.id.editok);
    	Button buttoncancel = (Button) findViewById(R.id.editcancel);
    	this.editdatetime = (ImageButton) findViewById(R.id.editdatetime);
    	this.datetimelabel = (TextView) findViewById(R.id.datetimelabel);
    	
    	buttonok.setOnClickListener(this);
    	buttoncancel.setOnClickListener(this);
    	editdatetime.setOnClickListener(this);
        
    	event = (EventVo)getIntent().getExtras().get("event");
        
        updateUI();
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
			finishOk();
			break;
		case R.id.editcancel:
			finishCancel();
			break;
		case R.id.editdatetime:
			editDatetime();
			break;
		}
	}
}
