import React, { useState } from 'react';
import hotelService from '../services/HotelService';

const AssignManagerForm = ({ hotels, managers, onManagerAssigned, setMessage, setError }) => {
    const [selectedHotelId, setSelectedHotelId] = useState('');
    const [selectedManagerId, setSelectedManagerId] = useState('');

    const handleChange = (e) => {
        const { name, value } = e.target;
        if (name === "hotelId") {
            setSelectedHotelId(value);
        } else if (name === "managerId") {
            setSelectedManagerId(value);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage('');
        setError('');

        if (!selectedHotelId || !selectedManagerId) {
            setError('Please select both a hotel and a manager.');
            return;
        }

        try {
            await hotelService.assignManagerToHotel(selectedHotelId, selectedManagerId);
            setMessage('Manager assigned to hotel successfully!');
            setSelectedHotelId('');
            setSelectedManagerId('');
            onManagerAssigned(); 
        } catch (err) {
            console.error('Failed to assign manager:', err);
            setError(err.response ? err.response.data : err.message || 'Failed to assign manager.');
        }
    };

    return (
        <div className="bg-white p-8 rounded-lg shadow-md max-w-md w-full mx-auto">
            <h2 className="text-2xl font-bold text-gray-800 mb-6 text-center">Assign Manager to Hotel</h2>
            <form onSubmit={handleSubmit} className="space-y-4">
                <div>
                    <label htmlFor="hotelId" className="block text-gray-700 text-sm font-bold mb-2">Select Hotel:</label>
                    <select
                        id="hotelId"
                        name="hotelId"
                        value={selectedHotelId}
                        onChange={handleChange}
                        className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:border-transparent"
                        required
                    >
                        <option value="">-- Select a Hotel --</option>
                        {hotels.map((hotel) => (
                            <option key={hotel.hotelId} value={hotel.hotelId}>
                                {hotel.hotelName} ({hotel.location})
                            </option>
                        ))}
                    </select>
                </div>

                <div>
                    <label htmlFor="managerId" className="block text-gray-700 text-sm font-bold mb-2">Select Manager:</label>
                    <select
                        id="managerId"
                        name="managerId"
                        value={selectedManagerId}
                        onChange={handleChange}
                        className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:border-transparent"
                        required
                    >
                        <option value="">-- Select a Manager --</option>
                        {managers.map((manager) => (
                            <option key={manager.managerId} value={manager.managerId}>
                                {manager.managerName} (ID: {manager.managerId})
                            </option>
                        ))}
                    </select>
                </div>

                <button
                    type="submit"
                    className="w-full bg-indigo-600 hover:bg-indigo-700 text-white font-bold py-2 px-4 rounded-lg focus:outline-none focus:shadow-outline transition-colors duration-200"
                >
                    Assign Manager
                </button>
            </form>
        </div>
    );
};

export default AssignManagerForm;