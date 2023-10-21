package com.assignment.springboot.assignment;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DiscoveryService {
    @Autowired
    private DiscoveryResultRepository resultRepository;

    @Autowired
    private EC2InstanceRepository ec2InstanceRepository;

    @Autowired
    private S3BucketRepository s3BucketRepository;

    final String accessKey = "YOUR_ACCESS_KEY";
    final String secretKey = "YOUR_SECRET_KEY";
    public Boolean performEC2DiscoveryLogic(String jobId) {
        // Replace these values with your AWS credentials

            BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);

            // Create an AmazonEC2 client
            AmazonEC2 ec2Client = AmazonEC2Client.builder()
                    .withRegion(Regions.AF_SOUTH_1) // Replace with your desired AWS region
                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                    .build();

            // Describe EC2 ins`tances
            DescribeInstancesRequest request = new DescribeInstancesRequest();
            DescribeInstancesResult response = ec2Client.describeInstances(request);

            List<Instance> instances = response.getReservations()
                    .stream()
                    .flatMap(reservation -> reservation.getInstances().stream())
                    .toList();
        // Assuming 'instances' is a list of 'Instance' objects
        List<EC2Instance> ec2Instances = instances.stream()
                .map(instance -> {
                    EC2Instance ec2Instance = new EC2Instance();
                    ec2Instance.setInstanceId(instance.getInstanceId());
                    // Set other fields of 'EC2Instance' if needed
                    return ec2Instance;
                })
                .toList();
        ec2InstanceRepository.saveAll(ec2Instances);
            return !instances.isEmpty();
        }

    public boolean performS3DiscoveryLogic() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        List<S3bucket> s3buckets = new ArrayList<>();
        // Create an AmazonS3 client
        AmazonS3 s3Client = AmazonS3Client.builder()
                .withRegion(Regions.AP_SOUTH_1) // Replace with your desired region
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();

        // List all S3 buckets in the region
        List<Bucket> buckets = s3Client.listBuckets();

        // Process the discovered buckets
        for (Bucket bucket : buckets) {
            S3bucket s3bucket = new S3bucket();
            // Retrieve bucket name and perform further processing
            String bucketName = bucket.getName();

            // You can also list objects within each bucket, if needed
            ObjectListing objectListing = s3Client.listObjects(bucketName);
            List<S3ObjectSummary> objectSummaries = objectListing.getObjectSummaries();
            s3bucket.setBucketName(bucketName);
            s3bucket.setObjectsCount(objectSummaries.size());
            s3buckets.add(s3bucket);
        }
        s3BucketRepository.saveAll(s3buckets);
        // If any buckets or objects are discovered, consider the operation successful
        return !buckets.isEmpty();
    }

    @Async
    public void discoverS3Buckets(String jobId) {
        // Asynchronous S3 discovery logic
        // Store the results in the database
        Discovery discovery = new Discovery();
        String status = performEC2DiscoveryLogic(jobId) ? "Success" : "Failed";
        discovery.setServiceType("S3");
        discovery.setStatus(status);
        discovery.setJobId(jobId);
        // Populate other fields
        resultRepository.save(discovery);
    }
    @Async
    public void discoverEC2Instances(String jobId) {
        // Asynchronous EC2 discovery logic
        // Set the status based on the result
        Discovery discovery = new Discovery();
        String status = performEC2DiscoveryLogic(jobId) ? "Success" : "Failed";
        discovery.setServiceType("EC2");
        discovery.setStatus(status);
        discovery.setJobId(jobId);
        resultRepository.save(discovery);
    }


    public String discoverServices(List<String> services) {
        // Generate a unique JobId (e.g., using UUID.randomUUID())
        String jobId = generateJobId();

        // Initiate asynchronous service discovery tasks
        for (String service : services) {
            if ("EC2".equalsIgnoreCase(service)) {
                discoverEC2Instances(jobId);
            } else if ("S3".equalsIgnoreCase(service)) {
                discoverS3Buckets(jobId);
            }
        }

        return jobId;
    }

    private String generateJobId() {
        // Implement job ID generation logic (e.g., using UUID)
        return UUID.randomUUID().toString();
    }
}
