package de.letsdoo.client.util;

import de.letsdoo.client.android.R;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.TextView;

public class TitleBar extends TextView {

	public TitleBar(Context context) {
		super(context);
		init();
	}
	
	public TitleBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	protected void init() {
		setPadding(3,3,3,3);
		setBackgroundResource(R.drawable.gradient_title);
		setTextColor(Color.WHITE);
	}
}
