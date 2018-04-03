package seniorproj.munchbox;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ViewEntry extends Activity {

    private String dish;
    private String restaurant;
    private String descriptionText;
    private ArrayList<String> tagsList;
    private int rating;
    private String imgPath;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_entry);

        dish = getIntent().getStringExtra("dishName");
        restaurant = getIntent().getStringExtra("restaurantName");
        descriptionText = getIntent().getStringExtra("description");
        tagsList = getIntent().getStringArrayListExtra("tags");
        String ratingText = getIntent().getStringExtra("rating");
        rating = getIntent().getIntExtra("ratingNum", 0);
        imgPath = getIntent().getStringExtra("imgPath");
        id = getIntent().getIntExtra("id", -1);

        TextView dishName = (TextView)findViewById(R.id.name);
        dishName.setText(dish);
        TextView restaurantName = (TextView)findViewById(R.id.restaurant);
        restaurantName.setText(restaurant);
        TextView description = (TextView)findViewById(R.id.description);
        description.setText(descriptionText);
        Bitmap image = BitmapFactory.decodeFile(imgPath);
        ImageView myImage = (ImageView) findViewById(R.id.imageView);
        myImage.setImageBitmap(image);

        StringBuilder tagsText = new StringBuilder();
        for (int i = 0; i < tagsList.size(); i++) {
            if (i != tagsList.size()-1) {
                tagsText.append(tagsList.get(i) + ", ");
            }
            else {
                tagsText.append(tagsList.get(i));
            }
        }
        String tagsString = tagsText.toString();
        TextView tags = (TextView)findViewById(R.id.tags);
        tags.setText(tagsString);
        TextView rating = (TextView)findViewById(R.id.rating);
        rating.setText(ratingText);
    }

    public void editEntryButton(View v) {
        Intent toEdit = new Intent(ViewEntry.this, EditEntry.class);
        toEdit.putExtra("dish", dish);
        toEdit.putExtra("restaurant", restaurant);
        toEdit.putExtra("description", descriptionText);
        toEdit.putExtra("tagsList", tagsList);
        toEdit.putExtra("rating", rating);
        toEdit.putExtra("imageAddr", imgPath);
        //For determining if Edit Entry has received an existing entry or is building a new one
        toEdit.putExtra("id", id);
        startActivity(toEdit);
    }
}
