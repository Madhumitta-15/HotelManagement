import React from 'react';
import { FaUserPlus, FaHotel, FaUserTie, FaBuilding, FaUsers, FaSignOutAlt } from 'react-icons/fa';
 
const AdminSidebar = ({ activeTab, setActiveTab, onLogout, fetchHotels, fetchManagers, setIsSidebarOpen }) => {
    return (
        <div className="bg-gradient-to-br from-indigo-800 to-indigo-700 text-white p-6 flex flex-col shadow-lg h-full">
            {setIsSidebarOpen && ( 
                <button
                    className="md:hidden absolute top-4 right-4 text-white text-2xl focus:outline-none"
                    onClick={() => setIsSidebarOpen(false)}
                    aria-label="Close sidebar"
                >
                    &times; 
                </button>
            )}
 
            <h2 className="text-3xl font-extrabold mb-8 text-center tracking-wide mt-4 md:mt-0">Admin Panel</h2>
            <nav className="flex-1">
                <ul>
                    <li className="mb-4">
                        <button
                            className={`flex items-center w-full text-left p-3 rounded-lg transition-all duration-200 ${activeTab === 'addManager' ? 'bg-indigo-600 shadow-md' : 'hover:bg-indigo-600 hover:shadow-md'}`}
                            onClick={() => {
                                setActiveTab('addManager');
                                if (setIsSidebarOpen) setIsSidebarOpen(false);
                            }}
                        >
                            <FaUserPlus className="mr-3 text-xl" />
                            Add New Manager
                        </button>
                    </li>
                    <li className="mb-4">
                        <button
                            className={`flex items-center w-full text-left p-3 rounded-lg transition-all duration-200 ${activeTab === 'addHotel' ? 'bg-indigo-600 shadow-md' : 'hover:bg-indigo-600 hover:shadow-md'}`}
                            onClick={() => {
                                setActiveTab('addHotel');
                                if (setIsSidebarOpen) setIsSidebarOpen(false);
                            }}
                        >
                            <FaHotel className="mr-3 text-xl" />
                            Add New Hotel
                        </button>
                    </li>
                    <li className="mb-4">
                        <button
                            className={`flex items-center w-full text-left p-3 rounded-lg transition-all duration-200 ${activeTab === 'assignManager' ? 'bg-indigo-600 shadow-md' : 'hover:bg-indigo-600 hover:shadow-md'}`}
                            onClick={() => {
                                setActiveTab('assignManager');
                                fetchHotels();
                                fetchManagers();
                                if (setIsSidebarOpen) setIsSidebarOpen(false);
                            }}
                        >
                            <FaUserTie className="mr-3 text-xl" />
                            Assign Manager to Hotel
                        </button>
                    </li>
                    <li className="mb-4">
                        <button
                            className={`flex items-center w-full text-left p-3 rounded-lg transition-all duration-200 ${activeTab === 'viewHotels' ? 'bg-indigo-600 shadow-md' : 'hover:bg-indigo-600 hover:shadow-md'}`}
                            onClick={() => {
                                setActiveTab('viewHotels');
                                if (setIsSidebarOpen) setIsSidebarOpen(false);
                            }}
                        >
                            <FaBuilding className="mr-3 text-xl" />
                            View All Hotels
                        </button>
                    </li>
                    <li className="mb-4">
                        <button
                            className={`flex items-center w-full text-left p-3 rounded-lg transition-all duration-200 ${activeTab === 'viewManagers' ? 'bg-indigo-600 shadow-md' : 'hover:bg-indigo-600 hover:shadow-md'}`}
                            onClick={() => {
                                setActiveTab('viewManagers');
                                if (setIsSidebarOpen) setIsSidebarOpen(false);
                            }}
                        >
                            <FaUsers className="mr-3 text-xl" />
                            View All Managers
                        </button>
                    </li>
                </ul>
            </nav>
            <div className="mt-8">
                <button
                    className="flex items-center w-full justify-center bg-red-600 hover:bg-red-700 text-white font-semibold py-3 px-4 rounded-lg transition-colors duration-200 shadow-md"
                    onClick={() => {
                        onLogout();
                        if (setIsSidebarOpen) setIsSidebarOpen(false);
                    }}
                >
                    <FaSignOutAlt className="mr-2 text-lg" />
                    Logout
                </button>
            </div>
        </div>
    );
};
 
export default AdminSidebar;