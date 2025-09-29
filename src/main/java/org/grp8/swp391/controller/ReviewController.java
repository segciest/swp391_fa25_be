package org.grp8.swp391.controller;


import org.grp8.swp391.entity.Review;
import org.grp8.swp391.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;
    @GetMapping("/{reviewId}")
    public ResponseEntity<?> getReviewById(@PathVariable Long reviewId){
        Review re = reviewService.findById(reviewId);
        return ResponseEntity.ok().body(re);
    }
    @PostMapping("/create")
    public ResponseEntity<?> createReview(@RequestBody Review re){
        try{
           Review save = reviewService.createReview(re);
           return ResponseEntity.ok().body(save);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateReview(@PathVariable Long id,@RequestBody Review re){
        try{

            Review updated = reviewService.updateReview(id,re);
            return ResponseEntity.ok().body(updated);
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteReviewById(@PathVariable Long id){
        try{
            reviewService.deleteReview(id);
            return ResponseEntity.ok().body("Review has been deleted");
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping
    public List<Review> getAllReviews(){
        return reviewService.getAll();
    }

    @GetMapping("/listing/{listingId}")
    public ResponseEntity<?> getReviewsByListing(@PathVariable String listingId) {
        try {
            return ResponseEntity.ok(reviewService.findByListingId(listingId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/reviewer/{userId}")
    public ResponseEntity<?> getReviewsByReviewer(@PathVariable String userId) {
        try {
            return ResponseEntity.ok(reviewService.findByReviewerId(userId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/reviewed/{userId}")
    public ResponseEntity<?> getReviewsAboutUser(@PathVariable String userId) {
        try {
            return ResponseEntity.ok(reviewService.findByReviewedUserId(userId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{userId}/rate")
    public ResponseEntity<?> getReviewsRate(@PathVariable String userId) {
        return ResponseEntity.ok(reviewService.getAverageRatingByListing(userId));
    }


}
