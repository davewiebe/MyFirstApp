package com.example.myfirstapp;

import java.util.Random;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;

public class DisplayMessageActivity extends MapActivity {

    private MapView mMapView;
    private MyLocationOverlay mMyLocationOverlay;
    private LocationManager mLocationManager;
    private Handler mHandler;
    private TextView mLat;
    private TextView mLon;
    
    private GeoPoint mHome;
    private GeoPoint mSmallItem;

    private static final int ONE_SECOND = 1000;
    private static final int TEN_SECONDS = 10000;
    private static final int TEN_METERS = 10;
    private static final int ONE_METER = 1;
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    
	@Override
	protected boolean isRouteDisplayed() {
	    return false;
	}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);
        

        mLat = (TextView) findViewById(R.id.latText);
        mLon = (TextView) findViewById(R.id.lonText);
        
        mMapView = (MapView) findViewById(R.id.mapview);
        mMapView.setBuiltInZoomControls(true);

    
        
        mMyLocationOverlay = new MyLocationOverlay(this, mMapView);
        mMyLocationOverlay.runOnFirstFix(new Runnable() { public void run() {
            mMapView.getController().animateTo(mMyLocationOverlay.getMyLocation());
        }});
                
        
        mMapView.getController().setZoom(18);
        mMapView.setClickable(true);
        mMapView.setEnabled(true);
        
     // Handler for updating text fields on the UI like the lat/long and address.
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
            	Location loc = (Location) msg.obj;
                switch (msg.what) {
                    /*case UPDATE_ADDRESS:
                        mAddress.setText((String) msg.obj);
                        break;
                    case UPDATE_LATLNG:
                        mLatLng.setText((String) msg.obj);
                        break;*/
                }
                
                if (mHome == null) {
                	mHome = new GeoPoint((int)(loc.getLatitude()*1e6), (int)(loc.getLongitude()*1e6));
                	
                	//Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, results)
                	
           
                	Random rand = new Random();
                	double max=Math.PI*2;

                	double randomNum = rand.nextDouble()*max;
                	double dy = Math.sin(randomNum)*100.0;// 100 meters
                	double dx = Math.cos(randomNum)*100.0;// 100 meters
                	
                	//P0(lat0,lon0) : initial position (unit : degrees)
                	//dx,dy : random offsets from your initial position in meters
                	//You can use an approximation to compute the position of the randomized position:

                	double lat = loc.getLatitude() + (180/Math.PI)*(dy/6378137);
                	double lon = loc.getLongitude() + (180/Math.PI)*(dx/6378137)/Math.cos(loc.getLatitude());

                	lat *= 1e6;
                    lon *= 1e6;
                	
                	mSmallItem = new GeoPoint((int) lat,(int) lon);

                	DrawItems();
                }
                
                Location location = (Location) msg.obj;
                

                String lon = Double.toString(((Location) msg.obj).getLongitude());
                String lat = Double.toString(((Location) msg.obj).getLatitude());
                
                mLat.setText(lat);
                mLon.setText(lon);
                
                float[] results = new float[1];
                
                Location.distanceBetween(location.getLatitude(), location.getLongitude(), mSmallItem.getLatitudeE6()/1.0e6, mSmallItem.getLongitudeE6()/1.0e6, results);
                
                if (results[0] < 20) {
                	mLat.setText("you're almost at the item! it looks nice.");
                }
                if (results[0] < 10){
                	mLon.setText("It's a map!!");
                }
            }
        };

        // Get a reference to the LocationManager object.
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }
    
    public void DrawItems(){
        OverlayItem overlayitem1 = new OverlayItem(mHome, "Hello!", "I'm Home!");
        OverlayItem overlayitem2 = new OverlayItem(mSmallItem, "Hello!", "I'm an item!");

        Drawable drawable = this.getResources().getDrawable(R.drawable.androidmarker);
        HelloItemizedOverlay itemizedoverlay = new HelloItemizedOverlay(drawable, this);
        
        itemizedoverlay.addOverlay(overlayitem2);
        itemizedoverlay.addOverlay(overlayitem1);

        mMapView.getOverlays().add(itemizedoverlay);
    }

    private final LocationListener listener = new LocationListener() {

        public void onLocationChanged(Location location) {
            // A new location update is received.  Do something useful with it.  Update the UI with
            // the location update.
            
        	updateUIText(location);
        	// DO SOMETHING WITH THE LOCATION updateUILocation(location);
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_display_message, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        setup();
        
        mMyLocationOverlay.enableMyLocation();

        Drawable drawable = this.getResources().getDrawable(R.drawable.androidmarker);
        HelloItemizedOverlay itemizedoverlay = new HelloItemizedOverlay(drawable, this);
/*
        GeoPoint point = new GeoPoint(mMyLocationOverlay.getMyLocation().getLatitudeE6(),        		mMyLocationOverlay.getMyLocation().getLongitudeE6());
        OverlayItem overlayitem = new OverlayItem(point, "Hola, Mundo!", "I'm in Mexico City!");

        GeoPoint point2 = new GeoPoint(mMyLocationOverlay.getMyLocation().getLatitudeE6() + 1000,        		mMyLocationOverlay.getMyLocation().getLongitudeE6() + 1000);
        OverlayItem overlayitem2 = new OverlayItem(point2, "Sekai, konichiwa!", "I'm in Japan!");

        itemizedoverlay.addOverlay(overlayitem2);
        itemizedoverlay.addOverlay(overlayitem);

        List<Overlay> mapOverlays = mMapView.getOverlays();
        mapOverlays.add(itemizedoverlay);
*/
        mMapView.getOverlays().add(mMyLocationOverlay);
    }
    
    private void updateUIText(Location location){
	    Message.obtain(mHandler,
	            0,
	            location).sendToTarget();
    }
    
    // Set up fine and/or coarse location providers depending on whether the fine provider or
    // both providers button is pressed.
    private void setup() {
        Location gpsLocation = null;
        Location networkLocation = null;
        mLocationManager.removeUpdates(listener);
        
        // Get coarse and fine location updates.
        // Request updates from both fine (gps) and coarse (network) providers.
        gpsLocation = requestUpdatesFromProvider(
                LocationManager.GPS_PROVIDER, R.string.not_support_gps);//"Hard code RESID later 'not support gps'"
        networkLocation = requestUpdatesFromProvider(
                LocationManager.NETWORK_PROVIDER, R.string.not_support_network);//"Hard code RESID later 'not support network'");

        // If both providers return last known locations, compare the two and use the better
        // one to update the UI.  If only one provider returns a location, use it.
        if (gpsLocation != null && networkLocation != null) {
        	updateUIText(getBetterLocation(gpsLocation, networkLocation));
        	//updateUILocation(getBetterLocation(gpsLocation, networkLocation));
        } else if (gpsLocation != null) {
        	updateUIText(gpsLocation);
        } else if (networkLocation != null) {
        	updateUIText(networkLocation);

        }
    }
    
    /** Determines whether one Location reading is better than the current Location fix.
      * Code taken from
      * http://developer.android.com/guide/topics/location/obtaining-user-location.html
      *
      * @param newLocation  The new Location that you want to evaluate
      * @param currentBestLocation  The current Location fix, to which you want to compare the new
      *        one
      * @return The better Location object based on recency and accuracy.
      */
    protected Location getBetterLocation(Location newLocation, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return newLocation;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = newLocation.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved.
        if (isSignificantlyNewer) {
            return newLocation;
        // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return currentBestLocation;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (newLocation.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(newLocation.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return newLocation;
        } else if (isNewer && !isLessAccurate) {
            return newLocation;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return newLocation;
        }
        return currentBestLocation;
        
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
          return provider2 == null;
        }
        return provider1.equals(provider2);
    }
    
    private Location requestUpdatesFromProvider(final String provider, final int errorResId) {
        Location location = null;
        if (mLocationManager.isProviderEnabled(provider)) {
            mLocationManager.requestLocationUpdates(provider, ONE_SECOND, ONE_METER, listener);
            location = mLocationManager.getLastKnownLocation(provider);
        } else {
            Toast.makeText(this, errorResId, Toast.LENGTH_LONG).show();
        }
        return location;
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        mMyLocationOverlay.disableMyLocation();
        mLocationManager.removeUpdates(listener);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
