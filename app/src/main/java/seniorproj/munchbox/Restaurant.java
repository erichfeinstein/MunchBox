package seniorproj.munchbox;

import android.location.Location;

public class Restaurant{
    private String name;
    private Double lat;
    private Double lng;

    public Restaurant(){
        name = "";
        lat = 0.0;
        lng = 0.0;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public void setName(String name){
        this.name = name;

    }

    public String getName(){
        return name;
    }
}
