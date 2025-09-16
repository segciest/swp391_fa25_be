package org.grp8.swp391.repository;

import org.grp8.swp391.entity.Contract;
import org.grp8.swp391.entity.Listing;
import org.grp8.swp391.entity.ListingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;


public interface ListingRepo extends JpaRepository<Listing, String> {
    Page<Listing> findByListing_Id(Long listingId, Pageable pageable);
    Page<Listing> findBySeller(Long sellerId, Pageable pageable);
    Page<Listing> findByStatus(ListingStatus status, Pageable pageable);
    Page<Listing> findByCategory(Long categoryId, Pageable pageable);

}
