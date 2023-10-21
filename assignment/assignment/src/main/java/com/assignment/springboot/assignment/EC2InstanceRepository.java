package com.assignment.springboot.assignment;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EC2InstanceRepository extends JpaRepository<EC2Instance, Long> {
}
