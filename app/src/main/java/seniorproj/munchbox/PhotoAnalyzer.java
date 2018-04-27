package seniorproj.munchbox;



import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.Feature;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import com.google.common.io.BaseEncoding;

/**
 * Created by Danny on 3/23/2018.
 */

public class PhotoAnalyzer {

    private  ArrayList<EntityAnnotation> labels;
    private static final String TAG = Activity.class.getSimpleName();
    private Context context;
    private static String API_KEY;
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private EditEntry editEntryActivity;

    /*Flow of control: constructor -> uploadImage -> callCloudVision */

    public PhotoAnalyzer(Bitmap image, Context context, Activity activity) {
        this.context = context;
        API_KEY = context.getString(R.string.mykey);
        this.editEntryActivity = (EditEntry) activity;
        labels = new ArrayList<EntityAnnotation>();
        uploadImage(image);
    }

    public void uploadImage(Bitmap image) {
        if (image != null) {
            callCloudVision(image);
        }
        else {
            Log.d(TAG, "Request made for null image");
        }
    }

    private static class ImageTask extends AsyncTask<Object, Void, ArrayList<EntityAnnotation>> {
        private final WeakReference<Activity> mainWeakReference;
        private Vision.Images.Annotate request;
        private ArrayList<EntityAnnotation> labelsInput;

        ImageTask(Activity activity, Vision.Images.Annotate annotate, ArrayList<EntityAnnotation> labels) {
            mainWeakReference = new WeakReference(activity);
            request = annotate;
            labelsInput = labels;
        }

        @Override
        protected ArrayList<EntityAnnotation> doInBackground(Object... params) {
            try {
                Log.d(TAG, "created cloud vision request object, sending");
                BatchAnnotateImagesResponse response = request.execute();
                labelsInput = (ArrayList<EntityAnnotation>)(response.getResponses().get(0).getLabelAnnotations());
                return labelsInput;
            } catch (GoogleJsonResponseException e) {
                Log.d(TAG, "failed to make API request because " + e.getContent());
            } catch (IOException e) {
                Log.d(TAG, "failed to make API request because of IOException " + e.getMessage());
            }
            System.out.println("Vision request failed.");
            return null;
        }
    }

    private void callCloudVision(final Bitmap bitmap) {
        try {
            ImageTask labelTask = new ImageTask(editEntryActivity, prepareAnnotationRequest(bitmap), labels) {
                protected void onPostExecute(ArrayList<EntityAnnotation> list) {
                    labels = list;
                    editEntryActivity.onBackgroundTaskComplete(getLabels());
                }
            };
            labelTask.execute();

        } catch (IOException e) {
            Log.d(TAG, "failed to make API request because of IOExecption " + e.getMessage());
        }
    }


    private Vision.Images.Annotate prepareAnnotationRequest(Bitmap bitmap) throws IOException {
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        final Bitmap image = bitmap;

        VisionRequestInitializer requestInitializer =
                new VisionRequestInitializer(API_KEY) {
                   @Override
                    protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                            throws IOException {
                        super.initializeVisionRequest(visionRequest);

                        String packageName = context.getPackageName();
                        visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                        String signature = getSignature(context.getPackageManager(), packageName);

                        visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, signature);
            }
        };

        Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null).setApplicationName("MunchBox");
        builder.setVisionRequestInitializer(requestInitializer);

        Vision vision = builder.build();

        BatchAnnotateImagesRequest request = new BatchAnnotateImagesRequest();
        request.setRequests(new ArrayList<AnnotateImageRequest>() {{
                                    AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

                                    Image encodedImage = new Image();
                                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                                    image.compress(Bitmap.CompressFormat.JPEG, 90, output);
                                    byte[] bytes = output.toByteArray();

                                    encodedImage.encodeContent(bytes);
                                    annotateImageRequest.setImage(encodedImage);
                                    annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                                        Feature labelDetection = new Feature();
                                        labelDetection.setType("LABEL_DETECTION");
                                        labelDetection.setMaxResults(8);
                                        add(labelDetection);
                                    }});

                                    add(annotateImageRequest);
                                }});
        Vision.Images.Annotate annotateRequest = vision.images().annotate(request);
        annotateRequest.setDisableGZipContent(true);
        Log.d(TAG, "created vision request object, sending");

        return annotateRequest;
    }

    public ArrayList<String> getLabels() {
        ArrayList<String> returnLabels = new ArrayList<String>();
        if (labels != null) {
            for (int i = 0; i < labels.size(); i++) {
                String curLabel = labels.get(i).getDescription();
                if (curLabel.equals("food")
                        || curLabel.equals("cuisine")
                        || curLabel.equals("dish")
                        || curLabel.equals("meal")
                        || curLabel.equals("lunch")
                        || curLabel.equals("dinner")
                        || curLabel.equals("recipe")
                        || curLabel.contains("vegetarian")) continue;
                else returnLabels.add(curLabel);
            }
        }
        return returnLabels;
    }

    /*Package stuff for use with the HTTP injections. */

    public static String getSignature(@NonNull PackageManager pm, @NonNull String packageName) {
        try {
            PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            if (packageInfo == null
                    || packageInfo.signatures == null
                    || packageInfo.signatures.length == 0
                    || packageInfo.signatures[0] == null) {
                return null;
            }
            return signatureDigest(packageInfo.signatures[0]);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    private static String signatureDigest(Signature sig) {
        byte[] signature = sig.toByteArray();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] digest = md.digest(signature);
            return BaseEncoding.base16().lowerCase().encode(digest);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
}
