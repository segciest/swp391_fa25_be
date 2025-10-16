package org.grp8.swp391.controller;


import org.grp8.swp391.service.AdminDashService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashController {

    @Autowired
    private AdminDashService adminDashService;
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<?> getDashboard() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", adminDashService.getTotalUsers());
        stats.put("activeListings", adminDashService.getActiveListing());
        stats.put("totalRevenue", adminDashService.getTotalRevenue());
        return ResponseEntity.ok(stats);
    }
}
