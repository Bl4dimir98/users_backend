package com.springboot.backend.usersapp.users_backend.services;

import java.util.List;
import java.util.Optional;


import com.springboot.backend.usersapp.users_backend.entities.User;

public interface UserService {

    List<User> findAll();

    Optional<User> findById(Long id);

    User save(User user);

    void deleteById(Long id);

}
