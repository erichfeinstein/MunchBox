package seniorproj.munchbox;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.Rating;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.FileProvider;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class ViewEntry extends Activity {

    private String dish;
    private String restaurant;
    private String dateText;
    private String descriptionText;
    private ArrayList<String> tagsList; //Not displayed, but passed to EditEntry
    private float ratingVal;
    private String imgPath;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_entry);

        dish = getIntent().getStringExtra("dishName");
        restaurant = getIntent().getStringExtra("restaurantName");
        dateText = getIntent().getStringExtra("date");
        descriptionText = getIntent().getStringExtra("description");
        ratingVal = getIntent().getIntExtra("rating", 0);
        imgPath = getIntent().getStringExtra("imgPath");
        id = getIntent().getIntExtra("id", -1);
        tagsList = getIntent().getStringArrayListExtra("tagsList");

        TextView dishName = (TextView)findViewById(R.id.name);
        dishName.setText(dish);
        TextView restaurantName = (TextView)findViewById(R.id.restaurant);
        restaurantName.setText(restaurant);
        TextView date = (TextView)findViewById(R.id.date);
        date.setText(dateText);
        TextView description = (TextView)findViewById(R.id.description);
        description.setText(descriptionText);
        Bitmap image = BitmapFactory.decodeFile(imgPath);
        ImageView myImage = (ImageView) findViewById(R.id.imageView);
        myImage.setImageBitmap(image);
        RatingBar rating = (RatingBar)findViewById(R.id.rating);
        ratingVal = ratingVal/2f;
        rating.setRating(ratingVal);

        //TODO add a swipe listener to swipe between entries from within ViewEntry
    }

    public void editEntryButton(View view) {
        view.setEnabled(false);
        Intent toEdit = new Intent(ViewEntry.this, EditEntry.class);
        toEdit.putExtra("dish", dish);
        toEdit.putExtra("restaurant", restaurant);
        toEdit.putExtra("description", descriptionText);
        toEdit.putExtra("tagsList", tagsList);
        toEdit.putExtra("rating", ratingVal);
        toEdit.putExtra("imageAddr", imgPath);
        //For determining if Edit Entry has received an existing entry or is building a new one
        toEdit.putExtra("id", id);
        finish();
        startActivity(toEdit);
    }

    public void deleteEntryButton(View view) {
        final Context context = this;
        AlertDialog.Builder alert = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.EntryDeleteDialog));
        alert.setTitle("Delete?");
        alert.setMessage("Are you sure you want to delete this entry?");
        alert.setCancelable(true);
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Toast.makeText(ViewEntry.this, "Deleting...", Toast.LENGTH_SHORT).show();

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("id", id);
                editor.putBoolean("toDelete", true);
                editor.apply();

                finish();

                //Delete image for cancelled entry?
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        final AlertDialog dialog = alert.create();
        dialog.show();
    }

    public void shareEntryButton(View view) {
        String provider = ViewEntry.this.getApplicationContext().getPackageName();
        Uri imageURI = FileProvider.getUriForFile(ViewEntry.this, provider + ".fileprovider",new File(imgPath));
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("*/*");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "A MunchBox journal entry!");
        shareIntent.putExtra(Intent.EXTRA_TEXT, dish + " @ " + restaurant + ". " + descriptionText + "... via MunchBox");
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageURI);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }
    /*Can't utilize URIs in Intents in SDK >= 24, so this solution is necessary. */
    private class MunchFileProvider extends FileProvider {
    }
}
