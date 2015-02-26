package nano.Mobo.Sens;

import com.darvds.ribbonmenu.RibbonMenuView;
import com.darvds.ribbonmenu.iRibbonMenuCallback;
import com.LiuLab.Mobo.Sens.R;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

public class Parameters extends Activity implements iRibbonMenuCallback {

    private CharSequence selectedTab = "Drive Wave";
    public static Context appContext = null;
    private RibbonMenuView rbmView;
    private int voltageToFrequency(double voltage)
    {
    	int ret = (int)(5270.89d - 1455.7d*voltage); 
    	return ret;
    	
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parameters);
        appContext = getApplicationContext();
        rbmView = (RibbonMenuView) findViewById(R.id.ribbonMenuView1);
        rbmView.setMenuClickCallback(this);
        rbmView.setMenuItems(R.menu.ribbon_menu);

        ActionBar actionbar = getActionBar();

        actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.Tab PlayerTab = actionbar.newTab().setText("Drive Wave");
        ActionBar.Tab StationsTab = actionbar.newTab().setText("Time");
        ActionBar.Tab AnalysisTab = actionbar.newTab().setText("Analysis");

        Fragment PlayerFragment = new AFragment();
        Fragment StationsFragment = new BFragment();
        Fragment AnalysisFragment = new CFragment();

        PlayerTab.setTabListener(new MyTabsListener(PlayerFragment));
        StationsTab.setTabListener(new MyTabsListener(StationsFragment));
        AnalysisTab.setTabListener(new MyTabsListener(AnalysisFragment));

        actionbar.addTab(PlayerTab);
        actionbar.addTab(StationsTab);
        actionbar.addTab(AnalysisTab);
        actionbar.setDisplayHomeAsUpEnabled(true);

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
        getMenuInflater().inflate(R.menu.activity_parameters, menu);
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
                SharedPreferences settings = PreferenceManager
                        .getDefaultSharedPreferences(appContext);
                Editor editor = settings.edit();

                if (selectedTab == "Drive Wave") {

                    editor.putInt(
                            "MainFrequency",
                            Integer.valueOf(((EditText) findViewById(R.id.bias_freq_data))
                                    .getText().toString()));
                    editor.putInt(
                            "MainAmplitude",
                            Integer.valueOf(((EditText) findViewById(R.id.bias_amp_data))
                                    .getText().toString()));
//                    editor.putInt(
//                            "MaxFrequency",
//                            voltageToFrequency( Double.valueOf(((EditText) findViewById(R.id.freq_max_data))
//                                    .getText().toString())));
                    editor.putInt(
                            "MaxFrequency",
                          Integer.valueOf(((EditText) findViewById(R.id.freq_max_data))
                                    .getText().toString()));
/*                    editor.putInt(
                            "MinFrequency",
                            voltageToFrequency(Double.valueOf(((EditText) findViewById(R.id.freq_min_data))
                                    .getText().toString())));*/
                    editor.putInt(
                            "MinFrequency",
                          Integer.valueOf(((EditText) findViewById(R.id.freq_min_data))
                                    .getText().toString()));
                    editor.putInt(
                            "MaxAmplitude",
                            Integer.valueOf(((EditText) findViewById(R.id.bias_ampmax_data))
                                    .getText().toString()));
                    editor.putInt(
                            "MinAmplitude",
                            Integer.valueOf(((EditText) findViewById(R.id.bias_ampmin_data))
                                    .getText().toString()));
                    editor.putInt(
                            "SamplingRate",
                            Integer.valueOf(((EditText) findViewById(R.id.bias_sample_data))
                                    .getText().toString()));
                } else if (selectedTab == "Time") {

                    editor.putInt(
                            "StableCycles",
                            Integer.valueOf(((EditText) findViewById(R.id.bias_time_data))
                                    .getText().toString()));
                    editor.putInt(
                            "Duration",
                            Integer.valueOf(((EditText) findViewById(R.id.analysis_duration))
                                    .getText().toString()));

                } else if (selectedTab == "Analysis") {

                    RadioGroup checkedRadioButton = (RadioGroup) findViewById(R.id.sampling_rate_radio_group);
                    int idx = checkedRadioButton.getCheckedRadioButtonId();

                    if (idx == -1) {

                    }

                    else {

                        editor.putInt("DownSamplingRate", idx);
                    }

                    CheckBox checkb = (CheckBox) findViewById(R.id.if_envelope);
                    if (checkb.isChecked()) {
                        editor.putInt("Envelope", 1);

                    } else {

                        editor.putInt("Envelope", 0);

                    }

                } else {

                }

