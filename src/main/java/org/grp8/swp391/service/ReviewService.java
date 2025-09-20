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

}
