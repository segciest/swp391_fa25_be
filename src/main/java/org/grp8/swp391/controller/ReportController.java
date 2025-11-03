package org.grp8.swp391.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.grp8.swp391.config.JwtUtils;
import org.grp8.swp391.dto.response.ReportResponse;
import org.grp8.swp391.entity.Listing;
import org.grp8.swp391.entity.Report;
import org.grp8.swp391.entity.ReportedStatus;
import org.grp8.swp391.entity.User;
import org.grp8.swp391.service.ListingService;
import org.grp8.swp391.service.ReportService;
import org.grp8.swp391.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/report")
public class ReportController {
    @Autowired
    private ReportService reportService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private ListingService listingService;

    @Autowired
    private UserService userService;


    @GetMapping("/id/{id}")
    public ResponseEntity<?> findReportById(@PathVariable Long id){
        try{
            Report re = reportService.findByReportId(id);
            return ResponseEntity.ok().body(re);
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MODERATOR')")

    public ResponseEntity<?> findAllReports(){
        List<Report> report = reportService.getAllReports();
        return ResponseEntity.ok().body(report);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReportById(@PathVariable Long id){
        Report re = reportService.findByReportId(id);
        if(re == null){
            return ResponseEntity.notFound().build();
        }
        reportService.deleteById(id);
        return ResponseEntity.ok().body(re);
    }

    @GetMapping("/pending")
    public ResponseEntity<?> findByPending(){
        List<Report> list = reportService.findByPendingStatus();
        return ResponseEntity.ok().body(list);
    }

    @PutMapping("/status/reject/{id}")
    public ResponseEntity<?> updateRejectStatus(@PathVariable Long id){
        try {
            Report re = reportService.updateReportStatus(id, ReportedStatus.REJECTED);
            return ResponseEntity.ok().body(re);
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PutMapping("/status/resolve/{id}")
    public ResponseEntity<?> updateResolveStatus(@PathVariable Long id){
        try {
            Report re = reportService.updateReportStatus(id, ReportedStatus.RESOLVED);
            return ResponseEntity.ok().body(re);
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



    @GetMapping("/reject")
    public ResponseEntity<?> findByRejected(){
        List<Report> list = reportService.findByRejectStatus();
        return ResponseEntity.ok().body(list);
    }

    @GetMapping("/resolve")
    public ResponseEntity<?> findByResolveStatus(){
        List<Report> list = reportService.findByResolveStatus();
        return ResponseEntity.ok().body(list);
    }

    @PutMapping("/update/{reportId}")
    public ResponseEntity<Report> updateReport(@PathVariable Long reportId, @RequestBody Report report) {
        Report updated = reportService.updateReport(reportId, report);
        return ResponseEntity.ok(updated);
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/status/{reportId}")
    public ResponseEntity<?> updateReportStatus(@PathVariable Long reportId,@RequestParam ReportedStatus status){
        try {
            Report report = reportService.updateReportStatus(reportId, status);
            return ResponseEntity.ok().body(report);
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/create")
    public ResponseEntity<?> createReport(@RequestBody Map<String, String> payload, HttpServletRequest request) {
        try {
            String token = jwtUtils.extractToken(request);
            if (token == null || !jwtUtils.checkValidToken(token)) {
                return ResponseEntity.status(401).body("Invalid or missing token");
            }

            String email = jwtUtils.getUsernameFromToken(token);
            User reporter = userService.findByUserEmail(email);
            if (reporter == null) {
                return ResponseEntity.status(404).body("User not found");
            }

            String listingId = payload.get("listingId");
            String reason = payload.get("reason");
            if (listingId == null || reason == null || reason.isBlank()) {
                return ResponseEntity.badRequest().body("Missing listingId or reason");
            }

            Listing listing = listingService.findById(listingId);
            if (listing == null) {
                return ResponseEntity.status(404).body("Listing not found");
            }

            Report saved = reportService.createReport(reporter, listing, reason);

            ReportResponse res = new ReportResponse(
                    saved.getReportId(),
                    reporter.getUserID(),
                    reporter.getUserName(),
                    reporter.getUserEmail(),

                    listing.getListingId(),
                    listing.getTitle(),
                    listing.getSeller() != null ? listing.getSeller().getUserName() : null,
                    (listing.getImages() != null && !listing.getImages().isEmpty())
                            ? listing.getImages().get(0).getUrl()
                            : null,

                    saved.getReason(),
                    saved.getStatus().name(),
                    saved.getCreateAt()
            );

            return ResponseEntity.status(201).body(res);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


    @GetMapping("/status/{status}")
    public ResponseEntity<?> getReportStatus(@PathVariable ReportedStatus status){
        List<Report> re = reportService.getReportsByStatus(status);
        return ResponseEntity.ok().body(re);
    }

}
