package de.letsdoo.client.util;

import android.app.ListActivity;
import android.content.Intent;
import de.letsdoo.client.android.R;

public class SlideListActivity extends ListActivity {

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
