package com.hrms.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hrms.model.Mobiles;
@Repository
public interface MobilesRepo extends JpaRepository<Mobiles, Long> {

}
