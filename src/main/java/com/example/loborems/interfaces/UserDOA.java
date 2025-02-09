package com.example.loborems.interfaces;

import com.example.loborems.models.User;

import java.util.List;

public interface UserDOA {

    public void save(User user);
    public void update(User user);
    public void delete(User user);
    public List<User> getAll();
    public User findClient(int id);

}
