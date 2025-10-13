package org.grp8.swp391.service;


import org.grp8.swp391.entity.*;
import org.grp8.swp391.repository.ListingRepo;
import org.grp8.swp391.repository.UserRepo;
import org.grp8.swp391.repository.UserSubRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;


@Service
public class ListingService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ListingRepo listingRepo;

    @Autowired
    private UserSubRepo userSubRepo;

    public Page<Listing> findAll(Pageable pageable) {
        return listingRepo.findAll(pageable);
    }

    public void delete(String id) {
        listingRepo.deleteById(id);
    }

    public Page<Listing> findByCategoryId(Long categoryId, Pageable pageable) {
        return listingRepo.findByCategory_CategoryId(categoryId, pageable);
    }

    public Page<Listing> findByStatus(ListingStatus status, Pageable pageable) {
        return listingRepo.findByStatus(status, pageable);
    }

    public Page<Listing> findBySellerId(String sellerId, Pageable pageable) {
        return listingRepo.findBySeller_UserID(sellerId, pageable);
    }

    public Listing save(Listing listing) {
        return listingRepo.save(listing);
    }

    public Listing create(Listing listing) {

        User seller = userRepo.findByUserID(listing.getSeller().getUserID());
        if (seller == null) {
            throw new RuntimeException("Seller not found.");
        }

        User_Subscription userSub = userSubRepo.findFirstByUserOrderByEndDateDesc(seller);
        if (userSub == null) {
            throw new RuntimeException("You must subscribe to a package before posting listings.");
        }


        Date now = new Date();
        if (userSub.getEndDate() != null && userSub.getEndDate().before(now)) {
            throw new RuntimeException("Your subscription has expired. Please renew to continue posting.");
        }


        Subscription sub = userSub.getSubscriptionId();
        if (sub == null) {
            throw new RuntimeException("Subscription information not found for this user.");
        }


        if (sub.getSubName().equalsIgnoreCase("Free")) {
            long postCount = listingRepo.countListingsByUser(seller.getUserID());
            if (postCount >= 1) {
                throw new RuntimeException("Free plan users can only post 1 listing. Please upgrade your plan.");
            }
        }



        listing.setCreatedAt(new Date());
        listing.setStatus(ListingStatus.PENDING);
        listing.setSeller(seller);

        if (listing.getImages() != null && !listing.getImages().isEmpty()) {
            for (Image img : listing.getImages()) {
                img.setListingId(listing);
            }
        }
        return listingRepo.save(listing);
    }

    public Listing updateById(String id, Listing lis) {
        Listing up = listingRepo.findById(id).orElseThrow(() -> new RuntimeException("Listing not found with id: " + id));


        if (lis.getTitle() != null) {
            up.setTitle(lis.getTitle());
        }
        if (lis.getDescription() != null) {
            up.setDescription(lis.getDescription());
        }
        if (lis.getBrand() != null) {
            up.setBrand(lis.getBrand());
        }
        if (lis.getModel() != null) {
            up.setModel(lis.getModel());
        }
        if (lis.getColor() != null) {
            up.setColor(lis.getColor());
        }
        if (lis.getYear() != null) {
            up.setYear(lis.getYear());
        }
        if (lis.getSeats() != null) {
            up.setSeats(lis.getSeats());
        }
        if (lis.getVehicleType() != null) {
            up.setVehicleType(lis.getVehicleType());
        }
        if (lis.getMileage() != null) {
            up.setMileage(lis.getMileage());
        }
        if (lis.getBatteryCapacity() != null) {
            up.setBatteryCapacity(lis.getBatteryCapacity());
        }
        if (lis.getCapacity() != null) {
            up.setCapacity(lis.getCapacity());
        }
        if (lis.getVoltage() != null) {
            up.setVoltage(lis.getVoltage());
        }
        if (lis.getCycleCount() != null) {
            up.setCycleCount(lis.getCycleCount());
        }
        if (lis.getBatteryLifeRemaining() != null) {
            up.setBatteryLifeRemaining(lis.getBatteryLifeRemaining());
        }
        if (lis.getPrice() != null) {
            up.setPrice(lis.getPrice());
        }
        // contract field removed - no longer handled
        if (lis.getStatus() != null) {
            up.setStatus(lis.getStatus());
        }
        if (lis.getCategory() != null) {
            up.setCategory(lis.getCategory());
        }

        up.setUpdatedAt(new Date());
        return listingRepo.save(up);
    }


    public Listing updateListingStatus(String id, ListingStatus status) {
        Listing lis = listingRepo.findByListingId(id);
        if (lis == null) {
            throw new RuntimeException("Listing not found with id: " + id);
        }
        lis.setStatus(status);
        return listingRepo.save(lis);
    }

    public Listing findById(String id) {
        return listingRepo.findById(id).orElseThrow(() -> new RuntimeException("Listing not found with id: " + id));
    }

    public Page<Listing> findByModel(String model, Pageable pageable) {
        return listingRepo.findByModelContainingIgnoreCase(model, pageable);
    }

    public Page<Listing> findByColor(String color, Pageable pageable) {
        return listingRepo.findByColorIgnoreCase(color, pageable);
    }

    public Page<Listing> findByBrand(String brand, Pageable pageable) {
        return listingRepo.findByBrandIgnoreCase(brand, pageable);
    }

    public Page<Listing> filterByPriceRange(Double minPrice, Double maxPrice, Pageable pageable) {
        return  listingRepo.findByPriceBetween(minPrice, maxPrice, pageable);
    }

    public Page<Listing>  findByVehicleType(String vehicleType, Pageable pageable) {
        return listingRepo.findByVehicleTypeIgnoreCase(vehicleType, pageable);
    }

    public Page<Listing> findByYearRange(Integer start, Integer end, Pageable pageable) {
        return listingRepo.findByYearBetween(start, end, pageable);
    }

    public Page<Listing> findAllActive(Pageable pageable) {
        return listingRepo.findByStatus(ListingStatus.ACTIVE, pageable);
    }





}
