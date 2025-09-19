package org.grp8.swp391.controller;

import org.grp8.swp391.entity.Listing;
import org.grp8.swp391.entity.ListingStatus;
import org.grp8.swp391.service.ListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/listing")
public class ListController {
    @Autowired
    private ListingService listingService;


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
    public ResponseEntity<?>  create(Listing listing) {
        return ResponseEntity.ok(listingService.create(listing));
    }
    @GetMapping("/seller/{id}")
    public ResponseEntity<?> getByUser(@PathVariable String id, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Listing> listings = listingService.findBySellerId(id, pageable);
        return ResponseEntity.ok(listings);
    }
    @GetMapping("/seller/{status}")
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
