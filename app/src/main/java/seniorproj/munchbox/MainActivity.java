package seniorproj.munchbox;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    LinkedList<JournalEntry> Journal = new LinkedList<JournalEntry>();
    public Image newEntryPhoto = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void createEntry(Image newEntryPhoto)
    {
        Journal.add(new JournalEntry(newEntryPhoto));
    }


}
