import React, { useState, useEffect, useMemo } from 'react';
import axios from 'axios';
import authService from '../../services/AuthServices';
import { useNavigate,useParams} from 'react-router-dom';
import toast from 'react-hot-toast';
 
const API_BASE_URL = 'http://localhost:8095';
 
const MyBookings = () => {
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const { userId } = useParams();
  const [currentUserId, setCurrentUserId] = useState(null);
  const [currentUsername, setCurrentUsername] = useState('');
 
  useEffect(() => {
    const token = authService.getCurrentUserToken();
    if (!token) {
      setError('No authentication token found. Please log in.');
      setLoading(false);
      return;
    }
 
    const userId = localStorage.getItem('userId');
    const username = localStorage.getItem('username');
 
    if (!userId || !username) {
      setError('User ID or Username not found in local storage. Please log in again.');
      setLoading(false);
      authService.logout();
      return;
    }
 
    setCurrentUserId(userId);
    setCurrentUsername(username);
 
    const fetchBookings = async () => {
      try {
        authService.setAuthHeader();
        const response = await axios.get(`${API_BASE_URL}/bookings/guest/${userId}`);
        setBookings(response.data);
      } catch (err) {
        setError(err.response?.data?.message || 'Failed to fetch booking details. Server might be down or endpoint incorrect.');
        console.error('Bookings fetch error:', err.response?.data || err.message || err);
      } finally {
        setLoading(false);
      }
    };
 
    fetchBookings();
  }, []);
 
  const mostRecentBooking = useMemo(() => {
    if (bookings.length === 0) {
      return null;
    }
    const sortedBookings = [...bookings].sort((a, b) => b.bookingId - a.bookingId);
    return sortedBookings[0];
  }, [bookings]);
 
  const handleCancelBooking = async (bookingId) => {
    if (!window.confirm(`Are you sure you want to cancel the most recent booking (ID: ${bookingId})? This action cannot be undone.`)) {
      return;
    }
    setLoading(true);
    try {
      authService.setAuthHeader();
      await axios.delete(`${API_BASE_URL}/bookings/cancel/${bookingId}`);
      setBookings(prevBookings => prevBookings.filter(booking => booking.bookingId !== bookingId));
      alert(`Booking ${bookingId} canceled successfully.`);
      toast.success("Refunded Amount will be reaching you in 4-5 business days");
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to cancel booking. Please try again.');
      console.error('Booking cancellation error:', err.response?.data || err.message || err);
      if (err.response?.status === 401){
        authService.logout();
        navigate('/login');
      }
    } finally {
      setLoading(false);
    }
  };
 
  if (loading) return <div className="p-8 text-center text-gray-600">Loading booking status...</div>;
  if (error) return <div className="p-8 text-center text-red-500">Error: {error}</div>;
 
  return (
    <div className="p-8 bg-white rounded-lg shadow-md max-w-4xl mx-auto my-8 relative">
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
      <h2 className="text-3xl font-bold mb-6 text-gray-800 text-center">Your Bookings</h2>
      {currentUsername && (
        <p className="text-xl text-gray-700 mb-4 text-center">Bookings for: {currentUsername}</p>
      )}
      {bookings.length === 0 ? (
        <p className="text-center text-gray-500">No bookings found for this user.</p>
      ) : (
        <div className="overflow-x-auto">
          <table className="min-w-full bg-white border border-gray-200 rounded-lg overflow-hidden">
            <thead className="bg-gray-100">
              <tr>
                <th className="py-3 px-4 border-b border-gray-200 text-left text-sm font-semibold text-gray-600 uppercase">Booking ID</th>
                <th className="py-3 px-4 border-b border-gray-200 text-left text-sm font-semibold text-gray-600 uppercase">Room ID</th>
                <th className="py-3 px-4 border-b border-gray-200 text-left text-sm font-semibold text-gray-600 uppercase">Check-in</th>
                <th className="py-3 px-4 border-b border-gray-200 text-left text-sm font-semibold text-gray-600 uppercase">Check-out</th>
                <th className="py-3 px-4 border-b border-gray-200 text-left text-sm font-semibold text-gray-600 uppercase">Room Type</th>
                <th className="py-3 px-4 border-b border-gray-200 text-left text-sm font-semibold text-gray-600 uppercase">Rooms</th>
                <th className="py-3 px-4 border-b border-gray-200 text-left text-sm font-semibold text-gray-600 uppercase">Total Amount</th>
                <th className="py-3 px-4 border-b border-gray-200 text-left text-sm font-semibold text-gray-600 uppercase">Payment Status</th>
                <th className="py-3 px-4 border-b border-gray-200 text-left text-sm font-semibold text-gray-600 uppercase"></th>
              </tr>
            </thead>
            <tbody>
              {bookings.map((booking) => (
                <tr key={booking.bookingId} className="hover:bg-gray-50 transition duration-150">
                  <td className="py-3 px-4 border-b border-gray-200 text-sm text-gray-800">{booking.bookingId}</td>
                  <td className="py-3 px-4 border-b border-gray-200 text-sm text-gray-800">{booking.roomId}</td>
                  <td className="py-3 px-4 border-b border-gray-200 text-sm text-gray-800">{booking.checkInDate}</td>
                  <td className="py-3 px-4 border-b border-gray-200 text-sm text-gray-800">{booking.checkOutDate}</td>
                  <td className="py-3 px-4 border-b border-gray-200 text-sm text-gray-800">{booking.roomType}</td>
                  <td className="py-3 px-4 border-b border-gray-200 text-sm text-gray-800">{booking.numberOfRooms}</td>
                  <td className="py-3 px-4 border-b border-gray-200 text-sm text-gray-800">₹{booking.amountPaid?.toFixed(2) || 'N/A'}</td>
                  <td className="py-3 px-4 border-b border-gray-200 text-sm text-gray-800">
                    <span className={`px-2 py-1 rounded-full text-xs font-semibold ${
                      booking.paymentStatus === 'SUCCESS' ? 'bg-green-100 text-green-800' :
                      booking.paymentStatus === 'PENDING' ? 'bg-yellow-100 text-yellow-800' :
                      'bg-red-100 text-red-800'
                    }`}>
                      {booking.paymentStatus || 'N/A'}
                    </span>
                  </td>
                  <td className="py-3 px-4 border-b border-gray-200 text-sm text-gray-800">
                    {mostRecentBooking && booking.bookingId === mostRecentBooking.bookingId && booking.paymentStatus === 'SUCCESS' && (
                      <button
                        onClick={() => handleCancelBooking(booking.bookingId)}
                        className="bg-red-500 hover:bg-red-600 text-white font-bold py-1 px-3 rounded text-xs transition duration-150"
                      >
                        Cancel
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};
 
export default MyBookings;
 