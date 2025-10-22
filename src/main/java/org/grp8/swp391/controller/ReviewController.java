package org.grp8.swp391.controller;


import jakarta.servlet.http.HttpServletRequest;
import org.grp8.swp391.config.JwtUtils;
import org.grp8.swp391.dto.request.CreateReviewRequest;
import org.grp8.swp391.entity.Review;
import org.grp8.swp391.entity.User;
import org.grp8.swp391.service.ReviewService;
import org.grp8.swp391.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;


    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserService userService;


    @GetMapping("/{reviewId}")
    public ResponseEntity<?> getReviewById(@PathVariable Long reviewId){
        Review re = reviewService.findById(reviewId);
        return ResponseEntity.ok().body(re);
    }
    @PostMapping("/create")
    public ResponseEntity<?> createReview( @RequestBody CreateReviewRequest req,HttpServletRequest request){
        try {
            String token = jwtUtils.extractToken(request);
            if (token == null || !jwtUtils.checkValidToken(token)) {
                return ResponseEntity.status(401).body("Invalid or missing token");
            }
            String email = jwtUtils.getUsernameFromToken(token);
            User reviewer = userService.findByUserEmail(email);

            Review review = reviewService.createReview(reviewer.getUserID(), req.getSellerId(),req.getRate(), req.getComment()
            );

            return ResponseEntity.ok(review);
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
        return ResponseEntity.ok(reviewService.getAverageRating(userId));
    }

    @GetMapping("/summary/{userId}")
    public ResponseEntity<?> getUserReviewSummary(@PathVariable String userId) {
        double avg = reviewService.getAverageRating(userId);
        List<Review> reviews = reviewService.findByReviewedUserId(userId);

        Map<String, Object> res = new HashMap<>();
        res.put("averageRating", avg);
        res.put("totalReviews", reviews.size());
        res.put("reviews", reviews);

        return ResponseEntity.ok(res);
    }


}
