package seniorproj.munchbox;

import android.renderscript.ScriptGroup;

/**
 * Created by jxy367 on 2/23/2018.
 */

import android.os.AsyncTask;
import android.util.JsonReader;

import java.io.*;
import java.net.*;

public class MapsRequest extends AsyncTask<URL, Integer, Long> {
    @Override
    protected Long doInBackground(URL... urls) {
        long completed;
        for (completed = 0; completed<urls.length; completed++) {
            System.out.println(completed);
            request(urls[(int)completed]);
            if (isCancelled()) break;
        }
        return completed;
    }

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
