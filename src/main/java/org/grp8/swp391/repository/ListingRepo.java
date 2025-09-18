package org.grp8.swp391.repository;

import org.grp8.swp391.entity.Listing;
import org.grp8.swp391.entity.ListingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;


public interface ListingRepo extends JpaRepository<Listing, String> {
    Listing findByListingId(String listingId);


    Page<Listing> findBySeller_UserID(String sellerId, Pageable pageable);

    Page<Listing> findByStatus(ListingStatus status, Pageable pageable);

    // hoặc tìm theo categoryId
    Page<Listing> findByCategory_CategoryId(Long categoryId, Pageable pageable);
}
