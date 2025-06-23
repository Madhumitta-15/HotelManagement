package com.hotel.Booking_System.serviceTest;

import com.hotel.Booking_System.model.Guest;
import com.hotel.Booking_System.repository.userRepo.GuestRepository;
import com.hotel.Booking_System.service.userService.GuestServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import com.hotel.Booking_System.dto.userdto.GuestDTO;
import com.hotel.Booking_System.dto.userdto.UserRepresentationDTO;
import com.hotel.Booking_System.dto.userdto.UserUpdateDTO;
import com.hotel.Booking_System.exception.ManagerNotFoundException;
import com.hotel.Booking_System.exception.UserAlreadyExistsException;
import com.hotel.Booking_System.exception.UserNotFoundException;
import com.hotel.Booking_System.model.Hotel;
import com.hotel.Booking_System.model.Manager;
import com.hotel.Booking_System.model.Payment;
import com.hotel.Booking_System.model.Booking;
import com.hotel.Booking_System.model.enums.Role;
import com.hotel.Booking_System.repository.hotelRepo.HotelRepository;
import com.hotel.Booking_System.repository.userRepo.ManagerRepository;
import com.hotel.Booking_System.service.userService.*;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.test.util.ReflectionTestUtils; // For setting private fields
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.*;
@SpringBootTest
class UserServicesTest{
 
	    @Mock 

	    private BCryptPasswordEncoder encoder;
 
		// --- GuestServiceImpl Tests ---
 
		@Mock

		private GuestRepository guestRepository;

		@InjectMocks

		private GuestServiceImpl guestService;

		@BeforeEach

		void setUpGuestService() {

			encoder = new BCryptPasswordEncoder(12);

			ReflectionTestUtils.setField(guestService, "encoder", encoder);

		}
 
		@Test

		@DisplayName("GuestServiceImpl: Register new guest - Success")

		void testRegisterGuestSuccess() throws UserAlreadyExistsException {
 
			Guest newGuest = new Guest();

			newGuest.setGuestName("Test Guest");

			newGuest.setGuestUserName("testguestuser");

			newGuest.setGuestEmail("test@example.com");

			newGuest.setGuestContact("1234567890");

			newGuest.setGuestPassword("password");

			when(guestRepository.findByGuestUserName(anyString())).thenReturn(Optional.empty());

			when(guestRepository.findByGuestEmail(anyString())).thenReturn(Optional.empty());
 
			when(guestRepository.findByGuestContact(anyString())).thenReturn(Optional.empty());

			when(guestRepository.save(any(Guest.class))).thenAnswer(invocation -> {

				Guest savedGuest = invocation.getArgument(0);

				savedGuest.setGuestId(1); // Simulate ID being set upon saving

				return savedGuest;
 
			});
 
			Guest registeredGuest = guestService.register(newGuest);

			assertNotNull(registeredGuest);

			assertEquals("Test Guest", registeredGuest.getGuestName());

			assertTrue(encoder.matches("password", registeredGuest.getGuestPassword()));

			assertEquals(Role.GUEST, registeredGuest.getRole());

			verify(guestRepository, times(1)).save(any(Guest.class));
 
		}
 
		@Test

		@DisplayName("GuestServiceImpl: Register new guest - User already exists")

		void testRegisterGuestUserAlreadyExists() {

			Guest newGuest = new Guest();

			newGuest.setGuestUserName("existinguser");

			when(guestRepository.findByGuestUserName(anyString())).thenReturn(Optional.of(new Guest()));

			assertThrows(UserAlreadyExistsException.class, () -> guestService.register(newGuest));

			verify(guestRepository, never()).save(any(Guest.class));

		}
 
		@Test

		@DisplayName("GuestServiceImpl: Delete guest - Success")

		void testDeleteGuestSuccess() throws UserNotFoundException {

			String guestName = "GuestToDelete";

			Guest guest = new Guest();

			guest.setGuestName(guestName);

			when(guestRepository.findByGuestName(guestName)).thenReturn(Optional.of(guest));

			doNothing().when(guestRepository).delete(any(Guest.class));

			guestService.deleteGuest(guestName);

			verify(guestRepository, times(1)).delete(guest);

		}

