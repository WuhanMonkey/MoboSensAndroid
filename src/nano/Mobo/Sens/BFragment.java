package nano.Mobo.Sens;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import com.LiuLab.Mobo.Sens.R;

public class BFragment extends Fragment {
    View storeViewId;
    int[] model;

    EditText time_data;
    EditText analysis_duration;

    private void updateCycle() {

        double percent = (double) model[3] / 100;
        time_data.setText(Integer.toString((int) (1+percent * 5)));

    }

    private void updateAnalysisDuration() {

        int power, res;
        if (model[2] == 0) {
            power = 0;

        } else {
            power = (int) Math.log10(model[2]);

        }
        res = (int) (30 * Math.pow(2, power));
        analysis_duration.setText(Integer.toString(res));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        model = new int[4];
        View v = inflater.inflate(R.layout.bfragment, container, false);
        storeViewId = v;
        time_data = (EditText) v.findViewById(R.id.bias_time_data);
        analysis_duration = (EditText) v.findViewById(R.id.analysis_duration);

        SeekBar time_bar = (SeekBar) v.findViewById(R.id.seekBar4);
        SeekBar analysis_duration_bar = (SeekBar) v.findViewById(R.id.seekBar5);

        analysis_duration_bar
                .setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

                    public void onProgressChanged(SeekBar seekBar,
                            int progress, boolean fromUser) {

                        if (fromUser) {
                            int[] fake_progress = new int[3];
                            for (int i = 0; i < 3; i++) {
                                fake_progress[i] = 50 * i;

                            }
                            if (fromUser) {
                                if (progress < 50) {

                                    seekBar.setProgress(fake_progress[0]);
                                    model[2] = fake_progress[0];
                                }

                                else if (progress >= 50 && progress < 100) {
                                    seekBar.setProgress(fake_progress[1]);
                                    model[2] = fake_progress[1];

                                }

                                else {

                                    seekBar.setProgress(fake_progress[2]);
                                    model[2] = fake_progress[2];

                                }

                                updateAnalysisDuration();
                            }

                        }

                    }

                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }

                });

        time_bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress,
                    boolean fromUser) {

                int[] fake_progress = new int[6];
                for (int i = 0; i < 6; i++) {
                    fake_progress[i] = 20 * i;

                }
                if (fromUser) {
                    if (progress < 20) {

                        seekBar.setProgress(fake_progress[0]);
                        model[3] = fake_progress[0];
                    }

                    else if (progress >= 20 && progress < 40) {
                        seekBar.setProgress(fake_progress[1]);
                        model[3] = fake_progress[1];

                    }

                    else if (progress >= 40 && progress < 60) {

                        seekBar.setProgress(fake_progress[2]);
                        model[3] = fake_progress[2];

                    }

                    else if (progress >= 60 && progress < 80) {

                        seekBar.setProgress(fake_progress[3]);
                        model[3] = fake_progress[3];

                    }

                    else if (progress >= 80 && progress < 100) {

                        seekBar.setProgress(fake_progress[4]);
                        model[3] = fake_progress[4];

                    }

                    else {

                        seekBar.setProgress(fake_progress[5]);
                        model[3] = fake_progress[5];

                    }

                    updateCycle();
                }

            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }

        });
        return v;
    }

}
