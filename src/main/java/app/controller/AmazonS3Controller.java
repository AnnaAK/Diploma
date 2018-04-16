package app.controller;

import app.model.Files;
import app.model.User;
import app.repository.FileRepository;
import app.repository.UserRepository;
import app.service.AmazonS3Service;
import app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
        //user.setFiles(new HashSet<Files>(Arrays.asList(img)));
        userRepository.save(user);
        return "done";
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

