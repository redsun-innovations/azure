package com.redsun.fetch_data.service;

import com.redsun.fetch_data.model.User;
import com.redsun.fetch_data.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String fetchAllData(String path, String displayName) {
        return userRepository.fetchAllData(path, displayName);
    }
}