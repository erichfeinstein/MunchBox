package seniorproj.munchbox;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class EditEntry extends Activity {

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
    }

    public void saveEntryButton(View view) {
        EditText name = (EditText) findViewById(R.id.name);
        EditText restaurant = (EditText) findViewById(R.id.restaurant);
        EditText description = (EditText) findViewById(R.id.description);

        Intent intent = new Intent(EditEntry.this, MainActivity.class);
        intent.putExtra("name", name.getText().toString());
        intent.putExtra("restaurant", restaurant.getText().toString());
        intent.putExtra("description", description.getText().toString());
        intent.putExtra("imgPath", imgPath);
        startActivity(intent);
        //missing rating
    }

    @Override
    public void onBackPressed(){
        finish();
        Intent backToList = new Intent(EditEntry.this, MainActivity.class);
        //If StringExtra imageAddr is not null, delete image from directory (entry was in process of being created, but we don't want to save the image)
        startActivity(backToList);
    }
}