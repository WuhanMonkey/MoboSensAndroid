package nano.Mobo.Sens;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import com.LiuLab.Mobo.Sens.R;

public class MoboSens extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobo_sens);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_mobo_sens, menu);
        return true;
    }

    public void startSensor(View view) {
        
        Intent intent = new Intent(this, Sensing.class);
       // intent.putExtra("CalibrationFlag", 0);
        startActivity(intent);

    }
    
    //Sensing for calibration
   /*  
      public void startCalibrate(View view) {

        Intent intent = new Intent(this, Sensing.class);
        intent.putExtra("CalibrationFlag", 1);
        startActivity(intent);

    }*/

    public void configureParameters(View view) {
        Intent intent = new Intent(this, Parameters.class);

        startActivity(intent);

    }

    public void viewMap(View view) {
        Intent intent = new Intent(this, ViewMap.class);
        startActivity(intent);

    }

    public void weatherInformation(View view) {

        Intent intent = new Intent(this, Weather.class);
        startActivity(intent);

    }

    public void configureSettings(View view) {

        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);

    }
    public void createImpulse(View view) {

        Intent intent = new Intent(this, Impulse.class);
        startActivity(intent);

    }

}
