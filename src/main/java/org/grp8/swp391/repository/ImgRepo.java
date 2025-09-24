package org.grp8.swp391.repository;

import org.grp8.swp391.entity.Image;
import org.grp8.swp391.entity.Listing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ImgRepo extends JpaRepository<Image, Long> {
    Image findByImageId(Long imageId);
    Image findByListingId(Listing listingId);
    Image findByUrl(String url);

}
