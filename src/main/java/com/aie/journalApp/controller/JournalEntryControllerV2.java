package com.aie.journalApp.controller;

//controller ----> service -------> repository
import com.aie.journalApp.entity.JournalEntry;
import com.aie.journalApp.entity.User;
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
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/journal")
public class JournalEntryControllerV2 {

    @Autowired
    private JournalEntryService journalEntryService;
    @Autowired
    private UserService userService;




   @GetMapping
   public ResponseEntity<?>getAll(){
       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
       String userName = authentication.getName();
       User user = userService.findByUserName(userName);
       List<JournalEntry> journalEntryList = user.getJournalEntries();
        if(journalEntryList.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

       return new ResponseEntity<>(journalEntryList, HttpStatus.OK);
   }

   @PostMapping
    public ResponseEntity<JournalEntry> createEntry(@RequestBody JournalEntry myEntry){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            myEntry.setDate(LocalDateTime.now());
            journalEntryService.saveEntry( myEntry,userName);
            return new ResponseEntity<>(myEntry,HttpStatus.CREATED);

        }catch(Exception e){
            return new ResponseEntity<>(myEntry,HttpStatus.BAD_REQUEST);

        }
   }

   @GetMapping("/id/{myId}")
    public ResponseEntity<JournalEntry> getJournalEntryById(@PathVariable ObjectId myId){
       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
       String userName = authentication.getName();
       User user = userService.findByUserName(userName);
       List<JournalEntry>collect=user.getJournalEntries().stream().filter(x->x.getId().equals(myId)).collect(Collectors.toList());
       if(!collect.isEmpty()){
           Optional<JournalEntry> journalEntryOptional = journalEntryService.findById(myId);
           if(journalEntryOptional.isPresent()){
               return ResponseEntity.ok(journalEntryOptional.get());
           }
       }
        return ResponseEntity.notFound().build();
   }

    @DeleteMapping("/id/{myId}")
    public ResponseEntity<?> deleteJournalEntryById(@PathVariable ObjectId myId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
         boolean removed=journalEntryService.deleteById(myId,userName);
         if(removed){
             return new ResponseEntity<>(HttpStatus.NO_CONTENT);
         }else {
             return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
         }
    }

    @PutMapping("/id/{id}")
    public  ResponseEntity<?> updateJournalEntryById(@PathVariable ObjectId id,@RequestBody JournalEntry newEntry){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);
        List<JournalEntry>collect=user.getJournalEntries().stream().filter(x->x.getId().equals(id)).collect(Collectors.toList());
        if(!collect.isEmpty()){
            Optional<JournalEntry> journalEntry = journalEntryService.findById(id);
            if(journalEntry != null){
                JournalEntry old=journalEntry.get();
                old.setTitle(newEntry.getTitle()!=null && !newEntry.getTitle().equals("")?newEntry.getTitle():old.getTitle());
                old.setContent(newEntry.getContent()!=null && !newEntry.getContent().equals("")?newEntry.getContent():old.getContent());
                journalEntryService.saveEntry(old);
                return new ResponseEntity<>(old,HttpStatus.OK);
            }
        }


        return ResponseEntity.notFound().build();

    }

}
