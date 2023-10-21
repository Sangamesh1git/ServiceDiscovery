package com.assignment.springboot.assignment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ServiceController {

    @Autowired
    private DiscoveryService discoveryService;

    @Autowired
    private JobStatusService jobStatusService;

    @Autowired
    private S3BucketObjectsService s3BucketObjectsService;

    @PostMapping("/discover-services")
    public ResponseEntity<String> discoverServices(@RequestBody List<String> services) {
        String jobId = discoveryService.discoverServices(services);
        return ResponseEntity.ok(jobId);
    }

    @GetMapping("/{jobId}")
    public String getJobStatus(@PathVariable String jobId) {
        // Call the service to retrieve the job status
        String jobStatus = jobStatusService.getJobResult(jobId);
        return jobStatus;
    }

    @GetMapping("/{service}")
    public List<String> getDiscoveryResult(@PathVariable String service) {
        List<String> result = discoveryService.getDiscoveryResult(service);
        return result;
    }

    @PostMapping("/{bucketName}")
    public String getAndPersistS3BucketObjects(@PathVariable String bucketName) {
        String jobId = s3BucketObjectsService.getAndPersistS3BucketObjects(bucketName);
        return jobId;
    }

    @GetMapping("/{bucketName}")
    public long getBucketObjectCount(@PathVariable String bucketName) {
        long count = s3BucketObjectsService.getBucketObjectCount(bucketName);
        return count;
    }

    @GetMapping("/{bucketName}")
    public List<String> getObjectsMatchingPattern(
            @PathVariable String bucketName,
            @RequestParam String pattern
    ) {
        List<String> matchingObjects = s3BucketObjectsService.getObjectsMatchingPattern(bucketName, pattern);
        return matchingObjects;
    }
}
