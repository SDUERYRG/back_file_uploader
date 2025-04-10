package com.example.demo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Temp {
    @PostMapping("/helloworld")
    // @PostMapping("/helloworld")
    public boolean helloworld() {
        System.out.println("Hello World");
        return true;
    }
}