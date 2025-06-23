package com.hotel.Booking_System.service.hotelServices;

import com.hotel.Booking_System.dto.roomdto.RoomResponseDTO;
import com.hotel.Booking_System.model.Room;
import com.hotel.Booking_System.model.enums.RoomType;

import java.util.List;
import java.util.Optional;

public interface RoomService {
    List<RoomResponseDTO> getAllRoomsByHotel(Integer hotelId);
    Optional<Room> getRoomById(Integer roomId);
    Room addRoom(Integer hotelId, Room room);
    Room updateRoom(Integer roomId, Room room);
    void deleteRoom(Integer roomId);

    List<Room> getRoomsByHotelAndType(Integer hotelId, RoomType type);
}
