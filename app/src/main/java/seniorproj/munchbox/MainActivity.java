package seniorproj.munchbox;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private static ArrayList<JournalEntry> journal;

    private String recentImagePath;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int THUMBSIZE = 200;
    static final int PERMISSION_ALL = 1;
    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
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

        //Detect if new entry needs to be created
        String name = getIntent().getStringExtra("name");
        String restaurant = getIntent().getStringExtra("restaurant");
        String description = getIntent().getStringExtra("description");
        String imgPath = getIntent().getStringExtra("imgPath");
        if (name != null && restaurant != null && description != null) {
            JournalEntry newEntry = new JournalEntry();
            newEntry.setNameOfDish(name);
            newEntry.setRestaurantName(restaurant);
            newEntry.setDescription(description);
            newEntry.setIdentifier(journal.size());
            newEntry.setPhotoPath(imgPath);
            journal.add(newEntry);
        }

        //Save journal to SharedPrefs using Gson
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(journal);
        System.out.println(json);
        prefsEditor.putString("Journal", json);
        prefsEditor.commit();

        if (!hasPermissions(this, permissions)) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_ALL);
        }

        recyclerView = (RecyclerView) findViewById(R.id.entriesView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        reloadList(journal);

        //Create a .nomedia file so images captured by MunchBox don't get scanned by MediaScanner
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MunchCam");
        File nomedia = new File(mediaStorageDir.getPath() + File.separator + ".nomedia");
        try {
            nomedia.createNewFile();
        } catch (IOException e) {
            e.getMessage();
        }
    }

    public void createEntryButton(View view) {
        /* Prompt the user for a picture */
        Intent takePictureIntent = new Intent(MainActivity.this, MunchCam.class);
        startActivity(takePictureIntent);
    }

    //Changes what the list UI displays
    public void reloadList(ArrayList<JournalEntry> newList)
    {
        adapter = new MyAdapter(newList, this);
        recyclerView.setAdapter(adapter);
    }

    //Basic Search Function will be improved later
    public ArrayList<JournalEntry> searchTerm(String search)
    {
        ArrayList<JournalEntry> tempList = new ArrayList<JournalEntry>();
        for(JournalEntry i: journal)
        {
            ArrayList<String> words = i.getKeywords();
            for(String x: words)
            {
                x = x.toLowerCase(Locale.US);
                search = search.toLowerCase(Locale.US);
                if(x.indexOf(search) != -1)
                {
                    tempList.add(i);
                }
            }
        }
        return tempList;
    }

    //Only returns restaurants with the search term
    public ArrayList<JournalEntry> searchTermRestaurantsOnly(String search)
    {
        ArrayList<JournalEntry> tempList = new ArrayList<JournalEntry>();
        for(JournalEntry i: journal)
        {
            String restName = i.getRestaurantName();
            restName = restName.toLowerCase(Locale.US);
            search = search.toLowerCase(Locale.US);
            if(restName.indexOf(search) != -1)
            {
                tempList.add(i);
            }
        }
        return tempList;
    }

    //Sort By Specific Things
    public ArrayList<JournalEntry> searchByDate(Date searchDay)
    {
        ArrayList<JournalEntry> tempList = new ArrayList<>();
        for(JournalEntry search: journal)
        {
            if(search.getEntryDate().compareTo(searchDay) == 0)
            {
                tempList.add(search);
            }
        }
        return tempList;
    }

    public ArrayList<JournalEntry> sortByReview(String search)
    {
        ArrayList<JournalEntry> tempList = searchTerm(search);
        for(JournalEntry found: tempList)
        {
            System.out.println(found.getIdentifier() + ": " + found.getRating());
        }
        ArrayList<JournalEntry> sortedList = new ArrayList<JournalEntry>();
        for(int i = 10; i >= 0; i--)
        {
            for(JournalEntry found : tempList)
            {
                if (found.getRating() == i)
                {
                    sortedList.add(found);
                }
            }
        }
        return sortedList;
    }

    private void createEntry(Bitmap newEntryPhoto, String imagePath) {
        journal.add(new JournalEntry(newEntryPhoto, recentImagePath));
    }

    //TODO does this ever get called? I don't think so -Eric
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap thumb = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(recentImagePath), THUMBSIZE, THUMBSIZE);
            createEntry(thumb, recentImagePath);
            Intent intent = new Intent(MainActivity.this, EditEntry.class);
            intent.putExtra("foodImagePath", recentImagePath);
            startActivity(intent);
            recentImagePath = "";
        }
    }

    private File createImageFile() throws IOException {
        String time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "JPEG_" + time + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File photo = File.createTempFile(fileName, ".jpg", storageDir);
        recentImagePath = photo.getAbsolutePath();
        return photo;
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
}
