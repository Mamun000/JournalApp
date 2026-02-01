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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/journal")
public class JournalEntryControllerV2 {

    @Autowired
    private JournalEntryService journalEntryService;
    @Autowired
    private UserService userService;

    @GetMapping("/entry")
    public String entryPoint(){
        return journalEntryService.entryPoint();
    }


   @GetMapping("/{userName}")
   public ResponseEntity<?>getAll(@PathVariable String userName){
       User user = userService.findByUserName(userName);
       List<JournalEntry> journalEntryList = user.getJournalEntries();
        if(journalEntryList.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

       return new ResponseEntity<>(journalEntryList, HttpStatus.OK);
   }

   @PostMapping("{userName}")
    public ResponseEntity<JournalEntry> createEntry(@RequestBody JournalEntry myEntry, @PathVariable String userName){
        try {
            myEntry.setDate(LocalDateTime.now());
            journalEntryService.saveEntry( myEntry,userName);
            return new ResponseEntity<>(myEntry,HttpStatus.CREATED);

        }catch(Exception e){
            return new ResponseEntity<>(myEntry,HttpStatus.BAD_REQUEST);

        }
   }

   @GetMapping("/id/{myId}")
    public ResponseEntity<JournalEntry> getJournalEntryById(@PathVariable ObjectId myId){
        Optional<JournalEntry> journalEntryOptional = journalEntryService.findById(myId);
        if(journalEntryOptional.isPresent()){
            return ResponseEntity.ok(journalEntryOptional.get());
        }
        return ResponseEntity.notFound().build();
   }

    @DeleteMapping("id/{userName}/{myId}")
    public ResponseEntity<?> deleteJournalEntryById(@PathVariable ObjectId myId,@PathVariable String userName){


         journalEntryService.deleteById(myId,userName);
         return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/id/{userName}/{id}")
    public  ResponseEntity<?> updateJournalEntryById(
            @PathVariable ObjectId id,
            @PathVariable String userName,
            @RequestBody JournalEntry newEntry){
        JournalEntry old = journalEntryService.findById(id).orElse(null);
        if(old != null){
            old.setTitle(newEntry.getTitle()!=null && !newEntry.getTitle().equals("")?newEntry.getTitle():old.getTitle());
            old.setContent(newEntry.getContent()!=null && !newEntry.getContent().equals("")?newEntry.getContent():old.getContent());
            journalEntryService.saveEntry(old);
            return new ResponseEntity<>(old,HttpStatus.OK);
        }

        return ResponseEntity.notFound().build();


    }



}
