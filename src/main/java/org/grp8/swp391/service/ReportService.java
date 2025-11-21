package org.grp8.swp391.service;

import jakarta.persistence.EntityNotFoundException;
import org.grp8.swp391.dto.response.ReportResponse;
import org.grp8.swp391.entity.*;
import org.grp8.swp391.repository.ReportRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Service
public class ReportService {
    @Autowired
    private ReportRepo reportRepo;
    @Autowired
    private ListingService listingService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private UserService userService;

    public Report createReport(User reporter, Listing listing, String reason, MultipartFile file) {
        if (reportRepo.existsByReporterAndListing(reporter, listing)) {
            throw new RuntimeException("You have already reported this listing.");
        }

        Report report = new Report();
        report.setReporter(reporter);
        report.setListing(listing);
        report.setReason(reason);
        report.setStatus(ReportedStatus.PENDING);
        report.setCreateAt(new Date());
        String url = cloudinaryService.uploadFile(file);
        report.setImgUrl(url);

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

    public List<Report> findByRejectStatus() {
        return reportRepo.findByStatus(ReportedStatus.REJECTED);
    }

    public List<Report> findByPendingStatus() {
        return reportRepo.findByStatus(ReportedStatus.PENDING);
    }

    public List<Report> findByResolveStatus() {
        return reportRepo.findByStatus(ReportedStatus.RESOLVED);
    }



    public ReportResponse handleReportAction(Long reportId, String actionType) {
        Report report = reportRepo.findByReportId(reportId);
        if (report == null) {
            throw new EntityNotFoundException("Report not found with id " + reportId);
        }

        Listing listing = report.getListing();
        if (listing == null) {
            throw new EntityNotFoundException("Listing not found for report " + reportId);
        }

        User seller = listing.getSeller();
        if (seller == null) {
            throw new EntityNotFoundException("Seller not found for listing " + listing.getListingId());
        }

        if (actionType == null || actionType.isBlank()) {
            throw new RuntimeException("Action type is required");
        }

        String action = actionType.trim();

        if (action.equals("bannedlisting")) {
            listing.setStatus(ListingStatus.BANNED);
            listingService.save(listing);
            report.setStatus(ReportedStatus.RESOLVED);

        } else if (action.equals("banneduser")) {
            seller.setUserStatus(UserStatus.BANNED);
            userService.save(seller);
            report.setStatus(ReportedStatus.RESOLVED);


        } else {
            throw new RuntimeException("Invalid action type. Use 'BannedListing', 'BannedUser', or 'ResolveOnly'.");
        }

        report.setCreateAt(new Date());
        reportRepo.save(report);

        return toReportResponse(report);
    }



    public ReportResponse toReportResponse(Report report) {
        if (report == null) return null;

        Listing listing = report.getListing();
        User reporter = report.getReporter();

        String thumbnail = (listing != null && listing.getImages() != null && !listing.getImages().isEmpty())
                ? listing.getImages().get(0).getUrl()
                : null;

        return new ReportResponse(
                report.getReportId(),
                reporter != null ? reporter.getUserID() : null,
                reporter != null ? reporter.getUserName() : null,
                reporter != null ? reporter.getUserEmail() : null,

                listing != null ? listing.getListingId() : null,
                listing != null ? listing.getTitle() : null,
                (listing != null && listing.getSeller() != null) ? listing.getSeller().getUserName() : null,
                thumbnail,

                report.getReason(),
                report.getStatus() != null ? report.getStatus().name() : null,
                report.getImgUrl(),
                report.getCreateAt()
        );
    }

    public List<ReportResponse> toReportResponseList(List<Report> reports) {
        return reports.stream()
                .map(this::toReportResponse)
                .toList();
    }











}
