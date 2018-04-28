package app.controller;

import app.model.Files;
import app.model.User;
import app.repository.FileRepository;
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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Controller
public class AmazonS3Controller {

    private AmazonS3Service s3client;

    @Qualifier("fileRepository")
    @Autowired
    private FileRepository fileRepository;

    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    AmazonS3Controller(AmazonS3Service s3client) {
        this.s3client = s3client;
    }

    /*@RequestMapping(value = "/storage", method = RequestMethod.GET)
    public ModelAndView uploadpage(ModelAndView modelAndView){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByEmail(auth.getName());
        Set<Files> listfiles = user.getFiles();
        modelAndView.setViewName("storage");
        return modelAndView;

    }*/

    /*public ModelAndView uploadpage(ModelAndView modelAndView, MultipartFile file){
        modelAndView.setViewName("storage");
        return modelAndView;
    }*/
    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    public String index() {
        return "upload";
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String uploadfun(@RequestParam("file") MultipartFile file){

        Files img = s3client.saveFileToS3(file);
        fileRepository.save(img);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByEmail(auth.getName());
        user.addFile(img);
        userRepository.save(user);
        return "done";
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

    /*@DeleteMapping("/deleteFile")
    public String deleteFile(Files fileUrl) {
        return this.s3client.deleteImageFromS3(fileUrl);
    }*/
}
    /*@RequestMapping(value={"/storage/uploadfile"}, method = RequestMethod.POST)
    public ModelAndView uploadfile(@RequestPart(value = "file") MultipartFile file){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("storage");
        s3client.uploadFile(file);
        modelAndView.addObject("successMessage", "Your file has been upload!");
        return modelAndView;
    }*/

