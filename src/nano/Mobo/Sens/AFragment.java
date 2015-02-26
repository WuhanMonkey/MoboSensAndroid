package nano.Mobo.Sens;

import java.text.DecimalFormat;

import nano.Mobo.Sens.RangeSeekBar.OnRangeSeekBarChangeListener;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.LiuLab.Mobo.Sens.R;

public class AFragment extends Fragment implements OnItemSelectedListener {
    private Spinner SpinnerWaveType;
    View storeViewId;

    EditText amp_data;
    EditText sample_data;
    EditText freq_data;
    EditText amp_min_data;
    EditText amp_max_data;
    EditText freq_min_data;
    EditText freq_max_data;

    private double voltage_min = -1.6d;
    private int amp;
    private int amp_min;
    private int amp_max;
    private int sample_rate;
    private int freq;
    private int freq_min;
    private int freq_max;
    
    private double frequencyToVoltage(int freq)
    {
    	double ret = 3.62087d - 0.000686957d*freq;
    	  DecimalFormat twoDForm = new DecimalFormat("#.##");
          ret =  Double.valueOf(twoDForm.format(ret));
        return ret;
    	
    }
    
    
    
    private void hideGroupElements(View v, int labelId, int areaId, int barId) {

        TextView Label = (TextView) v.findViewById(labelId);
        EditText Area = (EditText) v.findViewById(areaId);
        SeekBar Bar = (SeekBar) v.findViewById(barId);
        Label.setVisibility(View.GONE);
        Area.setVisibility(View.GONE);
        Bar.setVisibility(View.GONE);

    }

    private void displayGroupElements(View v, int labelId, int areaId, int barId) {

        TextView Label = (TextView) v.findViewById(labelId);
        EditText Area = (EditText) v.findViewById(areaId);
        SeekBar Bar = (SeekBar) v.findViewById(barId);
        Label.setVisibility(View.VISIBLE);
        Area.setVisibility(View.VISIBLE);
        Bar.setVisibility(View.VISIBLE);

    }

    private void updateSamplingRate() {

        double percent;

        percent = (double) sample_rate / 100;

        sample_data.setText(Integer.toString((int) (percent * 36100.0) + 8000));

    }

    private void updateAmplitude() {
        double percent;

        percent = (double) amp / 100;

        amp_data.setText(Integer.toString((int) (percent * 1000) + 285));

    }

    private void updateFrequency() {

        double percent;

        percent = (double) freq / 100;

        freq_data.setText(Integer.toString((int) (percent * 9000) + 1000));

    }
    private void updateVoltage(){
    	
    	double percent;
    	percent = voltage_min;
    	double decimalTwo  = (percent * 0.0158) - 1.6;
    	DecimalFormat twoDForm = new DecimalFormat("#.##");
    	decimalTwo  =  Double.valueOf(twoDForm.format(decimalTwo));
    	freq_max_data.setText(Double.toString(decimalTwo));
    	//percent = (double) voltage/100;
    	
    	
    	
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.afragment, container, false);
        storeViewId = v;

        /*
         * this is a nice feature but with less support and reliabiility
         * consider this later RangeSeekBar<Integer> seekBar = new
         * RangeSeekBar<Integer>(20, 75, getActivity ());
         * seekBar.setOnRangeSeekBarChangeListener(new
         * OnRangeSeekBarChangeListener<Integer>() { public void
         * onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue,
         * Integer maxValue) { // handle changed range values // Log.i("TAG",
         * "User selected new range values: MIN=" + minValue + ", MAX=" +
         * maxValue); } });
         * 
         * 
         * // add RangeSeekBar to pre-defined layout ViewGroup layout =
         * (ViewGroup)v.findViewById(R.id.afrag_layout);
         * layout.addView(seekBar);
         */

        amp_data = (EditText) v.findViewById(R.id.bias_amp_data);
        freq_data = (EditText) v.findViewById(R.id.bias_freq_data);
        sample_data = (EditText) v.findViewById(R.id.bias_sample_data);
        amp_min_data = (EditText) v.findViewById(R.id.bias_ampmin_data);
        amp_max_data = (EditText) v.findViewById(R.id.bias_ampmax_data);
        freq_min_data = (EditText) v.findViewById(R.id.freq_min_data);
        freq_max_data = (EditText) v.findViewById(R.id.freq_max_data);

