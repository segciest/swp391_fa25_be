package org.grp8.swp391.service;


import org.grp8.swp391.entity.Listing;
import org.grp8.swp391.entity.ListingStatus;
import org.grp8.swp391.repository.ListingRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;


@Service
public class ListingService {

    @Autowired
    private ListingRepo listingRepo;

    public Page<Listing> findAll(Pageable pageable) {
        return listingRepo.findAll(pageable);
    }

    public Page<Listing> findByCategoryId(Long categoryId, Pageable pageable) {
        return listingRepo.findByCategory(categoryId, pageable);
    }

    public Page<Listing> findByStatus(ListingStatus status, Pageable pageable) {
        return listingRepo.findByStatus(status, pageable);
    }

    public Page<Listing> findBySellerId(Long sellerId, Pageable pageable) {
        return listingRepo.findBySeller(sellerId, pageable);
    }

    public Listing save(Listing listing) {
        return listingRepo.save(listing);
    }

    public Listing create(Listing listing) {
        return listingRepo.save(listing);
    }

    public Listing updateById(String id, Listing lis) {
        Listing up = listingRepo.findById(id).orElseThrow(() -> new RuntimeException("Listing not found with id: " + id));
        up.setTitle(lis.getTitle());
        up.setDescription(lis.getDescription());
        up.setPrice(lis.getPrice());
        up.setStatus(lis.getStatus());
        up.setCategory(lis.getCategory());
        return listingRepo.save(up);
    }
}
