package seniorproj.munchbox;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
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
public class LocationGetter {
    private Context context;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location mlocation;
    private PlaceLikelihood likelyPlace;

    public LocationGetter(Context mContext){
        context = mContext;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {

            }
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}

        };

    }

    public void startListening(){
        // Register the listener with the Location Manager to receive location updates
        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
        }
        else{
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
    }

    public Location getLocation(){
        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            /*
            Need to ask for permission and then recall this function.
             */
            return locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        }
        else{
            return locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
    }

    /* Currently not working. Finishing HTTP implementation instead*/
    public PlaceLikelihood googleMapLocation(Location location){
        PlaceDetectionClient p = Places.getPlaceDetectionClient(context);
        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            System.out.println("sigh...");
        }
        else {
            //System.out.println("Get Current Place Start");
            Task<PlaceLikelihoodBufferResponse> placeResult = p.getCurrentPlace(null);
            //System.out.println("Get Current Place End");
            placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                    //System.out.println("Complete");
                    if (task.isSuccessful() && task.getResult() != null) {
                        PlaceLikelihoodBufferResponse buffer = task.getResult();
                        for (PlaceLikelihood placeLikelihood: buffer){
                            if(likelyPlace != null){
                                likelyPlace = placeLikelihood;
                            }
                        }
                    }

                    /*
                    Data storage
                    Use placeLikelihood.getPlace().otherFunction() for data
                    Talk to Eric about format.
                     */

                    //for (PlaceLikelihood placeLikelihood : likelyPlaces) {}
                    //likelyPlaces.release();
                }
            });
        }
        return likelyPlace;
    }
}
