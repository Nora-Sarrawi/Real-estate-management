package com.example.loborems.interfaces;

import com.example.loborems.models.Permission;

import java.util.List;

public interface PermissionDOA {

    public void save(Permission permission);
    public void update(Permission permission);
    public void delete(Permission permission);
    public List<Permission> getAll();
    public Permission findPermission(int id);
    public List<Permission> findByRoleId(int roleId);
}
