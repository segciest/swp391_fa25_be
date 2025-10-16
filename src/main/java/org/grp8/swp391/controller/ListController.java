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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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


    @GetMapping
    public ResponseEntity<?> getAllListings(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
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
        Page<Listing> listings = listingService.findAllActive(pageable);
        List<ListingDetailResponse> lis = listings.getContent()
                .stream()
                .map(listingService::toListingDetailResponse)
                .toList();
        return ResponseEntity.ok(lis);
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
    @PostMapping("/approve/{id}")
    public ResponseEntity<?> approveListing(@PathVariable String id) {
        try {
            Listing approveLis = listingService.updateListingStatus(id, ListingStatus.ACTIVE);
            return ResponseEntity.ok("Listing approved successfully with id: " + id);
        }catch(RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/reject/{id}")
    public ResponseEntity<?> rejectListing(@PathVariable String id) {
        try {
            Listing approveLis = listingService.updateListingStatus(id, ListingStatus.REJECTED);
            return ResponseEntity.ok("Listing rejected successfully with id: " + id);
        }catch(RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteListing(@PathVariable String id) {
        listingService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateListing(@PathVariable String id, @RequestBody Listing listing) {
        return ResponseEntity.ok(listingService.updateById(id, listing));
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
            return ResponseEntity.badRequest().body("Invalid listing JSON");
        }

        String email = jwtUtils.getUsernameFromToken(token);
        User seller = userRepo.findByUserEmail(email);
        listing.setSeller(seller);

        Listing saved = listingService.createListing(listing, files);

        return ResponseEntity.ok(Map.of(
                "message", "Đăng bài thành công!",
                "data", listingService.toListingResponse(saved)
        ));
    }
    @GetMapping("/seller/{id}")
    public ResponseEntity<?> getByUser(@PathVariable String id, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Listing> listings = listingService.findBySellerId(id, pageable);
        List<ListingResponse> response = listings.getContent()
                .stream()
                .map(listingService::toListingResponse)
                .toList();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getByStatus(@PathVariable ListingStatus status,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(listingService.findByStatus(status, pageable));

    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<?> getByCategory(@PathVariable Long categoryId,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(listingService.findByCategoryId(categoryId, pageable));
    }


    @GetMapping("/search/model")
    public ResponseEntity<?> searchByModel(@RequestParam String model,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(listingService.findByModel(model, pageable));
    }

    @GetMapping("/search/color")
    public ResponseEntity<?> searchByColor(@RequestParam String color,@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(listingService.findByColor(color, pageable));
    }

    @GetMapping("/search/brand")
    public ResponseEntity<?> searchByBrand(@RequestParam String brand, @RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(listingService.findByBrand(brand, pageable));
    }

    @GetMapping("/search/vehicle-type")
    public ResponseEntity<?> searchByVehicleType(@RequestParam String type, @RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(listingService.findByVehicleType(type, pageable));
    }

    @GetMapping("/filter/year")
    public ResponseEntity<?> filterByYear(@RequestParam int start,@RequestParam int end,@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(listingService.findByYearRange(start, end, pageable));
    }

    @GetMapping("/filter/price")
    public ResponseEntity<?> filterByPrice(@RequestParam Double min,@RequestParam Double max,@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(listingService.filterByPriceRange(min, max, pageable));
    }
}
