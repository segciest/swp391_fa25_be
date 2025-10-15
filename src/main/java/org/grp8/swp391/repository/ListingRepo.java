package org.grp8.swp391.repository;

import org.grp8.swp391.entity.Listing;
import org.grp8.swp391.entity.ListingStatus;
import org.grp8.swp391.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListingRepo extends JpaRepository<Listing, String> {
    Listing findByListingId(String listingId);

    @Query("SELECT COUNT(l) FROM Listing l WHERE l.seller.userID = :userID")
    long countListingsByUser(@Param("userID") String userID);

    Page<Listing> findBySeller_UserID(String sellerId, Pageable pageable);
    List<Listing> findBySeller_UserID(String sellerId);

    Page<Listing> findByStatus(ListingStatus status, Pageable pageable);

    Page<Listing> findByCategory_CategoryId(Long categoryId, Pageable pageable);
    Page<Listing> findByYear(Integer year, Pageable pageable);

    Page<Listing> findByModelContainingIgnoreCase(String model, Pageable pageable);
    Page<Listing> findByColorIgnoreCase(String color, Pageable pageable);
    Page<Listing> findByBrandIgnoreCase(String brand, Pageable pageable);
    Page<Listing> findByVehicleTypeIgnoreCase(String type, Pageable pageable);

    Page<Listing> findByYearBetween(int startYear, int endYear, Pageable pageable);
    Page<Listing> findByPriceBetween(Double min, Double max, Pageable pageable);
    Long countByStatus(ListingStatus status);
    Page<Listing> findByCityIgnoreCase(String city, Pageable pageable);

}
