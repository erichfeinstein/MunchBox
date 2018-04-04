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
    private Activity activity;

    /*Flow of control: constructor -> uploadImage -> callCloudVision */

    public PhotoAnalyzer(Bitmap image, Context context, Activity activity) {
        this.context = context;
        API_KEY = context.getString(R.string.mykey);
        this.activity = activity;
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

    private class RequestTask extends AsyncTask<Object, Void, ArrayList<EntityAnnotation>> {
        private final WeakReference<Activity> mainWeakReference;
        private Vision.Images.Annotate request;
        private ArrayList<EntityAnnotation> labelsInput;

        RequestTask(Activity activity, Vision.Images.Annotate annotate, ArrayList<EntityAnnotation> labels) {
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

        protected void onPostExecute(ArrayList<EntityAnnotation> result) {
            Activity activity = mainWeakReference.get();
            labels = result;
            //This bit is where the sample code sets the labels to the image. I don't think we do it here - Danny

        }
    }

    private void callCloudVision(final Bitmap bitmap) {
        try {
            RequestTask labelTask = new RequestTask(activity, prepareAnnotationRequest(bitmap), labels);
            labelTask.execute();
        }
        catch (IOException e) {
            Log.d(TAG, "failed to make API request because of IOExecption " + e.getMessage());
        }
    }

    private static String convertResponseToString(BatchAnnotateImagesResponse response) {
        StringBuilder message = new StringBuilder("I found these things:\n\n");

        List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();
        if (labels != null) {

            for (EntityAnnotation label : labels) {
                message.append(String.format(Locale.US, "%.3f: %s", label.getScore(), label.getDescription()));
                message.append("\n");
            }
        }
        else {
            message.append("nothing");
        }
        System.out.println(message.toString());
        return message.toString();
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
                                        labelDetection.setMaxResults(4);
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
        for (int i = 0; i < labels.size(); i++) {
            returnLabels.add(labels.get(i).getDescription());
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
