package com.mdtalalwasim.ecommerce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
public class TestVideoController {

	/**
	 * Renders the E2E test video recordings dashboard, listing all Playwright 
	 * browser recordings (.webm) sorted chronologically with the latest runs first.
	 */
    @GetMapping("/test-videos")
    public String showTestVideos(Model model) {
        // Resolve target/videos relative to the JVM working directory (project root)
        String workingDir = System.getProperty("user.dir");
        Path videosPath = Paths.get(workingDir, "target", "videos");
        File dir = videosPath.toFile();

        List<String> videoFiles = new ArrayList<>();

        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.getName().endsWith(".webm")) {
                        videoFiles.add(f.getName());
                    }
                }
                // Sort by last modified (newest first) so latest test runs appear at top
                videoFiles.sort(Comparator.comparingLong(name ->
                        -new File(dir, name).lastModified()));
            }
        }

        model.addAttribute("videos", videoFiles);
        model.addAttribute("videosDir", videosPath.toAbsolutePath().toString());
        model.addAttribute("videosExist", dir.exists());
        return "test-videos";
    }
}
