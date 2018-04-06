package seniorproj.munchbox;

import android.os.AsyncTask;
import android.os.Message;
import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class PlacesRequest extends AsyncTask<URL, Integer, List<Distance>> {
    @Override
    protected List<Distance> doInBackground(URL... urls) {
        long completed;
        ArrayList<Distance> d = new ArrayList<Distance>();
        for (completed = 0; completed<urls.length; completed++) {
            //System.out.println(completed);
            d.add(request(urls[(int)completed]));
            if (isCancelled()) break;
        }
        return d;
    }

    private Distance request(URL url){
        try {
            URL u = url;
            URLConnection conn = u.openConnection();
            InputStream is = conn.getInputStream();
            Distance distanceInformation = readJsonStream(is);
            return distanceInformation;

        }
        catch(Exception e){
            System.out.println(e.toString());
            return new Distance("Error", 0);
        }
    }

    //JSON data grabbing
    private Distance readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readMessage(reader);
        } finally {
            reader.close();
        }
    }

    private List<Distance> readMessagesArray(JsonReader reader) throws IOException {
        List<Distance> messages = new ArrayList<Distance>();
        reader.beginObject();
        while (reader.hasNext()) {
            messages.add(readMessage(reader));
        }
        reader.endArray();
        return messages;
    }

    private Distance readMessage(JsonReader reader) throws IOException {
        String destinationAddress = null;
        String originAddress = null;
        Distance distance = null;
        TravelTime travelTime = null;
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("destination_addresses")) {
                destinationAddress = getAddressFromArray(reader);
            } else if (name.equals("origin_addresses")) {
                originAddress = getAddressFromArray(reader);
            } else if (name.equals("rows")) {
                reader.beginArray();
                reader.beginObject();
            } else if (name.equals("elements")){
                reader.beginArray();
                reader.beginObject();
            } else if (name.equals("distance")) {
                distance = readDistance(reader);
            } else if (name.equals("duration")) {
                travelTime = readDuration(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        /*
        System.out.println("Destination address: " + destinationAddress);
        System.out.println("Origin address: " + originAddress);
        System.out.println(distance.toString());
        System.out.println(travelTime.toString());
        */
        return distance;
    }

    /*
    private List<Double> readDoublesArray(JsonReader reader) throws IOException {
        List<Double> doubles = new ArrayList<Double>();

        reader.beginArray();
        while (reader.hasNext()) {
            doubles.add(reader.nextDouble());
        }
        reader.endArray();
        return doubles;
    }
    */

    private String getAddressFromArray(JsonReader reader) throws IOException {
        String address = null;
        reader.beginArray();
        address = reader.nextString();
        reader.endArray();
        return address;
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
    }

    private TravelTime readDuration(JsonReader reader) throws IOException {
        String time = null;
        int seconds = -1;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("text")) {
                time = reader.nextString();
            } else if (name.equals("value")) {
                seconds = reader.nextInt();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new TravelTime(time, seconds);
    }
}
