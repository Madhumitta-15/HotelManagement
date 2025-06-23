function Card({ hotel, onViewDetails }) {
  return (
    <div className="border rounded-lg overflow-hidden shadow-md bg-white">
      <img
        src={hotel.imageUrl || "https://source.unsplash.com/400x300/?hotel,bed"}
        alt={hotel.hotelName}
        className="w-full h-48 object-cover"
      />
      <div className="p-4">
        <div className="flex justify-between items-center mb-1">
          <h3 className="text-lg font-semibold text-gray-800">{hotel.hotelName}</h3>
          {hotel.rating !== undefined && (
            <p className="text-yellow-500 text-sm font-medium">⭐ {hotel.rating.toFixed(1)}/5</p>
          )}
        </div>

        <p className="text-gray-500">{hotel.location}</p>

        {hotel.price !== undefined && (
          <p className="mt-2 text-sm text-gray-700">₹{hotel.price} / night</p>
        )}

        <button
          onClick={() => onViewDetails(hotel.hotelId)}
          className="mt-4 w-full bg-blue-500 text-white py-2 px-4 rounded hover:bg-blue-600 transition-colors"
        >
          View Details
        </button>
      </div>
    </div>
  );
}

export default Card;