		@Test

		@DisplayName("GuestServiceImpl: Delete guest - User not found")

		void testDeleteGuestUserNotFound() {

			String guestName = "NonExistentGuest";

			when(guestRepository.findByGuestName(guestName)).thenReturn(Optional.empty());

			assertThrows(UserNotFoundException.class, () -> guestService.deleteGuest(guestName));

			verify(guestRepository, never()).save(any(Guest.class));

		}
 
		@Test

		@DisplayName("GuestServiceImpl: View guest by name - Success")

		void testViewGuestByNameSuccess() throws UserNotFoundException {

			String guestName = "ViewableGuest";

			Guest guest = new Guest();

			guest.setGuestId(1);

			guest.setGuestName(guestName);

			guest.setGuestEmail("view@example.com");

			guest.setGuestContact("9876543210");

			when(guestRepository.findByGuestName(guestName)).thenReturn(Optional.of(guest));

			UserRepresentationDTO result = guestService.viewGuest(guestName);

			assertNotNull(result);

			assertEquals(1, result.getId());

			assertEquals(guestName, result.getName());

			assertEquals("view@example.com", result.getEmail());

			assertEquals("9876543210", result.getContact());

		}
 
		@Test

		@DisplayName("GuestServiceImpl: View guest by name - User not found")

		void testViewGuestByNameNotFound() {

			String guestName = "NonExistentGuest";

			when(guestRepository.findByGuestName(guestName)).thenReturn(Optional.empty());

			assertThrows(UserNotFoundException.class, () -> guestService.viewGuest(guestName));

		}
 
		@Test

		@DisplayName("GuestServiceImpl: Guest profile by username - Success")

		void testGuestProfileByUsernameSuccess() throws UserNotFoundException {

			String guestUserName = "profileuser";

			Guest guest = new Guest();

			guest.setGuestId(1);

			guest.setGuestName("Profile Guest");

			guest.setGuestUserName(guestUserName);

			guest.setGuestEmail("profile@example.com");

			guest.setGuestContact("1122334455");

			Hotel hotel = new Hotel();

			hotel.setHotelId(10);

			guest.setHotelBooked(hotel);

			// Correctly initialize Booking objects with setters

			Booking booking1 = new Booking();

			booking1.setBookingId(101);

			booking1.setRoomType("Deluxe");
 
			// If 'guest' or 'room' fields in Booking are non-nullable in your model, set them here.
 
			// booking1.setGuest(guest);
 
			// booking1.setRoom(new Room());

			Booking booking2 = new Booking();

			booking2.setBookingId(102);

			booking2.setRoomType("Standard");

			guest.setBookings(Arrays.asList(booking1, booking2));

			// Correctly initialize Payment objects with setters

			Payment payment1 = new Payment();

			payment1.setPaymentId(201);

			payment1.setAmount(100.0);

			// payment1.setGuest(guest);
 
			Payment payment2 = new Payment();

			payment2.setPaymentId(202);

			payment2.setAmount(150.0);

			guest.setPayments(Arrays.asList(payment1, payment2));

			when(guestRepository.findByGuestUserName(guestUserName)).thenReturn(Optional.of(guest));

			GuestDTO result = guestService.guestProfile(guestUserName);

			assertNotNull(result);
 
			assertEquals(1, result.getGuestId());

			assertEquals("Profile Guest", result.getGuestName());

			assertEquals(guestUserName, result.getGuestUserName());

			assertEquals("profile@example.com", result.getGuestEmail());

			assertEquals("1122334455", result.getGuestContact());

			assertEquals(10, result.getHotelId());

			assertEquals(2, result.getBookingHistory().size());

			assertEquals(101, result.getBookingHistory().get(0).getBookingId());

			assertEquals(2, result.getPaymentHistory().size());

			assertEquals(201, result.getPaymentHistory().get(0).getPaymentId());
 
		}

 
@Test

		@DisplayName("GuestServiceImpl: Guest profile by username - User not found")

