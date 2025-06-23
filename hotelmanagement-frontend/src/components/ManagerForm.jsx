import React, { useState } from 'react';
import authService from '../services/AuthServices';

const ManagerForm = ({ onManagerAdded, setMessage, setError }) => {
    const [managerData, setManagerData] = useState({
        managerName: '',
        managerPassword: '',
        managerEmail: '',
        managerContact: '',
    });

    const handleChange = (e) => {
        const { name, value } = e.target;
        setManagerData((prevData) => ({
            ...prevData,
            [name]: value,
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage('');
        setError(''); 

        try {
            const response = await authService.registerManager(
                managerData.managerName,
                managerData.managerPassword,
                managerData.managerEmail,
                managerData.managerContact
            );
            setMessage(response || 'Manager added successfully!');
            setManagerData({ 
                managerName: '',
                managerPassword: '',
                managerEmail: '',
                managerContact: '',
            });
            onManagerAdded(); 
        } catch (err) {
            console.error('Failed to add manager:', err);
            setError(err.response ? err.response.data : err.message || 'Failed to add manager.');
        }
    };

    return (
        <div className="bg-white p-8 rounded-lg shadow-md max-w-md w-full mx-auto">
            <h2 className="text-2xl font-bold text-gray-800 mb-6 text-center">Add New Manager</h2>
            <form onSubmit={handleSubmit} className="space-y-4">
                <input
                    type="text"
                    name="managerName"
                    placeholder="Manager Name"
                    value={managerData.managerName}
                    onChange={handleChange}
                    className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:border-transparent"
                    required
                />
                <input
                    type="password"
                    name="managerPassword"
                    placeholder="Password"
                    value={managerData.managerPassword}
                    onChange={handleChange}
                    className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:border-transparent"
                    required
                />
                <input
                    type="email"
                    name="managerEmail"
                    placeholder="Email"
                    value={managerData.managerEmail}
                    onChange={handleChange}
                    className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:border-transparent"
                    required
                />
                <input
                    type="tel"
                    name="managerContact"
                    placeholder="Contact Number"
                    value={managerData.managerContact}
                    onChange={handleChange}
                    className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-indigo-400 focus:border-transparent"
                    required
                />
                <button
                    type="submit"
                    className="w-full bg-indigo-600 hover:bg-indigo-700 text-white font-bold py-2 px-4 rounded-lg focus:outline-none focus:shadow-outline transition-colors duration-200"
                >
                    Add Manager
                </button>
            </form>
        </div>
    );
};

export default ManagerForm;