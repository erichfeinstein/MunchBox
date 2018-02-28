package seniorproj.munchbox;

import android.graphics.Bitmap;
import android.media.Image;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.WindowManager;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private List<JournalEntry> journal;

    //LinkedList<JournalEntry> Journal = new LinkedList<JournalEntry>();
    public Image newEntryPhoto = null;
    private String recentImagePath;
    static final int REQUEST_IMAGE_CAPTURE = 1;

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
            listItem.setIdentifier(i);
            listItem.setPhotoID(R.drawable.sample_image);
            journal.add(listItem);
        }

        adapter = new MyAdapter(journal, this);
        recyclerView.setAdapter(adapter);
    }

    public void createEntryButton(View view)
    {
        /* Prompt the user for a picture */
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
        /* The camera only comes up now after I press the back button. Need to test on phone to see if this always the case. */
    }

    public void createEntry(Bitmap newEntryPhoto)
    {
        journal.add(new JournalEntry());
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            createEntry(imageBitmap);
            Intent intent = new Intent(MainActivity.this, EditEntry.class);
            startActivity(intent);
        }
    }

}
