package com.redsun.fetch_data.controller;

import com.redsun.fetch_data.model.User;
import com.redsun.fetch_data.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/facet")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/get")
    public List<String> getQueryData(@RequestParam String name, @RequestParam String classCode) {
        return userService.getQueryData(name, classCode);
    }

    @PostMapping("/list")
    public List<String> listBase36Ids(@RequestBody List<User> facetGroups) {
        return userService.listQueryData(facetGroups);
    }

    // GET endpoint
    @GetMapping("/search")
    public List<String> searchQueryData(@RequestParam("name") String name) {
        return userService.searchQueryData(name);
    }
}