package seniorproj.munchbox;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.FrameLayout;

/**
 * Created by eric on 3/4/18.
 */

public class MunchCam extends Activity {
    private Camera cam;
    private MunchCamPreview munchCamPreview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_preview);

        cam = getCameraInstance();

        munchCamPreview = new MunchCamPreview(this, cam);
        FrameLayout prev = (FrameLayout) findViewById(R.id.camera_preview);
        prev.addView(munchCamPreview);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MunchCam.this, new String[] {Manifest.permission.CAMERA}, 100);

        }
    }

    protected Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e){

        }
        return c;
    }
}
