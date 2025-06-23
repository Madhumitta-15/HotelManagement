package com.hotel.Booking_System.controller.room;

import com.hotel.Booking_System.dto.roomdto.RoomResponseDTO;
import com.hotel.Booking_System.exception.ResourceNotFoundException;
import com.hotel.Booking_System.exception.RoomUnavailableException;
import com.hotel.Booking_System.model.Room;
import com.hotel.Booking_System.model.enums.RoomType;
import com.hotel.Booking_System.service.hotelServices.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("rooms")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;

    //add rooms
    @PostMapping("/addroom/hotel/{hotelId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> addRoom(@PathVariable Integer hotelId, @Valid @RequestBody Room room) {

        try {

            Room savedRoom = roomService.addRoom(hotelId, room);

            RoomResponseDTO responseDTO = new RoomResponseDTO();
            responseDTO.setRoomId(savedRoom.getRoomId());
            responseDTO.setType(savedRoom.getType());
            responseDTO.setPrice(savedRoom.getPrice());
            responseDTO.setAvailability(savedRoom.getAvailability());
            responseDTO.setFeatures(savedRoom.getFeatures());
            if (savedRoom.getHotel() != null) {
                responseDTO.setHotelId(savedRoom.getHotel().getHotelId());
                responseDTO.setHotelName(savedRoom.getHotel().getHotelName());
            }
            return ResponseEntity.status(HttpStatus.OK).body("Room Added successfully");
        } catch (RoomUnavailableException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Room with type " + room.getType() + " and features " + room.getFeatures() + " already exists in this hotel.");
        }
    }


    //get all rooms within the hotel
    @GetMapping("/getallrooms/{hotelId}")
    public ResponseEntity<List<RoomResponseDTO>> getAllRoomsByHotel(@PathVariable Integer hotelId) {
        return ResponseEntity.ok(roomService.getAllRoomsByHotel(hotelId));
    }

    //get room by id
    @GetMapping("/getroom/{roomId}")
    public ResponseEntity<Room> getRoomById(@PathVariable Integer roomId) {
        return roomService.getRoomById(roomId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    //updating room by room id

    @PutMapping("/updateroom/{roomId}")
    public ResponseEntity<String> updateRoom(@PathVariable Integer roomId, @Valid @RequestBody Room room) {
        try {
            Room updatedRoom = roomService.updateRoom(roomId, room);
            return ResponseEntity.status(HttpStatus.OK).body("room updated successfully");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("room id notfound");
        }
    }

    //delete room by room id
    @DeleteMapping("/deleteroom/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Integer roomId) {
        try {
            roomService.deleteRoom(roomId);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    //get room by type
    @GetMapping("/getroom/{hotelId}/by-type/{roomType}")
    public ResponseEntity<List<Room>> getRoomsByHotelAndType(
            @PathVariable Integer hotelId,
            @PathVariable RoomType roomType) {
        List<Room> rooms = roomService.getRoomsByHotelAndType(hotelId,roomType);
        if (rooms.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(rooms);
    }

}
