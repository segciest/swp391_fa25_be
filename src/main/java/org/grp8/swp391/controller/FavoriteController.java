package org.grp8.swp391.controller;


import jakarta.servlet.http.HttpServletRequest;
import org.grp8.swp391.config.JwtUtils;
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

    @Autowired
    private JwtUtils jwtUtils;


    @GetMapping("/user")
    public ResponseEntity<?> getFavoriteByUser(HttpServletRequest req){
        try {
            String token = jwtUtils.extractToken(req);
            if (!jwtUtils.checkValidToken(token) || token == null) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            String email = jwtUtils.getUsernameFromToken(token);
            if (email == null) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            User us = userService.findByUserEmail(email);


            List<Favorite> favor = favoriteService.findByUser(us);
            return new ResponseEntity<>(favor, HttpStatus.OK);
        }catch(RuntimeException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/toggle")
    public ResponseEntity<?> toggleFavorite(HttpServletRequest req, @RequestParam String listingId) {
        String token = jwtUtils.extractToken(req);
        if (token == null || !jwtUtils.checkValidToken(token)) {
            return ResponseEntity.status(401).body("Invalid or missing token");
        }

        String email = jwtUtils.getUsernameFromToken(token);
        User user = userService.findByUserEmail(email);
        Listing listing = listingService.findById(listingId);

        Favorite result = favoriteService.toggleFavorite(user, listing);

        if (result == null) {
            return ResponseEntity.ok("Removed from favorites");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
    @DeleteMapping("/remove")
    public ResponseEntity<?> deleteFavorite(HttpServletRequest req, @RequestParam String listingId) {
        try {
            String token = jwtUtils.extractToken(req);
            if (token == null || !jwtUtils.checkValidToken(token)) {
                return ResponseEntity.status(401).body("Invalid or missing token");
            }
            String email = jwtUtils.getUsernameFromToken(token);
            User user = userService.findUserById(email);

            Listing listing = listingService.findById(listingId);
            favoriteService.removeFavorite(user, listing);
            return ResponseEntity.ok("Removed from favorites successfully");
        }catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
