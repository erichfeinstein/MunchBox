package seniorproj.munchbox;

import android.media.Image;
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
        Intent intent = new Intent(MainActivity.this, EditEntry.class);
        startActivity(intent);
    }
    public void viewEntryButton(View view)
    {
        Intent intent = new Intent(MainActivity.this, ViewEntry.class);
        startActivity(intent);
    }

    /*public void createEntry(Image newEntryPhoto)
    {
        Journal.add(new JournalEntry());
    }*/


}
