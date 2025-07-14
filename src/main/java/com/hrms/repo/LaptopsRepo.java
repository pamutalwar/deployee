package com.hrms.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hrms.model.Laptops;
@Repository
public interface LaptopsRepo extends JpaRepository<Laptops, Long> {

}
