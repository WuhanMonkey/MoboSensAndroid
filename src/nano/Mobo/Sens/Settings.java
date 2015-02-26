package nano.Mobo.Sens;

import com.darvds.ribbonmenu.RibbonMenuView;
import com.darvds.ribbonmenu.iRibbonMenuCallback;
import com.LiuLab.Mobo.Sens.R;

import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class Settings extends Activity implements iRibbonMenuCallback {
    private RibbonMenuView rbmView;

    @TargetApi(11)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final CheckBox checkBox = (CheckBox) findViewById(R.id.UseAddr);
        if (checkBox.isChecked()) {
            checkBox.setChecked(false);
        }
        checkBox.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                // is chkIos checked?
                if (((CheckBox) v).isChecked()) {

                    EditText temp = (EditText) findViewById(R.id.addr_field);
                    temp.setEnabled(true);

                } else {

                    EditText temp = (EditText) findViewById(R.id.addr_field);
                    temp.setEnabled(false);

                }

            }
        });

        rbmView = (RibbonMenuView) findViewById(R.id.ribbonMenuView1);
        rbmView.setMenuClickCallback(this);
        rbmView.setMenuItems(R.menu.ribbon_menu);

        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            rbmView.toggleMenu();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {

            rbmView.toggleMenu();

            return true;

        } else {

            switch (item.getItemId()) {
            case R.id.menu_save:
                Toast.makeText(Parameters.appContext, "Settings are saved!",
                        Toast.LENGTH_SHORT).show();
                return true;

            case R.id.menu_default:
                Toast.makeText(Parameters.appContext,
                        "Default settings are retrieved!", Toast.LENGTH_SHORT)
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
            }

        }
    }

    public void RibbonMenuItemClick(int itemId) {
        // TODO Auto-generated method stub
        Intent intent;
        switch (itemId) {

        case R.id.item_sensor:
            finish();
            intent = new Intent(this, Sensing.class);
            startActivityForResult(intent, 1);

            break;
/** changed for test version @2.23
        case R.id.item_map:
            finish();
            intent = new Intent(this, ViewMap.class);

            startActivityForResult(intent, 1);

            break;
		case R.id.item_param:
			finish();
			intent = new Intent(this, Parameters.class);
			startActivityForResult(intent, 1);
			break;
        case R.id.item_weather:
            finish();
            intent = new Intent(this, Weather.class);

            startActivityForResult(intent, 1);

            break;
            
        case R.id.item_impulse:
        	finish();
        	intent = new Intent(this, Impulse.class);
        	
        	startActivityForResult(intent, 1);
        	break;
  **/
        case R.id.item_exit:
            finishActivity(1);
            finish();
            break;

        default:
            // do nothing yourself

        }

    }
}
