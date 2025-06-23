import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import authService from '../../services/AuthServices';
import hotelService from '../../services/HotelService';
import roomService from '../../services/RoomServices';

// Import the new components
import Navbar from '../../components/Navbarman';
import Sidebar from '../../components/sidebar';
import Alerts from '../../components/Alerts';
import HotelDetails from '../../components/HotelDetails';
import RoomList from '../../components/RoomList';
import RoomForm from '../../components/RoomForm';

const ManagerDashboard = () => {
    const navigate = useNavigate();
    const [managerId, setManagerId] = useState(null);
    const [managerUsername, setManagerUsername] = useState('');
    const [assignedHotel, setAssignedHotel] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [activeContent, setActiveContent] = useState('hotelDetails');
    const [rooms, setRooms] = useState([]);
    const [roomsLoading, setRoomsLoading] = useState(false);
    const [roomsError, setRoomsError] = useState('');
    const [selectedRoom, setSelectedRoom] = useState(null);
    const [roomFormData, setRoomFormData] = useState({
        roomId: '',
        roomType: 'REGULAR',
        price: '',
        features: '',
        availability: '',
        imageUrl:'',
    });
    const [formSuccess, setFormSuccess] = useState(false);
    const [formError, setFormError] = useState(null);

    const handleRoomFormChange = (e) => {
        const { name, value } = e.target;
        setRoomFormData(prevData => ({
            ...prevData,
            [name]: value
        }));
    };

    const fetchRoomsForHotel = useCallback(async (hotelId) => {
        if (!hotelId) {
            setRooms([]);
            return;
        }
        setRoomsLoading(true);
        setRoomsError('');
        try {
            const fetchedRooms = await roomService.getAllRoomsByHotel(hotelId);
            setRooms(fetchedRooms);
        } catch (err) {
            console.error('Error fetching rooms:', err);
            const errorMessage = err.response?.data || err.message || 'Failed to fetch rooms.';
            setRoomsError(errorMessage);
            setRooms([]);
        } finally {
            setRoomsLoading(false);
        }
    }, []); 

    const handleAddRoomSubmit = async (e) => {
        e.preventDefault();
        setFormSuccess(false);
        setFormError(null);

        if (!assignedHotel?.hotelId) {
            setFormError("No hotel assigned to add rooms to.");
            return;
        }

        try {
            const roomToSend = {
                type: roomFormData.roomType.toUpperCase(),
                price: parseFloat(roomFormData.price),
                features: roomFormData.features,
                availability: parseInt(roomFormData.availability, 10),
                imageUrl: roomFormData.imageUrl,
            };

            await roomService.addRoom(assignedHotel.hotelId, roomToSend);
            setFormSuccess(true);
            setRoomFormData({ roomId: '', roomType: 'REGULAR', price: '', features: '', availability: '' ,imageUrl:''});
            fetchRoomsForHotel(assignedHotel.hotelId);
            setActiveContent('viewRooms');
        } catch (err) {
            const errorMessage = err.response?.data || err.message || 'Failed to add room.';
            setFormError(errorMessage);
            console.error('Error adding room:', err);
        }
    };

    const handleUpdateRoomSubmit = async (e) => {
        e.preventDefault();
        setFormSuccess(false);
        setFormError(null);

        if (!selectedRoom?.roomId) {
            setFormError("No room selected for update.");
            return;
        }

        try {
            const roomToSend = {
                type: roomFormData.roomType.toUpperCase(),
                price: parseFloat(roomFormData.price),
                features: roomFormData.features,
                availability: parseInt(roomFormData.availability, 10),
                imageUrl: roomFormData.imageUrl,
            };

            await roomService.updateRoom(selectedRoom.roomId, roomToSend);
            setFormSuccess(true);
            setSelectedRoom(null);
            setRoomFormData({ roomId: '', roomType: 'REGULAR', price: '', features: '', availability: '',imageUrl:'' });
            fetchRoomsForHotel(assignedHotel.hotelId);
            setActiveContent('viewRooms');
        } catch (err) {
            const errorMessage = err.response?.data || err.message || 'Failed to update room.';
            setFormError(errorMessage);
            console.error('Error updating room:', err);
        }
    };

    const handleDeleteRoom = async (roomId) => {
        if (window.confirm("Are you sure you want to delete this room? This action cannot be undone.")) {
            try {
                await roomService.deleteRoom(roomId);
                setFormSuccess(true);
                setFormError(null);
                fetchRoomsForHotel(assignedHotel.hotelId);
                setActiveContent('viewRooms');
            } catch (err) {
                const errorMessage = err.response?.data || err.message || 'Failed to delete room.';
                setFormError(errorMessage);
                setFormSuccess(false);
                console.error('Error deleting room:', err);
            }
        }
    };

    useEffect(() => {
        const checkAuth = async () => {
            const userRole = authService.getCurrentUserRole();
            const userIdFromStorage = localStorage.getItem('userId');
            const usernameFromStorage = localStorage.getItem('username');

            if (userRole !== 'MANAGER') {
                console.warn('Unauthorized access attempt to manager dashboard.');
                authService.logout();
                navigate('/login');
                return;
            }

            if (userIdFromStorage) {
                setManagerId(parseInt(userIdFromStorage, 10));
            } else {
                setError('Manager ID not found in storage. Please log in again.');
                setLoading(false);
                authService.logout();
                navigate('/login');
            }

            if (usernameFromStorage) {
                setManagerUsername(usernameFromStorage);
            }
        };
        checkAuth();
    }, [navigate]);

    useEffect(() => {
        const fetchAssignedHotel = async () => {
            if (managerId === null) {
                setLoading(false);
                return;
            }

            setLoading(true);
            setError('');
            try {
                const hotel = await hotelService.getHotelByManagerId(managerId);
                setAssignedHotel(hotel);
                if (!hotel && activeContent !== 'hotelDetails') {
                    setActiveContent('hotelDetails');
                }
            } catch (err) {
                console.error('Error fetching assigned hotel:', err);
                const errorMessage = err.response?.data || err.message || 'Failed to fetch assigned hotel.';
                setError(errorMessage);
                setAssignedHotel(null);
                setActiveContent('hotelDetails');
            } finally {
                setLoading(false);
            }
        };
        fetchAssignedHotel();
    }, [managerId, activeContent]);

    useEffect(() => {
        if (assignedHotel?.hotelId) {
            fetchRoomsForHotel(assignedHotel.hotelId);
        } else {
            setRooms([]);
        }
    }, [assignedHotel, fetchRoomsForHotel]);

    const handleSelectRoomForUpdate = (room) => {
        setSelectedRoom(room);
        setRoomFormData({
            roomId: room.roomId,
            roomType: room.type,
            price: room.price,
            features: room.features,
            availability: room.availability?.toString() || '',
            imageUrl: room.imageUrl || '',
        });
        setFormSuccess(false);
        setFormError(null);
        setActiveContent('updateRoom');
    };

    const handleLogout = () => {
        authService.logout();
        navigate('/login');
    };

    const getContentTitle = () => {
        switch (activeContent) {
            case 'hotelDetails': return 'Hotel Details';
            case 'viewRooms': return 'Rooms Overview';
            case 'addRoom': return 'Add New Room';
            case 'updateRoom': return `Update Room`;
            default: return 'Dashboard';
        }
    };

    return (
        <div className="min-h-screen bg-gray-50 font-sans antialiased w-full overflow-x-hidden h-screen flex flex-col">
            <Navbar
                activeContent={activeContent}
                setActiveContent={setActiveContent}
                assignedHotel={assignedHotel}
                handleLogout={handleLogout}
                resetRoomForm={() => {
                    setRoomFormData({ roomId: '', roomType: 'REGULAR', price: '', features: '', availability: '', imageUrl: ''  });
                    setFormSuccess(false);
                    setFormError(null);
                    setSelectedRoom(null);
                }}
            />

        <div className="flex flex-col md:flex-row w-full gap-6 flex-grow overflow-hidden pt-16 max-w-full mx-0">
                <Sidebar
                    managerUsername={managerUsername}
                    assignedManager={assignedHotel?.assignedManager}
                    hotelName={assignedHotel?.hotelName}
                />

                <main className="flex-grow overflow-y-auto p-4 md:p-6 bg-white rounded-none shadow-none max-w-full">
                    <h1 className="text-3xl font-bold text-gray-900 mb-8">{getContentTitle()}</h1>

                    <Alerts
                        loading={loading}
                        error={error}
                        formSuccess={formSuccess}
                        formError={formError}
                    />

                    {activeContent === 'hotelDetails' && (
                        <div className="w-full h-full max-w-5xl mx-auto">
                            <HotelDetails
                                assignedHotel={assignedHotel}
                                loading={loading}
                                error={error}
                                managerId={managerId}
                            />
                        </div>
                    )}

                    {activeContent === 'viewRooms' && assignedHotel && (
                        <RoomList
                            rooms={rooms}
                            roomsLoading={roomsLoading}
                            roomsError={roomsError}
                            hotelName={assignedHotel.hotelName}
                            handleSelectRoomForUpdate={handleSelectRoomForUpdate}
                            handleDeleteRoom={handleDeleteRoom}
                        />
                    )}

                    {activeContent === 'addRoom' && assignedHotel && (
                        <div className="w-full h-full max-w-5xl mx-auto">
                            <RoomForm
                                title={`Add New Room to ${assignedHotel.hotelName}`}
                                formData={roomFormData}
                                onFormChange={handleRoomFormChange}
                                onSubmit={handleAddRoomSubmit}
                                submitButtonText="Add Room"
                            />
                        </div>
                    )}

                    {activeContent === 'updateRoom' && assignedHotel && selectedRoom && (
                        <RoomForm
                            title={`Update Room (ID: ${selectedRoom.roomId})`}
                            formData={roomFormData}
                            onFormChange={handleRoomFormChange}
                            onSubmit={handleUpdateRoomSubmit}
                            submitButtonText="Update"
                            onCancel={() => setActiveContent('viewRooms')}
                        />
                    )}
                </main>
            </div>
        </div>
    );
};

export default ManagerDashboard;