		void testGuestProfileByUsernameNotFound() {

			String guestUserName = "nonexistentprofile";

			when(guestRepository.findByGuestUserName(guestUserName)).thenReturn(Optional.empty());
 
			assertThrows(UserNotFoundException.class, () -> guestService.guestProfile(guestUserName));

		}
 
		@Test

		@DisplayName("GuestServiceImpl: Update guest by name - Success")

		void testUpdateGuestByNameSuccess() throws UserNotFoundException {

			String guestName = "GuestToUpdate";

			Guest existingGuest = new Guest();

			existingGuest.setGuestId(1);

			existingGuest.setGuestName(guestName);

			existingGuest.setGuestEmail("old@example.com");

			existingGuest.setGuestContact("1111111111");
 
			UserUpdateDTO updateDTO = new UserUpdateDTO();

			updateDTO.setName("Updated Guest");

			updateDTO.setEmail("new@example.com");

			updateDTO.setContact("2222222222");
 
			when(guestRepository.findByGuestName(guestName)).thenReturn(Optional.of(existingGuest));

			when(guestRepository.save(any(Guest.class))).thenAnswer(invocation -> invocation.getArgument(0));
 
			Guest updatedGuest = guestService.updateGuest(guestName, updateDTO);
 
			assertNotNull(updatedGuest);

			assertEquals("Updated Guest", updatedGuest.getGuestName());

			assertEquals("new@example.com", updatedGuest.getGuestEmail());

			assertEquals("2222222222", updatedGuest.getGuestContact());

			verify(guestRepository, times(1)).save(existingGuest);

		}
 
		@Test

		@DisplayName("GuestServiceImpl: Update guest by name - User not found")

		void testUpdateGuestByNameNotFound() {

			String guestName = "NonExistentGuest";

			UserUpdateDTO updateDTO = new UserUpdateDTO();

			when(guestRepository.findByGuestName(guestName)).thenReturn(Optional.empty());
 
			assertThrows(UserNotFoundException.class, () -> guestService.updateGuest(guestName, updateDTO));

			verify(guestRepository, never()).save(any(Guest.class));

		}
 
		@Test

		@DisplayName("GuestServiceImpl: Update guest by ID - Success")

		void testUpdateGuestByIdSuccess() {

			Integer guestId = 1;

			Guest existingGuest = new Guest();

			existingGuest.setGuestId(guestId);

			existingGuest.setGuestName("Original Name");

			existingGuest.setGuestEmail("original@example.com");

			existingGuest.setGuestContact("111");
 
			UserUpdateDTO updateDTO = new UserUpdateDTO();

			updateDTO.setName("Updated Name");

			updateDTO.setEmail("updated@example.com");

			updateDTO.setContact("222");
 
			when(guestRepository.findById(guestId)).thenReturn(Optional.of(existingGuest));

			when(guestRepository.findByGuestEmail(updateDTO.getEmail())).thenReturn(Optional.empty()); // No conflict

			when(guestRepository.save(any(Guest.class))).thenAnswer(invocation -> invocation.getArgument(0));
 
			assertDoesNotThrow(() -> guestService.updateGuestById(guestId, updateDTO));
 
			assertEquals("Updated Name", existingGuest.getGuestName());

			assertEquals("updated@example.com", existingGuest.getGuestEmail());

			assertEquals("222", existingGuest.getGuestContact());

			verify(guestRepository, times(1)).save(existingGuest);

		}
 
		@Test

		@DisplayName("GuestServiceImpl: Update guest by ID - Guest not found")

		void testUpdateGuestByIdNotFound() {

			Integer guestId = 99;

			UserUpdateDTO updateDTO = new UserUpdateDTO();
 
			when(guestRepository.findById(guestId)).thenReturn(Optional.empty());
 
			RuntimeException thrown = assertThrows(RuntimeException.class, () -> guestService.updateGuestById(guestId, updateDTO));

			assertEquals("Guest not found with ID: " + guestId, thrown.getMessage());

			verify(guestRepository, never()).save(any(Guest.class));

		}
 
		@Test

		@DisplayName("GuestServiceImpl: Update guest by ID - Email already in use")

