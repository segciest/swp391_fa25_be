package org.grp8.swp391.controller;


import org.grp8.swp391.entity.Favorite;
import org.grp8.swp391.entity.Listing;
import org.grp8.swp391.entity.User;
import org.grp8.swp391.service.FavoriteService;
import org.grp8.swp391.service.ListingService;
import org.grp8.swp391.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorite")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private UserService userService;

    @Autowired
    private ListingService listingService;


    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getFavoriteByUser(@PathVariable String userId){
        User user = userService.findUserById(userId);
        if(user==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<Favorite> favor = favoriteService.findByUser(user);
        return new ResponseEntity<>(favor, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<?> addFavorite(@RequestParam String userId, @RequestParam String listingId) {
        try {
            User user = userService.findUserById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            Listing listing = listingService.findById(listingId);
            if (listing == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Listing not found");
            }

            Favorite favorite = favoriteService.addFavorite(user, listing);
            return ResponseEntity.status(HttpStatus.CREATED).body(favorite);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping("/remove")
    public ResponseEntity<?> deleteFavorite(@RequestParam String userId, @RequestParam String listingId) {
        try {
            User user = userService.findUserById(userId);
            Listing listing = listingService.findById(listingId);
            favoriteService.removeFavorite(user, listing);
            return ResponseEntity.ok("Removed from favorites successfully");
        }catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
