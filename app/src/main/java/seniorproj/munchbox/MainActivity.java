package seniorproj.munchbox;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private PopupWindow popup;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private static ArrayList<JournalEntry> journal;
    private static ArrayList<JournalEntry> journalCopy;

    private LocationListener locationListener;
    private static LocationManager locationManager;
    private float accuracy;
    private Location currentLocation;
    private String lastNetwork;

    private int lastSortType = 0; //0 is date, 1 is review, 2 is alphabetical, 3 is distance...
    private int newID = 0;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int PERMISSION_ALL = 1;
    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION};
    //File internalStorageDir = getFilesDir();
    //File savedJournal = new File(internalStorageDir, "savejournal.csv");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Start LocationManager and LocationListener
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                if(currentLocation == null) {
                    currentLocation = new Location(LocationManager.NETWORK_PROVIDER);
                    currentLocation.setLatitude(0.0);
                    currentLocation.setLongitude(0.0);
                }
                else if(location.distanceTo(currentLocation) > 100){

                    currentLocation = location;
                    updateDistances();
                    lastNetwork = location.getProvider();
                    /*
                    System.out.println(location.getProvider());
                    System.out.println("Updated due to distance change");
                    System.out.println(currentLocation.getLatitude() + ", " + currentLocation.getLongitude() + ": " + currentLocation.getAccuracy());
                    */
                }
                else if(location.getAccuracy() < accuracy){
                    accuracy = location.getAccuracy();
                    updateDistances();
                    lastNetwork = location.getProvider();
                    /*
                    System.out.println(location.getProvider());
                    System.out.println("Updated due to accuracy change");
                    System.out.println(currentLocation.getLatitude() + ", " + currentLocation.getLongitude() + ": " + currentLocation.getAccuracy());
                    */
                }
                else{
                    //System.out.println(location.getProvider() + ": ");
                    //System.out.println("Distance to current best guess: " + location.distanceTo(currentLocation));
                    //System.out.println("Radial Accuracy: " + location.getAccuracy());
                }
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}

        };
        //Start requesting updates
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            System.out.println("No Fine Location Permission");
        }
        else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }

        //Read the journal
        if (journal == null) {
            SharedPreferences prefs = getPreferences(MODE_PRIVATE);
            Gson gson = new Gson();
            String json = prefs.getString("Journal", "");
            journal = gson.fromJson(json, new TypeToken<List<JournalEntry>>(){}.getType());
            if (journal == null) journal = new ArrayList<>();
        }
        //Re-assign ID values
        int i = 0;
        while (i < journal.size()) {
            journal.get(i).setIdentifier(i);
            i++;
        }
        updateDistances();
        newID = i; //Save the new ID value

        adapter = new MyAdapter(journal, this);
        recyclerView = findViewById(R.id.entriesView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        searchView = findViewById(R.id.searchField);
        boolean resetList = getIntent().getBooleanExtra("resetList", false);
        if (resetList) filter("");
        reloadList();

        journalCopy = new ArrayList<>(journal);

        //Save journal to SharedPrefs using Gson
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(journal);
        prefsEditor.putString("Journal", json);
        prefsEditor.apply();

        if (!hasPermissions(this, permissions)) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_ALL);
        }

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //Create a .nomedia file so images captured by MunchBox don't get scanned by MediaScanner
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MunchCam");
        File nomedia = new File(mediaStorageDir.getPath() + File.separator + ".nomedia");
        try {
            nomedia.createNewFile();
        } catch (IOException e) {
            e.getMessage();
        }

        //Filter results by text of searchField
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query == null) {
                    journal = new ArrayList<>(journalCopy);
                    reloadList();
                    return true;
                }
                else {
                    filter(query);
                    reloadList();
                    return true;
                }
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText == null) {
                    journal = new ArrayList<>(journalCopy);
                    reloadList();
                    return true;
                }
                else {
                    filter(newText);
                    reloadList();
                    return true;
                }
            }
        });
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });
    }

    @Override
    public void onResume() {
        //Resume the search
        super.onResume();
        searchView.setQuery(searchView.getQuery().toString(), true);

        //Read the journal from SharedPrefs

        SharedPreferences prefsJournal = getPreferences(MODE_PRIVATE);
        Gson gsonJournalRead = new Gson();
        String jsonJournal = prefsJournal.getString("Journal", "");
        journal = gsonJournalRead.fromJson(jsonJournal, new TypeToken<List<JournalEntry>>(){}.getType());
        if (journal == null) journal = new ArrayList<>();

        journalCopy = new ArrayList<>(journal);

        //Re-enable create button after it was disabled to prevent double clicking
        ImageButton createEntryButton = findViewById(R.id.createNewEntry);
        createEntryButton.setEnabled(true);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();

        //Get all possible information for creating a new entry, editing an entry, and deleting an entry
        String name = prefs.getString("name", "");
        String restaurant = prefs.getString("restaurant", "");
        String description = prefs.getString("description", "");
        String imgPath = prefs.getString("imgPath", "");
        int rating = prefs.getInt("rating", 0);
        int id = prefs.getInt("id", -1);
        double latitude = Double.longBitsToDouble(prefs.getLong("latitude", 0));
        double longitude = Double.longBitsToDouble(prefs.getLong("longitude", 0));

        //Get tags from Gson
        Gson gson = new Gson();
        String json = prefs.getString("tags", "");
        ArrayList tags = gson.fromJson(json, new TypeToken<List<String>>(){}.getType());

        //Determine if we are creating a new entry, editing an entry, or deleting an entry (or none)
        boolean toAdd = prefs.getBoolean("toAdd", false);
        boolean toEdit = prefs.getBoolean("toEdit", false);
        boolean toDelete = prefs.getBoolean("toDelete", false); //no id: default value

        //Make new entry
        if (toAdd) {
            System.out.println("Making new entry with ID: " + journal.size());
            JournalEntry newEntry = new JournalEntry();
            newEntry.setNameOfDish(name);
            newEntry.setRestaurantName(restaurant);
            newEntry.setDescription(description);
            newEntry.setIdentifier(newID);
            newID++; //set ID to next ID available an increment available ID
            newEntry.setPhotoPath(imgPath);
            newEntry.setRating(rating);
            newEntry.setTags(tags);
            newEntry.setLongitude(longitude);
            newEntry.setLatitude(latitude);
            journal.add(newEntry);
        }

        //Update existing  entry
        if (toEdit) {
            System.out.println("Editing entry");
            //Find entry with id
            for (int i = 0; i < journal.size(); i++) {
                if (id == journal.get(i).getIdentifier()) {
                    JournalEntry updateEntry = journal.get(i);
                    System.out.println("Found entry with ID " + id);

                    updateEntry.setNameOfDish(name);
                    updateEntry.setRestaurantName(restaurant);
                    updateEntry.setDescription(description);
                    updateEntry.setPhotoPath(imgPath);
                    updateEntry.setRating(rating);
                    updateEntry.setTags(tags);
                    break;
                }
            }
        }

        //Delete entry with given ID
        if (toDelete) {
            System.out.println("Deleting entry");
            //Delete entry
            for (int i = 0; i < journal.size(); i++) {
                if (journal.get(i).getIdentifier() == id) {
                    journal.remove(i);
                }
            }
        }

        //Remove command to delete entry
        editor.remove("toAdd");
        editor.remove("toEdit");
        editor.remove("toDelete");
        editor.remove("id");

        //Remove all entry information (this prevents entries from being created every time MainActivity resumes)
        editor.remove("name");
        editor.remove("restaurant");
        editor.remove("description");
        editor.remove("imgPath");
        editor.remove("rating");
        editor.remove("id");
        editor.remove("tags");

        //Remove add and edit protocols
        editor.remove("add");
        editor.remove("edit");

        editor.apply();

        //From onCreate
        adapter = new MyAdapter(journal, this);
        recyclerView = findViewById(R.id.entriesView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        System.out.println("Journal size: " + journal.size());

        //Write journal to SharedPrefs
        //Using same SharedPrefs object as the reader in the beginning of onResume
        SharedPreferences.Editor writerEditor = prefsJournal.edit();
        Gson gsonJournal = new Gson();
        String write = gsonJournal.toJson(journal);
        writerEditor.putString("Journal", write);
        writerEditor.apply();

        updateDistances();

        if (lastSortType == 0) Collections.sort(journal, Comparators.getDateComparator());
        if (lastSortType == 1) Collections.sort(journal, Comparators.getRateComparator());
        if (lastSortType == 2) Collections.sort(journal, Comparators.getDishNameComparator());
        //TODO connected to Distance Comparator
        if (lastSortType == 3) Collections.sort(journal, Comparators.getDistanceComparator());

        reloadList();
    }

    public static void filter(String text) {
        text = text.toLowerCase();
        journal.clear();
        for(JournalEntry item: journalCopy){
            ArrayList<String> keywords = item.getKeywords();
            for (int i = 0; i < keywords.size(); i++) {
                if (keywords.get(i).toLowerCase().contains(text)) {
                    journal.add(item);
                    break;
                }
            }
        }
    }

    @SuppressWarnings("ResourceType")
    public void sortByButton(View view) {
        LayoutInflater inflater = getLayoutInflater();
        popup = new PopupWindow(inflater.inflate(R.layout.activity_sort_by_pop,(ViewGroup)findViewById(R.layout.activity_main)));
        popup.setFocusable(true);
        popup.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.showAsDropDown(view);
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
    }

    public void closePopup(){
        popup.dismiss();
    }

    public void createEntryButton(View view) {
        view.setEnabled(false);
        /* Prompt the user for a picture */
        Intent takePictureIntent = new Intent(MainActivity.this, MunchCam.class);
        startActivity(takePictureIntent);
    }

    //Changes what the list UI displays
    public void reloadList()
    {
        recyclerView.setAdapter(adapter);
    }

    public void sortByDate(View view) {
        Collections.sort(journal, Comparators.getDateComparator());
        reloadList();
        closePopup();
        lastSortType = 0;
    }

    public void sortByReview(View view)
    {
        Collections.sort(journal,Comparators.getRateComparator());
        reloadList();
        closePopup();
        lastSortType = 1;
    }

    public void sortAlphabeticallyByDishName(View view)
    {
        Collections.sort(journal, Comparators.getDishNameComparator());
        reloadList();
        closePopup();
        lastSortType = 2;
    }

    public void sortByDistance(View view)
    {
        Collections.sort(journal, Comparators.getDistanceComparator());
        reloadList();
        closePopup();
        lastSortType = 3;
    }

    public List<JournalEntry> removeNonFavorites(View view)
    {
        journalCopy = new ArrayList(journal);

        for(JournalEntry j: journalCopy)
        {
            if(!j.isFavorite())
            {
                journalCopy.remove(j);
            }
        }

        return journalCopy;
    }

    public JournalEntry findEntryByName(String name, ArrayList<JournalEntry> journal)
    {
        for(JournalEntry j: journal)
        {
            if(name.equalsIgnoreCase(j.getNameOfDish()))
            {
                return j;
            }
        }
        return null;
    }

    private void createEntry(Bitmap newEntryPhoto, String imagePath) {
        journal.add(new JournalEntry(newEntryPhoto, imagePath));
    }

    public List<JournalEntry> getJournal() {
        return journal;
    }

    //For permissions checking
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for(int i: grantResults) {
            if (!(i == PackageManager.PERMISSION_GRANTED)) {
                alertUser();
            }
        }
    }

    //Generates a dialog popup if user has not accepted permissions
    public void alertUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                ActivityCompat.requestPermissions(MainActivity.this, permissions, PERMISSION_ALL);

            }
        });
        builder.setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                System.exit(0);
            }
        });
        builder.setMessage(R.string.dialog_message).setTitle(R.string.dialog_title);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @SuppressWarnings("ResourceType")
    public void openInfoButton(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/munchboxweb/home"));
        startActivity(browserIntent);
    }

    //For checking permissions
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void resetJournal() {
        filter("");
    }

    public static LocationManager getLocationManager() {
        return locationManager;
    }

    public void updateDistances() {
        //System.out.println("Update Distance Started");
        Location location;
        //Start requesting updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if(lastNetwork == null){
                location = new Location("");
                location.setLatitude(39.8283);
                location.setLongitude(-98.5795);
            }
            else if (lastNetwork.equals(LocationManager.GPS_PROVIDER)) {
                location = locationManager.getLastKnownLocation(lastNetwork);
            }
            else {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            ArrayList<Location> locations = new ArrayList<>();
            for (JournalEntry j : journal) {
                locations.add(j.getLocation());
            }
            if(!locations.isEmpty()) {
                URL url = URLMaker.distanceMatrixURL(this, location, locations);
                DistanceMatrixRequest dmRequest = new DistanceMatrixRequest();
                List<Distance> distances = new ArrayList<>();
                try {
                    distances = dmRequest.execute(url).get();
                } catch (Exception e) {
                    System.out.println("Update Distances Failed: " + e.toString());
                }
                for (JournalEntry j : journal) {
                    if (distances == null) {
                        System.out.println("dmrequest distances is null");
                        j.setDistanceMeters(0);
                        j.setDistance("0");
                    } else if (!distances.isEmpty()) {
                        Distance distance = distances.remove(0);
                        j.setDistanceMeters(distance.getMeters());
                        j.setDistance(distance.getMiles());
                        //System.out.println(j.getDistanceMeters());
                    } else {
                        //System.out.println("not enough distances");
                        j.setDistanceMeters(0);
                        j.setDistance("0");
                    }
                    //System.out.println(j.getDistanceMeters());
                    //System.out.println(j.getDistance());
                }
            }
        }
        else {
            System.out.print("Update Distance: Does Not Have Fine Location Permission");
        }
        //System.out.println("Update Distance Finished");
    }
}
