package nano.Mobo.Sens;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
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
import android.app.ProgressDialog;
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

public class Sensing extends android.support.v4.app.FragmentActivity implements iRibbonMenuCallback {
	//final String CSERVER =  "http://junleqian.com:8080/items/";
	private TwitterApp mTwitter;
	private final int START_BOUND = 4266;
	private int algoType = 0;
	private final int SENSING_BOUND = 4399;
	private CheckBox pinhole = null;
	private final int SINGLE_POINT_INDEX = 4472;
	private static String TWITTER_CONSUMER_KEY = "dWpqGG7GJnpgZbN3g";
    private static String TWITTER_CONSUMER_SECRET = "mXWGCr8XQC8du6B3nzuBDlGGTiN9jVO7wz7umMbJQ";
    // Preference Constants
    String TWITTER_ACCESS_TOKEN ="";
    String TWITTER_ACCESS_TOKEN_SECRET = "";
	//private MobileServiceClient mClient;
	private LinearLayout mLayout = null;
	private boolean allow = false;
	private GraphView mGraphView;
	// private ChartSurfaceView ChartSurface = null;
	private RibbonMenuView rbmView;
	private AudioRecord recorder = null;
	AudioManager am = null;
	PowerManager pm = null;
	PowerManager.WakeLock wl = null;
	AudioTrack playTrack = null;
	short[] waveform = null;
	//int windowSize = 882;
	//int bufferSize = 4410;
	short[] recAudioPCM = null;
	ArrayList<Double> windowFreqList = new ArrayList<Double>(0);
	ArrayList<GraphViewData> mGraphViewDataList = new ArrayList<GraphViewData>(
			0);
	GraphViewData[] mGraphViewArray;
	private static final long MIN_TIME = 0;
	private static final float MIN_DISTANCE = 0;
	private double concentration = 0.000;
	private short RecAmpMinimum = 32767;

	private MyLocation myLocation = new MyLocation();

	int bytesRecorded = 0;
	private Thread startRecThread = null;
	private Thread startPlayThread = null;
    private Thread progressbar = null;
    private boolean progressbar_flag = true;
	private double samplingRate = 44100;
	public final static int eyeSize = 44100 * 20 / 1000;
	public final static int windowUpperBound = 5000;
	public final static int windowLowerBound = -5000;

	public final static int windowSkipSize = 500;
	private int lastZoneArea = 0;
	private int lastLogicalStatus = 0;
	private int numberOfLogicalOnes = 0;
	private double recordSamplingRate = 44100; // 8000 Hz for temporary use,
												// 44100 work for any
												// device(guaranteed by API)
	private double time = 60;
	private ProgressDialog progress;

	private int stableCycles = 4;

	private enum SigType {
		SINE, TRIANGULAR, FREQ_CHANGE, TRIANGULAR_MODULATION,CALIBRATION, SIN_FIXED
	};

	SigType SigCat = SigType.FREQ_CHANGE;
	private int mainFreq = 1000;
	private int stabCycles = 0; // 0 is only for demo
	private int triFreq = 50;
	private int mainAmp = 13311; 
	private int Imp_MaxAmp = 900;
	private int minAmp = 0;
	private int maxAmp = (int)(32767*0.8);
	private int minFreq = 5100;
	private int maxFreq = 7600;
	private int period = 30;
	private int perioddefault = 56;
	private int ShortsLeft = 0;
	//calibration frequency	
		private static int []CalibrationFreq= {5000,5500,7400,7500};
		private static int Fixed_Freq = 100;
		
		private double FreqA = 0;
		private double FreqB = 0;
		private double FreqC = 0;
		private double FreqD = 0;
		private CheckBox CalibrationFlag = null;
		private long FinalA = 0;
		private long FinalB = 0;
		private double peak = 0;
		
		private CheckBox SinFlag = null;
		private short[] FirstBuffer = new short [44100];
		private short[] SecondBuffer = new short[44100];
		private boolean sin_switch_flag = true;
	// private int offset = 0;
	private double currLong = 0.00;
	private double currLat = 0.00;
	boolean m_stop = true;
	private Writer writer;
	public void setAllow(boolean in)
	{
		
		allow = in;
		
	}
	
	
	AudioTrack.OnPlaybackPositionUpdateListener playbackPositionListener = new AudioTrack.OnPlaybackPositionUpdateListener() {

		public void onPeriodicNotification(AudioTrack track) {

		}

		public TwitterApp getMyTwitter()
		{
			return mTwitter;
			
		}
		
		public void onMarkerReached(AudioTrack track) {

			playTrack.flush();
			playTrack.stop();
			playTrack.release();
			playTrack = null;
			stopRecording();
			Button handle = (Button) findViewById(R.id.inner_stop);

			handle.setVisibility(View.INVISIBLE);

			handle = (Button) findViewById(R.id.inner_twitter);
			handle.setVisibility(View.VISIBLE);

			TextView StatusContent = (TextView) findViewById(R.id.status_content);
			TextView StatusDetails = (TextView) findViewById(R.id.status_details);
			StatusContent.setText("Send");
			StatusDetails.setText("Press Cloud button to send data on Cloud.");

		}
	};

	public LocationResult locationResult = new LocationResult() {

		@Override
		public void gotLocation(Location location) {
			// TODO Auto-generated method stub
			if (location != null) {

				currLong = location.getLongitude();
				currLat = location.getLatitude();

				String strloc = currLat + "," + currLong;

			}
		}
	};

	private void findCurrentLocation() {

		myLocation.getLocation(this, locationResult);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		   if (android.os.Build.VERSION.SDK_INT > 9) {
			      StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			      StrictMode.setThreadPolicy(policy);
			    }

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sensing);
		progress = new ProgressDialog(this);
		
		//get calibrationflag
		//Bundle extras = getIntent().getExtras();
		//CalibrationFlag = extras.getInt("CalibrationFlag");
		//Log.d("CaliTag", "CalibrationFlag is : " + Integer.toString(CalibrationFlag))
				

		
		rbmView = (RibbonMenuView) findViewById(R.id.ribbonMenuView1);
		rbmView.setMenuClickCallback(this);
		rbmView.setMenuItems(R.menu.ribbon_menu);
		// ChartSurface = (ChartSurfaceView) findViewById(R.id.chart_surface);
		mLayout = (LinearLayout) findViewById(R.id.chart_surface);
		mGraphView = new LineGraphView(this, "Frequency");
		mGraphView.setViewPort(2, 100);
		mGraphView.setScrollable(true);
		mGraphView.setScalable(true);
		mGraphView.setShowLegend(true);
		mLayout.addView(mGraphView);

