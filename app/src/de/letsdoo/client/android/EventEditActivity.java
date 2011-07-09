package de.letsdoo.client.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import de.letsdoo.client.entity.Event;
import de.letsdoo.client.util.Utils;
import de.potpiejimmy.util.AsyncUITask;

public class EventEditActivity extends Activity implements OnClickListener {
	
	private EditText editText = null;
	private Event event = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.eventedit);
        
    	Button buttonok = (Button) findViewById(R.id.editok);
    	Button buttoncancel = (Button) findViewById(R.id.editcancel);
    	this.editText = (EditText) findViewById(R.id.edittext);
    	
    	buttonok.setOnClickListener(this);
    	buttoncancel.setOnClickListener(this);
        
        if (getIntent().getExtras() != null && getIntent().getExtras().get("event") != null)
        	event = (Event)getIntent().getExtras().get("event");
        else
        	event = new Event("");
        
        editText.setText(event.getName());
    }

    protected void addOrUpdate()
    {
    	event.setName(editText.getText().toString());
		new Updater().go("Speichern...");
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
    
	protected class Updater extends AsyncUITask<String>
	{
		public Updater() { super(EventEditActivity.this); }
		
		public String doTask()
		{
	    	try{
	    		if (event.getId() != null)
	    			Utils.getApp(EventEditActivity.this).getEventsAccessor().updateItem(event.getId(), event);
	    		else
	    			Utils.getApp(EventEditActivity.this).getEventsAccessor().insertItem(event);
	    		return "Gespeichert";
	    	} catch (Exception ex) {
	    		ex.printStackTrace();
	    		return ex.toString();
	    	}
		}
		
		public void done(String result)
		{
    		Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
    		finishOk();
		}
	}

	public void onClick(View view) {
		switch (view.getId())
		{
		case R.id.editok:
			addOrUpdate();
			break;
		case R.id.editcancel:
			finishCancel();
			break;
		}
	}
	
}
