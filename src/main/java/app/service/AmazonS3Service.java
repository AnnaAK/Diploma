package app.service;

import app.model.Files;
import app.model.Role;
import app.model.User;
import app.repository.FileRepository;
import app.repository.UserRepository;
import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

@Service
public class AmazonS3Service {
    @Autowired
    private AmazonS3 s3client;

    @Value("${jsa.s3.bucket}")
    private String bucketName;

    @Value("${jsa.s3.endpointUrl}")
    private String endpointUrl;

    public Files saveFileToS3(MultipartFile multipartFile) {

        try {
            File fileToUpload = convertFromMultiPart(multipartFile);
            String key = Instant.now().getEpochSecond() + "_" + fileToUpload.getName();
            s3client.putObject(new PutObjectRequest(bucketName, key, fileToUpload));

            /* get signed URL (valid for one year) */
            GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, key);
            generatePresignedUrlRequest.setMethod(HttpMethod.GET);
            generatePresignedUrlRequest.setExpiration(DateTime.now().plusYears(1).toDate());

            //URL signedUrl = s3client.generatePresignedUrl(generatePresignedUrlRequest);
            String signedUrl = endpointUrl + "/" + bucketName + "/" + key;

            return new Files(key, signedUrl, fileToUpload.getName());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private File convertFromMultiPart(MultipartFile multipartFile)
        throws IOException {

            File file = new File(multipartFile.getOriginalFilename());
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(multipartFile.getBytes());
            fos.close();

            return file;
        }

    public void deleteImageFromS3(Files customerImage){
        s3client.deleteObject(new DeleteObjectRequest(bucketName, customerImage.getKey()));
    }

}
