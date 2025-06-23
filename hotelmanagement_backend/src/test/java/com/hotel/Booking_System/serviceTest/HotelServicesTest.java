package com.hotel.Booking_System.serviceTest;

import com.hotel.Booking_System.dto.hoteldto.HotelDTO;
import com.hotel.Booking_System.dto.hoteldto.HotelDetailsDTO;
import com.hotel.Booking_System.dto.hoteldto.HotelResponseDTO;
import com.hotel.Booking_System.dto.reviewdto.ReviewDTO;
import com.hotel.Booking_System.dto.reviewdto.ReviewMapper;
import com.hotel.Booking_System.dto.roomdto.RoomResponseDTO;
import com.hotel.Booking_System.dto.userdto.ManagerDTO;
import com.hotel.Booking_System.exception.*;
import com.hotel.Booking_System.model.Hotel;
import com.hotel.Booking_System.model.Manager;
import com.hotel.Booking_System.model.Review;
import com.hotel.Booking_System.model.Room;
import com.hotel.Booking_System.model.enums.RoomType;
import com.hotel.Booking_System.repository.hotelRepo.HotelRepository;
import com.hotel.Booking_System.repository.hotelRepo.RoomRepository;
import com.hotel.Booking_System.repository.reviewRepo.ReviewRepository;
import com.hotel.Booking_System.repository.userRepo.ManagerRepository;
import com.hotel.Booking_System.service.hotelServices.HotelServiceImpl;
import com.hotel.Booking_System.service.hotelServices.RoomServiceImpl;
import com.hotel.Booking_System.service.userService.ManagerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HotelServicesTest {

    // --- Nested class for RoomServiceImpl Tests ---
    @Nested
    class RoomServiceImplTest {

        @Mock
        private RoomRepository roomRepository;

        @Mock
        private HotelRepository hotelRepository;

        @InjectMocks
        private RoomServiceImpl roomService;

        private Hotel hotel;
        private Room room1;
        private Room room2;

        @BeforeEach
        void setUp() {
            // Instantiate Hotel using constructor and setters
            hotel = new Hotel();
            hotel.setHotelId(1);
            hotel.setHotelName("Test Hotel");
            hotel.setLocation("Test Location");

            // Instantiate Room using constructor and setters
            room1 = new Room();
            room1.setRoomId(101);
            room1.setType(RoomType.DELUXE);
            room1.setPrice(150.0);
            room1.setAvailability(1);
            room1.setFeatures("TV, AC");
            room1.setImageUrl("room1.jpg");
            room1.setHotel(hotel);

            room2 = new Room();
            room2.setRoomId(102);
            room2.setType(RoomType.REGULAR);
            room2.setPrice(100.0);
            room2.setAvailability(0);
            room2.setFeatures("TV");
            room2.setImageUrl("room2.jpg");
            room2.setHotel(hotel);
        }

        @Test
        void getRoomById_existingRoom_returnsRoom() {
            when(roomRepository.findById(101)).thenReturn(Optional.of(room1));

            Optional<Room> foundRoom = roomService.getRoomById(101);

            assertTrue(foundRoom.isPresent());
            assertEquals(room1.getRoomId(), foundRoom.get().getRoomId());
            verify(roomRepository, times(1)).findById(101);
        }

        @Test
        void getRoomById_nonExistingRoom_returnsEmptyOptional() {
            when(roomRepository.findById(999)).thenReturn(Optional.empty());

            Optional<Room> foundRoom = roomService.getRoomById(999);

            assertFalse(foundRoom.isPresent());
            verify(roomRepository, times(1)).findById(999);
        }

        @Test
        void addRoom_newRoomToExistingHotel_returnsSavedRoom() {
            when(hotelRepository.findById(1)).thenReturn(Optional.of(hotel));
            when(roomRepository.findByHotelAndTypeOrFeatures(any(Hotel.class), any(RoomType.class), anyString())).thenReturn(Optional.empty());
            when(roomRepository.save(any(Room.class))).thenReturn(room1);

            Room savedRoom = roomService.addRoom(1, room1);

            assertNotNull(savedRoom);
            assertEquals(room1.getRoomId(), savedRoom.getRoomId());
            assertEquals(hotel, savedRoom.getHotel());
            verify(hotelRepository, times(1)).findById(1);
            verify(roomRepository, times(1)).findByHotelAndTypeOrFeatures(hotel, room1.getType(), room1.getFeatures());
            verify(roomRepository, times(1)).save(room1);
        }

        @Test
        void addRoom_toNonExistingHotel_throwsResourceNotFoundException() {
            when(hotelRepository.findById(999)).thenReturn(Optional.empty());

            ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
                roomService.addRoom(999, room1);
            });

            assertEquals("Hotel not found with id: 999", thrown.getMessage());
            verify(hotelRepository, times(1)).findById(999);
            verify(roomRepository, never()).findByHotelAndTypeOrFeatures(any(), any(), any());
            verify(roomRepository, never()).save(any());
        }

        @Test
        void addRoom_existingRoomInHotel_throwsRoomUnavailableException() {
            when(hotelRepository.findById(1)).thenReturn(Optional.of(hotel));
            when(roomRepository.findByHotelAndTypeOrFeatures(any(Hotel.class), any(RoomType.class), anyString())).thenReturn(Optional.of(room1));

            RoomUnavailableException thrown = assertThrows(RoomUnavailableException.class, () -> {
                roomService.addRoom(1, room1);
            });

            assertEquals("Room with type 'DELUXE' and features 'TV, AC' already exists in this hotel.", thrown.getMessage());
            verify(hotelRepository, times(1)).findById(1);
            verify(roomRepository, times(1)).findByHotelAndTypeOrFeatures(hotel, room1.getType(), room1.getFeatures());
            verify(roomRepository, never()).save(any());
        }


        @Test
        void updateRoom_existingRoom_returnsUpdatedRoom() {
            Room updatedRoomDetails = new Room();
            updatedRoomDetails.setType(RoomType.ULTRADELUXE);
            updatedRoomDetails.setPrice(300.0);
            updatedRoomDetails.setAvailability(1);
            updatedRoomDetails.setFeatures("Jacuzzi, Balcony");
            updatedRoomDetails.setImageUrl("ultradeluxe.jpg");

            when(roomRepository.findById(101)).thenReturn(Optional.of(room1));
            when(roomRepository.save(any(Room.class))).thenReturn(updatedRoomDetails);

            Room result = roomService.updateRoom(101, updatedRoomDetails);

            assertNotNull(result);
            assertEquals(RoomType.ULTRADELUXE, result.getType());
            assertEquals(300.0, result.getPrice());
            assertEquals(1, result.getAvailability());
            assertEquals("Jacuzzi, Balcony", result.getFeatures());
            assertEquals("ultradeluxe.jpg", result.getImageUrl());
            verify(roomRepository, times(1)).findById(101);
            verify(roomRepository, times(1)).save(room1);
        }

        @Test
        void updateRoom_nonExistingRoom_throwsResourceNotFoundException() {
            when(roomRepository.findById(999)).thenReturn(Optional.empty());

            ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
                roomService.updateRoom(999, room1);
            });

            assertEquals("Room not found with id: 999", thrown.getMessage());
            verify(roomRepository, times(1)).findById(999);
            verify(roomRepository, never()).save(any());
        }

        @Test
        void deleteRoom_existingRoom_deletesSuccessfully() {
            when(roomRepository.existsById(101)).thenReturn(true);
            doNothing().when(roomRepository).deleteById(101);

            assertDoesNotThrow(() -> roomService.deleteRoom(101));

            verify(roomRepository, times(1)).existsById(101);
            verify(roomRepository, times(1)).deleteById(101);
        }

        @Test
        void deleteRoom_nonExistingRoom_throwsResourceNotFoundException() {
            when(roomRepository.existsById(999)).thenReturn(false);

            ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
                roomService.deleteRoom(999);
            });

            assertEquals("Room not found with id: 999", thrown.getMessage());
            verify(roomRepository, times(1)).existsById(999);
            verify(roomRepository, never()).deleteById(any());
        }

        @Test
        void getRoomsByHotelAndType_returnsFilteredRooms() {
            when(roomRepository.findByHotel_HotelIdAndType(1, RoomType.DELUXE)).thenReturn(Collections.singletonList(room1));

            List<Room> rooms = roomService.getRoomsByHotelAndType(1, RoomType.DELUXE);

            assertNotNull(rooms);
            assertFalse(rooms.isEmpty());
            assertEquals(1, rooms.size());
            assertEquals(room1.getType(), rooms.get(0).getType());
            verify(roomRepository, times(1)).findByHotel_HotelIdAndType(1, RoomType.DELUXE);
        }

        @Test
        void getRoomsByHotelAndType_noRoomsFound_returnsEmptyList() {
            when(roomRepository.findByHotel_HotelIdAndType(1, RoomType.ULTRADELUXE)).thenReturn(Collections.emptyList());

            List<Room> rooms = roomService.getRoomsByHotelAndType(1, RoomType.ULTRADELUXE);

            assertNotNull(rooms);
            assertTrue(rooms.isEmpty());
            verify(roomRepository, times(1)).findByHotel_HotelIdAndType(1, RoomType.ULTRADELUXE);
        }


        @Test
        void getAllRoomsByHotel_returnsRoomResponseDTOList() {
            List<Room> rooms = Arrays.asList(room1, room2);
            when(roomRepository.findByHotel_HotelId(1)).thenReturn(rooms);

            List<RoomResponseDTO> result = roomService.getAllRoomsByHotel(1);

            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(room1.getRoomId(), result.get(0).getRoomId());
            assertEquals(room2.getRoomId(), result.get(1).getRoomId());
            assertEquals(room1.getAvailability(), result.get(0).getAvailability());
            assertEquals(room2.getAvailability(), result.get(1).getAvailability());
            verify(roomRepository, times(1)).findByHotel_HotelId(1);
        }

        @Test
        void getAllRoomsByHotel_noRoomsInHotel_returnsEmptyList() {
            when(roomRepository.findByHotel_HotelId(1)).thenReturn(Collections.emptyList());

            List<RoomResponseDTO> result = roomService.getAllRoomsByHotel(1);

            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(roomRepository, times(1)).findByHotel_HotelId(1);
        }
    }

    // --- Nested class for HotelServiceImpl Tests ---
    @Nested
    class HotelServiceImplTest {

        @Mock
        private HotelRepository hotelRepository;
        @Mock
        private ReviewRepository reviewRepository;
        @Mock
        private ManagerRepository managerRepository;
        @Mock
        private ManagerService managerService;
        @Mock
        private ReviewMapper reviewMapper;

        @InjectMocks
        private HotelServiceImpl hotelService;

        private Hotel hotel1;
        private Hotel hotel2;
        private Manager manager1;
        private Review review1;
        private HotelDTO hotelDTO1;

        @BeforeEach
        void setUp() {
            // Instantiate Manager using constructor and setters
            manager1 = new Manager();
            manager1.setManagerId(1);
            manager1.setManagerName("John Doe");

            // Instantiate Hotel using constructor and setters
            hotel1 = new Hotel();
            hotel1.setHotelId(1);
            hotel1.setHotelName("Grand Hyatt");
            hotel1.setLocation("New York");
            hotel1.setAmenities("Pool, Gym");
            hotel1.setImageUrl("hyatt.jpg");
            hotel1.setDescription("Luxury hotel");
            hotel1.setManager(manager1);

            hotel2 = new Hotel();
            hotel2.setHotelId(2);
            hotel2.setHotelName("Hilton Garden Inn");
            hotel2.setLocation("London");
            hotel2.setAmenities("Restaurant");
            hotel2.setImageUrl("hilton.jpg");
            hotel2.setDescription("Business hotel");
            // No manager for hotel2 initially

            // Instantiate Review using constructor and setters
            review1 = new Review();
            review1.setReviewId(1);
            review1.setComment("Great stay!");
            review1.setRating(5);
            review1.setHotel(hotel1);

            // HotelDTO instantiation remains the same as it's not a model class
            hotelDTO1 = new HotelDTO();
            hotelDTO1.setHotelName("Grand Hyatt");
            hotelDTO1.setLocation("New York");
            hotelDTO1.setAmenities("Pool, Gym");
            hotelDTO1.setImageUrl("hyatt.jpg");
            hotelDTO1.setDescription("Luxury hotel");
            ManagerDTO managerDTO = new ManagerDTO();
            managerDTO.setManagerId(1);
            managerDTO.setManagerName("John Doe");
            hotelDTO1.setManager(managerDTO);
        }

        @Test
        void getAllHotels_returnsListOfHotelDTOs() {
            when(hotelRepository.findAll()).thenReturn(Arrays.asList(hotel1, hotel2));

            List<HotelDTO> result = hotelService.getAllHotels();

            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("Grand Hyatt", result.get(0).getHotelName());
            assertEquals("New York", result.get(0).getLocation());
            assertEquals("John Doe", result.get(0).getManager().getManagerName());
            assertEquals("Hilton Garden Inn", result.get(1).getHotelName());
            assertNull(result.get(1).getManager());
            verify(hotelRepository, times(1)).findAll();
        }

        @Test
        void getAllHotels_noHotels_returnsEmptyList() {
            when(hotelRepository.findAll()).thenReturn(Collections.emptyList());

            List<HotelDTO> result = hotelService.getAllHotels();

            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(hotelRepository, times(1)).findAll();
        }

        @Test
        void getHotelById_existingHotel_returnsHotel() {
            when(hotelRepository.findById(1)).thenReturn(Optional.of(hotel1));

            Optional<Hotel> foundHotel = hotelService.getHotelById(1);

            assertTrue(foundHotel.isPresent());
            assertEquals(hotel1.getHotelId(), foundHotel.get().getHotelId());
            verify(hotelRepository, times(1)).findById(1);
        }

        @Test
        void getHotelById_nonExistingHotel_returnsEmptyOptional() {
            when(hotelRepository.findById(999)).thenReturn(Optional.empty());

            Optional<Hotel> foundHotel = hotelService.getHotelById(999);

            assertFalse(foundHotel.isPresent());
            verify(hotelRepository, times(1)).findById(999);
        }

        @Test
        void gethoteldetails_existingHotel_returnsHotelDetailsDTO() {
            when(hotelRepository.findById(1)).thenReturn(Optional.of(hotel1));

            HotelDetailsDTO result = hotelService.gethoteldetails(1);

            assertNotNull(result);
            assertEquals(hotel1.getHotelName(), result.getHotelName());
            assertEquals(hotel1.getLocation(), result.getLocation());
            assertEquals(hotel1.getAmenities(), result.getAmenities());
            assertEquals(hotel1.getImageUrl(), result.getImageUrl());
            assertEquals(hotel1.getDescription(), result.getDescription());
            verify(hotelRepository, times(1)).findById(1);
        }

        @Test
        void gethoteldetails_nonExistingHotel_throwsHotelNotFoundException() {
            when(hotelRepository.findById(999)).thenReturn(Optional.empty());

            HotelNotFoundException thrown = assertThrows(HotelNotFoundException.class, () -> {
                hotelService.gethoteldetails(999);
            });

            assertEquals("Hotel not found with ID: 999", thrown.getMessage());
            verify(hotelRepository, times(1)).findById(999);
        }

        @Test
        void addHotel_newHotel_addsSuccessfully() {
            when(hotelRepository.findByHotelNameAndLocation(anyString(), anyString())).thenReturn(Optional.empty());
            when(hotelRepository.save(any(Hotel.class))).thenReturn(hotel1);

            assertDoesNotThrow(() -> hotelService.addHotel(hotelDTO1));

            verify(hotelRepository, times(1)).findByHotelNameAndLocation(hotelDTO1.getHotelName(), hotelDTO1.getLocation());
            verify(hotelRepository, times(1)).save(any(Hotel.class));
        }

        @Test
        void addHotel_existingHotel_throwsHotelAlreadyExistsException() {
            when(hotelRepository.findByHotelNameAndLocation(anyString(), anyString())).thenReturn(Optional.of(hotel1));

            HotelAlreadyExistsException thrown = assertThrows(HotelAlreadyExistsException.class, () -> {
                hotelService.addHotel(hotelDTO1);
            });

            assertEquals("Hotel with name 'Grand Hyatt' at location 'New York' already exists.", thrown.getMessage());
            verify(hotelRepository, times(1)).findByHotelNameAndLocation(hotelDTO1.getHotelName(), hotelDTO1.getLocation());
            verify(hotelRepository, never()).save(any());
        }

        @Test
        void getHotelsByRoomType_returnsFilteredHotels() {
            Hotel hotelWithDeluxeRoom = new Hotel();
            hotelWithDeluxeRoom.setHotelId(3);
            hotelWithDeluxeRoom.setHotelName("ABC Hotel");
            hotelWithDeluxeRoom.setLocation("Delhi");

            when(hotelRepository.findHotelsByRoomType(RoomType.DELUXE)).thenReturn(Collections.singletonList(hotelWithDeluxeRoom));

            List<HotelDetailsDTO> result = hotelService.getHotelsByRoomType(RoomType.DELUXE);

            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(1, result.size());
            assertEquals("ABC Hotel", result.get(0).getHotelName());
            verify(hotelRepository, times(1)).findHotelsByRoomType(RoomType.DELUXE);
        }

        @Test
        void getHotelsByRoomType_noHotelsFound_throwsResourceNotFoundException() {
            when(hotelRepository.findHotelsByRoomType(RoomType.ULTRADELUXE)).thenReturn(Collections.emptyList());

            ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
                hotelService.getHotelsByRoomType(RoomType.ULTRADELUXE);
            });

            assertEquals("No hotels found with room type: ULTRADELUXE", thrown.getMessage());
            verify(hotelRepository, times(1)).findHotelsByRoomType(RoomType.ULTRADELUXE);
        }

        @Test
        void getHotelsByLocationAndRoomType_returnsFilteredHotels() {
            Hotel hotelInLocationWithRoomType = new Hotel();
            hotelInLocationWithRoomType.setHotelId(4);
            hotelInLocationWithRoomType.setHotelName("XYZ Inn");
            hotelInLocationWithRoomType.setLocation("London");

            when(hotelRepository.findHotelsByLocationAndRoomType("London", RoomType.REGULAR)).thenReturn(Collections.singletonList(hotelInLocationWithRoomType));
            when(reviewRepository.findByHotel(any(Hotel.class))).thenReturn(Collections.emptyList());

            List<HotelDetailsDTO> result = hotelService.getHotelsByLocationAndRoomType("London", RoomType.REGULAR);

            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(1, result.size());
            assertEquals("XYZ Inn", result.get(0).getHotelName());
            verify(hotelRepository, times(1)).findHotelsByLocationAndRoomType("London", RoomType.REGULAR);
        }

        @Test
        void getHotelsByLocationAndRoomType_noHotelsFound_throwsResourceNotFoundException() {
            when(hotelRepository.findHotelsByLocationAndRoomType("Paris", RoomType.ULTRADELUXE)).thenReturn(Collections.emptyList());

            ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
                hotelService.getHotelsByLocationAndRoomType("Paris", RoomType.ULTRADELUXE);
            });

            assertEquals("No hotels found in 'Paris' with room type: ULTRADELUXE", thrown.getMessage());
            verify(hotelRepository, times(1)).findHotelsByLocationAndRoomType("Paris", RoomType.ULTRADELUXE);
        }


        @Test
        void updateHotel_existingHotel_returnsUpdatedHotel() {
            HotelDTO updateDTO = new HotelDTO();
            updateDTO.setHotelName("Updated Grand Hyatt");
            updateDTO.setLocation("New York City");
            updateDTO.setAmenities("Pool, Gym, Spa");
            updateDTO.setImageUrl("updated_hyatt.jpg");

            when(hotelRepository.findById(1)).thenReturn(Optional.of(hotel1));
            when(hotelRepository.save(any(Hotel.class))).thenReturn(hotel1);

            Hotel updatedHotel = hotelService.updateHotel(1, updateDTO);

            assertNotNull(updatedHotel);
            assertEquals("Updated Grand Hyatt", updatedHotel.getHotelName());
            assertEquals("New York City", updatedHotel.getLocation());
            assertEquals("Pool, Gym, Spa", updatedHotel.getAmenities());
            assertEquals("updated_hyatt.jpg", updatedHotel.getImageUrl());
            verify(hotelRepository, times(1)).findById(1);
            verify(hotelRepository, times(1)).save(hotel1);
        }

        @Test
        void updateHotel_nonExistingHotel_throwsHotelNotFoundException() {
            HotelDTO updateDTO = new HotelDTO();
            when(hotelRepository.findById(999)).thenReturn(Optional.empty());

            HotelNotFoundException thrown = assertThrows(HotelNotFoundException.class, () -> {
                hotelService.updateHotel(999, updateDTO);
            });

            assertEquals("Hotel not found with id: 999", thrown.getMessage());
            verify(hotelRepository, times(1)).findById(999);
            verify(hotelRepository, never()).save(any());
        }

        @Test
        void deleteHotel_existingHotel_deletesSuccessfully() {
            when(hotelRepository.findById(1)).thenReturn(Optional.of(hotel1));
            doNothing().when(hotelRepository).deleteById(1);

            assertDoesNotThrow(() -> hotelService.deleteHotel(1));

            verify(hotelRepository, times(1)).findById(1);
            verify(hotelRepository, times(1)).deleteById(1);
        }

        @Test
        void deleteHotel_nonExistingHotel_throwsHotelNotFoundException() {
            when(hotelRepository.findById(999)).thenReturn(Optional.empty());

            HotelNotFoundException thrown = assertThrows(HotelNotFoundException.class, () -> {
                hotelService.deleteHotel(999);
            });

            assertEquals("Hotel with ID '999' not found.", thrown.getMessage());
            verify(hotelRepository, times(1)).findById(999);
            verify(hotelRepository, never()).deleteById(any());
        }

        @Test
        void getHotelsByLocation_returnsFilteredHotelsWithReviews() {
            List<Hotel> hotelsInLocation = Collections.singletonList(hotel1);
            List<Review> reviewsForHotel1 = Collections.singletonList(review1);
            ReviewDTO reviewDTO1 = new ReviewDTO(); // Instantiate ReviewDTO
            reviewDTO1.setComment("Great stay!");
            reviewDTO1.setRating(5);

            when(hotelRepository.findByLocationIgnoreCase("New York")).thenReturn(hotelsInLocation);
            when(reviewRepository.findByHotel(hotel1)).thenReturn(reviewsForHotel1);
            when(reviewMapper.toReviewDtoList(reviewsForHotel1)).thenReturn(Collections.singletonList(reviewDTO1));

            List<HotelResponseDTO> result = hotelService.getHotelsByLocation("New York");

            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(1, result.size());
            assertEquals("Grand Hyatt", result.get(0).getHotelName());
            assertEquals(1, result.get(0).getReviews().size());
            assertEquals("Great stay!", result.get(0).getReviews().get(0).getComment());
            verify(hotelRepository, times(1)).findByLocationIgnoreCase("New York");
            verify(reviewRepository, times(1)).findByHotel(hotel1);
            verify(reviewMapper, times(1)).toReviewDtoList(reviewsForHotel1);
        }

        @Test
        void getHotelsByLocation_noHotelsFound_returnsEmptyList() {
            when(hotelRepository.findByLocationIgnoreCase("NonExistent")).thenReturn(Collections.emptyList());

            List<HotelResponseDTO> result = hotelService.getHotelsByLocation("NonExistent");

            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(hotelRepository, times(1)).findByLocationIgnoreCase("NonExistent");
            verify(reviewRepository, never()).findByHotel(any());
            verify(reviewMapper, never()).toReviewDtoList(any());
        }

        @Test
        void addReviewToHotel_existingHotel_addsReviewSuccessfully() {
            Review newReview = new Review();
            newReview.setComment("Good service");
            newReview.setRating(4);

            when(hotelRepository.findById(1)).thenReturn(Optional.of(hotel1));
            when(reviewRepository.save(any(Review.class))).thenReturn(newReview);

            Review savedReview = hotelService.addReviewToHotel(1, newReview);

            assertNotNull(savedReview);
            assertEquals("Good service", savedReview.getComment());
            assertEquals(hotel1, savedReview.getHotel());
            verify(hotelRepository, times(1)).findById(1);
            verify(reviewRepository, times(1)).save(newReview);
        }

        @Test
        void addReviewToHotel_nonExistingHotel_throwsResourceNotFoundException() {
            Review newReview = new Review();
            newReview.setComment("Good service");
            newReview.setRating(4);

            when(hotelRepository.findById(999)).thenReturn(Optional.empty());

            ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
                hotelService.addReviewToHotel(999, newReview);
            });

            assertEquals("Hotel not found with id: 999", thrown.getMessage());
            verify(hotelRepository, times(1)).findById(999);
            verify(reviewRepository, never()).save(any());
        }

        @Test
        void assignManagerToHotel_successfulAssignment() {
            Manager newManager = new Manager();
            newManager.setManagerId(2);
            newManager.setManagerName("Jane Doe");

            Hotel hotelWithoutManager = new Hotel();
            hotelWithoutManager.setHotelId(3);
            hotelWithoutManager.setHotelName("New Hotel");
            hotelWithoutManager.setLocation("Chennai");

            when(hotelRepository.findById(3)).thenReturn(Optional.of(hotelWithoutManager));
            when(managerRepository.findById(2)).thenReturn(Optional.of(newManager));
            when(hotelRepository.save(any(Hotel.class))).thenReturn(hotelWithoutManager);

            Hotel assignedHotel = hotelService.assignManagerToHotel(3, 2);

            assertNotNull(assignedHotel);
            assertEquals(newManager, assignedHotel.getManager());
            verify(hotelRepository, times(1)).findById(3);
            verify(managerRepository, times(1)).findById(2);
            verify(hotelRepository, times(1)).save(hotelWithoutManager);
        }

        @Test
        void assignManagerToHotel_hotelNotFound_throwsHotelNotFoundException() {
            when(hotelRepository.findById(999)).thenReturn(Optional.empty());

            HotelNotFoundException thrown = assertThrows(HotelNotFoundException.class, () -> {
                hotelService.assignManagerToHotel(999, 1);
            });

            assertEquals("hotel not found", thrown.getMessage());
            verify(hotelRepository, times(1)).findById(999);
            verify(managerRepository, never()).findById(any());
            verify(hotelRepository, never()).save(any());
        }

        @Test
        void assignManagerToHotel_managerNotFound_throwsManagerNotFoundException() {
            when(hotelRepository.findById(1)).thenReturn(Optional.of(hotel1));
            when(managerRepository.findById(999)).thenReturn(Optional.empty());

            ManagerNotFoundException thrown = assertThrows(ManagerNotFoundException.class, () -> {
                hotelService.assignManagerToHotel(1, 999);
            });

            assertEquals("manager not found", thrown.getMessage());
            verify(hotelRepository, times(1)).findById(1);
            verify(managerRepository, times(1)).findById(999);
            verify(hotelRepository, never()).save(any());
        }

        @Test
        void assignManagerToHotel_managerAlreadyAssignedToThisHotel_throwsManagerAlreadyAssignedException() {
            when(hotelRepository.findById(1)).thenReturn(Optional.of(hotel1));
            when(managerRepository.findById(1)).thenReturn(Optional.of(manager1));

            ManagerAlreadyAssignedException thrown = assertThrows(ManagerAlreadyAssignedException.class, () -> {
                hotelService.assignManagerToHotel(1, 1);
            });

            assertEquals("Manager is already assigned to this hotel.", thrown.getMessage());
            verify(hotelRepository, times(1)).findById(1);
            verify(managerRepository, times(1)).findById(1);
            verify(hotelRepository, never()).save(any());
        }

        @Test
        void assignManagerToHotel_hotelAlreadyHasAManager_throwsHotelAlreadyAssignedException() {
            Manager newManager = new Manager();
            newManager.setManagerId(2);
            newManager.setManagerName("Jane Doe");

            when(hotelRepository.findById(1)).thenReturn(Optional.of(hotel1));
            when(managerRepository.findById(2)).thenReturn(Optional.of(newManager));

            HotelAlreadyAssignedException thrown = assertThrows(HotelAlreadyAssignedException.class, () -> {
                hotelService.assignManagerToHotel(1, 2);
            });

            assertEquals("This hotel already has a manager assigned.", thrown.getMessage());
            verify(hotelRepository, times(1)).findById(1);
            verify(managerRepository, times(1)).findById(2);
            verify(hotelRepository, never()).save(any());
        }

        @Test
        void viewHotelByManagerId_returnsHotelDTO() {
            when(managerService.getHotelByManagerId(1)).thenReturn(hotel1);

            HotelDTO result = hotelService.viewHotelByManagerId(1);

            assertNotNull(result);
            assertEquals(hotel1.getHotelId(), result.getHotelId());
            assertEquals(hotel1.getHotelName(), result.getHotelName());
            assertEquals(hotel1.getLocation(), result.getLocation());
            assertEquals(hotel1.getImageUrl(), result.getImageUrl());
            assertNotNull(result.getManager());
            assertEquals(manager1.getManagerId(), result.getManager().getManagerId());
            assertEquals(manager1.getManagerName(), result.getManager().getManagerName());
            verify(managerService, times(1)).getHotelByManagerId(1);
        }

        @Test
        void viewHotelByManagerId_managerHasNoHotel_throwsExceptionFromManagerService() {
            when(managerService.getHotelByManagerId(999)).thenThrow(new ResourceNotFoundException("Manager not found or no hotel assigned"));

            ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
                hotelService.viewHotelByManagerId(999);
            });

            assertEquals("Manager not found or no hotel assigned", thrown.getMessage());
            verify(managerService, times(1)).getHotelByManagerId(999);
        }
    }
}