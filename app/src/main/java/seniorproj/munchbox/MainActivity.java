package seniorproj.munchbox;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SearchView searchView;
    private static RecyclerView recyclerView;
    private static MyAdapter adapter;
    private static ArrayList<JournalEntry> journal;
    private static ArrayList<JournalEntry> journalCopy;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int PERMISSION_ALL = 1;
    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION};
    //File internalStorageDir = getFilesDir();
    //File savedJournal = new File(internalStorageDir, "savejournal.csv");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (journal == null) {
            SharedPreferences prefs = getPreferences(MODE_PRIVATE);
            Gson gson = new Gson();
            String json = prefs.getString("Journal", "");
            journal = gson.fromJson(json, new TypeToken<List<JournalEntry>>(){}.getType());
            if (journal == null) journal = new ArrayList<JournalEntry>();
        }

        adapter = new MyAdapter(journal, this);
        recyclerView = (RecyclerView) findViewById(R.id.entriesView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        searchView = findViewById(R.id.searchField);
        boolean resetList = getIntent().getBooleanExtra("resetList", false);
        if (resetList) filter("");
        reloadList(journal);

        journalCopy = new ArrayList<>(journal);

        //Save journal to SharedPrefs using Gson
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(journal);
        prefsEditor.putString("Journal", json);
        prefsEditor.commit();

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
                    reloadList(journal);
                    return true;
                }
                else {
                    filter(query);
                    reloadList(journal);
                    return true;
                }
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText == null) {
                    journal = new ArrayList<>(journalCopy);
                    reloadList(journal);
                    return true;
                }
                else {
                    filter(newText);
                    reloadList(journal);
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
        if (journal == null) journal = new ArrayList<JournalEntry>();

        //Re-enable create button after it was disabled to prevent double clicking
        Button createEntryButton = (Button) findViewById(R.id.createNewEntry);
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
        //Get tags from Gson
        Gson gson = new Gson();
        String json = prefs.getString("tags", "");
        ArrayList tags = gson.fromJson(json, new TypeToken<List<String>>(){}.getType());

        //Determine if we are creating a new entry, editing an entry, or deleting an entry (or none)
        boolean toAdd = prefs.getBoolean("add", false);
        boolean toEdit = prefs.getBoolean("edit", false);
        boolean toDelete = prefs.getBoolean("toDelete", false); //no id: default value

        //Make new entry
        if (toAdd && imgPath != null) {
            System.out.println("Making new entry with ID: " + journal.size());
            JournalEntry newEntry = new JournalEntry();
            newEntry.setNameOfDish(name);
            newEntry.setRestaurantName(restaurant);
            newEntry.setDescription(description);
            newEntry.setIdentifier(journal.size());
            newEntry.setPhotoPath(imgPath);
            newEntry.setRating(rating);
            newEntry.setTags(tags);
            journal.add(newEntry);
        }
        //Update existing  entry
        if (toEdit && imgPath != null) {
            System.out.println("Editing entry");
            //Find entry with id
            for (int i = 0; i < journal.size(); i++) {
                if (id == journal.get(i).getIdentifier()) {
                    JournalEntry updateEntry = journal.get(i);
                    System.out.println("Found entry with ID " + id);

                    updateEntry.setNameOfDish(name);
                    updateEntry.setRestaurantName(restaurant);
                    updateEntry.setDescription(description);
                    updateEntry.setIdentifier(journal.size());
                    updateEntry.setPhotoPath(imgPath);
                    updateEntry.setRating(rating);
                    updateEntry.setTags(tags);
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

        editor.commit();

        //From onCreate
        adapter = new MyAdapter(journal, this);
        recyclerView = (RecyclerView) findViewById(R.id.entriesView);
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
        writerEditor.commit();

        reloadList(journal);
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
        PopupWindow popup = new PopupWindow(inflater.inflate(R.layout.activity_sort_by_pop,(ViewGroup)findViewById(R.layout.activity_main)));
        popup.setFocusable(true);
        popup.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.showAsDropDown(view);
    }

    public void createEntryButton(View view) {
        view.setEnabled(false);
        /* Prompt the user for a picture */
//        finish();
        Intent takePictureIntent = new Intent(MainActivity.this, MunchCam.class);
        startActivity(takePictureIntent);
    }

    //Changes what the list UI displays
    public static void reloadList(ArrayList<JournalEntry> newList)
    {
        recyclerView.setAdapter(adapter);
    }

    public void sortByDate(View view) {
        Collections.sort(journal, Comparators.getDateComparator());
        reloadList(journal);
    }

    public void sortByReview(View view)
    {
        Collections.sort(journal,Comparators.getRateComparator());
        reloadList(journal);
        /*
        journalCopy = new ArrayList(journal);
        journal.clear();
        for(int i = 10; i >= 0; i--)
        {
            for(JournalEntry found : journalCopy)
            {
                if (found.getRating() == i)
                {
                    journal.add(found);
                }
            }
        }
        reloadList(journal);
        */
    }

    public void sortAlphabeticallyByDishName(View view)
    {
        /*
        journalCopy = new ArrayList(journal);
        ArrayList<String> stringList = new ArrayList<String>();
        for(JournalEntry j: journalCopy)
        {
            stringList.add(j.getNameOfDish());
        }
        Collections.sort(stringList, String.CASE_INSENSITIVE_ORDER);
        journal.clear();
        for(String s: stringList)
        {
            journal.add(findEntryByName(s, journalCopy));
        }

        reloadList(journal);
        */
        Collections.sort(journal, Comparators.getDishNameComparator());
        reloadList(journal);
    }

    public void sortByDistance(View view)
    {
        /*
        double currentX = 40;
        double currentY = 60;
        checkDistances(currentX, currentY);

        journalCopy = new ArrayList(journal);
        journal.clear();

        checkDistances(currentX, currentY);

        for(JournalEntry j: journalCopy)
        {
            double dist = j.getDistanceLastChecked();
            for(int i = 0; i < journal.size(); i++)
            {
                if(dist <= journal.get(i).getDistanceLastChecked())
                {
                    journal.add(i, j);
                    i = journal.size();
                }
            }
        }
        */
        reloadList(journal);
    }

    public void sortByFrequency(View view)
    {
        journalCopy = new ArrayList(journal);
        journal.clear();
        int highestFrequency = 0;

        for(JournalEntry f: journalCopy)
        {
            if(f.getFrequency() > highestFrequency)
            {
                highestFrequency = f.getFrequency();
            }
        }
        while(highestFrequency > 0)
        {
            for (JournalEntry j : journalCopy)
            {
                if (j.getFrequency() == highestFrequency)
                {
                    journal.add(j);
                }
            }
        }
        reloadList(journal);
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

    public void checkDistances(double x, double y)
    {
        for(JournalEntry j: journal)
        {
            double newX = Math.pow(Math.abs(j.getXLocation() - x), 2);
            double newY = Math.pow(Math.abs(j.getYLocation() - y), 2);
            j.setDistanceLastChecked((Double)Math.sqrt(newX + newY));
        }
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
        for (int i = 0; i < grantResults.length; i++) {
            if (!(grantResults.length > 0 && grantResults[i] == PackageManager.PERMISSION_GRANTED)) {
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
}
