import React from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faEdit, faTrash } from '@fortawesome/free-solid-svg-icons';

const RoomList = ({ rooms, roomsLoading, roomsError, hotelName, handleSelectRoomForUpdate, handleDeleteRoom }) => {
    return (
        <div className="bg-white shadow rounded-md p-6">
            <h2 className="text-xl font-semibold text-gray-800 mb-4">Rooms in {hotelName}</h2>
            {roomsLoading && <p className="text-indigo-700 italic">Loading rooms...</p>}
            {roomsError && <p className="text-red-700">Error loading rooms: {roomsError}</p>}
            {rooms.length > 0 ? (
                <div className="overflow-x-auto">
                    <table className="min-w-full leading-normal">
                        <thead className="bg-gray-100">
                            <tr>
                                
                                <th className="px-5 py-3 border-b-2 border-gray-200 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">Type</th>
                                <th className="px-5 py-3 border-b-2 border-gray-200 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">Price</th>
                                <th className="px-5 py-3 border-b-2 border-gray-200 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">Features</th>
                                <th className="px-5 py-3 border-b-2 border-gray-200 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">Image</th> 
                                <th className="px-5 py-3 border-b-2 border-gray-200 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">Availability</th>
                                <th className="px-5 py-3 border-b-2 border-gray-200 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">Actions</th>
                            </tr>
                        </thead>
                        <tbody className="bg-white">
                            {rooms.map(room => (
                                <tr key={room.roomId}>
                                  
                                    <td className="px-5 py-5 border-b border-gray-200 text-sm">{room.type}</td>
                                    <td className="px-5 py-5 border-b border-gray-200 text-sm">₹{room.price?.toFixed(2) || 'N/A'}</td>
                                    <td className="px-5 py-5 border-b border-gray-200 text-sm">{room.features || 'N/A'}</td>
                                    <td className="px-5 py-5 border-b border-gray-200 text-sm">
                                        {room.imageUrl ? (
                                            <img
                                                src={room.imageUrl}
                                                alt={`Room ${room.roomId}`}
                                                className="w-16 h-16 object-cover rounded-md" 
                                            />
                                        ) : (
                                            <span className="text-gray-500">No Image</span>
                                        )}
                                    </td>
                                    {/* End new table data for Image */}
                                    <td className="px-5 py-5 border-b border-gray-200 text-sm">{room.availability}</td>
                                    <td className="px-5 py-5 border-b border-gray-200 text-sm">
                                        <button
                                            onClick={() => handleSelectRoomForUpdate(room)}
                                            className="text-blue-500 hover:text-blue-700 mr-2"
                                            title="Update Room"
                                        >
                                            <FontAwesomeIcon icon={faEdit} />
                                        </button>
                                        <button
                                            onClick={() => handleDeleteRoom(room.roomId)}
                                            className="text-red-500 hover:text-red-700"
                                            title="Delete Room"
                                        >
                                            <FontAwesomeIcon icon={faTrash} />
                                        </button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            ) : (
                !roomsLoading && !roomsError && <p className="text-gray-600 italic">No rooms found for this hotel.</p>
            )}
        </div>
    );
};

export default RoomList;