package seniorproj.munchbox;

import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.widget.EditText;
import android.widget.ImageView;
import android.view.View;
import android.widget.TextView;
import java.util.ArrayList;

public class ViewEntry extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_entry);

        String dish = getIntent().getStringExtra("dishName");
        String restaurant = getIntent().getStringExtra("restaurantName");
        String descriptionText = getIntent().getStringExtra("description");
        ArrayList<String> tagsList = getIntent().getStringArrayListExtra("tags");
        String ratingText = convertRatingToStars(getIntent().getIntExtra("rating", 0));

        TextView dishName = (TextView)findViewById(R.id.dishName);
        dishName.setText(dish);
        TextView restaurantName = (TextView)findViewById(R.id.restaurant);
        restaurantName.setText(restaurant);
        TextView description = (TextView)findViewById(R.id.description);
        description.setText(descriptionText);

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

    private String convertRatingToStars(int ratingAsInt){
        StringBuilder stars = new StringBuilder();
        if (ratingAsInt % 2 == 0){
            for (int i = 0; i < ratingAsInt; i+=2) {
                stars.append("★");
            }
            return stars.toString();
        } else {
            for (int i = 0; i < ratingAsInt-1; i+=2) {
                stars.append("★");
            }
            stars.append("½");
            return stars.toString();
        }
    }
}
