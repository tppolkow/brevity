package com.fydp.backend.service;

import com.fydp.backend.model.User;

import java.util.Optional;

public interface IUserService {

    Optional<User> findById(String id);
    void saveUser(User user);
}
