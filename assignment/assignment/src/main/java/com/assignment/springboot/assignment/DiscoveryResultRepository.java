package com.assignment.springboot.assignment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiscoveryResultRepository extends JpaRepository<Discovery, Long> {

    List<Discovery> findByJobId(String jobId);

}
