package org.grp8.swp391.controller;


import org.grp8.swp391.entity.Role;

import org.grp8.swp391.entity.RoleType;
import org.grp8.swp391.service.RoleService;
import org.grp8.swp391.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/role")
public class RoleController {
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id){
        try{
            Role role = roleService.findById(id);
            return ResponseEntity.ok().body(role);

        }catch(RuntimeException e){
        return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{name}")
    public ResponseEntity<?> findByName(@PathVariable String name){
        try{
            Role role = roleService.findByName(name);
            return ResponseEntity.ok().body(role);
        }catch(RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping
    public ResponseEntity<?> findAll(){
        List<Role> role = roleService.getAll();
        return ResponseEntity.ok().body(role);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id){
        roleService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody Role role){
        Role savedRole = roleService.save(role);
        return ResponseEntity.ok().body(savedRole);
    }

    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody Role role){
        Role savedRole = roleService.save(role);
        return ResponseEntity.ok().body(savedRole);
    }

}
