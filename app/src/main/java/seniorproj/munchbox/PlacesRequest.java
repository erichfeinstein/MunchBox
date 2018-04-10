package seniorproj.munchbox;

import android.location.Location;
import android.os.AsyncTask;
import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class PlacesRequest extends AsyncTask<URL, Integer, List<Location>> {
    @Override
    protected List<Location> doInBackground(URL... urls) {
        long completed;
        ArrayList<Location> d = new ArrayList<>();
        for (completed = 0; completed < urls.length; completed++) {
            d = request(urls[(int) completed]);
            if (isCancelled()){
                System.out.println("Cancelled");
                break;
            }
        }
        return d;
    }

    private ArrayList<Location> request(URL url) {
        try {
            URLConnection conn = url.openConnection();
            InputStream is = conn.getInputStream();
            return readJsonStream(is);

        } catch (Exception e) {
            System.out.println(e.toString() +  ". Places Request request failed.");
            return new ArrayList<>();
        }
    }

    //JSON data grabbing
    private ArrayList<Location> readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readMessage(reader);
        } finally {
            reader.close();
        }
    }

    private ArrayList<Location> readMessage(JsonReader reader) throws IOException {
        ArrayList<Location> locations = new ArrayList<>();
        reader.beginObject();
        while (reader.hasNext()) {
            String value = reader.nextName();
            if (value.equals("results")) {
                locations = getResultFromArray(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return locations;
    }

    private ArrayList<Location> getResultFromArray(JsonReader reader) throws IOException {
        ArrayList<Location> locations = new ArrayList<>();
        reader.beginArray();
        while (reader.hasNext()) {
            Location l = getNameFromObject(reader);
            if(l != null){
                locations.add(l);
            }
        }
        reader.endArray();
        return locations;
    }

    private Location getNameFromObject(JsonReader reader) throws IOException {
        String name;
        Location l = new Location("");
        reader.beginObject();
        while (reader.hasNext()) {
            name = reader.nextName();
            if(name.equals("geometry")) {
                l = getLocationFromObject(reader, l);
            }
            else if(name.equals("name")) {
                l.setProvider(reader.nextString());
            }
            else{
                reader.skipValue();
            }
        }
        reader.endObject();
        return l;
    }

    private Location getLocationFromObject(JsonReader reader, Location l) throws  IOException{
        String name;
        reader.beginObject();
        while(reader.hasNext()){
            name = reader.nextName();
            if(name.equals("location")) {
                l = getLatLngFromObject(reader, l);
            }
            else{
                reader.skipValue();
            }
        }
        reader.endObject();
        return l;
    }

    private Location getLatLngFromObject(JsonReader reader, Location l) throws IOException{
        String name;
        reader.beginObject();
        while(reader.hasNext()){
            name = reader.nextName();
            if(name.equals("lat")) {
                l.setLatitude(reader.nextDouble());
            }
            else if(name.equals("lng")) {
                l.setLongitude(reader.nextDouble());
            }
            else{
                reader.skipValue();
            }
        }
        reader.endObject();
        return l;
    }
}