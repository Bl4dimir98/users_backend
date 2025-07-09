package com.springboot.backend.usersapp.users_backend.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.backend.usersapp.users_backend.entities.Role;
import com.springboot.backend.usersapp.users_backend.entities.User;
import com.springboot.backend.usersapp.users_backend.models.IUser;
import com.springboot.backend.usersapp.users_backend.models.UserRequest;
import com.springboot.backend.usersapp.users_backend.repositories.RoleRepository;
import com.springboot.backend.usersapp.users_backend.repositories.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return ((List<User>) this.userRepository.findAll()).stream().map(user -> {

            boolean admin = user.getRoles().stream().anyMatch(role -> "ROLE_ADMIN".equals(role.getName()));
            user.setAdmin(admin);
            return user;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> findAll(Pageable pageable) {
        return this.userRepository.findAll(pageable).map(user -> {
            boolean admin = user.getRoles().stream().anyMatch(role -> "ROLE_ADMIN".equals(role.getName()));
            user.setAdmin(admin);
            return user;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional
    public User save(User user) {
        user.setRoles(getRoles(user));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public Optional<User> update(UserRequest user, Long id) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            User userDb = userOptional.get();
            userDb.setEmail(user.getEmail());
            userDb.setName(user.getName());
            userDb.setLastname(user.getLastname());
            userDb.setUsername(user.getUsername());
            userDb.setRoles(getRoles(user));
            return Optional.of(userRepository.save(userDb));
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    private List<Role> getRoles(IUser user) {
        List<Role> roles = new ArrayList<>();
        Optional<Role> optionalRoleUser = roleRepository.findByName("ROLE_USER");
        optionalRoleUser.ifPresent(role -> roles.add(role));

        if (user.isAdmin()) {
            Optional<Role> optionalRoleAdmin = roleRepository.findByName("ROLE_ADMIN");
            optionalRoleAdmin.ifPresent(role -> roles.add(role));
        }
        return roles;
    }

}