		void testUpdateGuestByIdEmailAlreadyInUse() {

			Integer guestId = 1;

			Guest existingGuest = new Guest();

			existingGuest.setGuestId(guestId);

			existingGuest.setGuestEmail("original@example.com");
 
			UserUpdateDTO updateDTO = new UserUpdateDTO();

			updateDTO.setEmail("taken@example.com");
 
			Guest otherGuestWithSameEmail = new Guest();

			otherGuestWithSameEmail.setGuestId(2);

			otherGuestWithSameEmail.setGuestEmail("taken@example.com");
 
			when(guestRepository.findById(guestId)).thenReturn(Optional.of(existingGuest));

			when(guestRepository.findByGuestEmail(updateDTO.getEmail())).thenReturn(Optional.of(otherGuestWithSameEmail));
 
			RuntimeException thrown = assertThrows(RuntimeException.class, () -> guestService.updateGuestById(guestId, updateDTO));

			assertEquals("Email 'taken@example.com' is already in use by another guest.", thrown.getMessage());

			verify(guestRepository, never()).save(any(Guest.class));

		}
 
		// --- ManagerServiceImpl Tests ---

		@Mock

		private ManagerRepository managerRepository;

		@Mock

		private HotelRepository hotelRepository;
 
		@InjectMocks

		private ManagerServiceImpl managerService;
 
		@BeforeEach

		void setUpManagerService() {

			MockitoAnnotations.openMocks(this);			ReflectionTestUtils.setField(managerService, "encoder", encoder);

		}
 
 
		@Test

		@DisplayName("ManagerServiceImpl: Add hotel manager - Manager already exists")

		void testAddHotelManagerAlreadyExists() {

			Manager newManager = new Manager();

			newManager.setManagerName("Existing Manager");

			when(managerRepository.findByManagerName(anyString())).thenReturn(Optional.of(new Manager()));
 
			assertThrows(UserAlreadyExistsException.class, () -> managerService.addHotelManager(newManager));

			verify(managerRepository, never()).save(any(Manager.class));

		}
 
		@Test

		@DisplayName("ManagerServiceImpl: Delete manager - Success with associated hotel")

		void testDeleteManagerSuccessWithHotel() throws ManagerNotFoundException {

			int managerId = 1;

			Manager managerToDelete = new Manager();

			managerToDelete.setManagerId(managerId);

			Hotel associatedHotel = new Hotel();

			associatedHotel.setHotelId(10);

			associatedHotel.setManager(managerToDelete);

			managerToDelete.setHotel(associatedHotel); // Set the hotel on the manager as well
 
			when(managerRepository.findById(managerId)).thenReturn(Optional.of(managerToDelete));

			when(hotelRepository.save(any(Hotel.class))).thenAnswer(invocation -> invocation.getArgument(0));

			doNothing().when(managerRepository).deleteById(managerId);
 
			managerService.deleteManager(managerId);
 
			assertNull(associatedHotel.getManager());

			verify(hotelRepository, times(1)).save(associatedHotel);

			verify(managerRepository, times(1)).deleteById(managerId);

		}
 
 
		@Test

		@DisplayName("ManagerServiceImpl: Delete manager - Success without associated hotel")

		void testDeleteManagerSuccessWithoutHotel() throws ManagerNotFoundException {

			int managerId = 1;

			Manager managerToDelete = new Manager();

			managerToDelete.setManagerId(managerId);

			managerToDelete.setHotel(null); // No associated hotel
 
			when(managerRepository.findById(managerId)).thenReturn(Optional.of(managerToDelete));

			doNothing().when(managerRepository).deleteById(managerId);
 
			managerService.deleteManager(managerId);
 
			verify(hotelRepository, never()).save(any(Hotel.class)); // Should not save hotel

			verify(managerRepository, times(1)).deleteById(managerId);

		}
 
		@Test

		@DisplayName("ManagerServiceImpl: Delete manager - Manager not found")

