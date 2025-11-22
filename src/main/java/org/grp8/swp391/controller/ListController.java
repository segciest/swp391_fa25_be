package org.grp8.swp391.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.grp8.swp391.config.JwtUtils;
import org.grp8.swp391.dto.response.ListingDetailResponse;
import org.grp8.swp391.dto.response.ListingResponse;
import org.grp8.swp391.entity.Image;
import org.grp8.swp391.entity.Listing;
import org.grp8.swp391.entity.ListingStatus;
import org.grp8.swp391.entity.User;
import org.grp8.swp391.repository.UserRepo;
import org.grp8.swp391.service.ListingService;
import org.grp8.swp391.service.NotificationService;
import org.grp8.swp391.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.grp8.swp391.service.CloudinaryService;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/listing")
public class ListController {
    @Autowired
    private ListingService listingService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private NotificationService notificationService;
    @Autowired
    private UserService userService;


    @GetMapping
    public ResponseEntity<?> getAllListings(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Listing> listings = listingService.findAll(pageable);
        List<ListingDetailResponse> lis = listings.getContent()
                .stream()
                .map(listingService::toListingDetailResponse)
                .toList();
        return ResponseEntity.ok(lis);
    }
    @GetMapping("/active")
    public ResponseEntity<?> getAllActiveListings(@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "20") int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<Listing> listings = listingService.getAllSortedByPriority(pageable);

        List<ListingDetailResponse> lis = listings.getContent()
                .stream()
                .map(listingService::toListingDetailResponse)
                .toList();

