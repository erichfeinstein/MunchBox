package seniorproj.munchbox;



import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.model.*;
import com.google.api.services.vision.v1.VisionScopes;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Danny on 3/23/2018.
 */

public class PhotoAnalyzer {


    private String filePath;
    private Image photo;

    public void generateLabels() throws IOException, GeneralSecurityException {
        List<EntityAnnotation> requests = new ArrayList<>();

    }

    public Vision getVisionService() throws IOException, GeneralSecurityException {
        GoogleCredential credential = GoogleCredential.getApplicationDefault().createScoped(VisionScopes.all());
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        return new Vision.Builder(GoogleNetHttpTransport.newTrustedTransport(), jsonFactory, credential).setApplicationName("MunchBox").build();
    }

    public PhotoAnalyzer(String photoPath) {
        setFilePath(photoPath);
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
