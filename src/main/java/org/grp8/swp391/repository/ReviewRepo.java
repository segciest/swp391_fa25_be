package org.grp8.swp391.repository;

import org.grp8.swp391.entity.Listing;
import org.grp8.swp391.entity.Review;
import org.grp8.swp391.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ReviewRepo extends JpaRepository<Review,Integer> {
    Review findByReviewId(Long reviewId);
    List<Review> findByListing(Listing listing);
    List<Review> findByReviewer(User user);
    List<Review> findByReviewedUser(User user);

}
