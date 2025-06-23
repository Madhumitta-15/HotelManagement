import axios from 'axios';
import authService from './AuthServices'; 

const API_BASE_URL = 'http://localhost:8095'; 

const roomService = {
  //addRooom
  addRoom: async (hotelId, roomData) => { 
    try {
      authService.setAuthHeader();
      const response = await axios.post(`${API_BASE_URL}/rooms/addroom/hotel/${hotelId}`, roomData);
      return response.data; 
    } catch (error) {
      console.error('Failed to add room:', error.response ? error.response.data : error.message);
      throw error.response ? error.response.data : new Error('Failed to add room');
    }
  },

  // get all rooms within the hotel
  getAllRoomsByHotel: async (hotelId) => {
    try {
      authService.setAuthHeader();
      const response = await axios.get(`${API_BASE_URL}/rooms/getallrooms/${hotelId}`);
      return response.data;
    } catch (error) {
      console.error('Failed to get rooms by hotel:', error.response ? error.response.data : error.message);
      throw error.response ? error.response.data : new Error('Failed to get rooms by hotel');
    }
  },

  // get room by id
  getRoomById: async (roomId) => {
    try {
      authService.setAuthHeader();
      const response = await axios.get(`${API_BASE_URL}/rooms/getroom/${roomId}`);
      return response.data;
    } catch (error) {
      console.error('Failed to get room by ID:', error.response ? error.response.data : error.message);
      throw error.response ? error.response.data : new Error('Failed to get room by ID');
    }
  },

  // updating room by room id
  updateRoom: async (roomId, roomData) => {
    try {
      authService.setAuthHeader();
      const response = await axios.put(`${API_BASE_URL}/rooms/updateroom/${roomId}`, roomData);
      return response.data; 
    } catch (error) {
      console.error('Failed to update room:', error.response ? error.response.data : error.message);
      throw error.response ? error.response.data : new Error('Failed to update room');
    }
  },

  // delete room by room id
  deleteRoom: async (roomId) => {
    try {
      authService.setAuthHeader();
      await axios.delete(`${API_BASE_URL}/rooms/deleteroom/${roomId}`);
      return "Room deleted successfully"; 
    } catch (error) {
      console.error('Failed to delete room:', error.response ? error.response.data : error.message);
      throw error.response ? error.response.data : new Error('Failed to delete room');
    }
  },

 
};

export default roomService;