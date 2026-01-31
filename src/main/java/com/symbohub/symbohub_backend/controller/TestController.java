package com.symbohub.symbohub_backend.controller;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "✅ Backend connected successfully at " + java.time.LocalDateTime.now();
    }

  //  @GetMapping("/colleges/approved")
   // public List<Map<String, String>> getApprovedColleges() {
     //   return List.of(
       //         Map.of("id", "1", "name", "Test College 1", "status", "approved"),
         //       Map.of("id", "2", "name", "Test College 2", "status", "approved")
        //);
    //}
}