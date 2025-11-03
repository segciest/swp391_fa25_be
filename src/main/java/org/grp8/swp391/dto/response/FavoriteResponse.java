package org.grp8.swp391.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteResponse {
    private Long favoriteId;

    private String listingId;
    private String title;
    private Double price;
    private String thumbnailUrl;
    private String status;

    private String sellerName;
    private String sellerCity;

    private Date createdAt;
}
