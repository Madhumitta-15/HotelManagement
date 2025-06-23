import axios from 'axios';
import { jwtDecode } from 'jwt-decode';

const API_BASE_URL = 'http://localhost:8095'; 

const authService = {
  // Guest Registration
  registerGuest: async (guestName, guestUserName, guestPassword, guestEmail, guestContact) => {
    try {
      const response = await axios.post(`${API_BASE_URL}/guest/register`, {
        guestName: guestName,
        guestUserName: guestUserName,
        guestPassword: guestPassword,
        guestEmail: guestEmail,
        guestContact: guestContact
      });
      return response.data;
    } catch (error) {
      console.error('Registration error:', error.response ? error.response.data : error.message);
      throw error.response ? error.response.data : new Error('Registration failed');
    }
  },
  //Manager Registeration
  registerManager: async (managerName, managerPassword, managerEmail, managerContact) => {
    try {
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

  // Guest Login
  loginGuest: async (username, password) => {
    try {
      const response = await axios.post(`${API_BASE_URL}/guest/login`, { username, password });
      const token = response.data;
      localStorage.setItem('jwtToken', token);
      const decodedToken = jwtDecode(token);
      localStorage.setItem('userRole', decodedToken.role);
      localStorage.setItem('userId', decodedToken.userId);
      localStorage.setItem('username', username);
      return decodedToken;
    } catch (error) {
      console.error('Guest login error:', error.response ? error.response.data : error.message);
      throw error.response ? error.response.data : new Error('Login failed');
    }
  },

  // Admin Login
  loginAdmin: async (username, password) => {
    try {
      const response = await axios.post(`${API_BASE_URL}/admin/login`, { username, password });
      const token = response.data;
      localStorage.setItem('jwtToken', token);
      const decodedToken = jwtDecode(token);
      localStorage.setItem('userRole', decodedToken.role);
      localStorage.setItem('userId', decodedToken.userId);
      localStorage.setItem('username', username);
      return decodedToken;
    } catch (error) {
      console.error('Admin login error:', error.response ? error.response.data : error.message);
      throw error.response ? error.response.data : new Error('Admin login failed');
    }
  },

  // Manager Login
  loginManager: async (username, password) => {
    try {
      const response = await axios.post(`${API_BASE_URL}/manager/login`, { username, password });
      const token = response.data;
      localStorage.setItem('jwtToken', token);
      const decodedToken = jwtDecode(token);
      localStorage.setItem('userRole', decodedToken.role); 
      localStorage.setItem('userId', decodedToken.userId); 
      localStorage.setItem('username', username);
      return decodedToken;
    } catch (error) {
      console.error('Manager login error:', error.response ? error.response.data : error.message);
      throw error.response ? error.response.data : new Error('Manager login failed');
    }
  }, 

  // Logout function
  logout: () => {
    localStorage.removeItem('jwtToken');
    localStorage.removeItem('userRole');
    localStorage.removeItem('userId');
    localStorage.removeItem('username');
    window.location.reload();
  },

  // Get current user's token
  getCurrentUserToken: () => {
    return localStorage.getItem('jwtToken');
  },

  // Get current user's role
  getCurrentUserRole: () => {
    return localStorage.getItem('userRole');
  },

  // Add JWT to headers for authenticated requests
  setAuthHeader: () => {
    const token = authService.getCurrentUserToken();
    if (token) {
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    } else {
      delete axios.defaults.headers.common['Authorization'];
    }
  }
};

// Set the default auth header on app load
authService.setAuthHeader();


axios.interceptors.request.use(
  (config) => {
    const token = authService.getCurrentUserToken();
    if (token && !config.headers.Authorization) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

axios.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      console.warn('Unauthorized request, potentially expired token. Logging out...');
      authService.logout();
    }
    return Promise.reject(error);
  }
);

export default authService;