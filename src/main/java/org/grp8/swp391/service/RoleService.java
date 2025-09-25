package org.grp8.swp391.service;

import org.grp8.swp391.entity.Role;
import org.grp8.swp391.entity.RoleType;
import org.grp8.swp391.repository.RoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {
    @Autowired
    private RoleRepo roleRepo;

    public Role findByName(String name){
        return roleRepo.findByRoleName(name);
    }

    public Role findById(Long id){
        return roleRepo.findById(id).orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
    }

    public List<Role> getAll(){
        return roleRepo.findAll();
    }

    public Role save(Role role){
        return roleRepo.save(role);
    }

    public void delete(Role role){
        roleRepo.delete(role);
    }

    public void deleteById(Long id){
        roleRepo.deleteById(id);
    }
}
