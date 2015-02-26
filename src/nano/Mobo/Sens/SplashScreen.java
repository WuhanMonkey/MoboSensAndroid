package nano.Mobo.Sens;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import com.LiuLab.Mobo.Sens.R;

public class SplashScreen extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {

            e.printStackTrace();
        }

        Intent intent = new Intent(this, Directory.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_splash_screen, menu);
        return true;
    }
}
