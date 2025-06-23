import React from 'react';
import hotelService from '../services/HotelService';
import { FaTrash, FaEdit } from 'react-icons/fa'; 

const HotelList = ({ hotels, fetchHotels, setMessage, setError, onEdit }) => { 
    const handleDelete = async (hotelId, hotelName) => {
        if (window.confirm(`Are you sure you want to delete hotel "${hotelName}" (ID: ${hotelId})? This will also unassign any manager.`)) {
            setMessage('');
            setError('');
            try {
                await hotelService.deleteHotel(hotelId);
                setMessage(`Hotel "${hotelName}" deleted successfully!`);
                fetchHotels(); 
            } catch (err) {
                console.error('Failed to delete hotel:', err);
                setError(err.response ? err.response.data : err.message || 'Failed to delete hotel.');
            }
        }
    };

    
    const handleEdit = (hotel) => {
        if (onEdit) {
            onEdit(hotel); 
        }
    };

    return (
        <div className="bg-white p-8 rounded-lg shadow-md w-full">
            <h2 className="text-2xl font-bold text-gray-800 mb-6 text-center">Added Hotels</h2>
            {hotels.length > 0 ? (
                <div className="overflow-x-auto">
                    <table className="min-w-full leading-normal">
                        <thead>
                            <tr className="bg-gray-200 text-gray-600 uppercase text-sm leading-normal">
                                <th className="py-3 px-6 text-left">Hotel ID</th>
                                <th className="py-3 px-6 text-left">Name</th>
                                <th className="py-3 px-6 text-left">Location</th>
                                <th className="py-3 px-6 text-left">Amenities</th>
                                <th className="py-3 px-6 text-left">Image</th> 
                                <th className="py-3 px-6 text-left">Assigned Manager</th>
                                <th className="py-3 px-6 text-center">Actions</th>
                            </tr>
                        </thead>
                        <tbody className="text-gray-700 text-sm">
                            {hotels.map((hotel) => (
                                <tr key={hotel.hotelId} className="border-b border-gray-200 hover:bg-gray-100">
                                    <td className="py-3 px-6 text-left whitespace-nowrap">{hotel.hotelId}</td>
                                    <td className="py-3 px-6 text-left">{hotel.hotelName}</td>
                                    <td className="py-3 px-6 text-left">{hotel.location}</td>
                                    <td className="py-3 px-6 text-left">{hotel.amenities || 'N/A'}</td>
                                    <td className="py-3 px-6 text-left">
                                        {hotel.imageUrl ? (
                                            <img
                                                src={hotel.imageUrl}
                                                alt={hotel.hotelName}
                                                className="w-16 h-16 object-cover rounded-md"
                                            />
                                        ) : (
                                            'No Image'
                                        )}
                                    </td>
                                    <td className="py-3 px-6 text-left">
                                        {hotel.manager ? `${hotel.manager.managerName} (ID: ${hotel.manager.managerId})` : 'Not Assigned'}
                                    </td>
                                    <td className="py-3 px-6 text-center">
                                        <div className="flex justify-center items-center space-x-3">
                                            {/* Update Icon */}
                                            <button
                                                onClick={() => handleEdit(hotel)}
                                                className="text-blue-500 hover:text-blue-700 transition-colors duration-200"
                                                title="Edit Hotel"
                                            >
                                                <FaEdit size={20} />
                                            </button>
                                            {/* Delete Icon */}
                                            <button
                                                onClick={() => handleDelete(hotel.hotelId, hotel.hotelName)}
                                                className="text-red-500 hover:text-red-700 transition-colors duration-200"
                                                title="Delete Hotel"
                                            >
                                                <FaTrash size={20} />
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            ) : (
                <p className="text-center text-gray-600 py-4">No hotels added yet.</p>
            )}
        </div>
    );
};

export default HotelList;