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
    public ResponseEntity<?> addFavorite(HttpServletRequest req, @RequestParam String listingId) {
        try{
            String token = jwtUtils.extractToken(req);
            if (token == null || !jwtUtils.checkValidToken(token)) {
                return ResponseEntity.status(401).body("Invalid or missing token");
            }

            String email = jwtUtils.getUsernameFromToken(token);
            User user = userService.findUserById(email);
            Listing lis = listingService.findById(listingId);
            Favorite fav = favoriteService.addFavorite(user, lis);
            return ResponseEntity.status(HttpStatus.CREATED).body(fav);
        }catch(RuntimeException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
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
