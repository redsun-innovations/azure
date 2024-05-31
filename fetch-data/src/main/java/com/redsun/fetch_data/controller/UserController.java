package com.redsun.fetch_data.controller;

import com.redsun.fetch_data.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/fetchData")
    public String fetchData(@RequestParam String path, @RequestParam String displayName) {
        return userService.fetchAllData(path, displayName);
    }
}