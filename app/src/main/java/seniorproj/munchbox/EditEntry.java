package seniorproj.munchbox;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.services.vision.v1.model.EntityAnnotation;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

public class EditEntry extends Activity {


    private RecyclerView tagsRecyclerView;
    private ArrayList<EntityAnnotation> labels;
    private TagsAdapter tagsAdapter;
    private ArrayList<String> tags;
    private RecyclerView locationsRecyclerView;
    private LocationsAdapter locationsAdapter;
    private ArrayList<String> locations;

    private EditText name;
    private EditText restaurant;
    private EditText description;
    private RatingBar rating;

    private Button button;

    private String imgPath;

    private int id; //If this is -1, it means this is a new entry... if not, update existing entry

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_entry);

        name = (EditText) findViewById(R.id.title);
        restaurant = (EditText) findViewById(R.id.restaurant);
        description = (EditText) findViewById(R.id.description);
        rating = (RatingBar) findViewById(R.id.rating);

        button = (Button) findViewById(R.id.save);

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

        labels = new ArrayList<>();

        locationsRecyclerView = (RecyclerView) findViewById(R.id.locationsView);
        locationsRecyclerView.setHasFixedSize(true);
        locationsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        //Run GPS location analysis. If null... ?
        // LOCATION PROVIDER IS NAME OF RESTAURANT
        if (locations == null) {
            locations = new ArrayList<>();
            //Start LocationGetter. Can call locationGetter.getLocation() to receive Location object.
            LocationGetter locationGetter = new LocationGetter(this);
            locationGetter.startListening();

            //Get current location and identify closest food place
            Location currentLocation = locationGetter.getLocation();
            if (currentLocation != null) {
                URL u = URLMaker.placesURL(this, currentLocation);
                PlacesRequest p = new PlacesRequest();
                p.execute(u);
                ArrayList<Location> restaurants;
                try {
                    restaurants = (ArrayList<Location>) p.get();
                    if (restaurants.size() > 0) {
                        for (int i = 0; i < restaurants.size(); i++) {
                            locations.add(restaurants.get(i).getProvider());
                        }
                        restaurant.setText(locations.get(0)); //Set location text to best guess
                    }
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
            }
            restaurant.setHint("Enter a location..."); //Here is after attempt to find locations.
            //If the attempt fails and no locations are found, change hint to prompt user to enter their own location
        }

        id = getIntent().getIntExtra("id", -1);
        if (id != -1) {
            String nameText = getIntent().getStringExtra("dish");
            String restaurantText = getIntent().getStringExtra("restaurant");
            String descriptionText = getIntent().getStringExtra("description");
            tags = getIntent().getStringArrayListExtra("tagsList");
            int ratingValue = getIntent().getIntExtra("rating", 0);
            String imgPath = getIntent().getStringExtra("imgPath");

            name.setText(nameText);
            restaurant.setText(restaurantText);
            description.setText(descriptionText);
            loadTags(tags); //Right now this doesn't work: the tags aren't populated by the time we get here. Need to discuss -Danny
            rating.setRating(((float)ratingValue)/2);
            //Image already taken care of above
        }

        loadLocations(locations);

        //Run image analysis
        PhotoAnalyzer labelGen = new PhotoAnalyzer(image, this, this);
        tags = labelGen.getLabels();
    }

    //Saves either new entry or updates info of existing entry
    public void saveEntryButton(View view) {
        finish();
        int ratingAsInt = (int) (rating.getRating() * 2.0); //Multiply by 2 because ratings are stored on a 1-10 scale using ints
        Intent intent = new Intent(EditEntry.this, MainActivity.class);
        intent.putExtra("name", name.getText().toString());
        intent.putExtra("restaurant", restaurant.getText().toString());
        intent.putExtra("description", description.getText().toString());
        intent.putExtra("imgPath", imgPath);
        intent.putExtra("rating", ratingAsInt);
        intent.putExtra("tags", tags);
        intent.putExtra("id", id); //In MainActivity, check if not -1
        finish();
        startActivity(intent);
    }

    //TODO Replace 'Delete' with 'Save' button
    //Add this code to ViewEntry under a drop down menu for deleting
    public void deleteEntryButton(View view) {
        finish();
        Intent backToMain = new Intent(EditEntry.this, MainActivity.class);
        backToMain.putExtra("id", id); //Pass the id of the entry to delete
        backToMain.putExtra("toDelete", true);
        startActivity(backToMain);
    }

    private void loadLocations(ArrayList<String> locationsList) {
        locationsAdapter = new LocationsAdapter(locationsList, this);
        locationsRecyclerView.setAdapter(locationsAdapter);
    }

    private void loadTags(ArrayList<String> tagsList) {
        tagsAdapter = new TagsAdapter(tagsList, this);
        tagsRecyclerView.setAdapter(tagsAdapter);
        tagsAdapter.notifyDataSetChanged();
    }

    @SuppressWarnings("ResourceType")
    public void openAddTag(View view) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.enter_new_tag,(ViewGroup)findViewById(R.layout.activity_edit_entry));
        PopupWindow popup = new PopupWindow(layout);
        popup.setFocusable(true);
        popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        EditText tagEntry = (EditText) layout.findViewById(R.id.tag_enter);
        tagEntry.setOnEditorActionListener(new TagEnterListener());
        popup.showAsDropDown(view, 0, -popup.getHeight());
        showKeyboard(tagEntry);
    }
    class TagEnterListener implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                closeKeyboard(v);
                String newTag = v.getText().toString();
                tagsAdapter.addTag(newTag);
                v.setText("");
                return true;
            }
            return false;
        }
    }
    public void showKeyboard(final EditText tagEntry){
        tagEntry.requestFocus();
        tagEntry.postDelayed(new Runnable(){
            @Override public void run() {
                InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.showSoftInput(tagEntry, 0);
            } },100);
    }
    public void closeKeyboard(final View v) {
        System.out.println("close keyboard");
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromInputMethod(v.getWindowToken(), 0);
        v.clearFocus();
    }

    @Override
    public void onBackPressed() {
        //"Discard changes to this entry?"
        new AlertDialog.Builder(this)
                .setTitle("Cancel")
                .setMessage("Discard changes to this entry?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(EditEntry.this, "Cancelling...", Toast.LENGTH_SHORT).show();
                        finish();
                        Intent backToList = new Intent(EditEntry.this, MainActivity.class);
                        //Delete image for cancelled entry
                        if (id == -1) {
                            File toDelete = new File(imgPath);
                            if (toDelete.exists()) {
                                if (toDelete.delete()) {
                                    System.out.println("File deleted");
                                } else {
                                    System.out.println("File not deleted");
                                }
                            }
                        }
                        startActivity(backToList);
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }



    public void onBackgroundTaskComplete(ArrayList<String> result) {
        tags = result;
        loadTags(tags);
    }
}