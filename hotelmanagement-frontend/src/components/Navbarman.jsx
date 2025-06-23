import React from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faHotel } from '@fortawesome/free-solid-svg-icons';

const Navbar = ({ activeContent, setActiveContent, assignedHotel, handleLogout, resetRoomForm }) => {
    return (
        <nav className="bg-indigo-700 shadow py-4">
           <div className="flex flex-col md:flex-row gap-8 w-full max-w-full h-full items-center justify-between px-6 md:px-12">
                <div className="text-white font-bold text-xl">
                    <FontAwesomeIcon icon={faHotel} className="mr-2" /> Hotel Manager
                </div>
                <div className="space-x-4">
                    <button
                        onClick={() => setActiveContent('hotelDetails')}
                        className={`text-gray-200 hover:text-white transition duration-200 py-2 px-3 rounded focus:outline-none focus:shadow-outline ${activeContent === 'hotelDetails' ? 'bg-indigo-600' : ''}`}
                    >
                        Hotel Details
                    </button>
                    <button
                        onClick={() => setActiveContent('viewRooms')}
                        className={`text-gray-200 hover:text-white transition duration-200 py-2 px-3 rounded focus:outline-none focus:shadow-outline ${activeContent === 'viewRooms' ? 'bg-indigo-600' : ''}`}
                    >
                        View Rooms
                    </button>
                    {assignedHotel && (
                        <button
                            onClick={() => {
                                setActiveContent('addRoom');
                                resetRoomForm(); 
                            }}
                            className={`text-gray-200 hover:text-white transition duration-200 py-2 px-3 rounded focus:outline-none focus:shadow-outline ${activeContent === 'addRoom' ? 'bg-indigo-600' : ''}`}
                        >
                            Add Room
                        </button>
                    )}
                    <button
                        onClick={handleLogout}
                        className="bg-red-500 hover:bg-red-700 text-white font-semibold py-2 px-4 rounded focus:outline-none focus:shadow-outline transition duration-200"
                    >
                        Logout
                    </button>
                </div>
            </div>
        </nav>
    );
};

export default Navbar;