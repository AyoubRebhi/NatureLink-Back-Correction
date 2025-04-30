package com.example.naturelink.Service;

import com.example.naturelink.Entity.User;

import java.util.List;
<<<<<<< HEAD
import java.util.Map;
=======
>>>>>>> origin/ayoub
import java.util.Optional;

public interface IUserService {

    List<User> getAllUsers();

    Optional<User> getUserById(Integer id);

    User addUser(User user);

<<<<<<< HEAD
    User updateUser(Integer id, Map<String, Object> updates);

    void deleteUser(Integer id);
    User blockUser(Integer id);
    User unblockUser(Integer id);
    User updateProfilePic(Integer id, String fileName);
=======
    User updateUser(Integer id, User user);

    void deleteUser(Integer id);
>>>>>>> origin/ayoub
}
