package seniorproj.munchbox;

import android.os.Message;
import android.renderscript.ScriptGroup;

/**
 * Created by jxy367 on 2/23/2018.
 */

import android.os.AsyncTask;
import android.util.JsonReader;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

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
            Message uselessMessage = readJsonStream(is); //ASK WHAT STRUCTURE THEY WANT THE DATA IN
        }
        catch(Exception e){
            System.out.println(e.toString());
        }
    }

    private Message readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readMessage(reader);
        } finally {
            reader.close();
        }
    }

    private List<Message> readMessagesArray(JsonReader reader) throws IOException {
        List<Message> messages = new ArrayList<Message>();
        reader.beginObject();
        while (reader.hasNext()) {
            messages.add(readMessage(reader));
        }
        reader.endArray();
        return messages;
    }

    private Message readMessage(JsonReader reader) throws IOException {
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
        return new Message();
    }

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
