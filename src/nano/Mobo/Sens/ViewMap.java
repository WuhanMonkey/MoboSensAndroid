package nano.Mobo.Sens;

import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_SATELLITE;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_TERRAIN;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import twitter4j.auth.AccessToken;
import nano.Mobo.Sens.TwitterApp.TwDialogListener;
import android.support.v4.app.Fragment;

import com.microsoft.windowsazure.mobileservices.*;
import com.LiuLab.Mobo.Sens.R;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.darvds.ribbonmenu.RibbonMenuView;
import com.darvds.ribbonmenu.iRibbonMenuCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ViewMap extends android.support.v4.app.FragmentActivity implements
        iRibbonMenuCallback, LocationListener, OnMarkerClickListener,
        OnInfoWindowClickListener, OnMarkerDragListener {
	//final String CSERVER =  "http://junleqian.com:8080/items/";
	private String responseText;
	private TwitterApp mTwitter;
	private static String TWITTER_CONSUMER_KEY = "dWpqGG7GJnpgZbN3g";
    private static String TWITTER_CONSUMER_SECRET = "mXWGCr8XQC8du6B3nzuBDlGGTiN9jVO7wz7umMbJQ";
  //  private MobileServiceClient mClient;
    private GoogleMap mMap;
    private boolean isFocused = false;
    private List<Item> mAdapter;
    private LocationManager locationManager;
    private static final long MIN_TIME = 0;
    private static final float MIN_DISTANCE = 0;
    private RibbonMenuView rbmView;

    @TargetApi(11)
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_map);

        rbmView = (RibbonMenuView) findViewById(R.id.ribbonMenuView1);
        rbmView.setMenuClickCallback(this);
        rbmView.setMenuItems(R.menu.ribbon_menu);
        mAdapter = new ArrayList<Item>();
        getActionBar().setDisplayHomeAsUpEnabled(true);
