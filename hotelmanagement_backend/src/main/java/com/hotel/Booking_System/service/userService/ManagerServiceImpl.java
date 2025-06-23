package com.hotel.Booking_System.service.userService;



import com.hotel.Booking_System.dto.userdto.UserRepresentationDTO;
import com.hotel.Booking_System.dto.userdto.UserUpdateDTO;
import com.hotel.Booking_System.exception.UserAlreadyExistsException;
import com.hotel.Booking_System.exception.UserNotFoundException;
import com.hotel.Booking_System.exception.ManagerNotFoundException;
import com.hotel.Booking_System.model.Hotel;
import com.hotel.Booking_System.model.Manager;
import com.hotel.Booking_System.model.enums.Role;
import com.hotel.Booking_System.repository.hotelRepo.HotelRepository;
import com.hotel.Booking_System.repository.userRepo.ManagerRepository;
import com.hotel.Booking_System.service.userService.ManagerService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ManagerServiceImpl implements ManagerService {

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private HotelRepository hotelRepository;



    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);


    //Adding hotel manager

    @Override
    public void addHotelManager(@Valid Manager manager)  {
        Optional<Manager> existingManagerByName = managerRepository.findByManagerName(manager.getManagerName());
        Optional<Manager> existingManagerByemail = managerRepository.findByManagerEmail(manager.getManagerEmail());
        Optional<Manager> existingManagerBycontact = managerRepository.findByManagerContact(manager.getManagerContact());

        if (existingManagerByName.isPresent() || existingManagerByemail.isPresent() || existingManagerBycontact.isPresent()) {
            throw new UserAlreadyExistsException("manager already exists.");
        } else {
            Manager newManager = new Manager();
            newManager.setManagerName(manager.getManagerName());
            newManager.setManagerPassword(manager.getManagerPassword());
            newManager.setManagerEmail(manager.getManagerEmail());
            newManager.setManagerContact(manager.getManagerContact());
            newManager.setManagerPassword(encoder.encode(manager.getManagerPassword()));
            newManager.setRole(Role.MANAGER);
            managerRepository.save(newManager);


        }
    }


    //delete hotel manager
    @Override
    @Transactional // <-- IMPORTANT: Add @Transactional to ensure atomicity
    public void deleteManager(int managerId) throws ManagerNotFoundException {
        Optional<Manager> managerToDeleteOptional = managerRepository.findById(managerId);

        if (managerToDeleteOptional.isPresent()) {
            Manager managerToDelete = managerToDeleteOptional.get();

            // Check if this manager is assigned to any hotel
            // In a OneToOne relationship, a manager can only be assigned to one hotel.
            // So, we get the hotel associated with this manager.
            Hotel associatedHotel = managerToDelete.getHotel(); // Access the hotel through the manager

            if (associatedHotel != null) {
                // Break the association from the Hotel side
                associatedHotel.setManager(null);
                hotelRepository.save(associatedHotel); // Save the hotel to persist the null manager_id
            }

            // Now, delete the manager
            managerRepository.deleteById(managerId);
        } else {
            throw new ManagerNotFoundException("Manager with ID '" + managerId + "' not found."); // Corrected message to use managerId
        }
    }


    //update hotel manager
    @Override
    public Manager updateManager(int managerId, UserUpdateDTO updatedManager) {
        Manager manager=managerRepository.findById(managerId).orElseThrow(()->new UserNotFoundException("Manager not found") );

        if(updatedManager.getName()!=null) {
            manager.setManagerName(updatedManager.getName());
        }
        if(updatedManager.getEmail()!=null) {
            manager.setManagerEmail(updatedManager.getEmail());
        }
        if(updatedManager.getContact()!=null) {
            manager.setManagerContact(updatedManager.getContact());
        }
        return managerRepository.save(manager);
    }

    //view hotel profile
    @Override
    public UserRepresentationDTO viewHotelManager(int managerId) {
        Manager manager=managerRepository.findById(managerId).orElseThrow(
                ()-> new ManagerNotFoundException("Manager Not Found"));

        UserRepresentationDTO managerDTO=new UserRepresentationDTO();
        managerDTO.setId(manager.getManagerId());
        managerDTO.setName(manager.getManagerName());
        managerDTO.setEmail(manager.getManagerEmail());
        managerDTO.setContact(manager.getManagerContact());
        return managerDTO;

    }


    //find the hotel assigned to the manager
    @Override

    public Hotel getHotelByManagerId(int managerId) {
        Manager manager = managerRepository.findById(managerId).orElseThrow(()->new ManagerNotFoundException("Manager not found"));
        return manager.getHotel(); // Directly access the associated Hotel object
    }


}

