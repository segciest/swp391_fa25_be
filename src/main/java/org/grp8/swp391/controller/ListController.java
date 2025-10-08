package org.grp8.swp391.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.grp8.swp391.config.JwtUtils;
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
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/listing")
public class ListController {
    @Autowired
    private ListingService listingService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private JwtUtils jwtUtils;


    @GetMapping
    public ResponseEntity<?> getAllListings(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Listing> listings = listingService.findAll(pageable);
        return ResponseEntity.ok(listings);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getListingById(@PathVariable String id) {
        try {
            Listing lis = listingService.findById(id);
            return ResponseEntity.ok(lis);
        }catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

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



    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteListing(@PathVariable String id) {
        listingService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateListing(@PathVariable String id, @RequestBody Listing listing) {
        return ResponseEntity.ok(listingService.updateById(id, listing));
    }
    @PostMapping("/create")
    public ResponseEntity<?>  create(@RequestBody Listing listing, HttpServletRequest request) {
        String token = jwtUtils.extractToken(request);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing token");
        }
        String email = jwtUtils.getUsernameFromToken(token);

        User seller = userRepo.findByUserEmail(email);
        if (seller == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        listing.setSeller(seller);
        Listing saved = listingService.create(listing);
        return ResponseEntity.ok(saved);
    }
    @GetMapping("/seller/{id}")
    public ResponseEntity<?> getByUser(@PathVariable String id, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Listing> listings = listingService.findBySellerId(id, pageable);
        return ResponseEntity.ok(listings);
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
                                           @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(listingService.findByCategoryId(categoryId, pageable));
    }


    @GetMapping("/search/model")
    public ResponseEntity<?> searchByModel(@RequestParam String model,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size) {
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
