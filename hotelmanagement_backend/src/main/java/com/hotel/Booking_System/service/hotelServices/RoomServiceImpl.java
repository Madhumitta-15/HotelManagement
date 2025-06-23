package com.hotel.Booking_System.service.hotelServices;

import com.hotel.Booking_System.dto.roomdto.RoomResponseDTO;
import com.hotel.Booking_System.exception.ResourceNotFoundException;
import com.hotel.Booking_System.exception.RoomUnavailableException;
import com.hotel.Booking_System.model.Hotel;
import com.hotel.Booking_System.model.Room;
import com.hotel.Booking_System.model.enums.RoomType;
import com.hotel.Booking_System.repository.hotelRepo.HotelRepository;
import com.hotel.Booking_System.repository.hotelRepo.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService{

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;

    //corrected
    private RoomResponseDTO mapRoomToResponseDTO(Room room) {
        if (room == null) {
            return null;
        }
        return RoomResponseDTO.builder()
                .roomId(room.getRoomId())
                .type(room.getType())
                .price(room.getPrice())
                .availability(room.getAvailability())
                .features(room.getFeatures())
                .imageUrl(room.getImageUrl())
                // .hotelId(room.getHotel() != null ? room.getHotel().getHotelId() : null) // Uncomment if needed
                // .hotelName(room.getHotel() != null ? room.getHotel().getHotelName() : null) // Uncomment if needed
                .build();
    }



    @Override
    public Optional<Room> getRoomById(Integer roomId) {

        return roomRepository.findById(roomId);
    }


    //adding room in the hotel
    @Override
    public Room addRoom(Integer hotelId, Room room) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + hotelId));

        Optional<Room> existingRoom = roomRepository.findByHotelAndTypeOrFeatures(hotel, room.getType(), room.getFeatures());

        if (existingRoom.isPresent()) {
            throw new RoomUnavailableException(
                    "Room with type '" + room.getType() + "' and features '" + room.getFeatures() + "' already exists in this hotel."
            );
        }
        room.setHotel(hotel);
        return roomRepository.save(room);
    }

    @Override
    public Room updateRoom(Integer roomId, Room room) {
        return roomRepository.findById(roomId)
                .map(existingRoom -> {
                    existingRoom.setType(room.getType());
                    existingRoom.setPrice(room.getPrice());
                    existingRoom.setAvailability(room.getAvailability());
                    existingRoom.setFeatures(room.getFeatures());
                    existingRoom.setImageUrl(room.getImageUrl());
                    return roomRepository.save(existingRoom);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + roomId));
    }

    @Override
    public void deleteRoom(Integer roomId) {
        if (!roomRepository.existsById(roomId)) {
            throw new ResourceNotFoundException("Room not found with id: " + roomId);
        }
        roomRepository.deleteById(roomId);
    }

    public List<Room> getRoomsByHotelAndType(Integer hotelId, RoomType type) {
        System.out.println("Filtering rooms for hotelId: " + hotelId + " and type: " + type);
        return roomRepository.findByHotel_HotelIdAndType(hotelId, type);
    }

    //corrected
    @Override
    public List<RoomResponseDTO> getAllRoomsByHotel(Integer hotelId) {

        List<Room>rooms=roomRepository.findByHotel_HotelId(hotelId);
        return rooms.stream()
                .map(this::mapRoomToResponseDTO)
                .collect(Collectors.toList());


    }
}
