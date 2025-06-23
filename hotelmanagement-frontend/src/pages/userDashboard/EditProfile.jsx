import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate} from 'react-router-dom';
import authService from '../../services/AuthServices'; 

const API_BASE_URL = 'http://localhost:8095'; 

const EditProfile = () => {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const [isEditing, setIsEditing] = useState(false); 
  const [updateMessage, setUpdateMessage] = useState(''); 

  
  const fetchProfile = async (username) => { 
    setLoading(true);
    setError('');
    try {
         
      const response = await axios.get(`${API_BASE_URL}/guest/viewguest/${username}`);
      setProfile({
        guestId: response.data.guestId,
        guestUserName: response.data.guestUserName,
        guestName: response.data.guestName || '',
        guestEmail: response.data.guestEmail || '',
        guestContact: response.data.guestContact || '',
        bookingHistory: response.data.bookingHistory,
        paymentHistory: response.data.paymentHistory
      });
    } catch (err) {
      
      if (err.response && err.response.status === 401) {
        setError('Unauthorized: Your session may have expired. Please log in again.');
        authService.logout(); 
      } else {
        setError(err.response?.data?.message || 'Failed to fetch user profile. Please check backend logs and network tab for details.');
      }
      console.error('Profile fetch error:', err.response?.data || err.message || err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    const username = localStorage.getItem('username');
    const token = authService.getCurrentUserToken();

    if (!username || !token) {
      setError('Authentication details missing. Please log in.');
      setLoading(false);
      authService.logout();
      return;
    }

    fetchProfile(username); 
  }, []); 

  // Handles changes in the input fields
  const handleChange = (e) => {
    setProfile({ ...profile, [e.target.name]: e.target.value });
  };

  // Handles the submission of updated profile data
  const handleUpdate = async () => {
    setUpdateMessage('');
    setError('');

    const username = localStorage.getItem('username'); 
    const token = authService.getCurrentUserToken();

    if (!username || !token || !profile) {
      setError('Authentication details or profile data missing for update. Please log in.');
      return;
    }

    
    const payload = {
      name: profile.guestName,    
      email: profile.guestEmail, 
      contact: profile.guestContact 
    };

    
    if (!payload.name || !payload.email || !payload.contact) {
      setError('All profile fields (Full Name, Email, Phone) are required.');
      return;
    }

    try {
      authService.setAuthHeader(); 
      await axios.patch(`${API_BASE_URL}/guest/updateprofile`, payload);
        
      setUpdateMessage('Profile updated successfully!');
      setIsEditing(false);
      fetchProfile(username); 
    } catch (err) {
      if (err.response && err.response.status === 401) {
        setError('Unauthorized: Your session may have expired. Please log in again.');
        authService.logout();
      } else {
        setError(err.response?.data?.message || 'Failed to update profile. Please check your input.');
      }
      console.error('Profile update error:', err.response?.data || err.message || err);
    }
  };

  if (loading) return <div className="py-12 text-center text-gray-600 font-semibold text-lg">Loading user details...</div>;
  if (error) return <div className="py-12 text-center text-red-600 font-semibold text-lg">Error: {error}</div>;
  if (!profile) return <div className="py-12 text-center text-gray-500 text-lg">No profile data available.</div>;

  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center p-4">
      <div className="bg-white rounded-2xl shadow-xl border border-gray-200 w-full max-w-2xl p-8 transform transition-all duration-300 hover:shadow-2xl">
      <button
          onClick={() => navigate('/')} 
          className="absolute top-6 left-6 p-2 rounded-full bg-gray-200 hover:bg-gray-300 transition-colors duration-200"
          title="Go back to Home"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            className="h-6 w-6 text-gray-700"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M10 19l-7-7m0 0l7-7m-7 7h18"
            />
          </svg>
        </button>
        <h2 className="text-4xl font-extrabold mb-8 text-gray-900 text-center tracking-tight">Your Profile</h2>
        {profile.guestUserName && (
          <p className="text-xl text-gray-600 mb-6 text-center font-medium">Welcome, {profile.guestUserName}!</p>
        )}

        {updateMessage && (
          <div className="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded-lg relative mb-6" role="alert">
            <span className="block sm:inline">{updateMessage}</span>
          </div>
        )}
        {error && (
          <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg relative mb-6" role="alert">
            <span className="block sm:inline">{error}</span>
          </div>
        )}

        <div className="space-y-6">
          <div className="flex items-center p-3 bg-gray-50 rounded-lg border border-gray-200">
            <span className="font-semibold text-gray-700 w-1/3">Guest ID:</span>
            <span className="text-gray-900 font-medium w-2/3">{profile.guestId}</span>
          </div>
          <div className="flex items-center p-3 bg-gray-50 rounded-lg border border-gray-200">
            <span className="font-semibold text-gray-700 w-1/3">Username:</span>
            <span className="text-gray-900 font-medium w-2/3">{profile.guestUserName}</span>
          </div>

          {isEditing ? (
            <>
              <div className="mb-4">
                <label className="block text-gray-800 text-sm font-semibold mb-2" htmlFor="guestName">
                  Full Name:
                </label>
                <input
                  type="text"
                  id="guestName"
                  name="guestName"
                  className="shadow-sm appearance-none border border-gray-300 rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition duration-200"
                  value={profile.guestName || ''}
                  onChange={handleChange}
                  required
                />
              </div>
             
              <div className="mb-4">
                <label className="block text-gray-800 text-sm font-semibold mb-2" htmlFor="guestEmail">
                  Email:
                </label>
                <input
                  type="email"
                  id="guestEmail"
                  name="guestEmail" 
                  className="shadow-sm appearance-none border border-gray-300 rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition duration-200"
                  value={profile.guestEmail || ''}
                  onChange={handleChange}
                  required
                />
              </div>
              
              <div className="mb-4">
                <label className="block text-gray-800 text-sm font-semibold mb-2" htmlFor="guestContact">
                  Phone:
                </label>
                <input
                  type="text"
                  id="guestContact"
                  name="guestContact" 
                  className="shadow-sm appearance-none border border-gray-300 rounded-lg w-full py-3 px-4 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition duration-200"
                  value={profile.guestContact || ''}
                  onChange={handleChange}
                  required
                />
              </div>
              
              <div className="flex justify-end space-x-3 mt-6">
                <button
                  onClick={handleUpdate}
                  className="bg-green-600 hover:bg-green-700 text-white font-bold py-3 px-6 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500 focus:ring-opacity-75 transition duration-300 ease-in-out transform hover:scale-105"
                >
                  Save Changes
                </button>
                <button
                  onClick={() => {
                    setIsEditing(false);
                    const username = localStorage.getItem('username');
                    if (username) fetchProfile(username);
                  }}
                  className="bg-gray-400 hover:bg-gray-500 text-white font-bold py-3 px-6 rounded-lg focus:outline-none focus:ring-2 focus:ring-gray-500 focus:ring-opacity-75 transition duration-300 ease-in-out transform hover:scale-105"
                >
                  Cancel
                </button>
              </div>
            </>
          ) : (
            <>
              <div className="flex items-center p-3 bg-gray-50 rounded-lg border border-gray-200">
                <span className="font-semibold text-gray-700 w-1/3">Full Name:</span>
                <span className="text-gray-900 font-medium w-2/3">{profile.guestName}</span>
              </div>
              <div className="flex items-center p-3 bg-gray-50 rounded-lg border border-gray-200">
                <span className="font-semibold text-gray-700 w-1/3">Email:</span>
                <span className="text-gray-900 font-medium w-2/3">{profile.guestEmail}</span>
              </div>
              <div className="flex items-center p-3 bg-gray-50 rounded-lg border border-gray-200">
                <span className="font-semibold text-gray-700 w-1/3">Phone:</span>
                <span className="text-gray-900 font-medium w-2/3">{profile.guestContact || 'N/A'}</span>
              </div>
              <div className="flex justify-end mt-6">
                <button
                  onClick={() => setIsEditing(true)}
                  className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 px-6 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-opacity-75 transition duration-300 ease-in-out transform hover:scale-105"
                >
                  Edit Profile
                </button>
              </div>
            </>
          )}

        </div>
      </div>
    </div>
  );
};
export default EditProfile;