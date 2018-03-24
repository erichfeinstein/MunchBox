package seniorproj.munchbox;



import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.model.*;
import com.google.api.services.vision.v1.VisionScopes;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Danny on 3/23/2018.
 */

public class PhotoAnalyzer {


    private String filePath;
    private Image photo;
    private Vision vision = null;
    private List<EntityAnnotation> labels;

    public List<EntityAnnotation> generateLabels(int maxResults) throws IOException {

        List<EntityAnnotation> requests = new ArrayList<>();
        RandomAccessFile file = new RandomAccessFile(filePath, "r");
        byte[] data = new byte[(int)file.length()];
        file.readFully(data);

        AnnotateImageRequest request =
                new AnnotateImageRequest().setImage(new Image().encodeContent(data)).setFeatures(ImmutableList.of(
                        new Feature().setType("LABEL_DETECTION").setMaxResults(maxResults)));

        Vision.Images.Annotate annotate = vision.images().annotate(new BatchAnnotateImagesRequest().setRequests(ImmutableList.of(request)));
        annotate.setDisableGZipContent(true);


        BatchAnnotateImagesResponse batchResponse = annotate.execute();
        assert batchResponse.getResponses().size() == 1;
        AnnotateImageResponse response = batchResponse.getResponses().get(0);
        if (response.getLabelAnnotations() == null) {
            throw new IOException(response.getError() != null ? response.getError().getMessage() : "Error.");
        }
        return labels = response.getLabelAnnotations();
    }

    public Vision getVisionService() throws IOException, GeneralSecurityException {
        GoogleCredential credential = GoogleCredential.getApplicationDefault().createScoped(VisionScopes.all());
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        return new Vision.Builder(GoogleNetHttpTransport.newTrustedTransport(), jsonFactory, credential).setApplicationName("MunchBox").build();
    }

    public PhotoAnalyzer(String photoPath) {
        try {
            vision = getVisionService();
        }
        catch (Exception e) {
            System.out.print(e.toString());
        }
        setFilePath(photoPath);

    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
