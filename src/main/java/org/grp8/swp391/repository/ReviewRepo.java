package org.grp8.swp391.repository;

import org.grp8.swp391.entity.Listing;
import org.grp8.swp391.entity.Review;
import org.grp8.swp391.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ReviewRepo extends JpaRepository<Review,Integer> {
    Review findByReviewId(Long reviewId);
    List<Review> findByReviewer(User user);
    List<Review> findByReviewedUser(User user);
    void deleteByReviewId(Long reviewId);
    List<Review> findByReviewer_UserID(String userId);
    List<Review> findByReviewedUser_UserID(String userId);
    @Query("SELECT AVG(r.rate) FROM Review r WHERE r.reviewedUser.userID = :userId")
    Double findAverageRatingByUser(String userId);
    boolean existsByReviewerAndReviewedUser(User reviewer, User reviewedUser);

}
