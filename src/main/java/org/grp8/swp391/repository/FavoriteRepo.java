package org.grp8.swp391.repository;


import org.grp8.swp391.entity.Favorite;
import org.grp8.swp391.entity.Listing;
import org.grp8.swp391.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepo extends JpaRepository<Favorite,Long> {
    Favorite getFavoriteById(Long id);
    void deleteById(Long id);
    List<Favorite> getFavoritesByUser(User userId);
    Optional<Favorite> findByUserAndListing(User user, Listing listing);
    void deleteByUserAndListing(User user, Listing listing);
    List<Favorite> findByUser(User user);

}
