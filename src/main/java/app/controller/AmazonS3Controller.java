package app.controller;

import app.model.Files;
import app.model.TextureFile;
import app.model.User;
import app.repository.FileRepository;
import app.repository.TextureRepository;
import app.repository.UserRepository;
import app.service.AmazonS3Service;
import app.service.UserService;
import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
public class AmazonS3Controller {

    private AmazonS3Service s3client;

    @Qualifier("fileRepository")
    @Autowired
    private FileRepository fileRepository;

    @Qualifier("textureRepository")
    @Autowired
    private TextureRepository textureRepository;

    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    AmazonS3Controller(AmazonS3Service s3client) {
        this.s3client = s3client;
    }

    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    public String index() {
        return "upload";
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String uploadfun(@RequestParam("file") List<MultipartFile> files) {
        for (MultipartFile file : files) {
            String extension = s3client.getExtension(file);
            //System.out.print(extension);
            String texture = "";

            if (extension.equals("mtl")) {
                TextureFile t = s3client.saveTextureToS3(file);
                textureRepository.save(t);
                try {
                    Files f = fileRepository.findFirstByName(t.getName());
                    f.setTLink(t.getUrl());
                    f.setExtension("mtl");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (extension.equals("obj")) {
                Files img = s3client.saveFileToS3(file);
                try {
                    texture = textureRepository.findFirstByTname(img.getName()).getUrl();
                    img.setExtension("mtl");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    img.setTLink(texture);
                    if (texture.equals("")) {img.setExtension("obj");}
                    fileRepository.save(img);
                    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                    User user = userService.findByEmail(auth.getName());
                    user.addFile(img);
                    userRepository.save(user);
                }
            } else {
                Files img = s3client.saveFileToS3(file);
                img.setExtension("img");
                fileRepository.save(img);
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                User user = userService.findByEmail(auth.getName());
                user.addFile(img);
                userRepository.save(user);
            }
        }
        System.out.print("DONE!");
        return "repo";
    }

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public String download() {
        ObjectMapper mapper = new ObjectMapper();
        S3Object obj = s3client.downloadFileFromS3("1524315673_skull.obj");
        try {
            mapper.writeValue(new File("../1524315673_skull.json"), obj);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "download";
    }

    @RequestMapping(value = "/viewer_obj", method = RequestMethod.GET)
    public String viewer_obj() {
        return "viewer_obj";
    }

    @RequestMapping(value = "/viewer_obj_mlt", method = RequestMethod.GET)
    public String viewer_mlt() {
        return "viewer_obj_mlt";
    }

    @RequestMapping(value = {"/repo"}, method = RequestMethod.GET)
    public ModelAndView repo() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("repo");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByEmail(auth.getName());
        //List<Files> list = user.getFiles();
        //System.out.print(list);
        modelAndView.addObject("f_list", user.getFiles());

        return modelAndView;
    }
}