/*
        try {
            mClient = new MobileServiceClient(
                    "https://mobonanosens.azure-mobile.net/",
                    "VFYRveLHGNmcnCOYGxluzmjOiHskkA33", this);
        } catch (MalformedURLException e) {

            Toast.makeText(getApplicationContext(),
                    "Database connection failed.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
  */
        
        mTwitter    = new TwitterApp(this, TWITTER_CONSUMER_KEY, TWITTER_CONSUMER_SECRET);
		mTwitter.setListener(mTwLoginDialogListener);
		if(mTwitter.hasAccessToken())
		{
			// display the log off text and button
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
	 
	           Toast.makeText(ViewMap.this, "Connected to Twitter as " + username, Toast.LENGTH_LONG).show();
	        }

			public void onError(String value) {
				// TODO Auto-generated method stub
				
			}
	 };
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            rbmView.toggleMenu();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
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

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_view_map, menu);
        return true;
    }

    public void RibbonMenuItemClick(int itemId) {

        Intent intent;
        switch (itemId) {

        case R.id.item_sensor:
            finish();
            intent = new Intent(this, Sensing.class);
            startActivityForResult(intent, 1);

            break;
/**Changed for test version @2.23
        case R.id.item_weather:
            finish();
            intent = new Intent(this, Weather.class);

            startActivityForResult(intent, 1);

            break;
		case R.id.item_param:
			finish();
			intent = new Intent(this, Parameters.class);
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
            // do nothing yourself

        }
    }

    public void onLocationChanged(Location arg0) {
        if (isFocused == false) {
            isFocused = true;
            CameraPosition cp = new CameraPosition.Builder()
                    .target(new LatLng(arg0.getLatitude(), arg0.getLongitude()))
                    .zoom(12).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
        }
    }

    protected boolean isRouteDisplayed() {

        return false;
    }

    private void setUpMapIfNeeded() {

        if (mMap == null) {

            mMap = ((SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map)).getMap();
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE,
                    this);

            if (mMap != null) {

                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the
     * camera. In this case, we just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap}
     * is not null.
     */
 private class Item{
	 public Item(double c, double lati, double longti, String timestamp)
	 {
		 concentration = c;
		 latitude = lati;
		 longtitude = longti;
		 this.timestamp = timestamp;
	 }
	 public double concentration;
	 public double latitude;
	 public double longtitude;
	 public String timestamp;
	 
 }
    private void setUpMap() {

        mMap.setMyLocationEnabled(true);

     // Creating HTTP client
    	HttpClient httpClient = new DefaultHttpClient();
    	 
    	// Creating HTTP Post
    	
    	List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
    	if(!mTwitter.hasAccessToken())
    	{
    		
    		mTwitter.authorize();
    		
    	}
    	
    	if(!mTwitter.hasAccessToken())
    	{
    		return;
    	}
    	
    	AccessToken mAccessToken = mTwitter.getAccessToken();
    	String token = mAccessToken.getToken();
    	String secret = mAccessToken.getTokenSecret();
    	nameValuePair.add(new BasicNameValuePair("consumerkey", TWITTER_CONSUMER_KEY));
    	nameValuePair.add(new BasicNameValuePair("consumersecret", TWITTER_CONSUMER_SECRET));
    	nameValuePair.add(new BasicNameValuePair("token", token));
    	nameValuePair.add(new BasicNameValuePair("tokensecret", secret));

    	
    	
    	
    	// Url Encoding the POST parameters
    	String paramString = URLEncodedUtils.format(nameValuePair, "utf-8");
    //	Log.i("debug-mobosens", paramString);
    	HttpGet httpGet = new HttpGet(getString(R.string.cserver)+"?"+paramString);
    	// Making HTTP Request
    	try {
    	    HttpResponse response = httpClient.execute(httpGet);
    	    responseText =  EntityUtils.toString(response.getEntity());
    	    
    	    
    	    
    	 //   Log.i("debug-mobosens", responseText);
    	    // writing response to log
    	   // Toast.makeText(this, responseText, Toast.LENGTH_SHORT).show();
    	 
    	} catch (ClientProtocolException e) {
    	    // writing exception to log
    	    e.printStackTrace();
    	         
    	} catch (IOException e) {
    	    // writing exception to log
    	    e.printStackTrace();
    	}
        
    	List<Item> result = new ArrayList<Item>();
    	//seperate the response text
    	String [] entries = responseText.split(",");
	

    	for(int i = 0;i<entries.length; i++)
    	{
    		
    		String [] splitted = entries[i].split("\\|");

    		int lastIndex = splitted[6].indexOf("]");
    		String longtiText = splitted[6].substring(0, lastIndex-1);
    	//	Log.i("debug-mobosens-6", longtiText);
    		double longtiD = Double.parseDouble(longtiText);
    		Item fresh = new Item(Double.parseDouble(splitted[3]), Double.parseDouble(splitted[5]), longtiD, splitted[0].substring(2));
    		result.add(fresh);
    		
    	}
    	
    	if(responseText!=null)	
    	{
    		  mAdapter.clear();

              for (Item item : result) {

                  mAdapter.add(item);

                  String tmp;
                  if (item.timestamp != null) {
                      tmp = item.timestamp;

                  } else {

                      tmp = "";
                  }
                  mMap.addMarker(new MarkerOptions()
                          .position(
                                  new LatLng(item.latitude,
                                          item.longtitude))
                          .title("Nitrate Concentration: "
                                  + item.concentration)
                          .snippet("Timestamp: " + tmp)

                          .icon(BitmapDescriptorFactory
                                  .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

              }

    		
    		
    	}
        /*
        mClient.getTable(Item.class)
                .select("concentration", "latitude", "longtitude", "timestamp")
                .execute(new TableQueryCallback<Item>() {
                    public void onCompleted(List<Item> result, int count,
                            Exception exception, ServiceFilterResponse response) {

                        Toast.makeText(getApplicationContext(),
                                "GeoPoints loaded.", Toast.LENGTH_SHORT).show();
                        if (exception == null) {

                            mAdapter.clear();

                            for (Item item : result) {

                                mAdapter.add(item);

                                String tmp;
                                if (item.timestamp != null) {
                                    tmp = item.timestamp;

                                } else {

                                    tmp = "";
                                }
                                mMap.addMarker(new MarkerOptions()
                                        .position(
                                                new LatLng(item.latitude,
                                                        item.longtitude))
                                        .title("Nitrate Concentration: "
                                                + item.concentration)
                                        .snippet("Timestamp: " + tmp)

                                        .icon(BitmapDescriptorFactory
                                                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                            }
                        } else {
                            exception.printStackTrace();
                        }
                    }
                */

    }

    public void onProviderDisabled(String arg0) {
    }

    public void onProviderEnabled(String provider) {
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    public void onMarkerDrag(Marker arg0) {
    }

    public void onMarkerDragEnd(Marker arg0) {
    }

    public void onMarkerDragStart(Marker arg0) {
    }

    public void onInfoWindowClick(Marker arg0) {
    }

    public boolean onMarkerClick(Marker arg0) {
        return false;
    }
}
