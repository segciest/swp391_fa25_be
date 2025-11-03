package org.grp8.swp391.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.grp8.swp391.config.JwtUtils;
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

            var listing = listingService.findById(listingId);
            if (listing == null) {
                return ResponseEntity.status(404).body("Listing not found");
            }

            Report report = new Report();
            report.setReporter(reporter);
            report.setListing(listing);
            report.setReason(reason);
            report.setStatus(ReportedStatus.PENDING);
            report.setCreateAt(new Date());

            Report saved = reportService.createReport(reporter, listing, reason);

            return ResponseEntity.status(201).body(saved);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/status/{status}")
    public ResponseEntity<?> getReportStatus(@PathVariable ReportedStatus status){
        List<Report> re = reportService.getReportsByStatus(status);
        return ResponseEntity.ok().body(re);
    }

}
