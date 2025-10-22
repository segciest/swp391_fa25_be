package org.grp8.swp391.service;

import jakarta.persistence.EntityNotFoundException;
import org.grp8.swp391.entity.Listing;
import org.grp8.swp391.entity.Report;
import org.grp8.swp391.entity.ReportedStatus;
import org.grp8.swp391.entity.User;
import org.grp8.swp391.repository.ReportRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ReportService {
    @Autowired
    private ReportRepo reportRepo;

    public Report createReport(User reporter, Listing listing, String reason) {
        if (reportRepo.existsByReporterAndListing(reporter, listing)) {
            throw new RuntimeException("You have already reported this listing.");
        }

        Report report = new Report();
        report.setReporter(reporter);
        report.setListing(listing);
        report.setReason(reason);
        report.setStatus(ReportedStatus.PENDING);
        report.setCreateAt(new Date());

        return reportRepo.save(report);
    }




    public List<Report> getAllReports() {
        return reportRepo.findAll();
    }

    public List<Report> getReportsByStatus(ReportedStatus status) {
        return reportRepo.findByStatus(status);
    }

    public Report updateReason(Long id,Report report){
        Report check = reportRepo.findById(id).orElse(null);
        if(check == null){
            throw new EntityNotFoundException("Report not found with id " + id);
        }
        check.setReason(report.getReason());
        return reportRepo.save(check);
    }

    public void deleteById(Long id){
        Report report = reportRepo.findById(id).orElse(null);
        if(report == null){
            throw new EntityNotFoundException("Report not found with id " + id);
        }
        reportRepo.delete(report);
    }

    public Report findByReportId(Long id){
        return reportRepo.findByReportId(id);
    }


    public Report updateReport(Long id, Report report) {
        Report check = reportRepo.findByReportId(id);
        if (check == null) {
            throw new EntityNotFoundException("Report not found with id " + id);
        }

        if (report.getReason() != null) {
            check.setReason(report.getReason());
        }

        return reportRepo.save(check);
    }

    public Report updateReportStatus(Long id, ReportedStatus status) {
        Report check = reportRepo.findByReportId(id);
        if (check == null) {
            throw new EntityNotFoundException("Report not found with id " + id);
        }
        check.setStatus(status);
        return reportRepo.save(check);
    }








}
