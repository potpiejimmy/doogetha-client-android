package com.doogetha.client.util;

import android.app.Activity;
import android.content.Intent;
import com.doogetha.client.android.R;

public class SlideActivity extends Activity {

	@Override
	public void finish() {
	    super.finish();
	    overridePendingTransition(R.anim.slide_still, R.anim.slide_out_right);
	}

	@Override
	public void startActivity(Intent intent) {
	    super.startActivity(intent);
	    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_still);
	}

	@Override
	public void startActivityForResult(Intent intent, int i) {
	    super.startActivityForResult(intent, i);
	    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_still);
	}
}
