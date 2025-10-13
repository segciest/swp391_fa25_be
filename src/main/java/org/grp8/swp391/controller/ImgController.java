package org.grp8.swp391.controller;


import org.grp8.swp391.entity.Image;
import org.grp8.swp391.entity.Listing;
import org.grp8.swp391.repository.ListingRepo;
import org.grp8.swp391.service.CloudinaryService;
import org.grp8.swp391.service.ImgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/images")
public class ImgController {

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private ImgService imgService;

    @Autowired
    private ListingRepo listingRepo;


    @PostMapping("/upload/{listingId}")
    public ResponseEntity<?> uploadSingleImage(
            @PathVariable String listingId,
            @RequestParam("file") MultipartFile file) {

        Listing listing = listingRepo.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        String imageUrl = cloudinaryService.uploadFile(file);

        Image image = new Image();
        image.setListingId(listing);
        image.setUrl(imageUrl);

        imgService.save(image);

        return ResponseEntity.ok(image);
    }

    @PostMapping("/upload-temp")
    public ResponseEntity<?> uploadTemp(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = cloudinaryService.uploadFile(file);
            return ResponseEntity.ok(Map.of("url", imageUrl));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/upload-temp-multiple")
    public ResponseEntity<?> uploadTempMultiple(@RequestParam("files") MultipartFile[] files) {
        try {
            List<String> urls = new ArrayList<>();
            for (MultipartFile file : files) {
                String url = cloudinaryService.uploadFile(file);
                urls.add(url);
            }
            return ResponseEntity.ok(Map.of("urls", urls));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Upload failed: " + e.getMessage());
        }
    }

    @PostMapping("/upload-multiple/{listingId}")
    public ResponseEntity<?> uploadMultipleImages(
            @PathVariable String listingId,
            @RequestParam("files") MultipartFile[] files) {

        Listing listing = listingRepo.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        List<Image> savedImages = new ArrayList<>();

        for (MultipartFile file : files) {
            String imageUrl = cloudinaryService.uploadFile(file);
            Image img = new Image();
            img.setListingId(listing);
            img.setUrl(imageUrl);
            savedImages.add(imgService.save(img));
        }

        return ResponseEntity.ok(savedImages);
    }

    @GetMapping("/listing/{listingId}")
    public ResponseEntity<?> getImagesByListing(@PathVariable String listingId) {
        Listing listing = listingRepo.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        return ResponseEntity.ok(imgService.getByListing(listing));
    }
}
