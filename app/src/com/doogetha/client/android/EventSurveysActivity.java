package com.doogetha.client.android;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
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
import com.doogetha.client.util.SlideListActivity;
import de.letsdoo.server.vo.EventVo;
import de.letsdoo.server.vo.SurveyVo;
import de.potpiejimmy.util.DroidLib;

public class EventSurveysActivity extends SlideListActivity implements OnItemClickListener, OnClickListener {

    private ArrayAdapter<SurveyVo> data = null;
	
	private ImageButton addButton = null;
	
	private EventVo event = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.surveys);
        
    	Button buttonok = (Button) findViewById(R.id.editok);
    	Button buttoncancel = (Button) findViewById(R.id.editcancel);
        this.addButton = (ImageButton)findViewById(R.id.addbutton);
        
        addButton.setOnClickListener(this);
    	buttonok.setOnClickListener(this);
    	buttoncancel.setOnClickListener(this);
    	
    	this.event = (EventVo)getIntent().getExtras().get("event");
    	
    	if (event.getSurveys() == null) event.setSurveys(new SurveyVo[0]);
    	
    	this.data = new ArrayAdapter<SurveyVo>(this, R.layout.survey_item, new ArrayList<SurveyVo>(Arrays.asList(event.getSurveys()))) {
    		@Override
    		public View getView(int position, View convertView, ViewGroup viewGroup) {
    			if (convertView == null) {
    	            LayoutInflater inflater = (LayoutInflater) getContext()
    	                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	            convertView = inflater.inflate(R.layout.survey_item, null);
    	        }
    			SurveyVo survey = getItem(position);
    			TextView name = (TextView) convertView.findViewById(R.id.surveyname);
    			name.setText(survey.getName());
    			TextView description = (TextView) convertView.findViewById(R.id.surveydescription);
    			description.setText(survey.getDescription());
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
      inflater.inflate(R.menu.participants_edit_context, menu);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
      AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
      switch (item.getItemId()) {
	      case R.id.deleteitem:
    		data.remove(data.getItem(info.position));
	        return true;
	      default:
	        return super.onContextItemSelected(item);
      }
    }
    
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// edit survey:
    	Intent intent = new Intent(getApplicationContext(), SurveyEditActivity.class);
    	intent.putExtra("survey", data.getItem(position));
    	startActivityForResult(intent, position+1);
	}

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
    	if (resultCode == RESULT_OK)
    	{
    		SurveyVo survey = (SurveyVo)data.getExtras().get("survey");
    		if (reqCode > 0) {
    			this.data.remove(this.data.getItem(reqCode-1));
    			this.data.insert(survey, reqCode-1);
    		} else
    			this.data.add(survey);
    	}
    }
    
    protected void addSurvey() {
    	DroidLib.alert(this, "Art der Abstimmung", new String[] {"Freie Abstimmung", "Terminfindung (Datum)", "Terminfindung (Datum und Uhrzeit)"}, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
		    	Intent intent = new Intent(getApplicationContext(), SurveyEditActivity.class);
		    	intent.putExtra("type", (byte)which);
		    	startActivityForResult(intent, 0);
			}
    	});
	}
    
    protected void saveValues()
    {
    	List<SurveyVo> surveys = new ArrayList<SurveyVo>();
    	for (int i=0; i<data.getCount(); i++)
    		surveys.add(data.getItem(i));
    	event.setSurveys(surveys.size() == 0 ? null : surveys.toArray(new SurveyVo[surveys.size()]));
    }
	
    protected void finishOk()
    {
    	saveValues();
    	
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
		case R.id.addbutton:
			addSurvey();
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
