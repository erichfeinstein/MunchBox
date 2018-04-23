package seniorproj.munchbox;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.net.URL;
import java.util.ArrayList;
import java.io.File;

public class EditEntry extends Activity {

    private RecyclerView tagsRecyclerView;
    private TagsAdapter tagsAdapter;
    private ArrayList<String> tags;
    private RecyclerView locationsRecyclerView;
    private LocationsAdapter locationsAdapter;
    private ArrayList<String> locations;

    private LocationManager locationManager;

    private EditText name;
    private EditText restaurant;
    private EditText description;
    private RatingBar rating;
    private Location location;

    private String imgPath;


    private int id; //If this is -1, it means this is a new entry... if not, update existing entry

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_entry);

        locationManager = MainActivity.getLocationManager();

        name =   findViewById(R.id.title);
        restaurant =  findViewById(R.id.restaurant);
        description = findViewById(R.id.description);
        rating = findViewById(R.id.rating);

        //Get image and display it
        imgPath = getIntent().getStringExtra("imageAddr");
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap image = BitmapFactory.decodeFile(imgPath);
        ImageView myImage = findViewById(R.id.imageView);
        myImage.setImageBitmap(image);

        tagsRecyclerView = findViewById(R.id.tagsView);
        tagsRecyclerView.setHasFixedSize(true);
        tagsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        locationsRecyclerView = findViewById(R.id.locationsView);
        locationsRecyclerView.setHasFixedSize(true);
        locationsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        if (tags == null) tags = new ArrayList<>();

        //Run GPS location analysis. If null... ?
        // LOCATION PROVIDER IS NAME OF RESTAURANT
        if (locations == null) {
            locations = new ArrayList<>();
            //Get current location and identify closest food place
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            }
            else{
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }

            if (location != null) {
                URL u = URLMaker.placesURL(this, location);
                PlacesRequest p = new PlacesRequest();
                p.execute(u);
                ArrayList<Location> restaurants;
                try {
                    restaurants = (ArrayList<Location>) p.get();
                    if (restaurants.size() > 0) {
                        for (int i = 0; i < restaurants.size(); i++) {
                            locations.add(restaurants.get(i).getProvider());
                        }
                    }

                } catch (Exception e) {
                    System.out.println(e.toString());
                }
            }

            restaurant.setHint("Enter a location..."); //Here is after attempt to find locations.
            //If the attempt fails and no locations are found, change hint to prompt user to enter their own location
        }

        id = getIntent().getIntExtra("id", -1);
        //If editing an existing entry
        if (id != -1) {
            String nameText = getIntent().getStringExtra("dish");
            String restaurantText = getIntent().getStringExtra("restaurant");
            String descriptionText = getIntent().getStringExtra("description");
            tags = getIntent().getStringArrayListExtra("tagsList");
            float ratingVal = getIntent().getFloatExtra("rating", 0);

            name.setText(nameText);
            restaurant.setText(restaurantText);
            description.setText(descriptionText);
            loadTags(tags);
            rating.setRating(ratingVal);
            //Image already taken care of above
        }
        loadLocations(locations);


        //Run image analysis
        if (tags.size() == 0) {
            PhotoAnalyzer labelGen = new PhotoAnalyzer(image, this, this);
            tags = labelGen.getLabels();
        }
    }

    //Saves either new entry or updates info of existing entry
    public void saveEntryButton(View view) {
        view.setEnabled(false);
        int ratingAsInt = (int) (rating.getRating() * 2.0); //Multiply by 2 because ratings are stored on a 1-10 scale using ints

        //Prevent deletion
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().remove("toDelete").apply();
        prefs.edit().remove("id").apply();
        //Add to prefs the details of the entry that is being edited/created
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("id", id);
        editor.putString("name", name.getText().toString());
        editor.putString("restaurant", restaurant.getText().toString());
        editor.putString("description", description.getText().toString());
        editor.putString("imgPath", imgPath);
        editor.putInt("rating", ratingAsInt);
        editor.putLong("latitude", Double.doubleToRawLongBits(location.getLatitude()));
        editor.putLong("longitude", Double.doubleToRawLongBits(location.getLongitude()));

        //Gson tags to String
        Gson gson = new Gson();
        String json = gson.toJson(tags);
        editor.putString("tags", json);

        if (id == -1) editor.putBoolean("toAdd", true); //New entry
        else editor.putBoolean("toEdit", true); //Existing entry

        editor.apply();

        finish();
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
        EditText tagEntry = layout.findViewById(R.id.tag_enter);
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
        AlertDialog.Builder alert = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.EntryDeleteDialog));
        if (id == -1) {
            alert.setTitle("Delete?");
            alert.setMessage("Cancel creating this entry?");
        }
        else {
            alert.setTitle("Cancel Edit?");
            alert.setMessage("Discard changes to this entry?");
        }
        alert.setCancelable(true);
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Toast.makeText(EditEntry.this, "Cancelling...", Toast.LENGTH_SHORT).show();
                finish();

                /*
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("id", id);*/

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



    public void onBackgroundTaskComplete(ArrayList<String> result) {
        tags = result;
        loadTags(tags);
    }
}