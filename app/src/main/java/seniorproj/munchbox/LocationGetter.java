package seniorproj.munchbox;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.PlacesOptions;
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

    public LocationGetter(Context mContext){
        context = mContext;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {}

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}

        };

    }

    public void startListening(){
        // Register the listener with the Location Manager to receive location updates
        if(context.checkPermission(Context.LOCATION_SERVICE, android.os.Process.myPid(), android.os.Process.myUid())
                != PackageManager.PERMISSION_GRANTED) {

        }
        else{
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
    }

    public Location getLocation(){
        if(context.checkPermission(Context.LOCATION_SERVICE, android.os.Process.myPid(), android.os.Process.myUid())
                != PackageManager.PERMISSION_GRANTED){
            /*
            Need to ask for permission and then recall this function.
             */
            return locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        }
        else{
            return locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

    }

    public void googleMapLocation(Location location){
        PlaceDetectionClient p = Places.getPlaceDetectionClient(context);
        if(context.checkPermission(Context.LOCATION_SERVICE, android.os.Process.myPid(), android.os.Process.myUid())
                != PackageManager.PERMISSION_GRANTED) {
            System.out.println("sigh...");
        }
        else {
                Task<PlaceLikelihoodBufferResponse> placeResult = p.getCurrentPlace(null);
            placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                    PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();

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

    }
}
