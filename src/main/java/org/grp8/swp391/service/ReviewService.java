package org.grp8.swp391.service;

import org.grp8.swp391.entity.Listing;
import org.grp8.swp391.entity.Review;
import org.grp8.swp391.entity.User;
import org.grp8.swp391.repository.ReviewRepo;
import org.grp8.swp391.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepo reviewRepo;

    @Autowired
    private UserRepo userRepo;

    public Review findById(Long reviewId){
        return reviewRepo.findByReviewId(reviewId);
    }

    public List<Review> getAll(){
        return reviewRepo.findAll();
    }


    public List<Review> getReviewByReviewer(User user){
        return reviewRepo.findByReviewer(user);
    }

    public List<Review> getReviewsForUser(User user) {
        return reviewRepo.findByReviewedUser(user);
    }

    public Review createReview(String reviewerId,String sellerId,int rate,String comment){
        User reviewer = userRepo.findByUserID(reviewerId);
        if(reviewer==null){
            throw new RuntimeException("Reviewer not found");
        }

        User seller = userRepo.findByUserID(sellerId);
        if(seller==null){
            throw new RuntimeException("Seller not found");
        }

        if(reviewRepo.existsByReviewerAndReviewedUser(reviewer,seller)){
            throw new RuntimeException("Already reviewed this user!");
        }

        Review review = new Review();
        review.setReviewer(reviewer);
        review.setReviewedUser(seller);
        review.setRate(rate);
        review.setComment(comment);
        review.setCreateDate(new Date());
        return  reviewRepo.save(review);
    }

    public void deleteReview(Long reviewId){
        if(reviewRepo.findByReviewId(reviewId)==null){
            throw new RuntimeException("Review not found");
        }
        reviewRepo.deleteByReviewId(reviewId);

    }

    public Review updateReview(Long id,Review review){
        Review check = reviewRepo.findByReviewId(id);
        if(check == null){
            throw new RuntimeException("Review not found");
        }

        if(review.getComment()!=null){
            check.setComment(review.getComment().trim());
        }

        if(review.getRate() > 0){
            check.setRate(review.getRate());
        }
        check.setCreateDate(new Date());
        return reviewRepo.save(check);
    }


    public List<Review> findByReviewerId(String userId) {
        return reviewRepo.findByReviewer_UserID(userId);
    }

    public List<Review> findByReviewedUserId(String userId) {
        return reviewRepo.findByReviewedUser_UserID(userId);
    }


    public double getAverageRating(String sellerId) {
        User seller = userRepo.findByUserID(sellerId);
        if (seller == null) {
            throw new RuntimeException("Seller not found with id: " + sellerId);
        }

        List<Review> reviews = reviewRepo.findByReviewedUser(seller);

        if (reviews == null || reviews.isEmpty()) {
            return 0.0;
        }

        double average = reviews.stream()
                .mapToInt(Review::getRate)
                .average()
                .orElse(0.0);

        return Math.round(average * 10.0) / 10.0;
    }



}
