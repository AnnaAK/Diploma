package app.service;

import app.model.Files;
import app.model.Role;
import app.model.TextureFile;
import app.model.User;
import app.repository.FileRepository;
import app.repository.TextureRepository;
import app.repository.UserRepository;
import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.FilenameUtils;


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

    @Qualifier("textureRepository")
    @Autowired
    private TextureRepository textureRepository;

    @Value("${jsa.s3.bucket}")
    private String bucketName;

    @Value("${jsa.s3.endpointUrl}")
    private String endpointUrl;

    public TextureFile saveTextureToS3 (MultipartFile multipartFile) {
        try {
            File fileToUpload = convertFromMultiPart(multipartFile);
            String key = Instant.now().getEpochSecond() + "_" + fileToUpload.getName();
            s3client.putObject(new PutObjectRequest(bucketName, key, fileToUpload)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            String signedUrl = endpointUrl + "/" + bucketName + "/" + key;

            String basename = FilenameUtils.getBaseName(fileToUpload.getName());

            return new TextureFile(basename, signedUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getExtension(MultipartFile multipartFile){
        try {
            File file = convertFromMultiPart(multipartFile);
            return FilenameUtils.getExtension(file.getName());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Files saveFileToS3(MultipartFile multipartFile) {

        try {
            File fileToUpload = convertFromMultiPart(multipartFile);
            String key = Instant.now().getEpochSecond() + "_" + fileToUpload.getName();
            s3client.putObject(new PutObjectRequest(bucketName, key, fileToUpload)
                    .withCannedAcl(CannedAccessControlList.PublicRead));

            String signedUrl = endpointUrl + "/" + bucketName + "/" + key;
            String basename = FilenameUtils.getBaseName(fileToUpload.getName());

            return new Files(key, signedUrl, basename);
        } catch (Exception e) {
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

    public void deleteImageFromS3(Files customerImage) {
        s3client.deleteObject(new DeleteObjectRequest(bucketName, customerImage.getKey()));
    }


    public S3Object downloadFileFromS3(String key) {
        try {
            S3Object s3object = s3client.getObject(new GetObjectRequest(
                    bucketName, key));
            return s3object;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}