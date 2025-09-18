package org.grp8.swp391.repository;

import org.grp8.swp391.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubRepo extends JpaRepository<Subscription, Long> {
    Subscription findBySubName(String subName);
    Subscription findBySubId(Long subId);
    Subscription deleteBySubId(Long subId);
    Subscription findByStatus(String status);


}
