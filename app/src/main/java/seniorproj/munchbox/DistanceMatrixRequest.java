package seniorproj.munchbox;

import android.os.Message;
import android.renderscript.ScriptGroup;

/*
 * Created by jxy367 on 2/23/2018.
 */

import android.os.AsyncTask;
import android.util.JsonReader;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/*
Takes URLs and gets JSON object.
doInBackground is generic and will be used again later.
For our purposes, the URLs should be properly formatted google distance matrix urls.
 */
public class DistanceMatrixRequest extends AsyncTask<URL, Integer, List<Distance>> {
    @Override
    protected List<Distance> doInBackground(URL... urls) {
        long completed;
        ArrayList<Distance> d = new ArrayList<>();
        return request(urls[0]);
    }

    private List<Distance> request(URL url){
        try {
            URL u = url;
            URLConnection conn = u.openConnection();
            InputStream is = conn.getInputStream();
            List<Distance> distanceInformation = readJsonStream(is);
            return distanceInformation;
        }
        catch(Exception e){
            System.out.println(e.toString());
            return new ArrayList<>();
        }
    }

    /* JSON data gathering.
    @return List of the distance objects retrieved from the JSON stream.
    @input in The input stream to be written to
     */
    private List<Distance> readJsonStream(InputStream in) throws IOException {
        ArrayList<Distance> distances = new ArrayList<>();
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        reader.beginObject();
        try {
            while(reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("rows")) {
                    reader.beginArray();
                    reader.beginObject();
                    name = reader.nextName();
                    distances = readDistanceObjects(reader);
                    reader.endObject();
                    reader.endArray();
                }
                else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        } finally {
            reader.close();
        }
        return distances;
    }

    /* Obtains list of Distances from JSON reader.
    @return An ArrayList of Distances parsed from the JSON document
    @input reader The JSONReader that will parse the response
     */
    private ArrayList<Distance> readDistanceObjects(JsonReader reader) throws IOException{
        ArrayList<Distance> distances = new ArrayList<>();
        reader.beginArray();
        while(reader.hasNext()){
            Distance d = readDistanceObject(reader);
            if(d != null){
                distances.add(d);
            }
        }
        reader.endArray();
        return distances;
    }

    /* Method to be used in conjunction with readDistanceObjects. Handles the work of parsing a single object. */
    private Distance readDistanceObject(JsonReader reader) throws IOException{
        Distance distance = null;
        reader.beginObject();
        while(reader.hasNext()){
            String name = reader.nextName();
            if(name.equals("distance")){
                distance = readDistance(reader);
            }
            else{
                reader.skipValue();
            }
        }
        reader.endObject();
        return distance;
    }

    private Distance readDistance(JsonReader reader) throws IOException {
        String distance = null;
        int distanceMeters = -1;
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("text")) {
                distance = reader.nextString();
            } else if (name.equals("value")) {
                distanceMeters = reader.nextInt();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new Distance(distance, distanceMeters);
    }}

