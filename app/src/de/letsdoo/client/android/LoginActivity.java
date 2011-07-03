package de.letsdoo.client.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class LoginActivity extends Activity implements OnClickListener {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.login);
        
    	Button loginbutton = (Button) findViewById(R.id.loginbutton);
    	loginbutton.setOnClickListener(this);
    }
    
	public void onClick(View view) {
		switch (view.getId())
		{
			case R.id.loginbutton:
				setResult(RESULT_OK);
				finish();
				break;
		}
	}

}
