package com.aie.journalApp.controller;

//controller ----> service -------> repository

import com.aie.journalApp.entity.JournalEntry;
import com.aie.journalApp.entity.User;
import com.aie.journalApp.repository.UserRepository;
import com.aie.journalApp.service.JournalEntryService;
import com.aie.journalApp.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<User> getAllUsers(){
        return userService.getAll();
    }


    @PutMapping()
    public ResponseEntity<User> updateUser(@RequestBody User user){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
       User userInDb= userService.findByUserName(userName);
       if(userInDb!=null){
           userInDb.setUserName(user.getUserName());
           userInDb.setPassword(user.getPassword());
           userService.saveEntry(userInDb);
       }
       return ResponseEntity.ok(userInDb);

    }
    @DeleteMapping
    public ResponseEntity<?> deleteUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userRepository.deleteByUserName(authentication.getName());
        return ResponseEntity.ok().build();
    }


}
