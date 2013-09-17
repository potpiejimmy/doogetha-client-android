package com.doogetha.client.util;

import java.text.MessageFormat;
import java.util.List;

import com.doogetha.client.android.CommentsActivity;
import com.doogetha.client.android.R;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import de.letsdoo.server.vo.EventCommentVo;
import de.letsdoo.server.vo.EventCommentsVo;
import de.letsdoo.server.vo.EventVo;

public class CommentsPreviewer implements OnClickListener {

	protected Activity activity = null;
	protected EventVo event = null;
	protected View commentsPreviewer = null;
	
	public CommentsPreviewer(Activity activity, EventVo event) {
		this.activity = activity;
		this.event = event;
		// comments previewer:
		commentsPreviewer = activity.findViewById(R.id.comments_previewer);
		commentsPreviewer.setBackgroundResource(android.R.drawable.list_selector_background);
		commentsPreviewer.setClickable(true);
		commentsPreviewer.setOnClickListener(this);
		update();
	}

	public void onClick(View v) {
		showComments();
	}

	protected void showComments() {
		Intent i = new Intent(activity.getApplicationContext(), CommentsActivity.class);
		i.putExtra("event", event);
		if (activity instanceof SlideActivity)
			((SlideActivity)activity).setSlideInAnim(R.anim.slide_in_bottom);
		activity.startActivityForResult(i, 0);
	}
	
	public void update() {
		TextView headLine = (TextView) commentsPreviewer.findViewById(R.id.comments_previewer_headline);
		TextView label = (TextView) commentsPreviewer.findViewById(R.id.comments_previewer_label);
		TextView sublabel = (TextView) commentsPreviewer.findViewById(R.id.comments_previewer_sublabel);
		
		EventCommentsVo vo = event.getComments();
		if (vo.getCount() == 1)
			headLine.setText(R.string.comment_one);
		else
			headLine.setText(MessageFormat.format(activity.getString(R.string.comments_n), vo.getCount()));
		
		List<EventCommentVo> comments = vo.getEventComments();
		EventCommentVo displayComment = (comments == null || comments.size() == 0) ? null : comments.get(0); /* display the newest comment */
		if (displayComment == null) {
			label.setVisibility(View.GONE);
			sublabel.setText("Kommentieren...");
		} else {
			label.setVisibility(View.VISIBLE);
			label.setText(displayComment.getComment());
			ContactsUtils.fillUserInfo(activity.getContentResolver(), displayComment.getUser());
			sublabel.setText(Utils.formatCommentSubline(Utils.getApp(activity), displayComment));
		}
    }
}
