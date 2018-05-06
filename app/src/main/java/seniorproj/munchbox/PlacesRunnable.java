package seniorproj.munchbox;

import android.location.Location;
import android.os.Handler;
import android.os.Message;

import java.net.URL;
import java.util.ArrayList;

public class PlacesRunnable implements Runnable {
    private URL u;
    private Handler mHandler;

    public PlacesRunnable(URL url, Handler mainHandler){
        u = url;
        mHandler = mainHandler;
    }

    public void run(){
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        System.out.println("Current thread ID: " + Thread.currentThread().getId());
        ArrayList<String> locations = new ArrayList<>();
        PlacesRequest p = new PlacesRequest();
        p.execute(u);
        ArrayList<Location> restaurants;
        try {
            restaurants = (ArrayList<Location>) p.get();
            if (restaurants.size() > 0) {
                for (int i = 0; i < restaurants.size(); i++) {
                    locations.add(restaurants.get(i).getProvider());
                }
            }
            mHandler.sendMessage(Message.obtain(mHandler, 1, locations));

        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }
}
