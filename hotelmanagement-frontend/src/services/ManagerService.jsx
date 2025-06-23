import axios from 'axios';
import authService from './AuthServices'; 

const API_BASE_URL = 'http://localhost:8095'; 

const managerService = {
  // Register Manager 
  registerManager: async (managerName, managerPassword, managerEmail, managerContact) => {
    try {
      authService.setAuthHeader();
      const response = await axios.post(`${API_BASE_URL}/manager/addManager`, {
        managerName: managerName,
        managerPassword: managerPassword,
        managerEmail: managerEmail,
        managerContact: managerContact
      });
      return response.data;
    } catch (error) {
      console.error('Manager registration error:', error.response ? error.response.data : error.message);
      throw error.response ? error.response.data : new Error('Manager registration failed');
    }
  },

  getAllManagers: async () => {
    try {
      authService.setAuthHeader(); 
      const response = await axios.get(`${API_BASE_URL}/admin/viewallmanagers`);
      return response.data;
    } catch (error) {
      console.error('Failed to get managers:', error.response ? error.response.data : error.message);
      throw error.response ? error.response.data : new Error('Failed to get managers');
    }
  },

  // Delete Manager
  deleteManager: async (managerId) => {
    try {
      authService.setAuthHeader(); 
      const response = await axios.delete(`${API_BASE_URL}/manager/deleteManager/${managerId}`);
      return response.data;
    } catch (error) {
      console.error(`Failed to delete manager with ID ${managerId}:`, error.response ? error.response.data : error.message);
      throw error.response ? error.response.data : new Error('Failed to delete manager');
    }
  },

  
};

export default managerService;