		pinhole = (CheckBox) findViewById(R.id.checkBox1);
		CalibrationFlag = (CheckBox) findViewById(R.id.checkBoxCalibrate);
		SinFlag =  (CheckBox) findViewById(R.id.checkBoxSine);
		Log.d("here","here1");
		// short [] test_array = generateSineWavefreq(1000.0,
		// 56.0, 44100.0, 1000, -1000);
		// GraphViewData [] test_data = new GraphViewData[1000];
		// for(int i = 0; i<1000; i++)
		// {
		// test_data[i] = new GraphViewData(i, test_array[i]);
		// }
		// GraphViewSeries testSeries = new GraphViewSeries("test", new
		// GraphViewSeriesStyle(Color.RED, 3), test_data);
		// mGraphView.addSeries(testSeries);

		findCurrentLocation();
/*
		try {
			mClient = new MobileServiceClient(
					"https://mobonanosens.azure-mobile.net/",
					"VFYRveLHGNmcnCOYGxluzmjOiHskkA33", this);
		} catch (MalformedURLException e) {

			Toast.makeText(getApplicationContext(),
					"Database connection failed.", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}*/
		AudioManager amanager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		amanager.setStreamVolume(AudioManager.STREAM_MUSIC,
				amanager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
				AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		/**
		mTwitter    = new TwitterApp(this, TWITTER_CONSUMER_KEY, TWITTER_CONSUMER_SECRET);
		mTwitter.setListener(mTwLoginDialogListener);
		if(mTwitter.hasAccessToken())
		{
			// display the log off text and button
		//	ImageButton mAccount =  (ImageButton) findViewById(R.id.btn_account);
			//mAccount.setVisibility(mAccount.VISIBLE);
		}
		else{
			mTwitter.authorize();
			//display the log off text and button
		}**/
	}

	public void AccountClick(View view)
	{
		if(mTwitter.hasAccessToken())
		{
			mTwitter.resetAccessToken();
			mTwitter.authorize();
			

		}
	else{
		mTwitter.authorize();
		//display the log off text and button
	}
		
	}
	
	 private final TwDialogListener mTwLoginDialogListener = new TwDialogListener() {
	        public void onComplete(String value) {
	            String username = mTwitter.getUsername();
	            username        = (username.equals("")) ? "No Name" : username;
	 
	       //     mTwitterBtn.setText("  Twitter  (" + username + ")");
	       //     mTwitterBtn.setChecked(true);
	       //     mTwitterBtn.setTextColor(Color.WHITE);
	 
	           Toast.makeText(Sensing.this, "Connected to Twitter as " + username, Toast.LENGTH_LONG).show();
	        }

			public void onError(String value) {
				// TODO Auto-generated method stub
				
			}
	 };
	 
