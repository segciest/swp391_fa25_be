package org.grp8.swp391.repository;

import org.grp8.swp391.entity.Listing;
import org.grp8.swp391.entity.Report;
import org.grp8.swp391.entity.ReportedStatus;
import org.grp8.swp391.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ReportRepo extends JpaRepository<Report,Long> {
    List<Report> findByListing(Listing listing);

    List<Report> findByReporter(User reporter);

    List<Report> findByStatus(ReportedStatus status);

    Report findByListingAndReporter(Listing listing, User reporter);
    boolean existsByReporterAndListing(User reporter, Listing listing);

    void deleteByReportId(Long reportId);

    void deleteByListing_ListingId(String listingId);

    Report findByReportId(Long reportId);

    Long countByStatus(ReportedStatus status);

    @Query(value = """
    SELECT CONCAT(YEAR(r.Create_At), '-W', DATEPART(ISO_WEEK, r.Create_At)) AS week,
           COUNT(*) AS count
    FROM report r
    WHERE YEAR(r.Create_At) = YEAR(GETDATE())
    GROUP BY YEAR(r.Create_At), DATEPART(ISO_WEEK, r.Create_At)
    ORDER BY week
""", nativeQuery = true)
    List<Object[]> getReportWeeklyGrowth();

    @Query(value = """
    SELECT FORMAT(r.Create_At, 'yyyy-MM') AS month, COUNT(*) AS count
    FROM report r
    WHERE YEAR(r.Create_At) = YEAR(GETDATE())
    GROUP BY FORMAT(r.Create_At, 'yyyy-MM')
    ORDER BY month
""", nativeQuery = true)
    List<Object[]> getReportMonthlyGrowth();

    @Query(value = """
    SELECT DATEPART(QUARTER, r.Create_At) AS quarter, COUNT(*) AS count
    FROM report r
    WHERE YEAR(r.Create_At) = YEAR(GETDATE())
    GROUP BY DATEPART(QUARTER, r.Create_At)
    ORDER BY DATEPART(QUARTER, r.Create_At)
""", nativeQuery = true)
    List<Object[]> getReportQuarterlyGrowth();

    @Query(value = """
    SELECT YEAR(r.Create_At) AS year, COUNT(*) AS count
    FROM report r
    GROUP BY YEAR(r.Create_At)
    ORDER BY year
""", nativeQuery = true)
    List<Object[]> getReportYearlyGrowth();

}
