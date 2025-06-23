import React from 'react';

function RoomCard({ room, isSelected, onSelect, isAvailable }) {
  const cardClasses = `
    border rounded-lg p-4 flex flex-col sm:flex-row items-center gap-4
    transition-all duration-200 ease-in-out
    ${isSelected ? 'border-blue-500 ring-2 ring-blue-200 shadow-md' : 'border-gray-200 hover:shadow-sm'}
    ${!isAvailable ? 'bg-gray-50 opacity-60 cursor-not-allowed' : 'bg-white cursor-pointer'}
  `;

  
  const defaultRoomImage = "https://placehold.co/400x300/e0e0e0/555555?text=Room+Image";

  return (
    <div className={cardClasses} onClick={isAvailable ? onSelect : undefined}>
      
      <div className="w-full sm:w-1/3 flex-shrink-0">
        <img
          src={room.imageUrl || defaultRoomImage}
          alt={room.type ? `${room.type} Room` : "Room Image"}
          className="w-full h-48 object-cover rounded-lg"
          onError={(e) => { e.target.onerror = null; e.target.src = defaultRoomImage; }} 
        />
      </div>

      {/* Room Details */}
      <div className="flex-1 text-center sm:text-left">
      
        <h3 className="text-2xl font-bold text-gray-900 mb-2">
          {room.type ? room.type.replace(/_/g, ' ') + ' Room' : 'Unknown Room Type'}
        </h3>
        {room.features && (
          <div className="mb-2 text-gray-700 text-sm">
            <h4 className="font-semibold text-gray-800 text-sm mb-1">Features:</h4>
            <p>{room.features}</p>
          </div>
        )}
        {room.availability !== null && room.availability !== undefined && (
          <p className="text-gray-600 text-sm mt-1">Total Rooms Available : {room.availability}</p>
        )}

      </div>

      {/* Price and Selection */}
      <div className="flex flex-col items-center sm:items-end mt-4 sm:mt-0">
        <p className="text-3xl font-bold text-gray-900 mb-2">₹{room.price}</p>
        {isAvailable ? (
          <span className="text-green-600 font-semibold text-sm mb-2">Available for Dates</span>
        ) : (
          <span className="text-red-500 font-semibold text-sm mb-2">Sold Out for Dates</span>
        )}
        
      </div>
    </div>
  );
}

export default RoomCard;