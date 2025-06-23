package com.hotel.Booking_System.repository.userRepo;

import com.hotel.Booking_System.model.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface ManagerRepository extends JpaRepository<Manager,Integer> {

    Optional<Manager> findByManagerName(String managerName);
    Optional<Manager> findByManagerEmail(String managerEmail);
    Optional<Manager>findByManagerContact(String managerContact);
}