		void testDeleteManagerNotFound() {

			int managerId = 99;

			when(managerRepository.findById(managerId)).thenReturn(Optional.empty());
 
			assertThrows(ManagerNotFoundException.class, () -> managerService.deleteManager(managerId));

			verify(managerRepository, never()).deleteById(anyInt());

			verify(hotelRepository, never()).save(any(Hotel.class));

		}
 
		@Test

		@DisplayName("ManagerServiceImpl: Update manager - Success")

		void testUpdateManagerSuccess() throws UserNotFoundException {

			int managerId = 1;

			Manager existingManager = new Manager();

			existingManager.setManagerId(managerId);

			existingManager.setManagerName("Old Manager Name");

			existingManager.setManagerEmail("old@manager.com");

			existingManager.setManagerContact("000");
 
			UserUpdateDTO updateDTO = new UserUpdateDTO();

			updateDTO.setName("New Manager Name");

			updateDTO.setEmail("new@manager.com");

			updateDTO.setContact("999");
 
			when(managerRepository.findById(managerId)).thenReturn(Optional.of(existingManager));

			when(managerRepository.save(any(Manager.class))).thenAnswer(invocation -> invocation.getArgument(0));
 
			Manager updatedManager = managerService.updateManager(managerId, updateDTO);
 
			assertNotNull(updatedManager);

			assertEquals("New Manager Name", updatedManager.getManagerName());

			assertEquals("new@manager.com", updatedManager.getManagerEmail());

			assertEquals("999", updatedManager.getManagerContact());

			verify(managerRepository, times(1)).save(existingManager);

		}

 


		@Test

		@DisplayName("ManagerServiceImpl: Update manager - Manager not found")

		void testUpdateManagerNotFound() {

			int managerId = 99;

			UserUpdateDTO updateDTO = new UserUpdateDTO();

			when(managerRepository.findById(managerId)).thenReturn(Optional.empty());
 
			assertThrows(UserNotFoundException.class, () -> managerService.updateManager(managerId, updateDTO));

			verify(managerRepository, never()).save(any(Manager.class));

		}
 
		@Test

		@DisplayName("ManagerServiceImpl: View hotel manager - Success")

		void testViewHotelManagerSuccess() throws ManagerNotFoundException {

			int managerId = 1;

			Manager manager = new Manager();

			manager.setManagerId(managerId);

			manager.setManagerName("Manager One");

			manager.setManagerEmail("manager1@example.com");

			manager.setManagerContact("12345");
 
			when(managerRepository.findById(managerId)).thenReturn(Optional.of(manager));
 
			UserRepresentationDTO result = managerService.viewHotelManager(managerId);
 
			assertNotNull(result);

			assertEquals(managerId, result.getId());

			assertEquals("Manager One", result.getName());

			assertEquals("manager1@example.com", result.getEmail());

			assertEquals("12345", result.getContact());

		}
 
		@Test

		@DisplayName("ManagerServiceImpl: View hotel manager - Manager not found")

		void testViewHotelManagerNotFound() {

			int managerId = 99;

			when(managerRepository.findById(managerId)).thenReturn(Optional.empty());
 
			assertThrows(ManagerNotFoundException.class, () -> managerService.viewHotelManager(managerId));

		}
 
		@Test

		@DisplayName("ManagerServiceImpl: Get hotel by manager ID - Success")

		void testGetHotelByManagerIdSuccess() throws ManagerNotFoundException {

			int managerId = 1;

			Manager manager = new Manager();

			manager.setManagerId(managerId);

			Hotel hotel = new Hotel();

			hotel.setHotelId(10);

			hotel.setHotelName("Grand Hotel");

			manager.setHotel(hotel);
 
			when(managerRepository.findById(managerId)).thenReturn(Optional.of(manager));
 
			Hotel resultHotel = managerService.getHotelByManagerId(managerId);
 
			assertNotNull(resultHotel);

			assertEquals(10, resultHotel.getHotelId());

			assertEquals("Grand Hotel", resultHotel.getHotelName());

		}
 
		@Test

		@DisplayName("ManagerServiceImpl: Get hotel by manager ID - Manager not found")

