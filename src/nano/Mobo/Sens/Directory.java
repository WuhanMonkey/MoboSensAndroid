package nano.Mobo.Sens;

import com.LiuLab.Mobo.Sens.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;

public class Directory extends Activity{
	@Override
	public void onCreate(Bundle savedInstanceState) {
		   if (android.os.Build.VERSION.SDK_INT > 9) {
			      StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			      StrictMode.setThreadPolicy(policy);
			    }

		super.onCreate(savedInstanceState);
		setContentView(R.layout.directory);
		}
	
	public void toSensing(View view){
		 Intent intent = new Intent(this, Sensing.class);
	        startActivity(intent);
	        finish();
	}
}