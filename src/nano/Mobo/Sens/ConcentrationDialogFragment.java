package nano.Mobo.Sens;

import com.LiuLab.Mobo.Sens.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

public class ConcentrationDialogFragment extends DialogFragment {
	private double concentration = 0.0;
	
	private String message = "";
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    concentration = getArguments().getInt("concentration");
	    if(concentration <0)
	    	message = "Concentration is too low to be sensed.";
	    else 
	    	message = "Concentration: "+Double.toString(concentration)+" ppm";
	}

	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
               .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	  //Sensing mSense = (Sensing) getActivity();
                	 // mSense.setAllow(true);
                       // FIRE ZE MISSILES!
                   }
               })
               ;
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
