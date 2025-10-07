package org.grp8.swp391.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateReviewRequest {
    private String reviewerId;
    private String sellerId;
    private int rate;
    private String comment;
}
