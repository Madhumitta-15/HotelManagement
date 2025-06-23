package com.hotel.Booking_System.controller.hotel;

import com.hotel.Booking_System.dto.hoteldto.HotelDTO;
import com.hotel.Booking_System.dto.hoteldto.HotelDetailsDTO;
import com.hotel.Booking_System.dto.hoteldto.HotelResponseDTO;
import com.hotel.Booking_System.exception.*;
import com.hotel.Booking_System.model.Hotel;
import com.hotel.Booking_System.model.enums.RoomType;
import com.hotel.Booking_System.service.hotelServices.HotelService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hotel")
public class HotelController {

    @Autowired
    private HotelService hotelService;


    //add hotel
    @PostMapping("/addHotel")
    public ResponseEntity<String> addHotel(@Valid @RequestBody HotelDTO hotelDTO) {
        try {
            hotelService.addHotel(hotelDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("Hotel added successfully.");
        } catch (HotelAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add hotel: " + e.getMessage());
        }
    }

    //get all hotels
    @GetMapping("/gethotelslist")
    public ResponseEntity<List<HotelDTO>> getAllHotels() {
        List<HotelDTO> hotels = hotelService.getAllHotels();
        if (hotels.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(hotels);
    }

    @GetMapping("/gethotel/{id}")
    public ResponseEntity<Hotel> getHotelById(@PathVariable Integer id) {
        return hotelService.getHotelById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new HotelNotFoundException("Hotel not found with ID: " + id));
    }

//    extra added on sat

    @GetMapping("/gethoteldetails/{hotelId}")
        public ResponseEntity<HotelDetailsDTO> gethoteldetails(@PathVariable Integer hotelId){
           HotelDetailsDTO hotel= hotelService.gethoteldetails(hotelId);
           return ResponseEntity.ok(hotel);
        }


    @GetMapping("gethotel/roomtype/{roomType}")
    public ResponseEntity<List<HotelDetailsDTO>> getHotelsByRoomType(@PathVariable String roomType) {
        try {
            RoomType type = RoomType.valueOf(roomType.toUpperCase());
            List<HotelDetailsDTO> hotels = hotelService.getHotelsByRoomType(type);

            if (hotels.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            return ResponseEntity.ok(hotels);

        } catch (ResourceNotFoundException e) { // This exception implies no hotels were found with the room type, which maps to 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // NEW ENDPOINT: Filter hotels by location and room type
    @GetMapping("findhotel/bylocationandroomtype")
    public ResponseEntity<List<HotelDetailsDTO>> getHotelsByLocationAndRoomType(
            @RequestParam String location,
            @RequestParam String roomType) {
        try {
            RoomType type = RoomType.valueOf(roomType.toUpperCase());
            List<HotelDetailsDTO> hotels = hotelService.getHotelsByLocationAndRoomType(location, type);

            if (hotels.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            return ResponseEntity.ok(hotels);
        
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    //updating hotel
    @PutMapping("/updatehotel/{hotelId}")
    public ResponseEntity<HotelResponseDTO> updateHotel(@PathVariable int hotelId, @RequestBody HotelDTO hotelRequestDTO) {
        try {
            Hotel hotel=hotelService.updateHotel(hotelId,hotelRequestDTO);
            HotelResponseDTO hotelResponseDTO=new HotelResponseDTO();
            hotelResponseDTO.setHotelId(hotel.getHotelId());
            hotelResponseDTO.setHotelName(hotel.getHotelName());
            hotelResponseDTO.setLocation(hotel.getLocation());
            hotelResponseDTO.setAmenities(hotel.getAmenities());
            hotelResponseDTO.setImageUrl(hotel.getImageUrl());

            return ResponseEntity.status(HttpStatus.OK).body(hotelResponseDTO);
        } catch (HotelNotFoundException e) {
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

    }

    //deleting hotel
    @DeleteMapping("/deleteHotel/{hotelId}")
    public ResponseEntity<String> deleteHotel(@PathVariable int hotelId) {
        try {
            hotelService.deleteHotel(hotelId);
            return ResponseEntity.ok("Hotel with ID '" + hotelId + "' deleted successfully.");
        } catch (HotelNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete hotel: " + e.getMessage());
        }
    }

    //get hotel by location
    @GetMapping("findhotel/by-location")
    public ResponseEntity<List<HotelResponseDTO>> getHotelsByLocation(@RequestParam String location) {
        List<HotelResponseDTO> hotelDTOs = hotelService.getHotelsByLocation(location);
        if (hotelDTOs.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(hotelDTOs);
    }


    //assign manager to hotel
    @PostMapping("/{hotelId}/assign-manager")
    public ResponseEntity<String> assignManagerToHotel(
            @PathVariable int hotelId,
            @RequestBody Map<String,Integer> managerIdMap) {
        Integer managerId = managerIdMap.get("managerId");

        try {
            hotelService.assignManagerToHotel(hotelId, managerId);
            return ResponseEntity.ok("manager assigned successfully");
        } catch (HotelNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Hotel not found");
        } catch (ManagerNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("manager with manager id "+managerId+" doesn't exist");
        } catch (ManagerAlreadyAssignedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("manager already assigned ");
        }catch(HotelAlreadyAssignedException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Hotel is already assigned with a manager");
        }
    }

    //view hotel assigned to manager
    @GetMapping("/manager/{managerId}")
    public ResponseEntity<HotelDTO> getHotelByManager(@PathVariable int managerId) {
        HotelDTO hotelDTO = hotelService.viewHotelByManagerId(managerId);
        return ResponseEntity.ok(hotelDTO);
    }
}