import React from "react";
import { useNavigate } from "react-router-dom";

 function HotelCard({ hotel }) {
  const navigate = useNavigate();

  const handleBookNowClick = () => {
    navigate("/booking");
  };

  return (
    <div className="bg-white rounded-2xl overflow-hidden shadow hover:shadow-lg transition duration-300">
      <img
        src={hotel.image}
        alt={hotel.name}
        className="h-48 w-full object-cover"
      />
      <div className="p-4">
        <h3 className="text-lg font-semibold text-gray-800">{hotel.name}</h3>
        <p className="text-sm text-gray-500">{hotel.location}</p>
        <p className="text-sm text-gray-600 mt-2">{hotel.description}</p>
        <div className="mt-4 flex justify-between items-center">
          <span className="text-blue-600 font-bold">₹{hotel.price}/night</span>
          <button
            onClick={handleBookNowClick}
            className="text-sm text-white bg-blue-600 px-3 py-1 rounded-md hover:bg-blue-700"
          >
            Book Now
          </button>
        </div>
      </div>
    </div>
  );
}
export default HotelCard;
