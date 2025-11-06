package org.grp8.swp391.service;


import org.grp8.swp391.dto.response.ListingDetailResponse;
import org.grp8.swp391.dto.response.ListingResponse;
import org.grp8.swp391.entity.*;
import org.grp8.swp391.repository.ListingRepo;
import org.grp8.swp391.repository.SubRepo;
import org.grp8.swp391.repository.UserRepo;
import org.grp8.swp391.repository.UserSubRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


@Service
public class ListingService {

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ListingRepo listingRepo;

    @Autowired
    private UserSubRepo userSubRepo;

    @Autowired
    private SubRepo  subRepo;



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

    public Listing createListing(Listing listing, MultipartFile[] files) {
        User seller = validateAndGetSeller(listing.getSeller().getUserID());

        validateSubscription(seller);
        listing.setCity(seller.getCity());
        listing.setContact(seller.getPhone());
        listing.setCreatedAt(new Date());
        listing.setStatus(ListingStatus.PENDING);
        listing.setSeller(seller);

        if (files != null && files.length > 0) {
            List<Image> images = uploadImages(files, listing);
            listing.setImages(images);
        }

        return listingRepo.save(listing);
    }


    private User validateAndGetSeller(String sellerId) {
        User seller = userRepo.findByUserID(sellerId);
        if (seller == null) {
            throw new RuntimeException("Seller not found.");
        }
        if (seller.getUserStatus() != UserStatus.ACTIVE) {
            throw new RuntimeException("You must verify your email before posting a listing.");
        }

        return seller;
    }

    private void validateSubscription(User seller) {
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

        if (userSub.getStatus().equalsIgnoreCase("ACTIVE") && sub.getSubName().equalsIgnoreCase("Free")) {
            long postCount = listingRepo.countListingsByUser(seller.getUserID());
            if (postCount >= 1) {
                throw new RuntimeException("Free plan users can only post 1 listing. Please upgrade your plan.");
            }
        }
    }


    private List<Image> uploadImages(MultipartFile[] files, Listing listing) {
        List<Image> images = new ArrayList<>();
        for (MultipartFile file : files) {
            String url = cloudinaryService.uploadFile(file);
            Image img = new Image();
            img.setUrl(url);
            img.setListingId(listing);
            images.add(img);
        }
        return images;
    }



