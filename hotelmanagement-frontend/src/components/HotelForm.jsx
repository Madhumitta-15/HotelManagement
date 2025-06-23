import React, { useState, useEffect } from 'react';
import hotelService from '../services/HotelService';
import { FaImage } from 'react-icons/fa';

const HotelForm = ({ onHotelAdded, setMessage, setError, hotelToEdit, setHotelToEdit }) => {
    const [hotelData, setHotelData] = useState({
        hotelName: '',
        location: '',
        amenities: '',
        imageUrl: '',
        description:'',

    });

    
    useEffect(() => {
        if (hotelToEdit) {
            setHotelData({
                hotelName: hotelToEdit.hotelName || '',
                location: hotelToEdit.location || '',
                amenities: hotelToEdit.amenities || '',
                imageUrl: hotelToEdit.imageUrl || '',
                description: hotelToEdit.description || '',
            });
        } else {
            setHotelData({
                hotelName: '',
                location: '',
                amenities: '',
                imageUrl: '',
                description: '',
            });
        }
    }, [hotelToEdit]); 
    const handleChange = (e) => {
        const { name, value } = e.target;
        setHotelData((prevData) => ({
            ...prevData,
            [name]: value,
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage('');
        setError('');

        try {
            let response;
            if (hotelToEdit) {
                response = await hotelService.updateHotel(hotelToEdit.hotelId, hotelData);
                setMessage(response || 'Hotel updated successfully!');
            } else {
                response = await hotelService.addHotel(hotelData);
                setMessage(response || 'Hotel added successfully!');
            }
            setHotelData({
                hotelName: '',
                location: '',
                amenities: '',
                imageUrl: '',
                description: '',    
            });
            setHotelToEdit(null); 
            onHotelAdded(); 
        } catch (err) {
            console.error('Operation failed:', err);
            setError(err.response ? err.response.data : err.message || 'Operation failed.');
        }
    };

    return (
        <div className="bg-white p-8 rounded-lg shadow-md max-w-md w-full mx-auto">
            <h2 className="text-2xl font-bold text-gray-800 mb-6 text-center">
                {hotelToEdit ? 'Edit Hotel' : 'Add New Hotel'}
            </h2>
            <form onSubmit={handleSubmit} className="space-y-4">
                <input
                    type="text"
                    name="hotelName"
                    placeholder="Hotel Name"
                    value={hotelData.hotelName}
                    onChange={handleChange}
                    className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:border-transparent"
                    required
                />
                <input
                    type="text"
                    name="location"
                    placeholder="Location"
                    value={hotelData.location}
                    onChange={handleChange}
                    className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:border-transparent"
                    required
                />
                <div className="relative flex items-center">
                    <FaImage className="absolute left-3 text-gray-400" />
                    <input
                        type="text"
                        name="imageUrl"
                        placeholder="Image URL (e.g., https://example.com/hotel.jpg)"
                        value={hotelData.imageUrl}
                        onChange={handleChange}
                        className="shadow appearance-none border rounded w-full py-2 pl-10 pr-3 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:border-transparent"
                    />
                </div>
                <input
                    type="textarea"
                    name="description"
                    placeholder="description "
                    value={hotelData.description}
                    onChange={handleChange}
                    className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:border-transparent"
                />
                <input
                    type="text"
                    name="amenities"
                    placeholder="Amenities (comma-separated)"
                    value={hotelData.amenities}
                    onChange={handleChange}
                    className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:border-transparent"
                />

                <button
                    type="submit"
                    className="w-full bg-indigo-600 hover:bg-indigo-700 text-white font-bold py-2 px-4 rounded-lg focus:outline-none focus:shadow-outline transition-colors duration-200"
                >
                    {hotelToEdit ? 'Update Hotel' : 'Add Hotel'}
                </button>
                {hotelToEdit && ( 
                    <button
                        type="button"
                        onClick={() => setHotelToEdit(null)}
                        className="w-full bg-gray-400 hover:bg-gray-500 text-white font-bold py-2 px-4 rounded-lg focus:outline-none focus:shadow-outline transition-colors duration-200 mt-2"
                    >
                        Cancel Edit
                    </button>
                )}
            </form>
        </div>
    );
};

export default HotelForm;