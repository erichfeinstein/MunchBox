package seniorproj.munchbox;

/**
 * Created by Jxy367 on 3/8/2018.
 */

public class TravelTime {
    private String time; //For users
    private int seconds; //For sorting (maybe)

    public TravelTime(String timeString, int timeSeconds){
        time = timeString;
        seconds = timeSeconds;
    }

    public String getTime(){
        return time;
    }

    public int getSeconds(){
        return seconds;
    }

    public String toString(){
        return "Time: " + time + ", Time in Seconds: " + Integer.toString(seconds);
    }

}