		void testGetHotelByManagerIdNotFound() {

			int managerId = 99;

			when(managerRepository.findById(managerId)).thenReturn(Optional.empty());
 
			assertThrows(ManagerNotFoundException.class, () -> managerService.getHotelByManagerId(managerId));

		}
 
		// --- AdminServiceImpl Tests ---

		@InjectMocks

		private AdminServiceImpl adminService;
 
		// guestRepository and managerRepository are already mocked above
 
		@Test

		@DisplayName("AdminServiceImpl: View all guests - Success")

		void testViewAllGuestsSuccess() {

			Guest guest1 = new Guest();

			guest1.setGuestId(1);

			guest1.setGuestName("Guest One");

			guest1.setGuestUserName("guestone");

			guest1.setGuestEmail("g1@example.com");

			guest1.setGuestContact("111");

			Hotel hotel1 = new Hotel();

			hotel1.setHotelId(10);

			guest1.setHotelBooked(hotel1);
 
			// Correctly initialize Booking and Payment objects with setters

			Booking booking1 = new Booking();

			booking1.setBookingId(101);

			booking1.setRoomType("Suite");

			// If 'guest' or 'room' fields in Booking are non-nullable, set them:

			// booking1.setGuest(guest1);

			// booking1.setRoom(new Room());
 
			Payment payment1 = new Payment();

			payment1.setPaymentId(201);

			payment1.setAmount(200.0);

			// If 'guest' field in Payment is non-nullable, set it:

			// payment1.setGuest(guest1);
 
			guest1.setBookings(Collections.singletonList(booking1));

			guest1.setPayments(Collections.singletonList(payment1));
 
 
			Guest guest2 = new Guest();

			guest2.setGuestId(2);

			guest2.setGuestName("Guest Two");

			guest2.setGuestUserName("guesttwo");

			guest2.setGuestEmail("g2@example.com");

			guest2.setGuestContact("222");

			guest2.setHotelBooked(null);

			guest2.setBookings(Collections.emptyList());

			guest2.setPayments(Collections.emptyList());
 
			when(guestRepository.findAll()).thenReturn(Arrays.asList(guest1, guest2));

			List<GuestDTO> guests = adminService.viewAllGuests();
 
			assertNotNull(guests);

			assertEquals(2, guests.size());
 
			GuestDTO dto1 = guests.get(0);

			// Corrected assertions to match GuestDTO's actual getter names

			assertEquals(1, dto1.getGuestId());

			assertEquals("Guest One", dto1.getGuestName());

			assertEquals("guestone", dto1.getGuestUserName());

			assertEquals("g1@example.com", dto1.getGuestEmail());

			assertEquals("111", dto1.getGuestContact());

			assertEquals(10, dto1.getHotelId());

			assertEquals(1, dto1.getBookingHistory().size());

			assertEquals(101, dto1.getBookingHistory().get(0).getBookingId());

			assertEquals("Suite", dto1.getBookingHistory().get(0).getRoomType());

			assertEquals(1, dto1.getPaymentHistory().size());

			assertEquals(201, dto1.getPaymentHistory().get(0).getPaymentId());

			assertEquals(200.0, dto1.getPaymentHistory().get(0).getAmount());
 
 
			GuestDTO dto2 = guests.get(1);

			// Corrected assertions to match GuestDTO's actual getter names

			assertEquals(2, dto2.getGuestId());

			assertEquals("Guest Two", dto2.getGuestName());

			assertEquals("guesttwo", dto2.getGuestUserName());

			assertEquals("g2@example.com", dto2.getGuestEmail());

			assertEquals("222", dto2.getGuestContact());

			assertNull(dto2.getHotelId());

			assertTrue(dto2.getBookingHistory().isEmpty());

			assertTrue(dto2.getPaymentHistory().isEmpty());
 
			verify(guestRepository, times(1)).findAll();

		}
 
 
		@Test

		@DisplayName("AdminServiceImpl: View all managers - Success")

