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
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import twitter4j.Twitter;
import twitter4j.auth.AccessToken;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.LineGraphView;
//import com.microsoft.windowsazure.mobileservices.*;
import com.LiuLab.Mobo.Sens.R;

import nano.Mobo.Sens.MyLocation.LocationResult;
import nano.Mobo.Sens.Parameters.MyTabsListener;
import nano.Mobo.Sens.TwitterApp.TwDialogListener;
import android.view.MenuItem;

import com.darvds.ribbonmenu.RibbonMenuView;
import com.darvds.ribbonmenu.iRibbonMenuCallback;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.AudioTrack.OnPlaybackPositionUpdateListener;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Impulse extends Activity implements iRibbonMenuCallback {
	private LinearLayout mLayout = null;
	private boolean allow = false;
	AudioManager am = null;
	PowerManager pm = null;
	PowerManager.WakeLock wl = null;
	AudioTrack playTrack = null;
	short[] waveform = null;
	short[] recAudioPCM = null;
	
	private CheckBox Zero = null;
	private CheckBox One = null;
	private CheckBox Two = null;
	private CheckBox Three = null;

	
	private int stabCycles = 0;
	private int ShortsLeft = 0;

	
		
	int bytesRecorded = 0;
	private Thread startPlayThread = null;
	private double samplingRate = 44100;											
	private double time = 12;
	private static int Fixed_Freq = 1000;
	private static int Impulse_Freq = 5000;
	private int mainAmp = 13311; 
	boolean m_stop = true;
	public static Context appContext = null;
    private RibbonMenuView rbmView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_impulse);
        appContext = getApplicationContext();
        rbmView = (RibbonMenuView) findViewById(R.id.ribbonMenuView1);
        rbmView.setMenuClickCallback(this);
        rbmView.setMenuItems(R.menu.ribbon_menu);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        
        if (android.os.Build.VERSION.SDK_INT > 9) {
        	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		      StrictMode.setThreadPolicy(policy);
		    }
        
       AudioManager amanager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		amanager.setStreamVolume(AudioManager.STREAM_MUSIC,
				amanager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
				AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND);
          
    }
    
    
    
    
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {

            rbmView.toggleMenu();

            return true;

        } else {
            return super.onOptionsItemSelected(item);
        }
    }
    
    public void start(View view) {
    	Log.d("here","start");
    	int channelConfiguration = AudioFormat.CHANNEL_IN_MONO;
		int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
		
		m_stop = false;

		Zero = (CheckBox) findViewById(R.id.Zero);
		One = (CheckBox) findViewById(R.id.One);
		Two = (CheckBox) findViewById(R.id.Two);
		Three = (CheckBox) findViewById(R.id.Three);

		if (pm == null) {
			pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

		}
		if (am == null) {
			am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		}
		if (wl == null) {
			wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Tag");
		}
		
		
		final int PlaySize = AudioTrack.getMinBufferSize((int) samplingRate,
				AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
		
		playTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
				(int) samplingRate, AudioFormat.CHANNEL_OUT_MONO,
				AudioFormat.ENCODING_PCM_16BIT, PlaySize,
				AudioTrack.MODE_STREAM);
		playTrack.play();
		Log.d("here","debug");
	
		if(Zero.isChecked()){
			Log.d("here","Zero");
			waveform = generateSineWavefreq(Fixed_Freq, Impulse_Freq, time, samplingRate,
					mainAmp, 0);
			
		
		
		}
		else if(One.isChecked()){
			Log.d("here","One");
			waveform = generateSineWavefreq(Fixed_Freq, Impulse_Freq, time, samplingRate,
					mainAmp, 1);
			
			
			
		}
		else if(Two.isChecked()){
			Log.d("here","Two");
			waveform = generateSineWavefreq(Fixed_Freq, Impulse_Freq, time, samplingRate,
					mainAmp, 2);
			
			
			
		}
		else if(Three.isChecked()){
			Log.d("here","Three");
			waveform = generateSineWavefreq(Fixed_Freq, Impulse_Freq, time, samplingRate,
					mainAmp, 3);
			
			
			
			
		}
		

		int bufSize;
		switch ((int) samplingRate) {
		case 44100:
			bufSize = (int) (samplingRate / 7);

			break;

		case 16000:

			bufSize = (int) (samplingRate / 8);
			break;

		case 8000:
			bufSize = (int) (samplingRate / 8);
			break;

		default:

			bufSize = (int) (samplingRate / 7);

		}

		if (bufSize < PlaySize / 2)
			bufSize = PlaySize / 2;

		while (stabCycles > -1) {
			
			ShortsLeft = waveform.length;
			while (m_stop == false && ShortsLeft >= bufSize) {
				int ret = playTrack.write(waveform, waveform.length
						- ShortsLeft, bufSize);
				ShortsLeft -= bufSize;
			}

			stabCycles--;
		}
		playTrack.pause();
		playTrack.flush();	
		//playTrack.write(waveform, 0, waveform.length);
		playTrack.stop();
		playTrack.release();
		playTrack = null;
		
    }
    
    
    
