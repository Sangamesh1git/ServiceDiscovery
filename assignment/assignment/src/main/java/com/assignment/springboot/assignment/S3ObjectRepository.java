package com.assignment.springboot.assignment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface S3ObjectRepository extends JpaRepository<S3Object, Long> {

    long countByBucketName(String bucketName);

    List<String> findObjectNamesByBucketNameAndObjectNameLike(String bucketName, String pattern);

}
