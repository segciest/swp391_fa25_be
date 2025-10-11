package org.grp8.swp391.service;

import org.grp8.swp391.entity.Image;
import org.grp8.swp391.entity.Listing;
import org.grp8.swp391.repository.ImgRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ImgService {
    @Autowired
    private ImgRepo imgRepo;

    public Image findByImgId(long id) {
        return imgRepo.findByImageId(id);

    }

    public Image findByUrl(String url) {
        return imgRepo.findByUrl(url);
    }

    public List<Image> findByListing(Listing listing) {
        return imgRepo.findByListingId(listing);
    }

    public Image save(Image image) {
        return imgRepo.save(image);
    }

    public List<Image> getByListing(Listing listing) {
        return imgRepo.findByListingId(listing);
    }

}
