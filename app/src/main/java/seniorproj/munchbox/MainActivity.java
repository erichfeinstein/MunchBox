package seniorproj.munchbox;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private List<ListItem> listItems;

    LinkedList<JournalEntry> Journal = new LinkedList<JournalEntry>();
    public Image newEntryPhoto = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.entriesView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listItems = new ArrayList<>();

        //Fill up list w/ dummy entries
        for (int i = 0; i <= 100; i++) {
            ListItem listItem = new ListItem("Burger " + (i+1), "Jolly");
            listItems.add(listItem);
        }

        adapter = new MyAdapter(listItems, this);
        recyclerView.setAdapter(adapter);
    }

    public void createEntry(Image newEntryPhoto)
    {
        Journal.add(new JournalEntry(newEntryPhoto));
    }


}
