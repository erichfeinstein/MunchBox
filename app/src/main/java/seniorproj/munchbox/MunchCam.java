package seniorproj.munchbox;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by eric on 3/4/18.
 */

public class MunchCam extends Activity {
    private Camera cam;
    private MunchCamPreview munchCamPreview;
    private byte[] tempImage;
    private String recentImagePath;

    private Button confirmButton;

    public static final int MEDIA_TYPE_IMAGE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_preview);

        cam = getCameraInstance();
        Camera.Parameters params = cam.getParameters();
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        cam.setParameters(params);
        munchCamPreview = new MunchCamPreview(this, cam);
        FrameLayout prev = (FrameLayout) findViewById(R.id.camera_preview);
        prev.addView(munchCamPreview);

        final Button captureButton = (Button) findViewById(R.id.button_capture);
        confirmButton = (Button) findViewById(R.id.button_confirm);
        final Button cancelButton = (Button) findViewById(R.id.button_cancel);
        confirmButton.setVisibility(View.INVISIBLE);
        confirmButton.setEnabled(false);
        cancelButton.setVisibility(View.INVISIBLE);
        cancelButton.setEnabled(false);

        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get an image from the camera
                        cam.takePicture(null, null, mPicture);
                        captureButton.setEnabled(false);
                        captureButton.setVisibility(View.INVISIBLE);
                        //wait on enable confirm
                        confirmButton.setVisibility(View.VISIBLE);
                        cancelButton.setEnabled(true);
                        cancelButton.setVisibility(View.VISIBLE);
                    }
                }
        );
        confirmButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // save image and send to new activity
                        Intent intent = new Intent(MunchCam.this, EditEntry.class);
                        intent.putExtra("imageAddr", recentImagePath);
                        startActivity(intent);
                    }
                }
        );
        cancelButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recreate();
                    }
                }
        );
    }

    @Override
    public void onPause(){
        super.onPause();
        finish();
    }

    protected Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e){

        }
        return c;
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null){
                System.out.println("Error creating media file");
                return;
            }

            try {
                recentImagePath = pictureFile.getPath().toString();
                Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);

                //Ignore EXIF info and just rotate
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);

                //Crop
                bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getWidth());

                //Convert rotated image to byte array then save
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] newData = stream.toByteArray();
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(newData);
                fos.close();
                confirmButton.setEnabled(true);
            } catch (FileNotFoundException e) {
                System.out.println("File not found: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("Error accessing file: " + e.getMessage());
            }
        }
    };

    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MunchCam");

        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                System.out.println("MunchBoxCam failed to create directory");
                return null;
            }
        }

        // Create a file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }
}
