package app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

public class ViewerController {
    @RequestMapping(value = "/viewer", method = RequestMethod.GET)
    public String index() {
        return "3Dviewer";
    }

}
