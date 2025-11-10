package org.grp8.swp391.repository;

import org.grp8.swp391.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListingRepo extends JpaRepository<Listing, String> {
    Listing findByListingId(String listingId);

    @Query("SELECT COUNT(l) FROM Listing l WHERE l.seller.userID = :userID")
    long countListingsByUser(@Param("userID") String userID);

    Page<Listing> findBySeller_UserID(String sellerId, Pageable pageable);
    List<Listing> findBySeller_UserID(String sellerId);

    Page<Listing> findByStatus(ListingStatus status, Pageable pageable);

    Page<Listing> findByCategory_CategoryId(Long categoryId, Pageable pageable);
    Page<Listing> findByYear(Integer year, Pageable pageable);

    Page<Listing> findByModelContainingIgnoreCase(String model, Pageable pageable);
    Page<Listing> findByColorIgnoreCase(String color, Pageable pageable);
    Page<Listing> findByBrandIgnoreCase(String brand, Pageable pageable);
    Page<Listing> findByVehicleTypeIgnoreCase(String type, Pageable pageable);

    Page<Listing> findByYearBetween(int startYear, int endYear, Pageable pageable);
    Page<Listing> findByPriceBetween(Double min, Double max, Pageable pageable);
    Long countByStatus(ListingStatus status);
    Page<Listing> findByCityIgnoreCase(String city, Pageable pageable);
Page<Listing> findByTitleContaining(@Param("title") String title, Pageable pageable);


    @Query("""
    SELECT l FROM Listing l
    JOIN l.seller u
    JOIN u.subid s
    WHERE l.status = 'ACTIVE'
    ORDER BY s.priorityLevel DESC, l.createdAt DESC
""")
    Page<Listing> findAllListingByPriorityAndDate(Pageable pageable);

    List<Listing> findByStatus(ListingStatus status);


    @Query("""
    SELECT l 
    FROM Listing l 
    WHERE l.status = 'PENDING' 
    ORDER BY l.createdAt DESC
""")
    Page<Listing> findPendingListingsOrderByCreatedAtDesc(Pageable pageable);


    List<Listing> findBySeller_UserIDAndStatus(String sellerId, ListingStatus status);

    @Query(value = """
    SELECT CONCAT(YEAR(l.Create_At), '-W', DATEPART(ISO_WEEK, l.Create_At)) AS week,
           COUNT(*) AS count
    FROM listing l
    WHERE YEAR(l.Create_At) = YEAR(GETDATE())
    GROUP BY YEAR(l.Create_At), DATEPART(ISO_WEEK, l.Create_At)
    ORDER BY week
""", nativeQuery = true)
    List<Object[]> getListingWeeklyGrowth();

    @Query(value = """
    SELECT FORMAT(l.Create_At, 'yyyy-MM') AS month, COUNT(*) AS count
    FROM listing l
    WHERE YEAR(l.Create_At) = YEAR(GETDATE())
    GROUP BY FORMAT(l.Create_At, 'yyyy-MM')
    ORDER BY month
""", nativeQuery = true)
    List<Object[]> getListingMonthlyGrowth();

    @Query(value = """
    SELECT YEAR(l.Create_At) AS year, COUNT(*) AS count
    FROM listing l
    GROUP BY YEAR(l.Create_At)
    ORDER BY year
""", nativeQuery = true)
    List<Object[]> getListingYearlyGrowth();

    @Query(value = """
    SELECT DATEPART(QUARTER, l.Create_At) AS quarter, COUNT(*) AS count
    FROM listing l
    WHERE YEAR(l.Create_At) = YEAR(GETDATE())
    GROUP BY DATEPART(QUARTER, l.Create_At)
    ORDER BY DATEPART(QUARTER, l.Create_At)
""", nativeQuery = true)
    List<Object[]> getListingQuarterlyGrowth();






}
