package com.assignment.springboot.assignment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ServiceController {

    @Autowired
    private DiscoveryService discoveryService;

    @PostMapping("/discover-services")
    public ResponseEntity<String> discoverServices(@RequestBody List<String> services) {
        String jobId = discoveryService.discoverServices(services);
        return ResponseEntity.ok(jobId);
    }
}