                editor.commit();

                Toast.makeText(Parameters.appContext, "Parameters are saved!",
                        Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);

            case R.id.menu_default:

                Toast.makeText(Parameters.appContext,
                        "Default parameters are retrieved!", Toast.LENGTH_SHORT)
                        .show();
                return super.onOptionsItemSelected(item);
            default:
                return super.onOptionsItemSelected(item);
            }
        }
    }

    public void RibbonMenuItemClick(int itemId) {

        Intent intent;
        switch (itemId) {

        case R.id.item_sensor:
            finish();
            intent = new Intent(this, Sensing.class);
            startActivityForResult(intent, 1);

            break;
/** Changed for test version @2.23
        case R.id.item_map:
            finish();
            intent = new Intent(this, ViewMap.class);

            startActivityForResult(intent, 1);

            break;

        case R.id.item_weather:
            finish();
            intent = new Intent(this, Weather.class);

            startActivityForResult(intent, 1);

            break;

        case R.id.item_settings:
            finish();
            intent = new Intent(this, Settings.class);

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

        }
    }

    class MyTabsListener implements ActionBar.TabListener {
        public Fragment fragment;

        public MyTabsListener(Fragment fragment) {
            this.fragment = fragment;
        }

        public void onTabReselected(Tab tab, FragmentTransaction ft) {

        }

        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            selectedTab = tab.getText();

            ft.replace(R.id.fragment_container, fragment);

        }

        public void onTabUnselected(Tab tab, FragmentTransaction ft) {

            SharedPreferences settings = PreferenceManager
                    .getDefaultSharedPreferences(appContext);
            Editor editor = settings.edit();
            CharSequence st = tab.getText();

            if (st == "Drive Wave") {

                editor.putInt("MainFrequency", Integer
                        .valueOf(((EditText) findViewById(R.id.bias_freq_data))
                                .getText().toString()));
                editor.putInt("MainAmplitude", Integer
                        .valueOf(((EditText) findViewById(R.id.bias_amp_data))
                                .getText().toString()));
/*                editor.putInt("MaxFrequency", voltageToFrequency(Double
                        .valueOf(((EditText) findViewById(R.id.freq_max_data))
                                .getText().toString())));*/
                editor.putInt("MaxFrequency", Integer
                        .valueOf(((EditText) findViewById(R.id.freq_max_data))
                                .getText().toString()));
/*                editor.putInt("MinFrequency", voltageToFrequency(Double
                        .valueOf(((EditText) findViewById(R.id.freq_min_data))
                                .getText().toString())));*/
                editor.putInt("MinFrequency", Integer
                        .valueOf(((EditText) findViewById(R.id.freq_min_data))
                                .getText().toString()));
                editor.putInt(
                        "MaxAmplitude",
                        Integer.valueOf(((EditText) findViewById(R.id.bias_ampmax_data))
                                .getText().toString()));
                editor.putInt(
                        "MinAmplitude",
                        Integer.valueOf(((EditText) findViewById(R.id.bias_ampmin_data))
                                .getText().toString()));
                editor.putInt(
                        "SamplingRate",
                        Integer.valueOf(((EditText) findViewById(R.id.bias_sample_data))
                                .getText().toString()));

            } else if (st == "Time") {

                editor.putInt("StableCycles", Integer
                        .valueOf(((EditText) findViewById(R.id.bias_time_data))
                                .getText().toString()));

                editor.putInt(
                        "Duration",
                        Integer.valueOf(((EditText) findViewById(R.id.analysis_duration))
                                .getText().toString()));

            }

            else if (st == "Analysis") {

                RadioGroup checkedRadioButton = (RadioGroup) findViewById(R.id.sampling_rate_radio_group);
                int idx = checkedRadioButton.getCheckedRadioButtonId();

                if (idx == -1) {

                }

                else {

                    editor.putInt("DownSamplingRate", idx);
                }

                CheckBox checkb = (CheckBox) findViewById(R.id.if_envelope);
                if (checkb.isChecked()) {
                    editor.putInt("Envelope", 1);

                } else {

                    editor.putInt("Envelope", 0);

                }

            }

            else {

            }
            editor.commit();

            ft.remove(fragment);
        }
    }
}