		void testViewAllManagersSuccess() {

			Manager manager1 = new Manager();

			manager1.setManagerId(1);

			manager1.setManagerName("Manager A");

			Manager manager2 = new Manager();

			manager2.setManagerId(2);

			manager2.setManagerName("Manager B");
 
			when(managerRepository.findAll()).thenReturn(Arrays.asList(manager1, manager2));
 
			List<Manager> managers = adminService.viewAllManagers();
 
			assertNotNull(managers);

			assertEquals(2, managers.size());

			assertEquals("Manager A", managers.get(0).getManagerName());

			assertEquals("Manager B", managers.get(1).getManagerName());

			verify(managerRepository, times(1)).findAll();

		}
 
		// --- JWTServiceImpl Tests ---

		@InjectMocks

		private JWTServiceImpl jwtService;
 
		private String testSecretKey;
 
		@BeforeEach

		void setUpJwtService() {

			try {

				KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");

				SecretKey sk = keyGen.generateKey();

				testSecretKey = Base64.getEncoder().encodeToString(sk.getEncoded());

				ReflectionTestUtils.setField(jwtService, "secretKey", testSecretKey);

			} catch (NoSuchAlgorithmException e) {

				fail("Failed to generate secret key for JWT tests: " + e.getMessage());

			}

		}

 
@Test

		@DisplayName("JWTServiceImpl: Generate token - Success")

		void testGenerateTokenSuccess() {

			UserDetails userDetails = User.withUsername("testuser")

					.password("encodedpassword")

					.roles("GUEST")

					.build();

			String userId = "user123";

			String role = "GUEST";
 
			String token = jwtService.generateToken(userDetails, userId, role);
 
			assertNotNull(token);

			assertFalse(token.isEmpty());
 
			Claims claims = jwtService.extractAllClaims(token);

			assertEquals("testuser", claims.getSubject());

			assertEquals(userId, claims.get("userId"));

			assertEquals(role, claims.get("role"));

			List<String> roles = claims.get("roles", List.class);

			assertNotNull(roles);

			assertTrue(roles.contains("ROLE_GUEST"));

			assertNotNull(claims.getIssuedAt());

			assertNotNull(claims.getExpiration());

		}
 
		@Test

		@DisplayName("JWTServiceImpl: Extract username from token")

		void testExtractUserName() {

			UserDetails userDetails = User.withUsername("extractuser")

					.password("pass")

					.roles("USER")

					.build();

			String token = jwtService.generateToken(userDetails, "u456", "USER");
 
			String username = jwtService.extractUserName(token);

			assertEquals("extractuser", username);

		}
 
		@Test

		@DisplayName("JWTServiceImpl: Extract roles from token")

		void testExtractRoles() {

			UserDetails userDetails = User.withUsername("roleuser")

					.password("pass")

					.roles("ADMIN", "STAFF")

					.build();

			String token = jwtService.generateToken(userDetails, "u789", "ADMIN");
 
			List<String> roles = jwtService.extractRoles(token);

			assertNotNull(roles);

			assertEquals(2, roles.size());

			assertTrue(roles.contains("ROLE_ADMIN"));

			assertTrue(roles.contains("ROLE_STAFF"));

		}
 
		@Test

		@DisplayName("JWTServiceImpl: Validate token - Valid token")

		void testValidateTokenValid() {

			UserDetails userDetails = User.withUsername("validuser")

					.password("pass")

					.roles("GUEST")

					.build();

			String token = jwtService.generateToken(userDetails, "v1", "GUEST");
 
			assertTrue(jwtService.validateToken(token, userDetails));

		}
 
		@Test

		@DisplayName("JWTServiceImpl: Validate token - Invalid username")

		void testValidateTokenInvalidUsername() {

			UserDetails userDetails = User.withUsername("validuser")

					.password("pass")

					.roles("GUEST")

					.build();

			String token = jwtService.generateToken(userDetails, "v1", "GUEST");
 
			UserDetails wrongUserDetails = User.withUsername("wronguser")

					.password("pass")

					.roles("GUEST")

					.build();
 
			assertFalse(jwtService.validateToken(token, wrongUserDetails));

		}
 
 
 
		// --- GuestUserDetailsService Tests ---

		@InjectMocks

		private GuestUserDetailsService guestUserDetailsService;

		// guestRepository is already mocked above
 
		@Test