        SeekBar amp_bar = (SeekBar) v.findViewById(R.id.seekBar1);
        SeekBar freq_bar = (SeekBar) v.findViewById(R.id.seekBar2);
        SeekBar sample_bar = (SeekBar) v.findViewById(R.id.seekBar3);
        SeekBar voltage_min_bar = (SeekBar) v.findViewById(R.id.seekBar_freq_max);
        
        voltage_min_bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress,
                    boolean fromUser) {

                voltage_min = progress;
           //     updateVoltage();


            }

            public void onStartTrackingTouch(SeekBar seekBar) {


            }

            public void onStopTrackingTouch(SeekBar seekBar) {


            }

        });

        
        amp_bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress,
                    boolean fromUser) {

                amp = progress;
                updateAmplitude();


            }

            public void onStartTrackingTouch(SeekBar seekBar) {


            }

            public void onStopTrackingTouch(SeekBar seekBar) {


            }

        });

        freq_bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress,
                    boolean fromUser) {

                if (fromUser) {
                    freq = progress;
                    updateFrequency();
                }

            }

            public void onStartTrackingTouch(SeekBar seekBar) {


            }

            public void onStopTrackingTouch(SeekBar seekBar) {


            }

        });

        sample_bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress,
                    boolean fromUser) {

                if (fromUser) {
                    sample_rate = progress;
                    updateSamplingRate();
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });
        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(Parameters.appContext);

        int retval;

        try {

            retval = settings.getInt("MainAmplitude", 0);

        } catch (ClassCastException e) {

            Editor editor = settings.edit();
            editor.putInt("MainAmplitude", 285);
            editor.commit();
            retval = settings.getInt("MainAmplitude", 0);

        }
        amp_data.setText(String.valueOf(retval));

        try {

            retval = settings.getInt("MainFrequency", 0);

        } catch (ClassCastException e) {

            Editor editor = settings.edit();
            editor.putInt("MainFrequency", 1000);
            editor.commit();
            retval = settings.getInt("MainFrequency", 1000);

        }
        freq_data.setText(String.valueOf(retval));

        try {

            retval = settings.getInt("SamplingRate", 0);

        } catch (ClassCastException e) {

            Editor editor = settings.edit();
            editor.putInt("SamplingRate", 16000);
            editor.commit();
            retval = settings.getInt("SamplingRate", 0);

        }
        sample_data.setText(String.valueOf(retval));

        try {

            retval = settings.getInt("MinAmplitude", 0);

        } catch (ClassCastException e) {

            Editor editor = settings.edit();
            editor.putInt("MinAmplitude", 0);
            editor.commit();
            retval = settings.getInt("MinAmplitude", 0);

        }
        amp_min_data.setText(String.valueOf(retval));

        try {

            retval = settings.getInt("MaxAmplitude", 0);

        } catch (ClassCastException e) {

            Editor editor = settings.edit();
            editor.putInt("MaxAmplitude", 0);
            editor.commit();
            retval = settings.getInt("MaxAmplitude", 0);

        }
        amp_max_data.setText(String.valueOf(retval));

        try {

            retval = settings.getInt("MinFrequency", 5100);

        } catch (ClassCastException e) {

            Editor editor = settings.edit();
            editor.putInt("MinFrequency", 5100);
            editor.commit();
            retval = settings.getInt("MinFrequency", 5100);
    
        }
       // freq_min_data.setText(String.valueOf(frequencyToVoltage(retval)));
        freq_min_data.setText(String.valueOf(retval));
      //  freq_min_data.setKeyListener(null);

        try {

            retval = settings.getInt("MaxFrequency", 7600);

        } catch (ClassCastException e) {

            Editor editor = settings.edit();
            editor.putInt("MaxFrequency", 7600);
            editor.commit();
            retval = settings.getInt("MaxFrequency", 7600);

        }
      //  freq_max_data.setText(String.valueOf(frequencyToVoltage(retval)));
        freq_max_data.setText(String.valueOf(retval));
        int progress = (int)((frequencyToVoltage(retval)+1.6)/0.0158);
        voltage_min_bar.setProgress(progress);

        return v;
    }

    public void onItemSelected(AdapterView<?> parentView,
            View selectedItemView, int position, long id) {
    	
        switch (position) {
        case 0:

            displayGroupElements(storeViewId, R.id.bias_amp_label,
                    R.id.bias_amp_data, R.id.seekBar1);
            displayGroupElements(storeViewId, R.id.bias_freq_label,
                    R.id.bias_freq_data, R.id.seekBar2);

            hideGroupElements(storeViewId, R.id.bias_ampmax_label,
                    R.id.bias_ampmax_data, R.id.seekBar_amp_max);
            hideGroupElements(storeViewId, R.id.freq_min_label,
                    R.id.freq_min_data, R.id.seekBar_freq_min);
            hideGroupElements(storeViewId, R.id.freq_max_label,
                    R.id.freq_max_data, R.id.seekBar_freq_max);
            hideGroupElements(storeViewId, R.id.modulation_period_label,
                    R.id.modulation_period_data, R.id.modulation_period_seekbar);

            break;

        case 1:

            displayGroupElements(storeViewId, R.id.bias_ampmin_label,
                    R.id.bias_ampmin_data, R.id.seekBar_amp_min);
            displayGroupElements(storeViewId, R.id.bias_ampmax_label,
                    R.id.bias_ampmax_data, R.id.seekBar_amp_max);

            hideGroupElements(storeViewId, R.id.bias_amp_label,
                    R.id.bias_amp_data, R.id.seekBar1);
            hideGroupElements(storeViewId, R.id.freq_min_label,
                    R.id.freq_min_data, R.id.seekBar_freq_min);
            hideGroupElements(storeViewId, R.id.freq_max_label,
                    R.id.freq_max_data, R.id.seekBar_freq_max);
            displayGroupElements(storeViewId, R.id.bias_freq_label,
                    R.id.bias_freq_data, R.id.seekBar2);
            hideGroupElements(storeViewId, R.id.modulation_period_label,
                    R.id.modulation_period_data, R.id.modulation_period_seekbar);

            break;
        case 2:

            displayGroupElements(storeViewId, R.id.freq_min_label,
                    R.id.freq_min_data, R.id.seekBar_freq_min);
            displayGroupElements(storeViewId, R.id.freq_max_label,
                    R.id.freq_max_data, R.id.seekBar_freq_max);
            displayGroupElements(storeViewId, R.id.bias_amp_label,
                    R.id.bias_amp_data, R.id.seekBar1);
            hideGroupElements(storeViewId, R.id.bias_freq_label,
                    R.id.bias_freq_data, R.id.seekBar2);
            hideGroupElements(storeViewId, R.id.bias_ampmin_label,
                    R.id.bias_ampmin_data, R.id.seekBar_amp_min);
            hideGroupElements(storeViewId, R.id.bias_ampmax_label,
                    R.id.bias_ampmax_data, R.id.seekBar_amp_max);
            displayGroupElements(storeViewId, R.id.modulation_period_label,
                    R.id.modulation_period_data, R.id.modulation_period_seekbar);
            break;

        case 3:

            displayGroupElements(storeViewId, R.id.modulation_period_label,
                    R.id.modulation_period_data, R.id.modulation_period_seekbar);
            displayGroupElements(storeViewId, R.id.bias_amp_label,
                    R.id.bias_amp_data, R.id.seekBar1);
            displayGroupElements(storeViewId, R.id.bias_freq_label,
                    R.id.bias_freq_data, R.id.seekBar2);
            hideGroupElements(storeViewId, R.id.bias_ampmin_label,
                    R.id.bias_ampmin_data, R.id.seekBar_amp_min);
            hideGroupElements(storeViewId, R.id.bias_ampmax_label,
                    R.id.bias_ampmax_data, R.id.seekBar_amp_max);
            hideGroupElements(storeViewId, R.id.freq_min_label,
                    R.id.freq_min_data, R.id.seekBar_freq_min);
            hideGroupElements(storeViewId, R.id.freq_max_label,
                    R.id.freq_max_data, R.id.seekBar_freq_max);

            break;

        default:

        }

    }

    public void onNothingSelected(AdapterView<?> parentView) {

    }

}
