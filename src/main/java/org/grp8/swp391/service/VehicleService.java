package org.grp8.swp391.service;


import org.grp8.swp391.entity.Vehicle;
import org.grp8.swp391.repository.VehicleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.util.List;


@Service
public class VehicleService {
    @Autowired
    private VehicleRepo vehicleRepo;

    public Page<Vehicle> getAll(Pageable pageable){
        return vehicleRepo.findAll(pageable);
    }

    public void delete(Long id){
        vehicleRepo.deleteById(id);
    }

    public Vehicle findById(Long id){
        return vehicleRepo.findByVehicleId(id);
    }

    public List<Vehicle> findByVehicleModel(String model){
        return vehicleRepo.findByVehicleModel(model);
    }

    public List<Vehicle> findByVehicleBrand(String brand){
        return vehicleRepo.findByVehicleBrand(brand);
    }
}
