import React from 'react';
import managerService from '../services/ManagerService';
import { FaTrash } from 'react-icons/fa';

const ManagerList = ({ managers, fetchManagers, fetchHotels, setMessage, setError }) => {
    const handleDelete = async (managerId, managerName) => {
        if (window.confirm(`Are you sure you want to delete manager "${managerName}" (ID: ${managerId})? This action cannot be undone.`)) {
            setMessage('');
            setError('');
            try {
                await managerService.deleteManager(managerId);
                setMessage(`Manager "${managerName}" deleted successfully!`);
                fetchManagers(); 
                fetchHotels(); 
            } catch (err) {
                console.error('Failed to delete manager:', err);
                setError(err.response ? err.response.data : err.message || 'Failed to delete manager.');
            }
        }
    };

    return (
        <div className="bg-white p-8 rounded-lg shadow-md w-full">
            <h2 className="text-2xl font-bold text-gray-800 mb-6 text-center">Added Managers</h2>
            {managers.length > 0 ? (
                <div className="overflow-x-auto">
                    <table className="min-w-full leading-normal">
                        <thead>
                            <tr className="bg-gray-200 text-gray-600 uppercase text-sm leading-normal">
                                <th className="py-3 px-6 text-left">Manager ID</th>
                                <th className="py-3 px-6 text-left">Name</th>
                                <th className="py-3 px-6 text-left">Email</th>
                                <th className="py-3 px-6 text-left">Contact</th>
                                <th className="py-3 px-6 text-center">Actions</th>
                            </tr>
                        </thead>
                        <tbody className="text-gray-700 text-sm">
                            {managers.map((manager) => (
                                <tr key={manager.managerId} className="border-b border-gray-200 hover:bg-gray-100">
                                    <td className="py-3 px-6 text-left whitespace-nowrap">{manager.managerId}</td>
                                    <td className="py-3 px-6 text-left">{manager.managerName}</td>
                                    <td className="py-3 px-6 text-left">{manager.managerEmail}</td>
                                    <td className="py-3 px-6 text-left">{manager.managerContact}</td>
                                    <td className="py-3 px-6 text-center">
                                        <button
                                            onClick={() => handleDelete(manager.managerId, manager.managerName)}
                                            className="bg-red-500 hover:bg-red-600 text-white font-bold py-2 px-4 rounded-lg inline-flex items-center transition-colors duration-200"
                                        >
                                            <FaTrash className="mr-2" />
                                            Delete
                                        </button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            ) : (
                <p className="text-center text-gray-600 py-4">No managers added yet.</p>
            )}
        </div>
    );
};

export default ManagerList;