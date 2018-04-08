package seniorproj.munchbox;

import android.os.AsyncTask;
import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class PlacesRequest extends AsyncTask<URL, Integer, List<String>> {
    @Override
    protected List<String> doInBackground(URL... urls) {
        long completed;
        ArrayList<String> d = new ArrayList<>();
        for (completed = 0; completed < urls.length; completed++) {
            d = request(urls[(int) completed]);
            if (isCancelled()) break;
        }
        return d;
    }

    private ArrayList<String> request(URL url) {
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
    private ArrayList<String> readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readMessage(reader);
        } finally {
            reader.close();
        }
    }

    private ArrayList<String> readMessage(JsonReader reader) throws IOException {
        ArrayList<String> names = null;
        reader.beginObject();
        while (reader.hasNext()) {
            String value = reader.nextName();
            if (value.equals("results")) {
                names = getResultFromArray(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return names;
    }

    private ArrayList<String> getResultFromArray(JsonReader reader) throws IOException {
        ArrayList<String> names = new ArrayList<>();
        reader.beginArray();
        while (reader.hasNext()) {
            String name = getNameFromObject(reader);
            if(name != null){
                names.add(name);
            }
        }
        reader.endArray();
        return names;
    }

    private String getNameFromObject(JsonReader reader) throws IOException {
        String restaurant_name = null;
        String name;
        reader.beginObject();
        while (reader.hasNext()) {
            name = reader.nextName();
            if (name.equals("name")){
                restaurant_name = reader.nextString();
            }
            else{
                reader.skipValue();
            }
        }
        reader.endObject();
        return restaurant_name;
    }
}