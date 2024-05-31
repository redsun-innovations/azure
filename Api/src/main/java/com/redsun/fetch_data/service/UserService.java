package com.redsun.fetch_data.service;

import com.redsun.fetch_data.model.User;
import com.redsun.fetch_data.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<String> getQueryData(String name, String classCode) {
        return userRepository.getQueryData(name, classCode);
    }
    public List<String> listQueryData(List<User> users) {
        return userRepository.listQueryData(users);
    }

    public List<String> searchQueryData(String name) {
        return userRepository.searchQueryData(name);
    }
}