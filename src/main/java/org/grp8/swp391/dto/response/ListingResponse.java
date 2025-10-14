package org.grp8.swp391.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListingResponse {
    private String listingId;
    private String title;
    private Double price;
    private List<String> imageUrls;
}
