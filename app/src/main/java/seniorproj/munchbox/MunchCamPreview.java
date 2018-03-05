package seniorproj.munchbox;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by eric on 3/4/18.
 */

public class MunchCamPreview extends SurfaceView implements SurfaceHolder.Callback{
    private SurfaceHolder munchHolder;
    private Camera cam;

    public MunchCamPreview(Context context, Camera camera) {
        super(context);
        cam = camera;

        munchHolder = getHolder();
        munchHolder.addCallback(this);
        munchHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            cam.setPreviewDisplay(munchHolder);
            cam.startPreview();
            cam.setDisplayOrientation(90);
        } catch (IOException e) {
            System.out.println("Error with MunchCamPreview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        //I think this is the function that will send info to next activity/Camera
        if (cam != null) {
            cam.stopPreview();
            cam.release();
            cam = null;
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (munchHolder.getSurface() == null){
            return;
        }

        try {
            cam.stopPreview();
        } catch (Exception e) {
            System.out.println("Error with MunchCamPreview, tried to stop non-existent preview: " + e.getMessage());
        }

        try {
            cam.setPreviewDisplay(munchHolder);
            cam.startPreview();
        } catch (Exception e) {
            System.out.println("Error with MunchCamPreview: " + e.getMessage());
        }
    }
}
