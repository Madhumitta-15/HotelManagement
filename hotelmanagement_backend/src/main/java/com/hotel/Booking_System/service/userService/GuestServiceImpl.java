package com.hotel.Booking_System.service.userService;

import com.hotel.Booking_System.dto.bookingdto.GuestBookingDTO;
import com.hotel.Booking_System.dto.bookingdto.GuestPaymentDTO;
import com.hotel.Booking_System.dto.userdto.GuestDTO;
import com.hotel.Booking_System.dto.userdto.UserRepresentationDTO;
import com.hotel.Booking_System.dto.userdto.UserUpdateDTO;
import com.hotel.Booking_System.exception.UserAlreadyExistsException;
import com.hotel.Booking_System.exception.UserNotFoundException;
import com.hotel.Booking_System.model.Guest;
import com.hotel.Booking_System.model.enums.Role;
import com.hotel.Booking_System.repository.userRepo.GuestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GuestServiceImpl implements GuestService {

    @Autowired
    private GuestRepository guestRepository;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    //Register new Guest
    @Override
    public Guest register( Guest newGuest) throws UserAlreadyExistsException {
        Optional<Guest> existingUserByName = guestRepository.findByGuestUserName(newGuest.getGuestUserName()); // Changed to findByGuestUserName
        Optional<Guest> existingUserByEmail = guestRepository.findByGuestEmail(newGuest.getGuestEmail());
        Optional<Guest> existingUserByContact = guestRepository.findByGuestContact(newGuest.getGuestContact());

        if (existingUserByName.isPresent() || existingUserByEmail.isPresent() ||existingUserByContact.isPresent()) {
            throw new UserAlreadyExistsException("User already exists.");
        }
        else {
            Guest guest = new Guest();
            guest.setGuestName(newGuest.getGuestName());
            guest.setGuestUserName(newGuest.getGuestUserName());
            guest.setGuestPassword(newGuest.getGuestPassword());
            guest.setGuestEmail(newGuest.getGuestEmail());
            guest.setGuestContact(newGuest.getGuestContact());
            guest.setHotelBooked(newGuest.getHotelBooked());
            guest.setGuestPassword(encoder.encode(newGuest.getGuestPassword()));
            guest.setRole(Role.GUEST);
            return guestRepository.save(guest);
        }
    }

    //deleting guest
    @Override
    public void deleteGuest(String guestName) throws  UserNotFoundException {
        // This method still uses guestName, assuming it's for actual name deletion
        Optional<Guest> guestOpt = guestRepository.findByGuestName(guestName);
        if (guestOpt.isPresent()) {
            Guest guestToDelete = guestOpt.get();
            guestRepository.delete(guestToDelete);
        } else {
            throw new UserNotFoundException("Guest with name '" + guestName + "' not found.");
        }
    }

    //viewing guest by actual guest name
    @Override
    public UserRepresentationDTO viewGuest(String guestname){
        Guest guest=guestRepository.findByGuestName(guestname).orElseThrow(
                () -> new UserNotFoundException("Guest not found"));
        UserRepresentationDTO guestDTO=new UserRepresentationDTO();
        guestDTO.setId(guest.getGuestId());
        guestDTO.setName(guest.getGuestName());
        guestDTO.setEmail(guest.getGuestEmail());
        guestDTO.setContact(guest.getGuestContact());
        return guestDTO;
    }

    //guest profile - FETCHING BY GUEST USERNAME (LOGIN USERNAME)
    @Override
    public GuestDTO guestProfile(String guestUserName) { // Renamed parameter for clarity
        // FIX: Use findByGuestUserName instead of findByGuestName
        System.out.println("GuestServiceImpl: Attempting to fetch guest profile for username: '" + guestUserName + "'"); // Keep this log for debugging
        Guest guest = guestRepository.findByGuestUserName(guestUserName).orElseThrow(
                () -> {
                    System.err.println("GuestServiceImpl: UserNotFoundException thrown for username: '" + guestUserName + "'"); // Keep this error log
                    return new UserNotFoundException("Guest not found");
                });

        return new GuestDTO(
                guest.getGuestId(),
                guest.getGuestName(),
                guest.getGuestUserName(), // Ensure this field is mapped
                guest.getGuestEmail(),
                guest.getGuestContact(),
                guest.getHotelBooked() != null ? guest.getHotelBooked().getHotelId() : null,
                guest.getBookings().stream()
                        .map(b -> new GuestBookingDTO(b.getBookingId(), b.getRoomType()))
                        .toList(),
                guest.getPayments().stream()
                        .map(p -> new GuestPaymentDTO(p.getPaymentId(), p.getAmount()))
                        .toList()
        );
    }

    //update guest
    @Override
    public Guest updateGuest(String guestname, UserUpdateDTO newGuest) {
        // This method still uses guestName, assuming it's for actual name update
        Guest guest=guestRepository.findByGuestName(guestname).orElseThrow(()->new UserNotFoundException("Guest not found") );
        guest.setGuestName(newGuest.getName());
        guest.setGuestEmail(newGuest.getEmail());
        guest.setGuestContact(newGuest.getContact());
        return guestRepository.save(guest);

    }

    //update guest by id
    @Override
    public void updateGuestById(Integer guestId, UserUpdateDTO newGuest) {
        Optional<Guest> guestOptional = guestRepository.findById(guestId);

        if (guestOptional.isEmpty()) {
            throw new RuntimeException("Guest not found with ID: " + guestId);
        }

        Guest existingGuest = guestOptional.get();

        if (newGuest.getName() != null && !newGuest.getName().isBlank()) {
            existingGuest.setGuestName(newGuest.getName());
        }

        if (newGuest.getEmail() != null && !newGuest.getEmail().isBlank()) {
            Optional<Guest> existingGuestWithNewEmail = guestRepository.findByGuestEmail(newGuest.getEmail());
            if (existingGuestWithNewEmail.isPresent() &&
                    !existingGuestWithNewEmail.get().getGuestId().equals(existingGuest.getGuestId())) {
                throw new RuntimeException("Email '" + newGuest.getEmail() + "' is already in use by another guest.");
            }
            existingGuest.setGuestEmail(newGuest.getEmail());
        }

        if (newGuest.getContact() != null && !newGuest.getContact().isBlank()) {
            existingGuest.setGuestContact(newGuest.getContact());
        }

        guestRepository.save(existingGuest);
    }

}