		@DisplayName("GuestUserDetailsService: Load user by username - Guest found")

		void testLoadGuestByUsernameFound() {

			String username = "guestuser";

			Guest guest = new Guest();

			guest.setGuestUserName(username);

			guest.setGuestPassword("encodedGuestPass");

			guest.setRole(Role.GUEST);
 
			when(guestRepository.findByGuestUserName(username)).thenReturn(Optional.of(guest));
 
			UserDetails loadedUserDetails = guestUserDetailsService.loadUserByUsername(username);
 
			assertNotNull(loadedUserDetails);

			assertEquals(username, loadedUserDetails.getUsername());

			assertEquals("encodedGuestPass", loadedUserDetails.getPassword());

			assertEquals(1, loadedUserDetails.getAuthorities().size());

			assertTrue(loadedUserDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_GUEST")));

		}
 
		@Test

		@DisplayName("GuestUserDetailsService: Load user by username - Guest not found")

		void testLoadGuestByUsernameNotFound() {

			String username = "nonexistentguest";

			when(guestRepository.findByGuestUserName(username)).thenReturn(Optional.empty());
 
			UsernameNotFoundException thrown = assertThrows(UsernameNotFoundException.class, () -> guestUserDetailsService.loadUserByUsername(username));

			assertEquals("Guest user not found with username: " + username, thrown.getMessage());

		}
 
		// --- ManagerUserDetailsService Tests ---

		@InjectMocks

		private ManagerUserDetailsService managerUserDetailsService;

		// managerRepository is already mocked above
 
		@Test

		@DisplayName("ManagerUserDetailsService: Load user by username - Manager found")

		void testLoadManagerByUsernameFound() {

			String username = "manageruser";

			Manager manager = new Manager();

			manager.setManagerName(username); // ManagerUserDetailsService uses managerName for username

			manager.setManagerPassword("encodedManagerPass");

			manager.setRole(Role.MANAGER);
 
			when(managerRepository.findByManagerName(username)).thenReturn(Optional.of(manager));
 
			UserDetails loadedUserDetails = managerUserDetailsService.loadUserByUsername(username);
 
			assertNotNull(loadedUserDetails);

			assertEquals(username, loadedUserDetails.getUsername());

			assertEquals("encodedManagerPass", loadedUserDetails.getPassword());

			assertEquals(1, loadedUserDetails.getAuthorities().size());

			assertTrue(loadedUserDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_MANAGER")));

		}
 
 
		// --- AdminUserDetailsService Tests ---

		@Mock

		private InMemoryUserDetailsManager inMemoryUserDetailsManager;
 
		@InjectMocks

		private AdminUserDetailsService adminUserDetailsService;
 
		@Test

		@DisplayName("AdminUserDetailsService: Load user by username - Admin found in in-memory manager")

		void testLoadAdminByUsernameFound() {

			String username = "adminuser";

			UserDetails adminUserDetails = User.withUsername(username)

					.password("encodedAdminPass")

					.roles("ADMIN")

					.build();
 
			when(inMemoryUserDetailsManager.loadUserByUsername(username)).thenReturn(adminUserDetails);
 
			UserDetails loadedUserDetails = adminUserDetailsService.loadUserByUsername(username);
 
			assertNotNull(loadedUserDetails);

			assertEquals(username, loadedUserDetails.getUsername());

			assertEquals("encodedAdminPass", loadedUserDetails.getPassword());

			assertEquals(1, loadedUserDetails.getAuthorities().size());

			assertTrue(loadedUserDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));

		}
 
		@Test

		@DisplayName("AdminUserDetailsService: Load user by username - Admin not found in in-memory manager")

		void testLoadAdminByUsernameNotFound() {

			String username = "nonexistentadmin";

			when(inMemoryUserDetailsManager.loadUserByUsername(username)).thenThrow(new UsernameNotFoundException("Admin not found"));
 
			UsernameNotFoundException thrown = assertThrows(UsernameNotFoundException.class, () -> adminUserDetailsService.loadUserByUsername(username));

			assertEquals("Admin user not found with username: " + username, thrown.getMessage());

		}

	}

 