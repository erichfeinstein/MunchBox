package seniorproj.munchbox;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private List<JournalEntry> journal;

    private String recentImagePath;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    static final int THUMBSIZE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int PERMISSION_ALL = 1;
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if(!hasPermissions(this, permissions)){
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_ALL);
        }

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
            journal.add(listItem);
        }

        adapter = new MyAdapter(journal, this);
        recyclerView.setAdapter(adapter);
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

    private void createEntry(Bitmap newEntryPhoto, String imagePath)
    {
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

    //TODO: If camera or read/write permissions are not given, app will not work and must close

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
