package seniorproj.munchbox;



import android.os.AsyncTask;

import com.google.api.gax.rpc.ClientContext;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageAnnotatorSettings;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import com.google.protobuf.ByteString;

/**
 * Created by Danny on 3/23/2018.
 */

public class PhotoAnalyzer extends AsyncTask<Void, Void, Void> {

    private String filePath;
    private List<EntityAnnotation> labels;

    @Override
    protected Void doInBackground(Void... params) {

        try {
            ImageAnnotatorClient vision = ImageAnnotatorClient.create();
            List<AnnotateImageRequest> requests = new ArrayList<>();
            RandomAccessFile file = new RandomAccessFile(filePath, "r");
            byte[] data = new byte[(int) file.length()];
            file.readFully(data);
            ByteString imgBytes = ByteString.copyFrom(data);

            Image photo = Image.newBuilder().setContent(imgBytes).build();
            Feature feature = Feature.newBuilder().setType(Type.LABEL_DETECTION).build();
            AnnotateImageRequest request =
                    AnnotateImageRequest.newBuilder().addFeatures(feature).setImage(photo).build();
            BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    System.out.printf("Error: %s\n", res.getError().getMessage());
                }
                for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
                    labels.add(annotation);
                }
            }

            vision.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void generateLabels() {
        try {
          execute();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PhotoAnalyzer(String photoPath) {
        setFilePath(photoPath);
     //   generateLabels();
    }

    public List<String> getLabels() {
        List<String> returnLabels = new ArrayList<String>();
        for (int i = 0; i < labels.size(); i++) {
            returnLabels.add(labels.get(i).getDescription());
        }
        return returnLabels;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
