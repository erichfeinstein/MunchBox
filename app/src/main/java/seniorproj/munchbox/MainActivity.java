package seniorproj.munchbox;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;

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

    private ArrayList<JournalEntry> journal;

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


        if (!hasPermissions(this, permissions)) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_ALL);
        }

        recyclerView = (RecyclerView) findViewById(R.id.entriesView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);



        if(loadData() != null)
        {
            journal = loadData();
        }
        else
        {
            journal = new ArrayList<>();
        }



        //Fill up list w/ dummy entries
        int year = 2015;
        int month = 1;
        int day = 14;

        int i;
        if(journal.isEmpty())
        {
            i = 0;
        }
        else
        {
            i = journal.size();
        }
        for (i = 0; i <= 100; i++) {
            JournalEntry listItem = new JournalEntry();

            if (i % 3 == 0) {
                listItem.renameDish("Ramen Noodles: " + i);
            } else if (i % 5 == 0) {
                listItem.renameDish("Meatball Sub: " + i);
            } else if (i % 7 == 0) {
                listItem.renameDish("Delicious Pancakes: " + i);
            } else {
                listItem.renameDish("Bacon Cheeseburger: " + i);
            }


            if (i % 3 == 0) {
                listItem.changeRestaurantName("Otani Noodle");
            } else if (i % 5 == 0) {
                listItem.changeRestaurantName("Subway");
            } else if (i % 7 == 0) {
                listItem.changeRestaurantName("IHOP");
            } else {
                listItem.changeRestaurantName("Jolly Scholar");
            }


            if (i % 3 == 0) {
                listItem.newTag("Ramen");
                listItem.newTag("Noodle");
            } else if (i % 5 == 0) {
                listItem.newTag("Meatball");
                listItem.newTag("Sandwich");
            } else if (i % 7 == 0) {
                listItem.newTag("Fluffy");
                listItem.newTag("Pancake");
            } else {
                listItem.newTag("Burger");
                listItem.newTag("Bacon");
            }
            listItem.newTag("Food");


            Random r = new Random();
            int randomDescription = r.nextInt((6 - 0) + 1) + 0;

            if (randomDescription == 1) {
                listItem.newDescription("I don't like this very much.");
            } else if (randomDescription == 2) {
                listItem.newDescription("This is just ok");
            } else if (randomDescription == 3) {
                listItem.newDescription("Better than I was expecting");
            } else if (randomDescription == 4) {
                listItem.newDescription("Worse than I had hoped for");
            } else if (randomDescription == 5) {
                listItem.newDescription("I wish this was better");
            } else {
                listItem.newDescription("This is great!");
            }

            int randomRating = r.nextInt((10 - 0) + 1) + 0;
            listItem.rateDish(randomRating);

            Date dayOfVisit = new GregorianCalendar(year, month, day).getTime();
            listItem.setEntryDate(dayOfVisit);

            listItem.setIdentifier(i);
            listItem.setPhotoID(R.drawable.sample_image);
            journal.add(listItem);

            day++;
            if(day >= 30)
            {
                day = 1;
                month++;
                if(month >= 12)
                {
                    month = 0;
                    year++;
                }
            }
        }

        reloadList(journal);

        Date searchForDay = new GregorianCalendar(2015, 2, 14).getTime();
        ArrayList<JournalEntry> searchedList = searchTerm("otani");// = searchByDate(searchForDay);
        reloadList(searchedList);


        //Create a .nomedia file so images captured by MunchBox don't get scanned by MediaScanner
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MunchCam");
        File nomedia = new File(mediaStorageDir.getPath() + File.separator + ".nomedia");
        try {
            nomedia.createNewFile();
        } catch (IOException e) {
            e.getMessage();
        }

        saveData();

    }

    public void createEntryButton(View view) {
        /* Prompt the user for a picture */
        Intent takePictureIntent = new Intent(MainActivity.this, MunchCam.class);
        startActivity(takePictureIntent);
        /*
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            String photoPath = "";
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                System.out.println("Error creating file for image container.");
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "seniorproj.munchbox.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        /* The camera only comes up now after I press the back button. Need to test on phone to see if this always the case. */

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

    //Saves the list of journal entries
    public void saveData()
    {
        String FILENAME = "SavedJournal";
        FileOutputStream outputStream = null;
        try
        {
            outputStream = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            ObjectOutputStream objectStream = new ObjectOutputStream(outputStream);
            for(JournalEntry i: journal)
            {
                objectStream.writeObject(i);
            }
            objectStream.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public ArrayList<JournalEntry> loadData()
    {
        String FILENAME = "SavedJournal";
        FileInputStream inputStream = null;
        ArrayList<JournalEntry> returnList = new ArrayList<JournalEntry>();
        try
        {
            inputStream = openFileInput(FILENAME);
            ObjectInputStream objectStream = new ObjectInputStream(inputStream);
            returnList = (ArrayList<JournalEntry>) objectStream.readObject();
            objectStream.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return returnList;
    }

    private void createEntry(Bitmap newEntryPhoto, String imagePath) {
        journal.add(new JournalEntry(newEntryPhoto, recentImagePath));
    }

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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < grantResults.length; i++) {
            if (!(grantResults.length > 0 && grantResults[i] == PackageManager.PERMISSION_GRANTED)) {
                alertUser();
            }
        }
    }

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
