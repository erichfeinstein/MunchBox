package seniorproj.munchbox;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.WindowManager;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private List<JournalEntry> journal;

    private String recentImagePath;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    static final int THUMBSIZE = 64;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.entriesView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        journal = new ArrayList<>();

        //Fill up list w/ dummy entries
        for (int i = 0; i <= 100; i++)
        {
            JournalEntry listItem = new JournalEntry();
            listItem.renameDish("Burger " + i);
            listItem.changeRestaurantName("Jolly Scholar");
            if(i % 5 == 0)
            {
                listItem.changeRestaurantName("Otani Noodle");
            }
            if(i % 5 == 0)
            {
                listItem.newTag("Noodle");
            }
            else
            {
                listItem.newTag("Burger");
            }
            listItem.newTag("Food");
            if(i % 4 == 0)
            {
                listItem.newDescription("I don't like this very much.");
            }
            else
            {
                listItem.newDescription("This is great!");
            }

            listItem.rateDish((i % 10) + 1);
            listItem.setIdentifier(i);
            listItem.setPhotoID(R.drawable.sample_image);
            System.out.println("---------- Name: " + listItem.getNameOfDish() + " --- Location: " + listItem.getRestaurantName() + " --- Description: " + listItem.getDescription() + " ---  Keywords: " + listItem.getKeywords() + " --- Rating: " + (float)((float)listItem.getRating()/2) + " --- Identifier: " + listItem.getIdentifier() + " --- PhotoID: " + listItem.getPhotoID());
            journal.add(listItem);
        }

        adapter = new MyAdapter(journal, this);
        recyclerView.setAdapter(adapter);
    }

    public void createEntryButton(View view) {
        /* Prompt the user for a picture */
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
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
    }

    private void createEntry(Bitmap newEntryPhoto, String imagePath)
    {
        journal.add(new JournalEntry(newEntryPhoto, recentImagePath));
        recentImagePath = "";
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap thumb = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(recentImagePath), THUMBSIZE, THUMBSIZE);
            createEntry(thumb, recentImagePath);
            Intent intent = new Intent(MainActivity.this, EditEntry.class);
            startActivity(intent);
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


}
