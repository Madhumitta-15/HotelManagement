package com.hotel.Booking_System.service.hotelServices;

import com.hotel.Booking_System.dto.hoteldto.HotelDTO;
import com.hotel.Booking_System.dto.hoteldto.HotelDetailsDTO;
import com.hotel.Booking_System.dto.hoteldto.HotelResponseDTO;
import com.hotel.Booking_System.dto.reviewdto.ReviewDTO;
import com.hotel.Booking_System.dto.reviewdto.ReviewMapper;
import com.hotel.Booking_System.dto.userdto.ManagerDTO;
import com.hotel.Booking_System.exception.*;
import com.hotel.Booking_System.model.Hotel;
import com.hotel.Booking_System.model.Manager;
import com.hotel.Booking_System.model.Review;
import com.hotel.Booking_System.model.enums.RoomType;
import com.hotel.Booking_System.repository.hotelRepo.HotelRepository;
import com.hotel.Booking_System.repository.reviewRepo.ReviewRepository;
import com.hotel.Booking_System.repository.userRepo.ManagerRepository;
import com.hotel.Booking_System.service.userService.ManagerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HotelServiceImpl implements HotelService {

    @Autowired
    private  HotelRepository hotelRepository;
    @Autowired
    private  ReviewRepository reviewRepository;
    @Autowired
    private  ManagerRepository managerRepository;
    @Autowired
    private  ManagerService managerService;
    @Autowired
    private ReviewMapper reviewMapper;


    //get all hotel list

    @Override
    public List<HotelDTO> getAllHotels() {
        List<Hotel> hotels = hotelRepository.findAll();
        return hotels.stream()
                .map(hotel -> {
                    HotelDTO dto = new HotelDTO();
                    dto.setHotelId(hotel.getHotelId());        // Sets 'id' in DTO
                    dto.setHotelName(hotel.getHotelName());    // Sets 'name' in DTO
                    dto.setLocation(hotel.getLocation());
                    dto.setAmenities(hotel.getAmenities());// Now uncommented
                    dto.setImageUrl(hotel.getImageUrl());
                    // Manager is NOT being explicitly set in HotelDTO here!
                    if (hotel.getManager() != null) {
                        ManagerDTO managerDto = new ManagerDTO();
                        managerDto.setManagerId(hotel.getManager().getManagerId());
                        managerDto.setManagerName(hotel.getManager().getManagerName());
                        dto.setManager(managerDto);
                    } else {
                        dto.setManager(null); // Explicitly set to null if no manager is assigned
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    //get hotel by hotelby hotelid
    @Override
    public Optional<Hotel> getHotelById(Integer hotelId) {
        return hotelRepository.findById(hotelId);
    }

    @Override
    public HotelDetailsDTO gethoteldetails(Integer hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new HotelNotFoundException("Hotel not found with ID: " + hotelId));
        HotelDetailsDTO dto=new HotelDetailsDTO();
        dto.setHotelName(hotel.getHotelName());
        dto.setLocation(hotel.getLocation());
        dto.setAmenities(hotel.getAmenities());
        dto.setImageUrl(hotel.getImageUrl());
        dto.setDescription(hotel.getDescription());
        return dto;
    }


    //Adding hotel

    @Override
    public void addHotel(HotelDTO hotelDTO) {
        Optional<Hotel> existingHotel = hotelRepository.findByHotelNameAndLocation(hotelDTO.getHotelName(), hotelDTO.getLocation());

        if (existingHotel.isPresent()) {
            throw new HotelAlreadyExistsException("Hotel with name '" + hotelDTO.getHotelName() + "' at location '" + hotelDTO.getLocation() + "' already exists.");
        } else {
            Hotel hotel = new Hotel();
            hotel.setHotelName(hotelDTO.getHotelName());
            hotel.setLocation(hotelDTO.getLocation());
            hotel.setAmenities(hotelDTO.getAmenities());
            hotel.setDescription(hotelDTO.getDescription());
            hotel.setImageUrl(hotelDTO.getImageUrl()); // <-- ADDED: Set imageUrl from DTO

            hotelRepository.save(hotel);
        }
    }

    //get by room type
    @Override
    public List<HotelDetailsDTO> getHotelsByRoomType(RoomType roomType) { // Changed parameter type to RoomType
        List<Hotel> hotels = hotelRepository.findHotelsByRoomType(roomType);
        if (hotels.isEmpty()) {
            // Consider returning an empty list instead of throwing an exception for "no results found"
            // unless it's genuinely an error. For filtering, an empty list is often expected.
            throw new ResourceNotFoundException("No hotels found with room type: " + roomType.name());
        }
        return hotels.stream()
                .map(hotel -> {
                    HotelDetailsDTO dto = new HotelDetailsDTO();
//                    dto.setHotelId(hotel.getHotelId()); // If HotelDetailsDTO has no ID, remove this
                    dto.setHotelName(hotel.getHotelName());
                    dto.setLocation(hotel.getLocation());
                    dto.setAmenities(hotel.getAmenities());
                    dto.setImageUrl(hotel.getImageUrl());

                    return dto;
                })
                .collect(Collectors.toList());
    }

    // NEW METHOD IMPLEMENTATION: Filter hotels by location and room type
    @Override
    public List<HotelDetailsDTO> getHotelsByLocationAndRoomType(String location, RoomType roomType) {
        List<Hotel> hotels = hotelRepository.findHotelsByLocationAndRoomType(location, roomType);
        if (hotels.isEmpty()) {
            // Again, consider if throwing an exception or returning an empty list is better here.
            // For a search/filter operation, returning an empty list might be more user-friendly.
            throw new ResourceNotFoundException("No hotels found in '" + location + "' with room type: " + roomType.name());
        }
        return hotels.stream()
                .map(hotel -> {
                    HotelDetailsDTO dto = new HotelDetailsDTO();
                    dto.setHotelName(hotel.getHotelName());
                    dto.setLocation(hotel.getLocation());
                    dto.setAmenities(hotel.getAmenities());
                    dto.setImageUrl(hotel.getImageUrl());

                    List<Review> reviews = reviewRepository.findByHotel(hotel);
                    List<ReviewDTO> reviewDTOs = reviewMapper.toReviewDtoList(reviews);


                    return dto;
                })
                .collect(Collectors.toList());
    }


    //updating hotel
    @Override
    public Hotel updateHotel(Integer hotelId, HotelDTO hotelRequestDTO) {
        Hotel existingHotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new HotelNotFoundException("Hotel not found with id: " + hotelId));

        if (hotelRequestDTO.getHotelName() != null && !hotelRequestDTO.getHotelName().isBlank()) {
            existingHotel.setHotelName(hotelRequestDTO.getHotelName());
        }
        if (hotelRequestDTO.getLocation() != null && !hotelRequestDTO.getLocation().isBlank()) {
            existingHotel.setLocation(hotelRequestDTO.getLocation());
        }
        // Amenities can be an empty string, so no .isBlank() if that's desired
        if (hotelRequestDTO.getAmenities() != null) {
            existingHotel.setAmenities(hotelRequestDTO.getAmenities());
        }

        if (hotelRequestDTO.getImageUrl() != null) { // Allow updating to null or empty string if user clears it
            existingHotel.setImageUrl(hotelRequestDTO.getImageUrl());
        }
//        if (hotelRequestDTO.getDescription() != null) { // Allow updating to null or empty string if user clears it
//            existingHotel.setDescription(hotelRequestDTO.getDescription());
//        }

        return hotelRepository.save(existingHotel);

    }

    //deleting Hotel
    @Override
    public void deleteHotel(int hotelId) throws HotelNotFoundException {
        Optional<Hotel> hotelToDelete = hotelRepository.findById(hotelId);
        if (hotelToDelete.isPresent()) {
            hotelRepository.deleteById(hotelId);
        } else {
            throw new HotelNotFoundException("Hotel with ID '" + hotelId + "' not found.");
        }
    }


    //get hotel by location
    @Override
    public List<HotelResponseDTO> getHotelsByLocation(String location) {
        List<Hotel> hotels = hotelRepository.findByLocationIgnoreCase(location);
        return hotels.stream()
                .map(hotel -> {
                    HotelResponseDTO dto = new HotelResponseDTO();
                    dto.setHotelId(hotel.getHotelId());
                    dto.setHotelName(hotel.getHotelName());
                    dto.setLocation(hotel.getLocation());
                    dto.setAmenities(hotel.getAmenities());
                    dto.setImageUrl(hotel.getImageUrl());
                    List<Review> reviews = reviewRepository.findByHotel(hotel);
                    List<ReviewDTO> reviewDTOs = reviewMapper.toReviewDtoList(reviews);
                    dto.setReviews(reviewDTOs);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    //add review to the hotel

    @Transactional
    public Review addReviewToHotel(Integer hotelId, Review review) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + hotelId));
        review.setHotel(hotel);
        return reviewRepository.save(review);
    }


    //assign manager to hotel

    @Override
    public Hotel assignManagerToHotel(int hotelId, Integer managerId) {
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(()->new HotelNotFoundException("hotel not found"));
        Manager manager = managerRepository.findById(managerId).orElseThrow(()-> new ManagerNotFoundException("manager not found"));

        if (hotel.getManager() != null && hotel.getManager().equals(manager)) {
            throw new ManagerAlreadyAssignedException("Manager is already assigned to this hotel.");
        }
        if (hotel.getManager() != null) {
            throw new HotelAlreadyAssignedException("This hotel already has a manager assigned.");
        }

        hotel.setManager(manager);
        return hotelRepository.save(hotel);
    }

    //find the hotel assigned to the manager

    @Override
    public HotelDTO viewHotelByManagerId(int managerId) {
        Hotel hotel = managerService.getHotelByManagerId(managerId);
        HotelDTO hotelDTO = new HotelDTO();
        hotelDTO.setHotelId(hotel.getHotelId());
        hotelDTO.setHotelName(hotel.getHotelName());
        hotelDTO.setLocation(hotel.getLocation());
        hotelDTO.setImageUrl(hotel.getImageUrl()); // <-- ADDED: Set imageUrl from the retrieved Hotel entity
        // If your HotelDTO has a manager field, populate it here as well
        if (hotel.getManager() != null) {
            ManagerDTO managerDto = new ManagerDTO();
            managerDto.setManagerId(hotel.getManager().getManagerId());
            managerDto.setManagerName(hotel.getManager().getManagerName());
            hotelDTO.setManager(managerDto); // Assuming HotelDTO has setManager(ManagerDTO)
        }
        return hotelDTO;
    }
}