private static short[] generateSineWavefreq(int Fixed_Freq, int Impulse_Freq,
			double seconds, double fs, double Amax, int mode) {
	Log.d("here","generate impulse");
	short[] imp =new short[(int) (seconds * fs)];
	double Fixed = (double) (fs / Fixed_Freq);	
	double Impulse = (double) (fs/Impulse_Freq);
	
	switch (mode){
		case 0:
			for(int i =0; i<(seconds*fs); i++ ){
				double angle = (2.0 * Math.PI * i) / Fixed;
				imp[i] = (short) (Math.sin(angle) * Amax);				
			}

			return imp;
			
			
		case 1:
			for(int i =0; i<(seconds*fs/12); i++ ){
				double angle = (2.0 * Math.PI * i) / Fixed;
				imp[i] = (short) (Math.sin(angle) * Amax);				
			}
			
			for(int i =(int)(seconds*fs/12); i<(seconds*fs/6); i++ ){
				double angle = (2.0 * Math.PI * i) / Impulse;
				imp[i] = (short) (Math.sin(angle) * Amax);				
			}
			
			for(int i =(int)(seconds*fs/6); i<(seconds*fs); i++ ){
				double angle = (2.0 * Math.PI * i) / Fixed;
				imp[i] = (short) (Math.sin(angle) * Amax);				
			}			
			return imp;
			
		case 2:
			for(int i =0; i<(seconds*fs/12); i++ ){
				double angle = (2.0 * Math.PI * i) / Fixed;
				imp[i] = (short) (Math.sin(angle) * Amax);				
			}
			
			for(int i =(int)(seconds*fs/12); i<(seconds*fs/6); i++ ){
				double angle = (2.0 * Math.PI * i) / Impulse;
				imp[i] = (short) (Math.sin(angle) * Amax);				
			}
		    
			for(int i =(int)(seconds*fs/6); i<(seconds*fs/4); i++ ){
				double angle = (2.0 * Math.PI * i) / Fixed;
				imp[i] = (short) (Math.sin(angle) * Amax);				
			}
			
			for(int i =(int)(seconds*fs/4); i<(seconds*fs/3); i++ ){
				double angle = (2.0 * Math.PI * i) / Impulse;
				imp[i] = (short) (Math.sin(angle) * Amax);				
			}
			
			for(int i =(int)(seconds*fs/3); i<(seconds*fs); i++ ){
				double angle = (2.0 * Math.PI * i) / Fixed;
				imp[i] = (short) (Math.sin(angle) * Amax);				
			}	
			
			return imp;
			
		case 3:
			for(int i =0; i<(seconds*fs/12); i++ ){
				double angle = (2.0 * Math.PI * i) / Fixed;
				imp[i] = (short) (Math.sin(angle) * Amax);				
			}
			
			for(int i =(int)(seconds*fs/12); i<(seconds*fs/6); i++ ){
				double angle = (2.0 * Math.PI * i) / Impulse;
				imp[i] = (short) (Math.sin(angle) * Amax);				
			}
		    
			for(int i =(int)(seconds*fs/6); i<(seconds*fs/4); i++ ){
				double angle = (2.0 * Math.PI * i) / Fixed;
				imp[i] = (short) (Math.sin(angle) * Amax);				
			}
			
			for(int i =(int)(seconds*fs/4); i<(seconds*fs/3); i++ ){
				double angle = (2.0 * Math.PI * i) / Impulse;
				imp[i] = (short) (Math.sin(angle) * Amax);				
			}
			for(int i =(int)(seconds*fs/3); i<(5*seconds*fs/12); i++ ){
				double angle = (2.0 * Math.PI * i) / Fixed;
				imp[i] = (short) (Math.sin(angle) * Amax);				
			}
			
			for(int i =(int)(5*seconds*fs/12); i<(seconds*fs/2); i++ ){
				double angle = (2.0 * Math.PI * i) / Impulse;
				imp[i] = (short) (Math.sin(angle) * Amax);				
			}
			for(int i =(int)(seconds*fs/2); i<(seconds*fs); i++ ){
				double angle = (2.0 * Math.PI * i) / Fixed;
				imp[i] = (short) (Math.sin(angle) * Amax);				
			}
			
			
			return imp;
			
		default:
			for(int i =0; i<(seconds*fs); i++ ){
				double angle = (2.0 * Math.PI * i) / Fixed;
				imp[i] = (short) (Math.sin(angle) * Amax);				
			}
			return imp;
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
    case R.id.item_param:
		finish();
		intent = new Intent(this, Parameters.class);
		startActivityForResult(intent, 1);
		break;
		
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
}