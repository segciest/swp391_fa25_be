package org.grp8.swp391.controller;

import org.grp8.swp391.entity.Report;
import org.grp8.swp391.entity.ReportedStatus;
import org.grp8.swp391.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/report")
public class ReportController {
    @Autowired
    private ReportService reportService;


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
    public ResponseEntity<?> findAllReports(){
        List<Report> report = reportService.getAllReports();
        return ResponseEntity.ok().body(report);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReportById(@PathVariable Long id){
        Report re = reportService.findByReportId(id);
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
    public ResponseEntity<?> updateReportStatus(@PathVariable Long reportId, ReportedStatus status){
        try {
            Report report = reportService.updateReportStatus(reportId, status);
            return ResponseEntity.ok().body(report);
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createReport(@RequestBody Report report){
        Report re = reportService.createReport(report);
        return ResponseEntity.ok().body(re);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getReportStatus(@PathVariable ReportedStatus status){
        List<Report> re = reportService.getReportsByStatus(status);
        return ResponseEntity.ok().body(re);
    }

}
