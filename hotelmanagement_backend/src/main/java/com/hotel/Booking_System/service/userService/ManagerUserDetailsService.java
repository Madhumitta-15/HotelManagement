package com.hotel.Booking_System.service.userService;


import com.hotel.Booking_System.model.Manager;
import com.hotel.Booking_System.repository.userRepo.ManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class ManagerUserDetailsService implements UserDetailsService {

    @Autowired
    private ManagerRepository managerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Manager> managerOpt = managerRepository.findByManagerName(username);
        if (managerOpt.isEmpty()) {
            throw new UsernameNotFoundException("Guest user not found with username: " + username);
        }
        Manager manager=managerOpt.get();
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_"+manager.getRole().name());  // Guests have only the GUEST role.  Important: No "ROLE_" prefix here.
        return new User(manager.getManagerName(), manager.getManagerPassword(), Collections.singletonList(authority));

    }
}



