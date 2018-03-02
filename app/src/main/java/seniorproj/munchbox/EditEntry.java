package seniorproj.munchbox;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.app.Activity;
import android.widget.EditText;
import android.widget.ImageView;
import android.view.View;
import java.io.File;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class EditEntry extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_entry);

        String picPath = getIntent().getStringExtra("foodImagePath");
        File picFile = new File(picPath);
        System.out.println("test" + picPath);

        if (picFile.exists()) {
//            ImageView foodPic = (ImageView) findViewById(R.id.imageView);
//            ImageView img = new ImageView(this);
//            img.setImageResource(foodPic);

            Bitmap myBitmap = BitmapFactory.decodeFile(picPath);

            ImageView myImage = (ImageView) findViewById(R.id.imageView);

            myImage.setImageBitmap(myBitmap);
        }
    }
}