package org.grp8.swp391.service;

import org.grp8.swp391.entity.Favorite;
import org.grp8.swp391.entity.Listing;
import org.grp8.swp391.entity.User;
import org.grp8.swp391.repository.FavoriteRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteRepo favoriteRepo;

    public List<Favorite> getAllFavorites()
    {
        return favoriteRepo.findAll();
    }

    public Favorite getFavoriteById(long id){
        return favoriteRepo.findById(id).orElse(null);
    }

    public Favorite addFavorite(User user, Listing listing){
        if(favoriteRepo.findByUserAndListing(user,listing).isPresent()){
            throw new RuntimeException("this listing is already favorite!");
        }

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setListing(listing);
        favorite.setCreatedAt(new Date());
        return favoriteRepo.save(favorite);
    }

    public void removeFavorite(User user, Listing listing){
         favoriteRepo.deleteByUserAndListing(user,listing);
    }

    public List<Favorite> findByUser(User user){
        return favoriteRepo.findByUser(user);
    }
}
