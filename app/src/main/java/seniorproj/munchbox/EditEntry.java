package seniorproj.munchbox;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

public class EditEntry extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_entry);

        //Get image and display it
        byte[] picBytes = getIntent().getByteArrayExtra("image");
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap image = BitmapFactory.decodeByteArray(picBytes, 0, picBytes.length, opts);
        ImageView myImage = (ImageView) findViewById(R.id.imageView);
        myImage.setImageBitmap(image);
    }

    @Override
    public void onBackPressed(){
        finish();
        Intent backToList = new Intent(EditEntry.this, MainActivity.class);
        startActivity(backToList);
    }
}