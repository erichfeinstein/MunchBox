package seniorproj.munchbox;

import android.content.Context;

import com.google.type.LatLng;

import java.net.MalformedURLException;
import java.net.URL;

public class URLMaker {

    public static URL distanceMatrixURL(Context context, LatLng start, LatLng end){
        String api_key = context.getString(R.string.mykey);
        try {
            String origin = start.getLatitude() + "%2C" + start.getLongitude();
            String destination = end.getLatitude() + "%2C" + end.getLongitude();
            String url_string = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=" + origin + "&destinations=" + destination + "&key=" + api_key;
            URL url = new URL(url_string);
            return url;
        }
        catch (MalformedURLException e){
            System.out.println(e.toString());
        }
        return null;
    }

    public static URL placesURL(Context context, LatLng location){
        String api_key = context.getString(R.string.mykey);
        try{
            String current_location = location.getLatitude() + "%2C" + location.getLongitude();
            String url_string = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + current_location + "&radius=20&key=" + api_key;
            URL url = new URL(url_string);
            return url;
        }
        catch (MalformedURLException e){
            System.out.println(e.toString());
        }
        return null;
    }

}
