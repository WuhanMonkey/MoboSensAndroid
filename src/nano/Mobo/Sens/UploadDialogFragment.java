package nano.Mobo.Sens;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.LiuLab.Mobo.Sens.R;

public class UploadDialogFragment extends DialogFragment {

	private double concentration = 0.0;
	private long FinalA =0;
	private long FinalB =0;
	private double peak =0.0;
	private String message = "";
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    concentration = getArguments().getDouble("Concentration");
	    FinalA = getArguments().getLong("FinalA");
	    FinalB = getArguments().getLong("FinalB");
	    peak = getArguments().getDouble("Peak");

	    if(concentration <0)
	    	message = "The concentration is too low or redo the test\n";
	    else 
	    	message = "Concentration: "+Double.toString(concentration)+" ppm\n" + "Fa: "+ Long.toString(FinalA)+ "Hz (for Calibration Only)\n" + "Fb: "+ Long.toString(FinalB)+ "Hz(for Calibration Only)\n"+"Peak:"+Double.toString(peak)+"Hz\n";
	    
	}
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message+getString(R.string.dialog_fire_upload))
               .setPositiveButton(R.string.fire, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // FIRE ZE MISSILES!
                	   FireMissilesDialogFragment mFire = new FireMissilesDialogFragment();
               		   mFire.show(getFragmentManager(), "Data Contribution");
                   }
               })
               .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // User cancelled the dialog
                   }
               });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}