    public Listing updateById(String id, Listing lis, User us) {
        Listing up = listingRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found with id: " + id));

        if (!up.getSeller().getUserID().equals(us.getUserID())
                && !us.getRole().getRoleName().equalsIgnoreCase("ADMIN")) {
            throw new RuntimeException("You do not have permission to edit this listing.");
        }

        if (lis.getTitle() != null && !lis.getTitle().isBlank()) {
            up.setTitle(lis.getTitle().trim());
        }

        if (lis.getDescription() != null && !lis.getDescription().isBlank()) {
            up.setDescription(lis.getDescription().trim());
        }

        if (lis.getBrand() != null) {
            up.setBrand(lis.getBrand().trim());
        }

        if (lis.getModel() != null) {
            up.setModel(lis.getModel().trim());
        }

        if (lis.getColor() != null) {
            up.setColor(lis.getColor().trim());
        }

        if (lis.getYear() != null && lis.getYear() >= 1900) { // tránh năm vô lý
            up.setYear(lis.getYear());
        }

        if (lis.getSeats() != null && lis.getSeats() > 0) {
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

        if (lis.getCapacity() != null ) {
            up.setCapacity(lis.getCapacity());
        }

        if (lis.getVoltage() != null) {
            up.setVoltage(lis.getVoltage());
        }

        if (lis.getCycleCount() != null && lis.getCycleCount() >= 0) {
            up.setCycleCount(lis.getCycleCount());
        }

        if (lis.getBatteryLifeRemaining() != null) {
            up.setBatteryLifeRemaining(lis.getBatteryLifeRemaining());
        }

        if (lis.getPrice() != null && lis.getPrice() >= 0) {
            up.setPrice(lis.getPrice());
        }



        up.setUpdatedAt(new Date());
        return listingRepo.save(up);
    }



    public Listing updateListingStatus(String id, ListingStatus status) {
        Listing lis = listingRepo.findByListingId(id);
        if (lis == null) {
            throw new RuntimeException("Listing not found with id: " + id);
        }



        if(status.equals(ListingStatus.ACTIVE)) {
            Date now = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(now);

            User seller = lis.getSeller();
            if (seller != null && seller.getSubid() != null) {
                String subName = seller.getSubid().getSubName();

                if (subName.equalsIgnoreCase("Free")) {
                    cal.add(Calendar.DATE, 30);
                } else if (subName.equalsIgnoreCase("Basic")) {
                    cal.add(Calendar.DATE, 60);
                } else if (subName.equalsIgnoreCase("Premium")) {
                    cal.add(Calendar.DATE, 75);
                } else if (subName.equalsIgnoreCase("Vip")) {
                    cal.add(Calendar.DATE, 90);
                } else {
                    cal.add(Calendar.DATE, 30);
                }
            } else {
                cal.add(Calendar.DATE, 30);
            }
            lis.setExpiredAt(cal.getTime());

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
        return listingRepo.findByPriceBetween(minPrice, maxPrice, pageable);
    }

    public Page<Listing> findByVehicleType(String vehicleType, Pageable pageable) {
        return listingRepo.findByVehicleTypeIgnoreCase(vehicleType, pageable);
    }

    public Page<Listing> findByYearRange(Integer start, Integer end, Pageable pageable) {
        return listingRepo.findByYearBetween(start, end, pageable);
    }

    public Page<Listing> findAllActive(Pageable pageable) {
        return listingRepo.findByStatus(ListingStatus.ACTIVE, pageable);
    }

    public Page<Listing> findAllPending(Pageable pageable) {
        return listingRepo.findPendingListingsOrderByCreatedAtDesc(pageable);
    }

    public Page<Listing> findByTitle(String title, Pageable pageable) {
        return listingRepo.findByTitleContaining(title, pageable);
    }


    public ListingResponse toListingResponse(Listing listing) {
        List<String> urls = listing.getImages() != null
                ? listing.getImages().stream().map(Image::getUrl).toList()
                : List.of();

        return new ListingResponse(
                listing.getListingId(),
                listing.getTitle(),
                listing.getPrice(),
                urls
        );
    }

    public ListingDetailResponse toListingDetailResponse(Listing listing) {
        if (listing == null) {
            return null;
        }


        List<String> imageUrls = new ArrayList<>();
        if (listing.getImages() != null && !listing.getImages().isEmpty()) {
            imageUrls = listing.getImages().stream()
                    .map(Image::getUrl)
                    .toList();
        }


        String sellerName = null;
        String sellerEmail = null;
        String sellerPhone = null;
        String sellerAvatar = null;

        if (listing.getSeller() != null) {
            sellerName = listing.getSeller().getUserName();
            sellerEmail = listing.getSeller().getUserEmail();
            sellerPhone = listing.getSeller().getPhone();
            sellerAvatar = listing.getSeller().getAvatarUrl();
        }

        return new ListingDetailResponse(
                listing.getListingId(),
                listing.getTitle(),
                listing.getDescription(),

                listing.getBrand(),
                listing.getModel(),
                listing.getColor(),
                listing.getYear(),
                listing.getSeats(),
                listing.getVehicleType(),

                listing.getMileage(),
                listing.getBatteryCapacity(),
                listing.getWarrantyInfo(),

                listing.getPrice(),
                listing.getContact(),

                listing.getCategory() != null ? listing.getCategory().getCategoryName() : null,
                sellerName,
                sellerEmail,
                sellerPhone,
                sellerAvatar,

                listing.getStatus() != null ? listing.getStatus().name() : null,
                listing.getCreatedAt() != null ? listing.getCreatedAt().toString() : null,
                listing.getUpdatedAt() != null ? listing.getUpdatedAt().toString() : null,

                imageUrls
        );
    }

    public Page<Listing> findBySellerCity(String sellerCity, Pageable pageable) {
        return listingRepo.findByCityIgnoreCase(sellerCity, pageable);
    }

    public Page<Listing> getAllSortedByPriority(Pageable pageable) {
        return listingRepo.findAllListingByPriorityAndDate(pageable);
    }


    public void checkExpiredListing() {
        Date now = new Date();
        List<Listing> active = listingRepo.findByStatus(ListingStatus.ACTIVE);
        for(Listing lis : active){
            if(lis.getExpiredAt() != null && lis.getExpiredAt().before(now)) {
                lis.setStatus(ListingStatus.EXPIRED);
                listingRepo.save(lis);
            }
        }

    }

    public List<Listing> findBySellerAndStatus(String seller, ListingStatus status) {
        List<Listing> lis = listingRepo.findBySeller_UserIDAndStatus(seller, status);
        if (lis == null || lis.isEmpty()) {
            throw new RuntimeException("Không tìm thấy bài đăng nào.");
        }


        return lis;
    }

}
