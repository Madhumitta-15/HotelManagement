import React, { useState, useEffect, useCallback } from 'react';
import authService from '../../services/AuthServices'; 
import managerService from '../../services/ManagerService'; 
import hotelService from '../../services/HotelService';
import { useNavigate } from 'react-router-dom';
import ManagerForm from '../../components/ManagerForm';
import HotelForm from '../../components/HotelForm';
import AssignManagerForm from '../../components/AssignManagerForm';
import HotelList from '../../components/HotelList';
import ManagerList from '../../components/ManagerList';
import AdminSidebar from '../../components/AdminSidebar';
 
const AdminDashboard = () => {
    const navigate = useNavigate();
 
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');
    const [activeTab, setActiveTab] = useState('viewHotels'); 
    const [isSidebarOpen, setIsSidebarOpen] = useState(false); 
 
    const [hotels, setHotels] = useState([]);
    const [managers, setManagers] = useState([]);
    const [editingHotel, setEditingHotel] = useState(null); 
 
    
    const fetchHotels = useCallback(async () => {
        setMessage(''); 
        setError('');  
        try {
            const hotelList = await hotelService.getAllHotels();
            setHotels(hotelList);
        } catch (err) {
            console.error('Error fetching hotels:', err);
            setError('Failed to load hotels for assignment and display.');
        }
    }, []);
 
    const fetchManagers = useCallback(async () => {
        setMessage('');
        setError('');   
        try {
            const managerList = await managerService.getAllManagers();
            setManagers(managerList);
        } catch (err) {
            console.error('Error fetching managers:', err);
            setError('Failed to load managers for assignment and display.');
        }
    }, []);
 
    // Effect for security check and initial data fetching
    useEffect(() => {
        const userRole = authService.getCurrentUserRole();
        if (userRole !== 'ADMIN') {
            navigate('/unauthorized');
            console.warn('Unauthorized access attempt to admin dashboard.');
        } else {
            fetchHotels();
            fetchManagers();
        }
    }, [navigate, fetchHotels, fetchManagers]);
 
 
    
    const handleManagerAdded = () => {
        setActiveTab('viewManagers');
        fetchManagers();
        setIsSidebarOpen(false); 
    };
 
    
    const handleHotelFormSubmission = () => {
        setEditingHotel(null); 
        setActiveTab('viewHotels'); 
        fetchHotels(); 
        setIsSidebarOpen(false); 
    };
 
    const handleManagerAssigned = () => {
        setActiveTab('viewHotels');
        fetchHotels(); 
        fetchManagers(); 
        setIsSidebarOpen(false); 
    };
 
    const handleLogout = () => {
        authService.logout();
        navigate('/login');
    };
 
    
    const handleEditHotel = (hotel) => {
        setEditingHotel(hotel);
        setActiveTab('addHotel'); 
        setMessage('');
        setError('');
        setIsSidebarOpen(false); 
    };
 
   
    const toggleSidebar = () => {
        setIsSidebarOpen(!isSidebarOpen);
    };
 
    return (
        <div className="flex flex-col md:flex-row min-h-screen bg-gray-100">
            
            <button
                className="md:hidden p-4 text-gray-700 focus:outline-none focus:bg-gray-200"
                onClick={toggleSidebar}
                aria-label="Open sidebar"
            >
                <svg
                    className="w-6 h-6"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                    xmlns="http://www.w3.org/2000/svg"
                >
                    <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth="2"
                        d="M4 6h16M4 12h16M4 18h16"
                    ></path>
                </svg>
            </button>
 
            {/* Sidebar */}
            <div
                className={`fixed inset-y-0 left-0 transform ${
                    isSidebarOpen ? 'translate-x-0' : '-translate-x-full'
                } md:relative md:translate-x-0 transition-transform duration-200 ease-in-out
                bg-gray-800 text-white w-64 md:w-auto z-40 md:z-auto md:flex-shrink-0`}
            >
                <AdminSidebar
                    activeTab={activeTab}
                    setActiveTab={(tab) => {
                        setActiveTab(tab);
                        setIsSidebarOpen(false); 
                    }}
                    onLogout={handleLogout}
                    fetchHotels={fetchHotels}
                    fetchManagers={fetchManagers}
                    setEditingHotel={setEditingHotel}
                    setIsSidebarOpen={setIsSidebarOpen}
                />
            </div>
 
           
            {isSidebarOpen && (
                <div
                    className="fixed inset-0 bg-black opacity-50 z-30 md:hidden"
                    onClick={toggleSidebar}
                ></div>
            )}
 
             {/* Main content area  */}
            <div className="flex-1 flex flex-col p-6 overflow-x-hidden overflow-y-auto">
                <h1 className="text-3xl font-bold text-gray-800 mb-6">Admin Dashboard</h1>
 
                {message && (
                    <div className="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded relative mb-4" role="alert">
                        <strong className="font-bold">Success!</strong>
                        <span className="block sm:inline"> {message}</span>
                    </div>
                )}
                {error && (
                    <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative mb-4" role="alert">
                        <strong className="font-bold">Error!</strong>
                        <span className="block sm:inline"> {error}</span>
                    </div>
                )}
 
                {activeTab === 'addManager' && (
                    <ManagerForm
                        onManagerAdded={handleManagerAdded}
                        setMessage={setMessage}
                        setError={setError}
                    />
                )}
 
                {activeTab === 'addHotel' && (
                    <HotelForm
                        onHotelAdded={handleHotelFormSubmission}
                        setMessage={setMessage}
                        setError={setError}
                        hotelToEdit={editingHotel}
                        setHotelToEdit={setEditingHotel}
                    />
                )}
 
                {activeTab === 'assignManager' && (
                    <AssignManagerForm
                        hotels={hotels}
                        managers={managers}
                        onManagerAssigned={handleManagerAssigned}
                        setMessage={setMessage}
                        setError={setError}
                    />
                )}
 
                {activeTab === 'viewHotels' && (
                    <HotelList
                        hotels={hotels}
                        fetchHotels={fetchHotels}
                        setMessage={setMessage}
                        setError={setError}
                        onEdit={handleEditHotel}
                    />
                )}
 
                {activeTab === 'viewManagers' && (
                    <ManagerList
                        managers={managers}
                        fetchManagers={fetchManagers}
                        fetchHotels={fetchHotels}
                        setMessage={setMessage}
                        setError={setError}
                    />
                )}
            </div>
        </div>
    );
};
 
export default AdminDashboard;