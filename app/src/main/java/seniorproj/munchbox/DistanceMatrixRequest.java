package seniorproj.munchbox;

import android.renderscript.ScriptGroup;

/**
 * Created by jxy367 on 2/23/2018.
 */

import android.os.AsyncTask;
import android.util.JsonReader;

import java.io.*;
import java.net.*;
/*
Takes URLs and gets JSON object. Exact JSON parsing to be decided on later.
doInBackground is generic and will be used again later.
For our purposes, the URLs should be properly formatted google distance matrix urls.
 */
public class DistanceMatrixRequest extends AsyncTask<URL, Integer, Long> { //AKA "Roll for Charisma"
    @Override
    protected Long doInBackground(URL... urls) {
        long completed;
        for (completed = 0; completed<urls.length; completed++) {
            request(urls[(int)completed]);
            if (isCancelled()) break;
        }
        return completed;
    }

    /*
    Sends an https request and gets back JSON reader.
    THIS NEEDS TO BE UPDATED FOR ACTUAL DATA GATHERING
     */
    private void request(URL url){
        try {
            URL u = url;
            URLConnection conn = u.openConnection();
            InputStream is = conn.getInputStream();
            JsonReader reader = new JsonReader(new InputStreamReader(is));
            //Figure out reading
            int numRead = 0;
            while (numRead > -1) {
                byte[] buf = new byte[1024 * 10];
                numRead = is.read(buf);
                System.out.print(new String(buf, 0, numRead));
            }

            // Json data grabbing

        }
        catch(Exception e){
            System.out.println(e.toString());
        }
    }

}
