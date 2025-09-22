package org.grp8.swp391.controller;


import org.grp8.swp391.entity.Review;
import org.grp8.swp391.entity.User;
import org.grp8.swp391.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    public ResponseEntity<?> getReviewById(Long reviewId){
        Review re = reviewService.findById(reviewId);
        return ResponseEntity.ok().body(re);
    }
    @PostMapping("/create")
    public ResponseEntity<?> createReview(Review re){
        try{
           Review save = reviewService.createReview(re);
           return ResponseEntity.ok().body(re);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateReview(@PathVariable Long id, Review re){
        try{

            Review updated = reviewService.updateReview(id,re);
            return ResponseEntity.ok().body(re);
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




}
