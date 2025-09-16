package org.grp8.swp391.repository;

import org.grp8.swp391.entity.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;

public interface VehicleRepo extends JpaRepository<Vehicle,Long> {
    Page<Vehicle> findByVehicleId(Long id);
    Page<Vehicle> findByVehicleModel(String model);
    Page<Vehicle> getAll(Pageable pageable);
    Vehicle save(Vehicle vehicle);
    Page<Vehicle> findByVehicleBrand(String brand, Pageable pageable);
    Vehicle deleteByVehicleId(Long id);
    Vehicle updateByVehicleId(Long id, Vehicle vehicle);


}
