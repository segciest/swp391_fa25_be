package org.grp8.swp391.service;

import org.grp8.swp391.entity.Listing;
import org.grp8.swp391.entity.Review;
import org.grp8.swp391.entity.User;
import org.grp8.swp391.repository.ReviewRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepo reviewRepo;

    public Review findById(Long reviewId){
        return reviewRepo.findByReviewId(reviewId);
    }

    public List<Review> getAll(){
        return reviewRepo.findAll();
    }

    public List<Review> getReviewsByListing(Listing listing){
        return reviewRepo.findByListing(listing);
    }

    public List<Review> getReviewByReviewer(User user){
        return reviewRepo.findByReviewer(user);
    }

    public List<Review> getReviewsForUser(User user) {
        return reviewRepo.findByReviewedUser(user);
    }

    public Review createReview(Review review){
        review.setCreateDate(new Date());
        return reviewRepo.save(review);
    }

    public void deleteReview(Long id){
        reviewRepo.deleteByReviewId(id);

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

    public List<Review> findByListingId(String listingId) {
        return reviewRepo.findByListing_ListingId(listingId);
    }

    public List<Review> findByReviewerId(String userId) {
        return reviewRepo.findByReviewer_UserID(userId);
    }

    public List<Review> findByReviewedUserId(String userId) {
        return reviewRepo.findByReviewedUser_UserID(userId);
    }


    public Double getAverageRatingByListing(String userId){
        return reviewRepo.findAverageRatingByUser(userId);
    }



}
