package com.hotel.Booking_System.dto.roomdto;

import com.hotel.Booking_System.model.enums.RoomType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class  RoomResponseDTO {
    private Integer roomId;
    private RoomType type;
    private Double price;
    private Integer availability;
    private String features;
    private String imageUrl;
    private Integer hotelId;
    private String hotelName;
}
