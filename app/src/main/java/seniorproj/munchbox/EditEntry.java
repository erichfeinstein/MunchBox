package seniorproj.munchbox;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.api.services.vision.v1.model.EntityAnnotation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EditEntry extends Activity {


    private RecyclerView tagsRecyclerView;
    private RecyclerView.Adapter tagsAdapter;
    private ArrayList<String> tags;

    private RecyclerView locationsRecyclerView;
    private RecyclerView.Adapter locationsAdapter;
    private ArrayList<String> locations;

    private String imgPath;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_entry);

        //Get image and display it
        imgPath = getIntent().getStringExtra("imageAddr");
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap image = BitmapFactory.decodeFile(imgPath);
        ImageView myImage = (ImageView) findViewById(R.id.imageView);
        myImage.setImageBitmap(image);

        tagsRecyclerView = (RecyclerView) findViewById(R.id.tagsView);
        tagsRecyclerView.setHasFixedSize(true);
        tagsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        PhotoAnalyzer labelGen = new PhotoAnalyzer(imgPath);

        locationsRecyclerView = (RecyclerView) findViewById(R.id.locationsView);
        locationsRecyclerView.setHasFixedSize(true);
        locationsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        //     tags = labelGen.getLabels();

        //Run GPS location analysis. If null... ?
        if (locations == null) {
            locations = new ArrayList<String>();
        }
        //Run image analysis. If null... "Add tags" in place of list
        if (tags == null) {
            tags = new ArrayList<String>();
        }

        //Dummy tags and locations
        //TODO remove
        tags.add("Burger");
        tags.add("Sushi");
        tags.add("Spicy");
        tags.add("Mexican");
        tags.add("Asian");
        tags.add("Chinese");
        tags.add("Yummy");
        tags.add("Sour");
        tags.add("Sweet");
        tags.add("Noodles");
        tags.add("Sexy");
        locations.add("Jolly");
        locations.add("Superior Pho");
        locations.add("Five Guys");
        locations.add("Chipotle");
        locations.add("Simply Greek");
        locations.add("Chopstick");
        locations.add("Qdoba");
        locations.add("Potbelly");

        loadLocations(locations);
        loadTags(tags);
    }

    public void saveEntryButton(View view) {
        EditText name = (EditText) findViewById(R.id.name);
        EditText restaurant = (EditText) findViewById(R.id.restaurant);
        EditText description = (EditText) findViewById(R.id.description);
        RatingBar rating = (RatingBar) findViewById(R.id.rating);
        int ratingAsInt = (int) (rating.getRating() * 2.0); //Multiply by 2 because ratings are stored on a 1-10 scale using ints

        Intent intent = new Intent(EditEntry.this, MainActivity.class);
        intent.putExtra("name", name.getText().toString());
        intent.putExtra("restaurant", restaurant.getText().toString());
        intent.putExtra("description", description.getText().toString());
        intent.putExtra("imgPath", imgPath);
        intent.putExtra("rating", ratingAsInt);

        startActivity(intent);
    }

    private void loadLocations(ArrayList<String> locationsList) {
        locationsAdapter = new LocationsAdapter(locationsList, this);
        locationsRecyclerView.setAdapter(locationsAdapter);
    }

    private void loadTags(ArrayList<String> tagsList) {
        tagsAdapter = new TagsAdapter(tagsList, this);
        tagsRecyclerView.setAdapter(tagsAdapter);
    }

    @Override
    public void onBackPressed(){
        finish();
        Intent backToList = new Intent(EditEntry.this, MainActivity.class);
        //If StringExtra imageAddr is not null, delete image from directory (entry was in process of being created, but we don't want to save the image)
        startActivity(backToList);
    }
}