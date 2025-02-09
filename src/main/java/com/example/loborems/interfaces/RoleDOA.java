package com.example.loborems.interfaces;

import com.example.loborems.models.Role;

import java.util.List;

public interface RoleDOA {

    public void save(Role role);       // Saves a role
    public void update(Role role);    // Updates a role
    public void delete(Role role);    // Deletes a role
    public List<Role> getAll();       // Retrieves all roles
    public Role findRole(int id);     // Finds a role by ID
    public Role findByName(String name); // Finds a role by its name
}