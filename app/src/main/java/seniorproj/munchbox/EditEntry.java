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

import java.util.ArrayList;
import java.util.List;

public class EditEntry extends Activity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<String> tags;

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

        recyclerView = (RecyclerView) findViewById(R.id.tagsView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        PhotoAnalyzer labelGen = new PhotoAnalyzer(imgPath);
   //     tags = labelGen.getLabels();

        //Run image analysis
        if (tags == null) {
            tags = new ArrayList<String>();
            tags.add("tag 1");
            tags.add("tag 2");
            tags.add("tag 3");
            tags.add("tag 4");
            tags.add("tag 1");
            tags.add("tag 2");
            tags.add("tag 3");
            tags.add("tag 4");
            tags.add("tag 1");
            tags.add("tag 2");
            tags.add("tag 3");
            tags.add("tag 4");
            tags.add("tag 1");
            tags.add("tag 2");
            tags.add("tag 3");
            tags.add("tag 4");
        }
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

    private void loadTags(List<String> tagsList) {
        adapter = new TagsAdapter(tagsList, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed(){
        finish();
        Intent backToList = new Intent(EditEntry.this, MainActivity.class);
        //If StringExtra imageAddr is not null, delete image from directory (entry was in process of being created, but we don't want to save the image)
        startActivity(backToList);
    }
}