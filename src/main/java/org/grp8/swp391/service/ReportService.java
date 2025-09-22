package org.grp8.swp391.service;

import jakarta.persistence.EntityNotFoundException;
import org.grp8.swp391.entity.Report;
import org.grp8.swp391.entity.ReportedStatus;
import org.grp8.swp391.repository.ReportRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ReportService {
    @Autowired
    private ReportRepo reportRepo;

    public Report createReport(Report report){
        report.setStatus(ReportedStatus.PENDING);
        report.setCreateAt(new Date());

        return reportRepo.save(report);
    }

    public Report updateReportStatus(Long id,ReportedStatus report){
        Report up =  reportRepo.findById(id).orElse(null);
        if(up == null){
            throw new EntityNotFoundException("Report not found with id " + id);
        }
        up.setStatus(report);
        return reportRepo.save(up);

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








}
