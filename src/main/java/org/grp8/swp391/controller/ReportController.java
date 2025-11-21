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
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/report")
public class ReportController {

    @Autowired private ReportService reportService;
    @Autowired private JwtUtils jwtUtils;
    @Autowired private ListingService listingService;
    @Autowired private UserService userService;

    @GetMapping("/id/{id}")
    public ResponseEntity<?> findReportById(@PathVariable Long id) {
        try {
            Report report = reportService.findByReportId(id);
            return ResponseEntity.ok(reportService.toReportResponse(report));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MODERATOR')")
    public ResponseEntity<?> findAllReports() {
        return ResponseEntity.ok(reportService.toReportResponseList(reportService.getAllReports()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReportById(@PathVariable Long id) {
        Report report = reportService.findByReportId(id);
        if (report == null) {
            return ResponseEntity.notFound().build();
        }
        reportService.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Report deleted successfully", "reportId", id));
    }



    @GetMapping("/reject")
    public ResponseEntity<?> findByRejected() {
        return ResponseEntity.ok(reportService.toReportResponseList(reportService.findByRejectStatus()));
    }

    @GetMapping("/resolve")
    public ResponseEntity<?> findByResolveStatus() {
        return ResponseEntity.ok(reportService.toReportResponseList(reportService.findByResolveStatus()));
    }

    @PutMapping("/status/reject/{id}")
    public ResponseEntity<?> updateRejectStatus(@PathVariable Long id) {
        try {
            Report report = reportService.updateReportStatus(id, ReportedStatus.REJECTED);
            return ResponseEntity.ok(reportService.toReportResponse(report));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/status/resolve/{id}")
    public ResponseEntity<?> updateResolveStatus(@PathVariable Long id) {
        try {
            Report report = reportService.updateReportStatus(id, ReportedStatus.RESOLVED);
            return ResponseEntity.ok(reportService.toReportResponse(report));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/status/{reportId}")
    public ResponseEntity<?> updateReportStatus(@PathVariable Long reportId, @RequestParam ReportedStatus status) {
        try {
            Report report = reportService.updateReportStatus(reportId, status);
            return ResponseEntity.ok(reportService.toReportResponse(report));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/update/{reportId}")
    public ResponseEntity<?> updateReport(@PathVariable Long reportId, @RequestBody Report report) {
        Report updated = reportService.updateReport(reportId, report);
        return ResponseEntity.ok(reportService.toReportResponse(updated));
    }
    @PreAuthorize("hasAuthority('USER') or hasAuthority('MODERATOR')")

    @PostMapping(value = "/create", consumes = "multipart/form-data")
    public ResponseEntity<?> createReport(
            @RequestParam("listingId") String listingId,
            @RequestParam("reason") String reason,
            @RequestPart(value = "images", required = false) MultipartFile[] file,
            HttpServletRequest request) {

        try {
            String token = jwtUtils.extractToken(request);
            if (token == null || !jwtUtils.checkValidToken(token))
                return ResponseEntity.status(401).body(Map.of("error", "Invalid or missing token"));

            String email = jwtUtils.getUsernameFromToken(token);
            User reporter = userService.findByUserEmail(email);
            if (reporter == null)
                return ResponseEntity.status(404).body(Map.of("error", "User not found"));

            Listing listing = listingService.findById(listingId);
            if (listing == null)
                return ResponseEntity.status(404).body(Map.of("error", "Listing not found"));

            Report saved = reportService.createReport(reporter, listing, reason, file);
            return ResponseEntity.status(201).body(reportService.toReportResponse(saved));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingReport(){
        try {
            List<Report> reports = reportService.findByPendingStatus();
            return ResponseEntity.ok(reportService.toReportResponseList(reports));
        }catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


    @GetMapping("/status/{status}")
    public ResponseEntity<?> getReportStatus(@PathVariable ReportedStatus status) {
        return ResponseEntity.ok(reportService.toReportResponseList(reportService.getReportsByStatus(status)));
    }
    @PreAuthorize("hasAuthority('ADMIN')")

    @PutMapping("/handle/{id}")
    public ResponseEntity<?> handleReport(@PathVariable Long id,@RequestParam String actionType) {
        try{
            ReportResponse report = reportService.handleReportAction(id, actionType);
            return ResponseEntity.ok(Map.of(
                    "message", "Report handled successfully",
                    "action", actionType,
                    "report", report
            ));
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));

        }
    }
}
