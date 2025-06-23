import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import authService from '../../services/AuthServices';
 
const API_BASE_URL = 'http://localhost:8095';
 
const LoyaltyPoints = () => {
  const [loyaltyBalance, setLoyaltyBalance] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const [currentUsername, setCurrentUsername] = useState('');
 
  useEffect(() => {
    const token = authService.getCurrentUserToken();
 
    if (!token) {
      setError('No authentication token found. Please log in.');
      setLoading(false);
      return;
    }
 
    const username = localStorage.getItem('username');
 
    if (!username) {
      setError('Username not found in local storage. Please log in again.');
      setLoading(false);
      authService.logout();
      return;
    }
 
    setCurrentUsername(username);
 
    const fetchLoyaltyBalance = async () => {
      try {
        authService.setAuthHeader();
        const response = await axios.get(`${API_BASE_URL}/loyalty/balance`);
        setLoyaltyBalance(response.data);
      } catch (err) {
        setError(
          err.response?.data?.message ||
          'Failed to fetch loyalty points. Server might be down or unauthorized.'
        );
        console.error('Loyalty points fetch error:', err);
      } finally {
        setLoading(false);
      }
    };
 
    fetchLoyaltyBalance();
  }, []);
 
  if (loading) return <div className="p-8 text-center text-gray-600">Loading loyalty points...</div>;
  if (error) return <div className="p-8 text-center text-red-500">Error: {error}</div>;
 
  return (
    <div className="p-8 bg-white rounded-lg shadow-md max-w-xl mx-auto my-8 text-center relative">
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
 
      <h2 className="text-3xl font-bold mb-6 text-gray-800">Your Loyalty Points</h2>
 
      {currentUsername && (
        <p className="text-xl text-gray-700 mb-4">For: {currentUsername}</p>
      )}
 
      {loyaltyBalance !== null ? (
        <p className="text-5xl font-extrabold text-blue-600">{loyaltyBalance}</p>
      ) : (
        <p className="text-lg text-gray-500">Loyalty balance not available.</p>
      )}
 
      <p className="text-md text-gray-600 mt-4">
        Earn more points with every booking!
      </p>
    </div>
  );
};
 
export default LoyaltyPoints;