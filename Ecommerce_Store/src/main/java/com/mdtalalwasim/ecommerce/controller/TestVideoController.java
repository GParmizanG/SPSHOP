package com.mdtalalwasim.ecommerce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Controller
public class TestVideoController {

    @GetMapping("/test-videos")
    public String showTestVideos(Model model) {
        File dir = new File("target/videos");
        List<String> videoFiles = new ArrayList<>();
        
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.getName().endsWith(".webm")) {
                        videoFiles.add(f.getName());
                    }
                }
            }
        }
        
        model.addAttribute("videos", videoFiles);
        return "test-videos";
    }
}
