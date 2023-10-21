package com.assignment.springboot.assignment;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class S3BucketObjectsService {

    final String accessKey = "YOUR_ACCESS_KEY";
    final String secretKey = "YOUR_SECRET_KEY";

    @Autowired
    private S3ObjectRepository s3ObjectRepository; // Your repository for storing S3 object information

    public String getAndPersistS3BucketObjects(String bucketName) {
        // Generate a unique JobId
        String jobId = generateJobId();

        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        // Create an AmazonS3 client
        AmazonS3 s3Client = AmazonS3Client.builder()
                .withRegion(Regions.AP_SOUTH_1) // Replace with your desired region
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();

        // List objects in the specified S3 bucket
        ObjectListing objectListing = s3Client.listObjects(bucketName);
        List<S3ObjectSummary> objectSummaries = objectListing.getObjectSummaries();

        // Iterate through the object summaries and store them in the database
        for (S3ObjectSummary objectSummary : objectSummaries) {
            S3Object s3Object = new S3Object();
            s3Object.setBucketName(bucketName);
            s3Object.setObjectName(objectSummary.getKey());
            s3Object.setJobId(jobId); // Associate with the job
            // You can add more fields as needed

            // Save the S3Object entity to the database
            s3ObjectRepository.save(s3Object);
        }

        return jobId;
    }

    private String generateJobId() {
        // Implement job ID generation logic (e.g., using UUID)
        return UUID.randomUUID().toString();
    }

    public long getBucketObjectCount(String bucketName) {
        // Query the database to count objects in the specified S3 bucket
        long count = s3ObjectRepository.countByBucketName(bucketName);

        return count;
    }

    public List<String> getObjectsMatchingPattern(String bucketName, String pattern) {
        // Query the database to retrieve S3 object names that match the pattern
        List<String> matchingObjects = s3ObjectRepository.findObjectNamesByBucketNameAndObjectNameLike(bucketName, pattern);

        return matchingObjects;
    }
}
