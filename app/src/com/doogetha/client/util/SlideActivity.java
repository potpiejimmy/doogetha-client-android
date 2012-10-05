package com.doogetha.client.util;

import android.app.Activity;
import android.content.Intent;
import com.doogetha.client.android.R;

public class SlideActivity extends Activity {

	private int slideInAnim = R.anim.slide_in_right;
	private int slideOutAnim = R.anim.slide_out_right;
	
	@Override
	public void finish() {
	    super.finish();
	    overridePendingTransition(R.anim.slide_still, slideOutAnim);
	}

	@Override
	public void startActivity(Intent intent) {
	    super.startActivity(intent);
	    overridePendingTransition(slideInAnim, R.anim.slide_still);
	}

	@Override
	public void startActivityForResult(Intent intent, int i) {
	    super.startActivityForResult(intent, i);
	    overridePendingTransition(slideInAnim, R.anim.slide_still);
	}

	public void setSlideInAnim(int slideInAnim) {
		this.slideInAnim = slideInAnim;
	}

	public void setSlideOutAnim(int slideOutAnim) {
		this.slideOutAnim = slideOutAnim;
	}
}
