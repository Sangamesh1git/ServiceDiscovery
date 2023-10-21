package com.assignment.springboot.assignment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobStatusService {

    @Autowired
    private DiscoveryResultRepository resultRepository; // Your repository for storing results

    public String getJobResult(String jobId) {
        // Retrieve the results associated with the provided jobId
        List<Discovery> results = resultRepository.findByJobId(jobId);

        if (results.isEmpty()) {
            return "Failed"; // Job with this JobId not found
        }

        // Check if all tasks have been completed successfully
        boolean allTasksSuccessful = results.stream().allMatch(result -> result.getStatus().equals("Success"));

        return allTasksSuccessful ? "Success" : "In Progress";
    }
}
