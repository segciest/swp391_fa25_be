package org.grp8.swp391.repository;

import org.grp8.swp391.entity.Listing;
import org.grp8.swp391.entity.Report;
import org.grp8.swp391.entity.ReportedStatus;
import org.grp8.swp391.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ReportRepo extends JpaRepository<Report,Long> {
    List<Report> findByListing(Listing listing);

    List<Report> findByReporter(User reporter);

    List<Report> findByStatus(ReportedStatus status);

    Report findByListingAndReporter(Listing listing, User reporter);


    void deleteByReportId(Long reportId);

    Report findByReportId(Long reportId);



}
