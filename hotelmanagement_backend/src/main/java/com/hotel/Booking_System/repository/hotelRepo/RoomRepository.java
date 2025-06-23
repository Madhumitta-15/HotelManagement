package com.hotel.Booking_System.repository.hotelRepo;

import com.hotel.Booking_System.model.Hotel;
import com.hotel.Booking_System.model.Room;
import com.hotel.Booking_System.model.enums.RoomType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface RoomRepository extends JpaRepository<Room,Integer> {
    List<Room> findByHotel_HotelId(Integer hotelId);
    Optional<Room> findById(Integer id);
    List<Room> findByHotel_HotelIdAndType(Integer hotelId, RoomType type);

    Room save(Room room);

    @Query("SELECT r FROM Room r WHERE r.hotel = :hotel AND (r.type = :type OR r.features = :features)")
    Optional<Room> findByHotelAndTypeOrFeatures(@Param("hotel") Hotel hotel,
                                                    @Param("type") RoomType type,
                                                    @Param("features") String features);

//    Optional<Room> findByHotelAndTypeAndFeatures(Hotel hotel, RoomType type, String features);
}
