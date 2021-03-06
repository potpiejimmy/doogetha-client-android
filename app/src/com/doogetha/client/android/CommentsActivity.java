package com.doogetha.client.android;

import android.content.Context;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.doogetha.client.android.rest.CommentsAccessor;
import com.doogetha.client.util.ContactsUtils;
import com.doogetha.client.util.SlideActivity;
import com.doogetha.client.util.Utils;

import de.letsdoo.server.vo.EventCommentVo;
import de.letsdoo.server.vo.EventCommentsVo;
import de.letsdoo.server.vo.EventVo;
import de.potpiejimmy.util.AsyncUITask;
import de.potpiejimmy.util.PullRefreshableListView;
import de.potpiejimmy.util.PullRefreshableListView.OnRefreshListener;

public class CommentsActivity extends SlideActivity implements OnItemClickListener, OnClickListener, OnRefreshListener {

	private EventVo event = null;

	private PullRefreshableListView commentsList = null;
	
	private ArrayAdapter<EventCommentVo> data = null;
	
	protected EventCommentVo currentComment = null;
	
	protected Button submitButton = null;
	protected EditText commentTF = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.comments);

    	this.event = (EventVo)getIntent().getExtras().get("event");
    	
        this.commentsList = (PullRefreshableListView) findViewById(R.id.commentslist);
        this.submitButton = (Button) findViewById(R.id.commentSubmitButton);
        this.commentTF = (EditText) findViewById(R.id.commentEditText);

    	this.data = new ArrayAdapter<EventCommentVo>(this, R.layout.comment_item) {
			@Override
			public View getView(int position, View convertView, ViewGroup viewGroup) {
				if (convertView == null) {
		            LayoutInflater inflater = (LayoutInflater) getContext()
		                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		            convertView = inflater.inflate(R.layout.comment_item, null);
		        }
				EventCommentVo comment = getItem(position);
				TextView label = (TextView) convertView.findViewById(R.id.comment_label);
				TextView sublabel = (TextView) convertView.findViewById(R.id.comment_sublabel);
				label.setText(comment.getComment());
				sublabel.setText(Utils.formatCommentSubline(Utils.getApp(CommentsActivity.this), comment));
		        return convertView;
			}
    	};
    	
    	this.commentsList.setAdapter(this.data);
    	this.commentsList.setOnRefreshListener(this);
    	this.commentsList.setUseScreenLockWhileRefreshing(true);

    	this.submitButton.setOnClickListener(this);
    	
    	this.setSlideOutAnim(R.anim.slide_out_bottom);
    	
    	// only if event comments not fully loaded, trigger a reload:
    	if (event.getComments().getEventComments() != null &&
    		event.getComments().getEventComments().size() < event.getComments().getCount())
    		reload();
    	else
    		// otherwise use the comments in the given event:
    		adaptComments(event.getComments());
    }
    
    public void onBackPressed()
    {
    	// always finish with result code okay so that 
    	// the current comments are returned back to the invoking activity
		finishOk();
    }
    
    protected void finishOk()
    {
    	Intent returnValue = new Intent();
    	returnValue.putExtra("comments", event.getComments());
    	setResult(RESULT_FIRST_USER, returnValue);
    	finish();
    }
    
	public void onRefresh()
	{
		reload();
	}

	public void onClick(View v)
	{
		switch (v.getId()) {
			case R.id.commentSubmitButton:
				submit();
				break;
		}
	}
	
	protected void submit()
	{
		String commentText = commentTF.getText().toString().trim();
		if (commentText.length() == 0) return;
		
		currentComment = new EventCommentVo();
		currentComment.setComment(commentText);
		
		new Inserter().go(getString(R.string.save));
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
	}
	
	protected void reload()
	{
		if (!commentsList.isRefreshing()) commentsList.setRefreshing();
		new DataLoader().go(getString(R.string.loading), false);
	}

	protected void adaptComments(EventCommentsVo comments)
	{
		if (comments == null || comments.getEventComments() == null) return;
		
		this.event.setComments(comments);
		for (EventCommentVo c : comments.getEventComments())
			data.add(c);
	}
	
    protected void loadingDone()
    {
    	this.commentsList.onRefreshComplete();
    }
    
    protected void submitDone()
    {
    	commentTF.setText("");
    }
    
	protected class DataLoader extends AsyncUITask<EventCommentsVo>
	{
		public DataLoader() { super(CommentsActivity.this); }
		
		public EventCommentsVo doTask() throws Throwable
		{
			CommentsAccessor ca = Utils.getApp(CommentsActivity.this).getCommentsAccessor();
    		ca.setCurrentBaseId(event.getId());
			return ca.getItems();
		}
		
		public void doneOk(EventCommentsVo result)
		{
			data.clear();
			if (result != null && result.getEventComments() != null) {
				for (EventCommentVo c : result.getEventComments()) {
					ContactsUtils.fillUserInfo(CommentsActivity.this.getContentResolver(), c.getUser());
				}
				adaptComments(result);
			}
			loadingDone();
		}

		public void doneFail(Throwable throwable)
		{
    		Toast.makeText(getApplicationContext(), throwable.toString(), Toast.LENGTH_SHORT).show();
			loadingDone();
		}
	}
	
	protected class Inserter extends AsyncUITask<String>
	{
		public Inserter() { super(CommentsActivity.this); }
		
		public String doTask() throws Throwable
		{
    		CommentsAccessor ca = Utils.getApp(CommentsActivity.this).getCommentsAccessor();
    		ca.setCurrentBaseId(event.getId()); // insert comment for current event
			ca.insertItem(currentComment);
    		return getString(R.string.ok);
		}
		
		public void doneOk(String result)
		{
    		//Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
    		submitDone();
    		reload();
		}

		public void doneFail(Throwable throwable)
		{
    		Toast.makeText(getApplicationContext(), throwable.toString(), Toast.LENGTH_SHORT).show();
		}
	}
}
