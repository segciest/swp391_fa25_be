package org.grp8.swp391.repository;

import org.grp8.swp391.entity.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface VehicleRepo extends JpaRepository<Vehicle,Long> {
    Vehicle findByVehicleId(Long id);
    List<Vehicle> findByVehicleModel(String model);
    Page<Vehicle> findAll(Pageable pageable);
    Vehicle save(Vehicle vehicle);
    List<Vehicle> findByVehicleBrand(String brand);
    void deleteByVehicleId(Long id);



}
