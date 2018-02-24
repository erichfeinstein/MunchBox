package seniorproj.munchbox;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by jxy367 on 2/23/2018.
 */

public class MapRequest {

    public void makeRequest(){
        // Check longitude and latitude format for android.

        /*
        For later testing
        LatLng sydney = new LatLng(-34, 151);
        LatLng mel = new LatLng(-37, 144);

        String origin = sydney.toString();
        String destination = mel.latitude + "%2C" + mel.longitude;
        */

        //
        String url = "http://google.com";

        try{
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            try {
                InputStream in = new BufferedInputStream(connection.getInputStream());
                //readStream(in);
            } finally {
                connection.disconnect();
            }
        }
        catch(MalformedURLException e){
            int y = 0;
        }
        catch (IOException e){
            int y = 0;
        }

    }
}