        return ResponseEntity.ok(lis);
    }

    @GetMapping("/prior")
    public ResponseEntity<?> getAllSortedListings(@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Listing> listings = listingService.getAllSortedByPriority(pageable);
            List<ListingDetailResponse> lis = listings.getContent()
                    .stream()
                    .map(listingService::toListingDetailResponse)
                    .toList();
            return ResponseEntity.ok(lis);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }


    @GetMapping("/filter/city")
    public ResponseEntity<?> filterByCity(@RequestParam String city,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Listing> listings = listingService.findBySellerCity(city, pageable);
        List<ListingResponse> result = listings.getContent()
                .stream()
                .map(listingService::toListingResponse)
                .toList();
        return ResponseEntity.ok(result);
    }


    @GetMapping("/pending")
    public ResponseEntity<?> getAllPendingListings(@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "20") int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<Listing> listings = listingService.findAllPending(pageable);
        List<ListingDetailResponse> lis = listings.getContent()
                .stream()
                .map(listingService::toListingDetailResponse)
                .toList();
        return ResponseEntity.ok(lis);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getListingById(@PathVariable String id) {
        try {
            Listing lis = listingService.findById(id);
            return ResponseEntity.ok(listingService.toListingDetailResponse(lis));
        }catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    @PreAuthorize("hasAuthority('ADMIN')")

    @PutMapping("/status/{id}")
    public ResponseEntity<?> updateListingStatus(@PathVariable String id, @RequestParam String status) {
        try {
            ListingStatus lisStatus = ListingStatus.valueOf(status.toUpperCase());
            Listing lis = listingService.updateListingStatus(id, lisStatus);
            return ResponseEntity.ok(lis);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid status value: " + status);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PreAuthorize("hasAuthority('ADMIN')")

    @PostMapping("/approve/{id}")
    public ResponseEntity<?> approveListing(@PathVariable String id) {
        try {
            Listing approvedListing = listingService.updateListingStatus(id, ListingStatus.ACTIVE);
            
            notificationService.notifyListingApproved(approvedListing.getSeller(), approvedListing);
            
            return ResponseEntity.ok("Listing approved successfully with id: " + id);
        }catch(RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/reject/{id}")
    public ResponseEntity<?> rejectListing(@PathVariable String id, @RequestParam(required = false) String reason) {
        try {
            Listing rejectedListing = listingService.updateListingStatus(id, ListingStatus.REJECTED);

            // If admin provided a reason, store it on the listing.message (no schema change)
            if (reason != null && !reason.isBlank()) {
                try {
                    rejectedListing.setMessage(reason);
                    listingService.save(rejectedListing);
                } catch (Exception ex) {
                    // ignore save failure of message but proceed to notify
                    System.err.println("Failed to save reject reason to listing: " + ex.getMessage());
                }
            }

            // Notify seller; include reason if provided
            try {
                if (reason != null && !reason.isBlank()) {
                    String msg = "Bài đăng của bạn đã bị từ chối. Lý do: " + reason;
                    notificationService.createNotification(rejectedListing.getSeller().getUserID(), msg);
                } else {
                    notificationService.notifyListingRejected(rejectedListing.getSeller(), rejectedListing);
                }
            } catch (Exception ex) {
                System.err.println("Failed to create rejection notification: " + ex.getMessage());
            }

            return ResponseEntity.ok("Listing rejected successfully with id: " + id);
        }catch(RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteListing(@PathVariable String id) {
        try{
        listingService.delete(id);
        return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateListing(@PathVariable String id, @RequestBody Listing listing, HttpServletRequest request) {
        try {
            String token = jwtUtils.extractToken(request);
            if (token == null || !jwtUtils.checkValidToken(token)) {
                return ResponseEntity.status(401).body("Invalid or missing token");
            }

            String email = jwtUtils.getUsernameFromToken(token);
            User user = userRepo.findByUserEmail(email);
            if (user == null) {
                return ResponseEntity.status(404).body("User not found");
            }

            Listing updated = listingService.updateById(id, listing, user);
            return ResponseEntity.ok(Map.of(
                    "message", "Listing updated successfully!",
                    "data", listingService.toListingDetailResponse(updated)
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping(value = "/create", consumes = "multipart/form-data")
    public ResponseEntity<?> create(
            @RequestPart("listing") String listingJson,
            @RequestPart(value = "files", required = false) MultipartFile[] files,
            HttpServletRequest request) {

        String token = jwtUtils.extractToken(request);
        if (token == null || !jwtUtils.checkValidToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing token");
        }

        Listing listing;
        try {
            ObjectMapper mapper = new ObjectMapper();
            listing = mapper.readValue(listingJson, Listing.class);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid listing JSON: " + e.getMessage());
        }

        String email = jwtUtils.getUsernameFromToken(token);
        User seller = userRepo.findByUserEmail(email);
        if (seller == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }
        listing.setSeller(seller);

        try {
            Listing saved = listingService.createListing(listing, files);
            return ResponseEntity.ok(Map.of(
                    "message", "Đăng bài thành công!",
                    "data", listingService.toListingResponse(saved)
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/city")
    public ResponseEntity<?> getListingByUserCity(@RequestParam String city,@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        try{
            Pageable pageable = PageRequest.of(page, size);
            Page<Listing> listings = listingService.findBySellerCity(city, pageable);
            List<ListingResponse> response = listings.getContent().stream()
                    .map(listingService::toListingResponse)
                    .toList();
            return ResponseEntity.ok(response);
        }catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/seller")
    public ResponseEntity<?> getByUser(HttpServletRequest request, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        String token  = jwtUtils.extractToken(request);
        if (token == null || !jwtUtils.checkValidToken(token)) {
            return ResponseEntity.status(401).body("Invalid or missing token");
        }

        String email = jwtUtils.getUsernameFromToken(token);
        User user = userRepo.findByUserEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Page<Listing> listings = listingService.findBySellerId(user.getUserID(), pageable);
        List<ListingDetailResponse> response = listings.getContent()
                .stream()
                .map(listingService::toListingDetailResponse)
                .toList();
        return ResponseEntity.ok(response);
    }


    @PreAuthorize("hasAuthority('ADMIN')")

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getByStatus(@PathVariable ListingStatus status,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(listingService.findByStatus(status, pageable));
        }catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<?> getByCategory(@PathVariable Long categoryId,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(listingService.findByCategoryId(categoryId, pageable));
        }catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/search/title")
    public ResponseEntity<?> getByTitle( @RequestParam(required = false, defaultValue = "") String title, @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "20") int size) {
        try{
            Pageable pageable = PageRequest.of(page, size);
            Page<Listing> listings = listingService.findByTitle(title, pageable);
            List<ListingDetailResponse> response = listings.getContent()
                    .stream()
                    .map(listingService::toListingDetailResponse)
                    .toList();
            return ResponseEntity.ok(response);
        }catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/search/model")
    public ResponseEntity<?> searchByModel(@RequestParam String model,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Listing> listings = listingService.findByModel(model, pageable);
            List<ListingDetailResponse> response = listings.getContent()
                    .stream()
                    .map(listingService::toListingDetailResponse)
                    .toList();
            return ResponseEntity.ok(response);
        }catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/search/color")
    public ResponseEntity<?> searchByColor(@RequestParam String color,@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Listing> listings = listingService.findByColor(color, pageable);
            List<ListingDetailResponse> response = listings.getContent()
                    .stream()
                    .map(listingService::toListingDetailResponse)
                    .toList();
            return ResponseEntity.ok(response);
        }catch(RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/search/brand")
    public ResponseEntity<?> searchByBrand(@RequestParam String brand, @RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Listing> listings = listingService.findByBrand(brand, pageable);
            List<ListingDetailResponse> response = listings.getContent()
                    .stream()
                    .map(listingService::toListingDetailResponse)
                    .toList();
            return ResponseEntity.ok(response);
        }catch(RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/search/vehicle-type")
    public ResponseEntity<?> searchByVehicleType(@RequestParam String type, @RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(listingService.findByVehicleType(type, pageable));
        }catch(RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/filter/year")
    public ResponseEntity<?> filterByYear(@RequestParam int start,@RequestParam int end,@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Listing> listings = listingService.findByYearRange(start, end, pageable);
            List<ListingDetailResponse> response = listings.getContent()
                    .stream()
                    .map(listingService::toListingDetailResponse)
                    .toList();
            return ResponseEntity.ok(response);
        }catch(RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/filter/price")
    public ResponseEntity<?> filterByPrice(@RequestParam Double min,@RequestParam Double max,@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Listing> listings = listingService.filterByPriceRange(min, max, pageable);
            List<ListingDetailResponse> response = listings.getContent()
                    .stream()
                    .map(listingService::toListingDetailResponse)
                    .toList();
            return ResponseEntity.ok(response);
        }catch(RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user-listing")
    public ResponseEntity<?> findByUserIdAndStatus(HttpServletRequest request,@RequestParam ListingStatus status){
        try{
            String token = jwtUtils.extractToken(request);
            if (token == null || !jwtUtils.checkValidToken(token)) {
                return ResponseEntity.status(401).body("Invalid or missing token");
            }
            String email = jwtUtils.getUsernameFromToken(token);
            User u = userService.findByUserEmail(email);
            List<Listing> lis = listingService.findBySellerAndStatus(u.getUserID(), status);
            List<ListingDetailResponse> response = lis
                    .stream()
                    .map(listingService::toListingDetailResponse)
                    .toList();
            return ResponseEntity.ok(response);
        }catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
