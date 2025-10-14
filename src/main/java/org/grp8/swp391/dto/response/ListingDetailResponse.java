package org.grp8.swp391.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListingDetailResponse {
    private String listingId;
    private String title;
    private String description;
    private String brand;
    private String model;
    private String color;
    private Integer year;
    private Double price;
    private String contact;
    private String categoryName;
    private String sellerName;
    private List<String> imageUrls;
}
