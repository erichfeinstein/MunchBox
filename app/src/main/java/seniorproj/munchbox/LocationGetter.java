package seniorproj.munchbox;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


/**
 * Created by Jxy367 on 3/2/2018.
 */


/*
Testing necessary for this one.
 */
public class LocationGetter implements Runnable {
    private Context context;
    private Handler mHandler;
    private LocationListener locationListener;
    private static LocationManager locationManager;
    private float accuracy;
    private Location currentLocation;

    public LocationGetter(LocationManager l, Context c, Handler mainHandler){
        System.out.println("Making LocationGetter");
        mHandler = mainHandler;
        locationManager = l;
        context = c;
        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                System.out.println("Location Changed");
                if(currentLocation == null) {
                    updateDistances(location);
                }
                else if(location.distanceTo(currentLocation) > 100){
                    updateLocation(location);
                }
                else if(location.getAccuracy() < accuracy){
                    updateLocation(location);
                }
                else{

                }
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}

        };

    }

    private void startListening(){
        // Register the listener with the Location Manager to receive location updates
        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            System.out.println("LocationGetter does not have permission");
        }
        else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20*1000, 10, locationListener);
        }
    }

    private void updateLocation(Location newLocation){
        mHandler.sendMessage(Message.obtain(mHandler, 2, newLocation));
        currentLocation = newLocation;
        accuracy = newLocation.getAccuracy();
    }

    private void updateDistances(Location newLocation){
        mHandler.sendMessage(Message.obtain(mHandler, 3, newLocation));
        currentLocation = newLocation;
        accuracy = newLocation.getAccuracy();
    }

    public void run(){
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        System.out.println("LG run");
        Looper.prepare();
        System.out.println("LG prepared");
        startListening();
        System.out.println("LG listening");
        Looper.loop();
    }

}
