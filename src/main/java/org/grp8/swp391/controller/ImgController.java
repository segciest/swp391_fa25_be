package org.grp8.swp391.controller;


import org.grp8.swp391.entity.Image;
import org.grp8.swp391.entity.Listing;
import org.grp8.swp391.service.ImgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/images")
public class ImgController {

    @Autowired
    private ImgService imgService;


    @GetMapping("/{id}")
    public ResponseEntity<?> findByImgId(@PathVariable long id){
        Image img = imgService.findByImgId(id);
        return ResponseEntity.ok().body(img);
    }



    @GetMapping("/listing/{listingId}")
    public ResponseEntity<?> findByListingId(@PathVariable String listingId){
        Listing listing = new Listing();
        listing.setListingId(listingId);
        List<Image> imgs = imgService.findByListing(listing);
        return ResponseEntity.ok(imgs);
    }
}