	@Override
	public void onStop() {

		m_stop = true;

		if (playTrack != null) {
			playTrack.pause();
			playTrack.flush();
			playTrack.stop();
			playTrack.release();
			playTrack = null;
		}

		if (recorder != null) {

			recorder.stop();
			recorder.release();
			recorder = null;
		}

		if (startRecThread != null && startRecThread.isAlive()) {

			startRecThread = null;
		}
		if (startPlayThread != null && startPlayThread.isAlive()) {

			startPlayThread = null;
		}
		if (progressbar != null && progressbar.isAlive()) {

			progressbar = null;
		}

		waveform = null;
		// ChartSurface.clearBBB();
		// ChartSurface.clearSDFandTime();

		System.gc();

		super.onStop();

	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_sensing, menu);
		return true;
	}

	@Override
	public void onStart() {
		super.onStart();

	}

	@Override
	public void onResume() {

		// ChartSurface.setResumed();
		super.onResume();
	}

	@Override
	public void onPause() {

		if (wl != null) {
			wl.release();
			wl = null;

		}
		super.onPause();

	}

	private static short scaleAmplitude(double Amax, double Amin,
			double sampleNumber, double fs, double seconds) {
		short Amp;
		double totalSamples = fs * seconds;
		double skew;
		skew = 2 * (Amax - Amin) / totalSamples;
		if (sampleNumber < totalSamples / 2) {

			Amp = (short) (skew * sampleNumber + Amin);

		}

		else {

			Amp = (short) (-skew * (sampleNumber - totalSamples / 2) + Amax);

		}

		return Amp;

	}

	private static short[] generateSineWavefreq(double frequencyOfSignal,
			double seconds, double fs, double Amax, double Amin) {
		short[] sin = new short[(int) (seconds * fs)];
		double samplingInterval = (double) (fs / frequencyOfSignal);

		for (int i = 0; i < sin.length; i++) {

			double angle = (2.0 * Math.PI * i) / samplingInterval;
			sin[i] = (short) (Math.sin(angle) * scaleAmplitude(Amax, Amin, i,
					fs, seconds));

		}
		return sin;
	}

	private static short[] generateNanoSensorWaveFreq(double Amax, double fs,
			double fmax, double seconds, double T) {

		double fc = fmax;
		double interval = 2 * Amax / (T * fs);
		short[] sin = new short[(int) (fs * seconds)];
		short[] A = new short[(int) (fs * seconds)];
		double temp = 0;
		for (int i = 0; (int) i < fs * seconds; i++) {

			if (i < (fs * seconds) / 2) {
				temp = temp + interval;
				A[i] = (short) temp;

			}

			else {
				temp = temp - interval;
				A[i] = (short) temp;

			}

		}

		for (int i = 0; i < fs * seconds; i++) {
			sin[i] = (short) (A[i] * Math.sin(2 * Math.PI * fc * (i / fs)));

		}

		return sin;
	}

	private static short[] generateTriWavefreq(double seconds, double fs,
			double fmax, double Amin, double Amax) {

		short[] tri = new short[(int) (seconds * fs)];
		double A = Amax - Amin;
		double temp = -1;
		double interval = 4 * 1 / (fs / fmax);
		long k = 0;

		for (short j = 0; j < seconds; j++) {

			for (int m = 0; m < fmax; m++) {

				int i = 0;
				for (; i < (fs / fmax) / 2; i++, k++) {
					tri[(int) k] = (short) (A * temp + Amin);
					temp = temp + interval;
				}

				for (; i < (fs / fmax); i++, k++) {
					tri[(int) k] = (short) (A * temp + Amin);
					temp = temp - interval;

				}

			}

		}

		return tri;
	}

	private static short[] freqChangeWaveFreq(double Amax, double fs,
			double fmin, double fmax, double time, double T) {

		//new edition double fixed_freq = 4450;
		int numPeriods = (int) (time / T);
		//	Log.i("debug-mobosens", Integer.toString(numPeriods));
		int numSamples = (int) (fs * time);
		short[] sweep = new short[numSamples];
		for (int k = 0; k < numPeriods; k++) {
			int numSamplesOnePeriod = (int) (fs * T);
			for (int i = 0; i < numSamplesOnePeriod; i++) {
				double t, fm;
				if (i < numSamplesOnePeriod / 2) {
					t = i / fs;
					fm = fmin + i * (fmax - fmin) / (numSamplesOnePeriod);
				} else {
					t = (numSamplesOnePeriod - i) / fs;
					fm = fmin + (numSamplesOnePeriod - i) * (fmax - fmin)
							/ (numSamplesOnePeriod);
				}
//				if(i==numSamplesOnePeriod/2){
//					Log.i("debug-mobosens", Double.toString(fm));
//				}
				sweep[k * numSamplesOnePeriod + i] = (short) (Amax * Math.sin(2
						* Math.PI * fm * t));

			}
		}

		return sweep;
	}
	private static short[] generateCalibrationWaveFreq (int[] frequencyOfSignal,
			double seconds, double fs, double Amax, double Amin) {
		
		Log.d("here","generate calibration wave freq");
		short[] cali =new short[(int) (seconds * fs)];
		
		//double []interval = {(seconds *fs)/4, (seconds*fs)/2, (seconds*fs)*3/4, seconds*fs};
	
		for(int j = 0; j<4; j++){
		
			double samplingInterval = (double) (fs / frequencyOfSignal[j]);			
					
			for(int i =(int) (j*(seconds*fs/4)); i<((j+1)*(seconds*fs/4)); i++ ){
					double angle = (2.0 * Math.PI * i) / samplingInterval;
					cali[i] = (short) (Math.sin(angle) * Amax);				
				}
		}
		
		
		return cali;
	}
	
	//new edition changed
	private static short[] generateSinFixedWave (int Fixed_Freq,
			double seconds, double fs, double Amax, double Amin) {
		
		Log.d("here","generate fixed sine wave freq");
		short[] fixed_sine =new short[(int) (seconds * fs)];
					
			double samplingInterval = (double) (fs / Fixed_Freq);			
					
			for(int i =0; i<seconds*fs; i++ ){
					double angle = (2.0 * Math.PI * i) / samplingInterval;
					fixed_sine[i] = (short) (Math.sin(angle) * Amax);				
				}
	
		
		
		return fixed_sine;
	}
	private short median(short buffer[]) {

		return buffer[buffer.length / 2];

	}

	private int inZone(int amp) {
		if (amp > windowUpperBound) {
			return 1;

		}

		if (amp < windowLowerBound) {

			return -1;

		}

		return 0;

	}

	private float mean(short buffer[]) {

		int len = buffer.length;

		float sum = 0;

		int i;

		for (i = 0; i < len; i++) {
			sum += buffer[i];
		}

		return sum / len;
	}

	private void writeDoubleArrayListToFile(ArrayList<Double> array) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String currentDateandTime = sdf.format(new Date());
		File f = new File(Environment.getExternalStorageDirectory()
				+ File.separator + "WindowFreqList" + currentDateandTime
				+ ".txt");
		FileOutputStream fo = null;
		if (!f.exists()) {
			try {
				f.createNewFile();
				fo = new FileOutputStream(f, false);
			} catch (IOException e) {
				fo = null;

				e.printStackTrace();
			}

		} else {
			// fo = null;
			try {
				fo = new FileOutputStream(f, true);
			} catch (FileNotFoundException e1) {
				fo = null;

				e1.printStackTrace();
			}
		}

		for (Double freq : array) {

			String str = freq.toString();
			// Log.i("debug", str);
			byte[] BBB = null;
			BBB = new byte[str.length()];
			for (int j = 0; j < str.length(); j++) {

				BBB[j] = (byte) str.charAt(j);

			}

			try {

				fo.write(BBB);
				fo.write('\n');
			} catch (IOException e) {
				fo = null;

				e.printStackTrace();
			}
		}

		try {
			fo.close();
			fo = null;
		} catch (IOException e) {

			fo = null;

			e.printStackTrace();

		}

	}
	
	private void writeAmplitudeListToFile(short [] bigbuffer) {
		
		Log.i("here_write", "write bigbuffer file");

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String currentDateandTime = sdf.format(new Date());
		File f = new File(Environment.getExternalStorageDirectory()
				+ File.separator + "AmplitudeList" + currentDateandTime
				+ ".txt");
		/**FileOutputStream fo = null;
		if (!f.exists()) {
			try {
				f.createNewFile();
				fo = new FileOutputStream(f, false);
			} catch (IOException e) {
				fo = null;

				e.printStackTrace();
			}

		} else {
			// fo = null;
			try {
				fo = new FileOutputStream(f, true);
			} catch (FileNotFoundException e1) {
				fo = null;

				e1.printStackTrace();
			}
		}**/
		try{
		writer = new BufferedWriter(new FileWriter(f));
		} catch (IOException e){
			e.printStackTrace();
		}
		for (int i = 0; i< bigbuffer.length; i++) {

			String str = Short.toString(bigbuffer[i]);
			// Log.i("debug", str);
			//byte[] BBB = null;
			//BBB = new byte[str.length()];
			//for (int j = 0; j < str.length(); j++) {

			//	BBB[j] = (byte) str.charAt(j);

			//}
			
			
			try {
				writer.write(str);
				writer.write('\n');
				//fo.write(str);
				//fo.write('\n');
			} catch (IOException e) {
				f = null;

				e.printStackTrace();
			}
		}
		Log.i("here_write", "write bigbuffer file done");
		try {
			writer.close();
			//fo.close();
			f = null;
		} catch (IOException e) {

			f = null;

			e.printStackTrace();

		}

	}

	private void startRecording() {
		
		
		Log.d("here","start recording inside");
		int channelConfiguration = AudioFormat.CHANNEL_IN_MONO;
		int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
		int minBufferSize = AudioRecord.getMinBufferSize(
				(int) recordSamplingRate, channelConfiguration, audioEncoding);
		final int bufferSize = minBufferSize;
		int tmpWindowSize = 882*2;
		while (tmpWindowSize*2 > minBufferSize)
		{
			
			tmpWindowSize /= 2;
		}
		final int windowSize = tmpWindowSize; //50 points in one second
		 


		 Log.i("debug-mobosens-recordingBuffer", String.valueOf(bufferSize));
		/* start a separate recording thread from here . . . */
		startRecThread = new Thread() {
			@Override
			public void run() {
				
				Log.d("here","new thread");
				android.os.Process
						.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

				if (recorder == null || m_stop == true)
					return;
				if (recorder.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
					
					recorder.startRecording();
				}

				int bufferNumber = 0;
			//	int windowIndexGold = 0;
				final float totalSamples = (float) (recordSamplingRate * time);
				float totalBuffers = totalSamples * 2 / bufferSize;
				short [] BigBuffer = new short [(int) totalSamples];
				int offset = 0;
				//collect the samples
				while (true) {
					Log.d("here","while inside");
					if (m_stop == true)
						break;
					//short[] buffer = new short[bufferSize / 2];
					int ret = -1;

					if (recorder != null && m_stop != true) {
						ret = recorder.read(BigBuffer, offset, bufferSize / 2);	
						offset+= bufferSize / 2;
					}
					System.gc();
				
				}
				Log.d("here","while outside");

				int windowIndex = 0;
				Log.i("debug-mobosens-size", Integer.toString(BigBuffer.length));
				
				
				//iterate through the samples
				int counter =0;
				int counter1 = 0;
				for (int i = 0; i <BigBuffer.length; i++) 
				{		counter++;
						//Log.i("here", "The raw data is:"+Short.toString(BigBuffer[i]));
						
						if (windowIndex == windowSize) {
							counter1++;
							windowIndex = 0;
							double timePerSample = recordSamplingRate / windowSize;
							double windowFreq = timePerSample
									* numberOfLogicalOnes;

							Double windowFreqObj = Double.valueOf(windowFreq);

							windowFreqList.add(windowFreqObj);
							// Log.i("Debug", Integer.toString(bufferNumber));
							//int length=0;
							//length = windowFreqList.size();
							//Log.d("here",Integer.toString(length));
							mGraphViewDataList
									.add(new GraphViewData((i-windowSize/2)/recordSamplingRate,
											windowFreq));
							// reset numberOf Logical Ones for next window
							numberOfLogicalOnes = 0;
							i -= windowSize*3/4;
						}
						
						
						// 1 is beyond upperbound, 0 is in zone, -1 is under
						// lowerbound
						int currentLogicalStatus = lastLogicalStatus;
						int currentZoneArea = inZone(BigBuffer[i]);
						if (lastZoneArea < 1 && currentZoneArea == 1) {
							currentLogicalStatus = 1;
						} else if (lastZoneArea > -1 && currentZoneArea == -1) {
							currentLogicalStatus = 0;
						}

						if (lastLogicalStatus == 0 && currentLogicalStatus == 1) {

							numberOfLogicalOnes++;
						}

						lastLogicalStatus = currentLogicalStatus;
						lastZoneArea = currentZoneArea;

						//windowIndexGold++;


						windowIndex++;
					
				    		
				}
				
				
				if(CalibrationFlag.isChecked()&&SinFlag.isChecked()&&sin_switch_flag == false){
					 for (int i =0; i<44100; i++){
						SecondBuffer[i]=BigBuffer[i];					
					 }
					 sin_switch_flag = true;
					 //writeAmplitudeListToFile(SecondBuffer);
					}
				
				if(CalibrationFlag.isChecked()&&SinFlag.isChecked()&&sin_switch_flag == true){
				 for (int i =0; i<44100; i++){
					FirstBuffer[i]=BigBuffer[i];					
				 }
				 sin_switch_flag = false;
				 writeAmplitudeListToFile(FirstBuffer);
				 for(int i =0; i<44100; i++){
				 Log.d("here_raw",Short.toString(FirstBuffer[i]));
				 }
				}
				
				
				
				
				
				
				int length=0;
				length = windowFreqList.size();
				Log.d("here initial",Integer.toString(counter));
				Log.d("here initial",Integer.toString(counter1));
				BigBuffer = null;
				System.gc();
				
				if(CalibrationFlag.isChecked()==false&&SinFlag.isChecked()==false){
					//writeDoubleArrayListToFile(windowFreqList);
					peak = peak_detection(windowFreqList);
					//concentration = (5499.53986-peak)/23.58551;
					//Changed by xinhao @ Feb.10th
					if(peak == 0){
						concentration = -1;
					}
					else{
					concentration = (5352.53385 - peak)/16.74462;
					}
				}
				else{
				concentration = CalculateWrapper(algoType);
				}
				//Changed for test version @2.23
				stopFollowupDialog();
				Log.i("debug-mobosens-concentration-r", Double.toString(concentration));
				runOnUiThread(new Runnable() {
					public void run() {

						mGraphViewArray = mGraphViewDataList
								.toArray(new GraphViewData[mGraphViewDataList
										.size()]);
						Log.i("Debug", Integer.toString(mGraphViewArray.length));
						GraphViewSeries testSeries = new GraphViewSeries(
								"freq", new GraphViewSeriesStyle(Color.RED, 3),
								mGraphViewArray);
						mGraphView.addSeries(testSeries);

					}
				});
				//mGraphViewDataList.clear();
				windowFreqList.clear();
				
			}

		};
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		startRecThread.start();

	}
    public double peak_detection(ArrayList<Double> windowFreqList){
    	
    	double data[]=new double[11996];
    	double data_part[]= new double[600];
    	double data_reversed[]= new double[600];
    	
    	double yy[]=new double[11993];
    	
    	for (int i=0;i < windowFreqList.size()-1;i++){
    	data[i] = windowFreqList.get(i);  	
    	}
    	Log.d("here peak",String.valueOf(windowFreqList.size()));
    	
    	yy[0]=data[0];
    	yy[1]=(data[0]+data[1]+data[2])/3;
    	
    	for (int i=0; i< 11988; i++){    
    	yy[i+2] = (data[i]+data[i+1]+data[i+2]+data[i+3]+data[i+4])/5;
    	}
    	
   
    	for (int i=9999; i<10599; i++){
    	   data_part[i-9999] = yy[i];
    	}

    	for (int i=0; i<599; i++){
    	   data_reversed[i]= data_part[599-i];
    	}

    	int slope=0;
    	int slope1=0;
    	int first_peak=0;
    	int first_high=0;
    	int zero_detect=0;
    	for(int i=0; i<100; i++){
    	    zero_detect = zero_detect + (int)data_reversed[i];
    	}
    	zero_detect = zero_detect/100;
    	Log.d("here peak", String.valueOf(zero_detect));
    	if(zero_detect > 20)
    	for(int i=1; i<599; i++){
    	    double prev=data_reversed[i];
    	    double curr=data_reversed[i+1];
    	    double next=data_reversed[i+2];
    	    
    	    if(prev < curr){
    	        slope = slope+1;
    	    }    
    	    else if(prev > curr){
    	        slope = slope-1;
    	    }    
    	    if(curr < next ){
    	        slope1 = 1;
    	    }    
    	    else if(curr > next || curr == next){
    	        slope1 = -1;
    	    }
    	    
    	    if(slope < 0){
    	        //if(prev <curr){
    	        //  slope1 = 1;
    	        //}    
    	        //else if(prev>curr){
    	        //   slope1 = -1;
    	        //}    
    	        if(slope1 == 1){
    	         first_peak = i;
    	         break;
    	        }
    	    }

    	}

    	else{
    	    for(int i=0; i<599; i++){
    	        if(data_reversed[i]>100){
    	        first_peak =i;
    	        break;
    	        }
    	    }       
    	}
    	
    	double test =0.0;
    	for(int i=first_peak; i<(first_peak + 40); i++){
    		    test = test + data_reversed[i];
    	} 
    	Log.d("here peak", String.valueOf(test));
    	if(test/40 > 100){
    	double threshold =0.0;
    	for(int i=first_peak; i< 595;i++){
    	   threshold=(data_reversed[i]+data_reversed[i+1]+data_reversed[i+2]+data_reversed[i+3]+data_reversed[i+4]+data_reversed[i+5]+data_reversed[i+6])/7; 
    	   if(data_reversed[i]>threshold){
    	       first_high = i;
    	       break;
    	   }
    	 }
    	}
    	else{
    		for(int i=first_peak; i <200; i++){
            if(data_reversed[i]>100){
                first_peak = i;
                break;
             }    
    		}
    		double threshold =0.0;
        	for(int i=first_peak; i< 594;i++){
        	   threshold=(data_reversed[i]+data_reversed[i+1]+data_reversed[i+2]+data_reversed[i+3]+data_reversed[i+4]+data_reversed[i+5]+data_reversed[i+6])/7; 
        	   if(data_reversed[i]>threshold){
        	       first_high = i;
        	       break;
        	   }
        	 }
    		
    	}
    	double min=data_reversed[first_high];

    	for(int i=first_high; i<599; i++){
    	    double curr_min = data_reversed[i];
    	    if(curr_min<=min){
    	        min = curr_min;
    		}
    	}	
    	
    	return min;
    }
    
	public double CalculateSinglePoint()
	{
		int length=0;
		length = windowFreqList.size();
		Log.d("here",Integer.toString(length));
		double cal = 0.0;
		if(windowFreqList!=null&&windowFreqList.isEmpty()!=true)
		{
			
			if(windowFreqList.size()<=SINGLE_POINT_INDEX)
			{
				
				cal = windowFreqList.get(windowFreqList.size()-1);
				
			}
			
			
			else{
				
				
				cal = windowFreqList.get(SINGLE_POINT_INDEX);
			}
			
		}
		
		double ret = (cal - 5543.57d)/(-18.93255d);
		
		
		return ret;
		
	}
	
	
	public double CalculateWrapper(int algorithmType)
	{
		double ret;
		if(algorithmType == 1||CalibrationFlag.isChecked()&&SinFlag.isChecked()==false)
		{   Log.d("here","calculate");
			ret = Calculate();
			
		}
		else if(algorithmType == 1||CalibrationFlag.isChecked()==false&&SinFlag.isChecked()){
			
			Log.d("here","sinflag calculate routine");
			ret = Difference();
			
		}
		else  {
			Log.d("here","calculate-singlepoint");
			ret = CalculateSinglePoint();	
			}
		
		return ret;
		
	}
	
	public double Difference() {
		double ret = 0;
		return ret;
	}
	
	
	public double Calculate() {
		double ret = 0.0;
		Log.d("here","test");
		
		
		double[] data = new double[(int) (samplingRate * time)];
		double value = 0;
	//	ret = -14 * RecAmpMinimum / 4900 + 749.0 / 49;
		

	//	if (ret < 0)
	//		ret = 0.00;
	if(CalibrationFlag.isChecked()){ Log.d("here","here2");
		/*if(!=null&&windowFreqList.isEmpty()!=true)
		{
			int startIndex = START_BOUND-1;
			double min = Double.MAX_VALUE;
//			int endIndex = SENSING_BOUND-1;
			if(windowFreqList.size()<SENSING_BOUND){
			//	Toast.makeText(getApplicationContext(), R.string.not_accurate, Toast.LENGTH_LONG).show();
				min = windowFreqList.get(windowFreqList.size()-1);
			}
		}*/
			//else{
				Log.d("here","calculating");
				int length=0;
				length = windowFreqList.size();
				Log.d("here",Integer.toString(length));
				double Start = 2.5/60*5995;
				double Start2 = 17.5/60*5995;
				double Start3 = 32.5/60*5995;
				double Start4 = 47.5/60*5995;
				// min = Double.MAX_VALUE;
				for(int i = (int)Start; i< (12.5/60*5995);i++)
				{
					data[i] = windowFreqList.get(i);
					value = value + data[i];
					//Log.d("here_final", "value for 5000HZ:" + String.valueOf(value) );
				}
				FreqA = value/(5996*10/60);
				value = 0;
				for(int i = (int)Start2; i< (27.5/60*5995);i++)
				{
					data[i] = windowFreqList.get(i);
					value = value + data[i];
				}
				FreqB = value/(5996*10/60);
				value = 0;
				for(int i = (int)Start3; i< (42.5/60*5995);i++)
				{
					data[i] = windowFreqList.get(i);
					value = value + data[i];
				}
				FreqC = value/(5996*10/60);
				value = 0;
				for(int i = (int)Start4; i< (57.5/60*5995);i++)
				{
					data[i] = windowFreqList.get(i);
					value = value + data[i];
				}
				FreqD = value/(5996*10/60);
				
				Log.d("here_final", "Finished calibration");
				
				Log.d("here_final", "freq for 5000HZ is:" + String.valueOf(FreqA) );
				Log.d("here_final", "freq for 5500HZ is:" + String.valueOf(FreqB) );
				Log.d("here_final", "freq for 7000HZ is:" + String.valueOf(FreqC) );
				Log.d("here_final", "freq for 7300HZ is:" + String.valueOf(FreqD) );
				
				
				
				double VA = VoltageReference(FreqA);
				double VB = VoltageReference(FreqB);
				double VC = VoltageReference(FreqC);
				double VD = VoltageReference(FreqD);
				Log.d("here_final", "VA value is:" + String.valueOf(VA) );
				Log.d("here_final", "VB value is:" + String.valueOf(VB) );
				Log.d("here_final", "VC value is:" + String.valueOf(VC) );
				Log.d("here_final", "VD value is:" + String.valueOf(VD) );
				
				
				
				
				
				double B1 = (CalibrationFreq[0]-CalibrationFreq[1])/(VA-VB);
				double A1 = CalibrationFreq[0] - B1*VA;
				double B2 = (CalibrationFreq[2]-CalibrationFreq[3])/(VC-VD);
				double A2 = CalibrationFreq[2] - B2*VC;
				
				double Fa = A1 + B1 * 4;
				double Fb = A2 + B2 *5.7;
				
				Log.d("here_final", "Fa value is:" + String.valueOf(Fa) );
				Log.d("here_final", "Fb value is:" + String.valueOf(Fb) );
				
				FinalA = Math.round(Fa);
				FinalB = Math.round(Fb);
				
		
	}
	else {
		if(windowFreqList!=null&&windowFreqList.isEmpty()!=true)
	    {
		int startIndex = START_BOUND-1;
		double min = Double.MAX_VALUE;
//		int endIndex = SENSING_BOUND-1;
		if(windowFreqList.size()<SENSING_BOUND){
		//	Toast.makeText(getApplicationContext(), R.string.not_accurate, Toast.LENGTH_LONG).show();
			min = windowFreqList.get(windowFreqList.size()-1);
		}
		else{
			
			// min = Double.MAX_VALUE;
			for(int i = startIndex; i<SENSING_BOUND;i++)
			{
				double raw = windowFreqList.get(i);
				if (raw < min){
					min = raw;
				}
				
			}
			
					
		}
		Log.i("debug-mobosens-mini", Double.toString(min));
		ret  = (min-5929.21304d)/(-32.30615d);	
	 }
	}
	    Log.i("debug-mobosens-ret", Double.toString(ret));
		
		return ret;
	}
	
	private double VoltageReference(double Freq) {
		double voltage = 0;
		double Vref=4.05;
		double a=0.720432;
		double b=0.496619;		
		voltage = (Vref-(a+b*(Freq/1000)))+Vref;		
		return voltage;
	}
	
	private void stopPlaying() {

		if (startPlayThread != null && startPlayThread.isAlive()) {
			startPlayThread = null;

		}

	}

	private void stopRecording() {

		if (recorder != null) {
			recorder.stop();
			recorder.release();
			recorder = null;
		}
		if (startRecThread != null && startRecThread.isAlive()) {

			startRecThread = null;
		}

	}

	public void startSensing(View view) {
		
		int channelConfiguration = AudioFormat.CHANNEL_IN_MONO;
		int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
		//windowSize = 882; // bufferSize / 4; //50 points in one second
	//	final int minBufferSize = AudioRecord.getMinBufferSize(
	//			(int) recordSamplingRate, channelConfiguration, audioEncoding);
	//	int tmpBufferSize = windowSize*4;
	//	while(tmpBufferSize < minBufferSize)
	//	{
	//		tmpBufferSize+=windowSize;
	//	}
	//	bufferSize = tmpBufferSize;
		runOnUiThread(new Runnable() {
			public void run() {
		mGraphView.removeAllSeries();}
		});
		mGraphViewDataList.clear();
		
		m_stop = false;

		if (Parameters.appContext == null) {
			Parameters.appContext = getApplicationContext();
		}

		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(Parameters.appContext);
		int retval;
		if(CalibrationFlag.isChecked()&&SinFlag.isChecked()==false) {	Log.d("here","here3");	
			try {
				retval = settings.getInt("SignalCategory", 4);
			} catch (ClassCastException e) {

				Editor editor = settings.edit();
				editor.putInt("SignalCategory", 4);
				editor.commit();
				retval = settings.getInt("SignalCategory", 4);
			}
		}
		else if (SinFlag.isChecked()){ Log.d("here", "here_sin");
		try {
			retval = settings.getInt("SignalCategory", 5);
		} catch (ClassCastException e) {

			Editor editor = settings.edit();
			editor.putInt("SignalCategory", 5);
			editor.commit();
			retval = settings.getInt("SignalCategory", 5);
		 }
		}
		else {
			try {
				retval = settings.getInt("SignalCategory", 2);
			} catch (ClassCastException e) {

				Editor editor = settings.edit();
				editor.putInt("SignalCategory", 2);
				editor.commit();
				retval = settings.getInt("SignalCategory", 2);
			}
		}
		switch (retval) {
		case 0:
			SigCat = SigType.SINE;
			break;
		case 1:
			SigCat = SigType.TRIANGULAR;
			break;
		case 2:
			SigCat = SigType.FREQ_CHANGE;
			break;
		case 3:
			SigCat = SigType.TRIANGULAR_MODULATION;
			break;
		case 4:
			SigCat = SigType.CALIBRATION;
			break;
		case 5:
			SigCat = SigType.SIN_FIXED;
			break;


		default:
			SigCat = SigType.SINE;
		}
		try {
			retval = settings.getInt("MinFrequency", 5100);
		} catch (ClassCastException e) {

			Editor editor = settings.edit();
			editor.putInt("MinFrequency", 5100);
			editor.commit();
			retval = settings.getInt("MinFrequency", 5100);

		}
		
		minFreq = retval;
		
		try {
			retval = settings.getInt("MaxFrequency", 7600);
		} catch (ClassCastException e) {

			Editor editor = settings.edit();
			editor.putInt("MaxFrequency", 7600);
			editor.commit();
			retval = settings.getInt("MaxFrequency", 7600);

		}
		
		maxFreq = retval;
		try {
			retval = settings.getInt("StableCycles", 2);
		} catch (ClassCastException e) {

			Editor editor = settings.edit();
			editor.putInt("StableCycles", 2);
			editor.commit();
			retval = settings.getInt("StableCycles", 2);

		}
		
		int cycles = retval;
		try {
			retval = settings.getInt("Duration", 30);
		} catch (ClassCastException e) {

			Editor editor = settings.edit();
			editor.putInt("Duration", 30);
			editor.commit();
			retval = settings.getInt("Duration", 30);

		}
		period = retval;
		if(CalibrationFlag.isChecked()&&SinFlag.isChecked()==false){
		time = period*2;}
		else{
		time = period*4;
		}
		Log.i("debug-mobosens", "minimum: "+Double.toString(minFreq));
		Log.i("debug-mobosens", "maximum: "+Double.toString(maxFreq));
		Log.i("debug-mobosens", "period: "+Double.toString(period));
		Log.i("debug-mobosens", "time: "+Double.toString(time));
		
		if (pm == null) {
			pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

		}
		if (am == null) {
			am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		}
		if (wl == null) {
			wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Tag");
		}

		wl.acquire();

//		int channelConfigurationPlay = AudioFormat.CHANNEL_IN_MONO;
//		int audioEncodingPlay = AudioFormat.ENCODING_PCM_16BIT;

		final int PlaySize = AudioTrack.getMinBufferSize((int) samplingRate,
				AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
		final int bufferSize = AudioRecord.getMinBufferSize(
				(int) recordSamplingRate, channelConfiguration, audioEncoding);

		switch (SigCat) {

		case SINE:
			waveform = generateSineWavefreq(mainFreq, time, samplingRate,
					mainAmp, minAmp);
			break;

		case TRIANGULAR:

			waveform = generateTriWavefreq(time, samplingRate, triFreq, minAmp,
					maxAmp);

			break;

		case FREQ_CHANGE:
			waveform = freqChangeWaveFreq(maxAmp, samplingRate, minFreq,
					maxFreq, time, period);
			break;

		case TRIANGULAR_MODULATION:
			waveform = generateNanoSensorWaveFreq(maxAmp, samplingRate,
					mainFreq, time, perioddefault);
			break;
		
		case CALIBRATION:
			waveform = generateCalibrationWaveFreq (CalibrationFreq, time, samplingRate,
					mainAmp, minAmp);
			break;
			
		case SIN_FIXED:
			waveform = generateSinFixedWave (Fixed_Freq, time, samplingRate,
					Imp_MaxAmp, minAmp);
			break;

		default:
			waveform = generateSineWavefreq(mainFreq, time, samplingRate,
					mainAmp, minAmp);

		}
		//frey test here
		playTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
				(int) samplingRate, AudioFormat.CHANNEL_OUT_MONO,
				AudioFormat.ENCODING_PCM_16BIT, PlaySize,
				AudioTrack.MODE_STREAM);
		recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT,
				(int) recordSamplingRate, AudioFormat.CHANNEL_IN_MONO,
				AudioFormat.ENCODING_PCM_16BIT, bufferSize);

		playTrack.play();
		//progress bar implemented by Chester 2.11.2015
		progress.setMessage("Sensing..");
		progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progress.setIndeterminate(false);
		progress.show();
		final double totalProgressTime = time;
		progressbar_flag = true;
		progressbar = new Thread(){
			@Override 
			public void run(){
				int currtime = 0;
				while(currtime < totalProgressTime && progressbar_flag == true){
					try{
						Log.d("here","time updates");

						sleep(1200);
						currtime++;
						progress.setProgress(currtime);
					}catch (InterruptedException e){
						e.printStackTrace();
					}
				}
				currtime = 0;
				progress.dismiss();
			}
		};
		
		startPlayThread = new Thread() {	
			public void run() {

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

				while (m_stop == false && stabCycles > -1) {
					if (m_stop == false && stabCycles == 0) {
						Log.d("here","start recording outside");
						startRecording();
					}
					ShortsLeft = waveform.length;
					while (m_stop == false && ShortsLeft >= bufSize) {
						int ret = playTrack.write(waveform, waveform.length
								- ShortsLeft, bufSize);
						ShortsLeft -= bufSize;
					}

					stabCycles--;
				}

				if (m_stop == false) {
					
					m_stop = true;
					progressbar_flag = false;
					stopRecording();
					waveform = null;
					if (progressbar != null && progressbar.isAlive()) {
						
						progressbar = null;
						Log.d("here","progressbar stopped");
					}
					
					playTrack.pause();
					playTrack.flush();
					playTrack.stop();
					playTrack.release();
					playTrack = null;
					// ChartSurface.setStopped();


					runOnUiThread(new Runnable() {
						public void run() {
							Log.d("here","Runnable UI sys");
							Button handle = (Button) findViewById(R.id.inner_stop);

							handle.setVisibility(View.INVISIBLE);

							handle = (Button) findViewById(R.id.inner_start);
							stabCycles=0;
							handle.setVisibility(View.VISIBLE);

							TextView StatusContent = (TextView) findViewById(R.id.status_content);
							TextView StatusDetails = (TextView) findViewById(R.id.status_details);
							StatusContent.setText("Send");
							StatusDetails
									.setText("Press Twitter button to send data on Twitter.");
						}
					});

					startPlayThread = null;
					//concentration = Calculate();
				    Log.i("debug-mobosens-concentration-2", Double.toString(concentration));
					
				}

			}

		};
		progressbar.start();
		startPlayThread.start();
		

		Button handle = (Button) findViewById(R.id.inner_start);
		handle.setVisibility(View.INVISIBLE);
		handle = (Button) findViewById(R.id.inner_stop);
		handle.setVisibility(View.VISIBLE);

		TextView StatusContent = (TextView) findViewById(R.id.status_content);
		TextView StatusDetails = (TextView) findViewById(R.id.status_details);
		StatusContent.setText("Sensing");
		StatusDetails
				.setText("System is sensing the sensor...Please wait for 2 Minutes.");

	}

	public void shipGeoData(View view) {
		
		if(mTwitter.hasAccessToken())
		{
			
		}
		else
		{
			mTwitter.authorize();
			if(mTwitter.hasAccessToken())
			{
				
			}
			else
			{
				Toast.makeText(this, "Send process failed because Twitter account is not set up correctly.", Toast.LENGTH_SHORT).show();
				return;
			}
		}
		
		AccessToken mAccessToken = mTwitter.getAccessToken();
		String token = mAccessToken.getToken();
		String secret = mAccessToken.getTokenSecret();
		Item item = new Item();
		String allowMe;
		if(allow == true)
		 allowMe = "Yes";
		else
		  allowMe = "No";
		String claimType = "#MoboSens";
		String sensorSubstance = "Nitrate";
		String phoneName = "Nexus 5";
		String comment = "N/A";
		item.concentration = concentration;
		item.latitude = currLat;
		item.longtitude = currLong;
		String unit = "ppm";
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss aa yyyy-MM-dd");
		String currentDateandTime = sdf.format(new Date());
		item.timestamp = currentDateandTime;
	//	HttpClient httpclient = new DefaultHttpClient();
		//URI url;

	    
	// Creating HTTP client
	HttpClient httpClient = new DefaultHttpClient();
	 
	// Creating HTTP Post
	HttpPost httpPost = new HttpPost(getString(R.string.cserver));
	List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
	
	nameValuePair.add(new BasicNameValuePair("allow", allowMe));
	nameValuePair.add(new BasicNameValuePair("phonename", phoneName));
	nameValuePair.add(new BasicNameValuePair("substance", sensorSubstance));
	nameValuePair.add(new BasicNameValuePair("claimtype", claimType));
	nameValuePair.add(new BasicNameValuePair("comment", comment));
	nameValuePair.add(new BasicNameValuePair("consumerkey", TWITTER_CONSUMER_KEY));
	nameValuePair.add(new BasicNameValuePair("consumersecret", TWITTER_CONSUMER_SECRET));
	nameValuePair.add(new BasicNameValuePair("token", token));
	nameValuePair.add(new BasicNameValuePair("tokensecret", secret));
	nameValuePair.add(new BasicNameValuePair("unit", unit));
	nameValuePair.add(new BasicNameValuePair("item[concen]", Double.toString(item.concentration)));
	nameValuePair.add(new BasicNameValuePair("item[lati]", Double.toString(item.latitude)));
	nameValuePair.add(new BasicNameValuePair("item[longti]", Double.toString(item.longtitude)));
	nameValuePair.add(new BasicNameValuePair("item[timestamp]", currentDateandTime));
	
	
	
	// Url Encoding the POST parameters
	try {
	    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
	}
	catch (UnsupportedEncodingException e) {
	    // writing error to Log
	    e.printStackTrace();
	}
	
	// Making HTTP Request
	try {
	    HttpResponse response = httpClient.execute(httpPost);
	 
	    // writing response to log
	    Log.d("Http Response:", response.toString());
	 
	} catch (ClientProtocolException e) {
	    // writing exception to log
	    e.printStackTrace();
	         
	} catch (IOException e) {
	    // writing exception to log
	    e.printStackTrace();
	}
		/*
		mClient.getTable(Item.class).insert(item,
				new TableOperationCallback<Item>() {
					public void onCompleted(Item entity, Exception exception,
							ServiceFilterResponse response) {
						if (exception == null) {
							Toast.makeText(getApplicationContext(),
									"Send success.", Toast.LENGTH_SHORT).show();
							// Insert succeeded
						} else {
							Toast.makeText(getApplicationContext(),
									"Send failed.", Toast.LENGTH_SHORT).show();
							// Insert failed
						}
					}
				});
				*/
		
	}

	private void stopFollowupDialog()
	{
		Bundle args = new Bundle();
	    args.putDouble("Concentration", concentration);
	    args.putLong("FinalA", FinalA);
	    args.putLong("FinalB", FinalB);
	    args.putDouble("Peak", peak);
	    
	    
	    UploadDialogFragment mUpload = new UploadDialogFragment();
	  //  ConcentrationDialogFragment mC = new ConcentrationDialogFragment();
	    mUpload.setArguments(args);
		mUpload.show(getFragmentManager(), "Data Uploading");
	 //   mC.show(getFragmentManager(), "Concentration");


		
	}

	public void stopSensing(View view) {

		Log.d("here","stop sensing");
		//concentration = CalculateWrapper(algoType);
	    //Log.i("debug-mobosens-concentration1", Double.toString(concentration));
		// ChartSurface.setStopped();
		m_stop = true;
		progressbar_flag = false;
		
		if (playTrack != null) {
			playTrack.pause();
			playTrack.flush();
			playTrack.stop();
			playTrack.release();
			playTrack = null;
		}
		stopPlaying();
		stopRecording();
		if (progressbar != null && progressbar.isAlive()) {
		
			progressbar = null;
			Log.d("here","progressbar stopped");
		}
		if (waveform != null)
			waveform = null;
		Button handle = (Button) findViewById(R.id.inner_stop);

		handle.setVisibility(View.INVISIBLE);

		handle = (Button) findViewById(R.id.inner_start);
		stabCycles=0;
		handle.setVisibility(View.VISIBLE);

		TextView StatusContent = (TextView) findViewById(R.id.status_content);
		TextView StatusDetails = (TextView) findViewById(R.id.status_details);
		StatusContent.setText("Send");
		StatusDetails.setText("Press Start to begin collecting data. Make sure the sensor is connected to the headset interface.");

		//StatusDetails.setText("Press Cloud button to send data on Cloud.");
		//Changed for test version @2.23
		stopFollowupDialog();
		windowFreqList.clear();
		

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			rbmView.toggleMenu();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void TwitterResults(View view) {

		// TODO: Twitter

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == android.R.id.home) {

			rbmView.toggleMenu();

			return true;

		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	public void RibbonMenuItemClick(int itemId) {
//Changed for test version @2.23
		Intent intent;

		switch (itemId) {

		//case R.id.item_map:
		//	finish();
		//	intent = new Intent(this, ViewMap.class);
		//	startActivityForResult(intent, 1);
		//
		//	break;

		/**case R.id.item_weather:
			finish();
			intent = new Intent(this, Weather.class);
			startActivityForResult(intent, 1);

			break;
		**/
		case R.id.item_param:
			finish();
			intent = new Intent(this, Parameters.class);
			startActivityForResult(intent, 1);
			break;
		/**case R.id.item_settings:
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
			// do nothing yourself

		}

	}

}
