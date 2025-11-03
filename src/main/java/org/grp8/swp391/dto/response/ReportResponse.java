package org.grp8.swp391.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {
    private Long reportId;

    private String reporterId;
    private String reporterName;
    private String reporterEmail;

    private String listingId;
    private String listingTitle;
    private String listingOwnerName;
    private String listingImageUrl;

    private String reason;
    private String status;
    private Date createdAt;
}
