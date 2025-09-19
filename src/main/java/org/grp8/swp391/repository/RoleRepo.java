package org.grp8.swp391.repository;

import org.grp8.swp391.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepo extends JpaRepository<Role, Long> {
    Role findByRoleName(String roleName);
    void deleteByRoleName(String roleName);
}
