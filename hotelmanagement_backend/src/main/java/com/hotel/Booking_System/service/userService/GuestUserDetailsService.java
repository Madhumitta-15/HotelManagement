package com.hotel.Booking_System.service.userService;


import com.hotel.Booking_System.model.Guest;
import com.hotel.Booking_System.repository.userRepo.GuestRepository;

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
public class GuestUserDetailsService implements UserDetailsService {

    @Autowired
    private GuestRepository guestRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Guest> guestOpt = guestRepository.findByGuestUserName(username);
        if (guestOpt.isEmpty()) {
            throw new UsernameNotFoundException("Guest user not found with username: " + username);
        }
        Guest guest=guestOpt.get();
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_"+guest.getRole().name());
        return new User(guest.getGuestUserName(), guest.getGuestPassword(), Collections.singletonList(authority));

    }
}



