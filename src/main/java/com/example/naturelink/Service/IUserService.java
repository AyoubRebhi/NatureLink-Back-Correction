package com.example.naturelink.Service;

import com.example.naturelink.Entity.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {

    List<User> getAllUsers();

    Optional<User> getUserById(Long id);

    User addUser(User user);

    User updateUser(Long id, User user);

    void deleteUser(Long id);
}
