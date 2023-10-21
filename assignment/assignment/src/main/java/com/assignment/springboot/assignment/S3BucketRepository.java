package com.assignment.springboot.assignment;

import com.assignment.springboot.assignment.S3bucket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface S3BucketRepository extends JpaRepository<S3bucket, Long> {
}
