import axios from 'axios';
import authService from './AuthServices'; 

const API_BASE_URL = 'http://localhost:8095'; 

const hotelService = {
  // Add Hotel
  addHotel: async (hotelData) => {
    try {
      authService.setAuthHeader(); 
      const response = await axios.post(`${API_BASE_URL}/hotel/addHotel`, hotelData);
      return response.data;
    } catch (error) {
      console.error('Failed to add hotel:', error.response ? error.response.data : error.message);
      throw error.response ? error.response.data : new Error('Failed to add hotel');
    }
  },

  getAllHotels: async () => {
    try {
      authService.setAuthHeader();
      const response = await axios.get(`${API_BASE_URL}/hotel/gethotelslist`);
      console.log('Hotels fetched successfully:', response.data);
      return response.data;
    } catch (error) {
      console.error('Failed to get hotels:', error.response ? error.response.data : error.message);
      throw error.response ? error.response.data : new Error('Failed to get hotels');
    }
  },

  updateHotel: async (hotelId, hotelData) => {    
    try {
      authService.setAuthHeader(); 
      const response = await axios.put(`${API_BASE_URL}/hotel/updatehotel/${hotelId}`, hotelData);
      return response.data;
    } catch (error) {
      console.error('Failed to update hotel:', error.response ? error.response.data : error.message);
      throw error.response ? error.response.data : new Error('Failed to update hotel');
    }
  },

  assignManagerToHotel: async (hotelId, managerId) => {
    try {
      authService.setAuthHeader();
      const response = await axios.post(
        `${API_BASE_URL}/hotel/${hotelId}/assign-manager`,
        { managerId: managerId } 
      );
      return response.data;
    } catch (error) {
      console.error('Failed to assign manager to hotel:', error.response ? error.response.data : error.message);
      throw error.response ? error.response.data : new Error('Failed to assign manager to hotel');
    }
  },

 //Delete Hotel
deleteHotel: async (hotelId) => {
  try {
    authService.setAuthHeader(); 
    const response = await axios.delete(`${API_BASE_URL}/hotel/deleteHotel/${hotelId}`);
    console.log(`Hotel with ID ${hotelId} deleted successfully.`);
    return response.data;
  } catch (error) {
    console.error(`Failed to delete hotel with ID ${hotelId}:`, error.response ? error.response.data : error.message);
    throw error.response ? error.response.data : new Error('Failed to delete hotel');
  }
},

  getHotelByManagerId: async (managerId) => {
    try {
      authService.setAuthHeader();
      const response = await axios.get(`${API_BASE_URL}/hotel/manager/${managerId}`);
      return response.data;
    } catch (error) {
      console.error(`Failed to get hotel for manager ${managerId}:`, error.response ? error.response.data : error.message);
      throw error.response ? error.response.data : new Error(`Failed to get hotel for manager ${managerId}`);
    }
  },
};

export default hotelService;