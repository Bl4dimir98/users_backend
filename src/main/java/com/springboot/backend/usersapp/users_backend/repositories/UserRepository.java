package com.springboot.backend.usersapp.users_backend.repositories;

import org.springframework.data.repository.CrudRepository;

import com.springboot.backend.usersapp.users_backend.entities.User;

public interface UserRepository extends CrudRepository<User, Long> {

}
