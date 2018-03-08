package seniorproj.munchbox;

/**
 * Created by Jxy367 on 3/8/2018.
 */

public class Distance {
    private String miles; //For user
    private int meters; //For sorting

    public Distance(String distanceMiles, int distanceMeters){
        miles = distanceMiles;
        meters = distanceMeters;
    }

    public String getMiles(){
        return miles;
    }

    public int getMeters(){
        return meters;
    }

    public String toString(){
        return "Miles: " + miles + ", Meters: " + Integer.toString(meters);
    }
}
