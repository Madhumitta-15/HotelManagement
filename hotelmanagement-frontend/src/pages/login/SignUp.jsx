import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import authService from '../../services/AuthServices';

function SignUp() {
  const [guestName, setGuestName] = useState('');
  const [guestUserName, setGuestUserName] = useState('');
  const [guestPassword, setGuestPassword] = useState('');
  const [guestEmail, setGuestEmail] = useState('');
  const [guestContact, setGuestPhoneNumber] = useState('');
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleRegister = async (e) => {
    e.preventDefault();
    setMessage('');
    setError('');
    try {
      const result = await authService.registerGuest(guestName, guestUserName, guestPassword, guestEmail, guestContact);
      setMessage(result || 'Registration successful! You can now log in.');
      setGuestName('');
      setGuestUserName(''); 
      setGuestPassword(''); 
      setGuestEmail('');    
      setGuestPhoneNumber(''); 
      navigate('/login'); 
    } catch (err) {
      setError(err.message || 'Registration failed. Please try again.');
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100">
      <div className="bg-white p-8 rounded-lg shadow-md w-full max-w-md">
        <h2 className="text-3xl font-bold text-center text-gray-800 mb-6">Guest Register</h2>
        <form onSubmit={handleRegister} className="space-y-4">
          <div>
            <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="guestName">
              Name:
            </label>
            <input
              type="text"
              id="guestName"
              className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
              value={guestName}
              onChange={(e) => setGuestName(e.target.value)}
              required
            />
          </div>
          <div>
            <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="guestUserName">
              Username:
            </label>
            <input
              type="text"
              id="guestUserName"
              className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
              value={guestUserName}
              onChange={(e) => setGuestUserName(e.target.value)}
              required
            />
          </div>
          <div>
            <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="guestPassword">
              Password:
            </label>
            <input
              type="password"
              id="guestPassword"
              className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 mb-3 leading-tight focus:outline-none focus:shadow-outline"
              value={guestPassword}
              onChange={(e) => setGuestPassword(e.target.value)}
              required
            />
          </div>
          <div>
            <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="guestEmail">
              Email:
            </label>
            <input
              type="email"
              id="guestEmail"
              className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
              value={guestEmail}
              onChange={(e) => setGuestEmail(e.target.value)}
              required
            />
          </div>
          <div>
            <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="guestContact">
              Phone Number:
            </label>
            <input
              type="text"
              id="guestContact"
              className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
              value={guestContact}
              onChange={(e) => setGuestPhoneNumber(e.target.value)}
              required
            />
          </div>
          <button
            type="submit"
            className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline w-full"
          >
            Register
          </button>
        </form>
        {message && <p className="text-green-500 text-sm mt-4 text-center">{message}</p>}
        {error && <p className="text-red-500 text-sm mt-4 text-center">{error}</p>}
        <p className="text-center text-gray-600 mt-4">
          Already have an account? <Link to="/login" className="text-blue-600 hover:underline">Login here</Link>
        </p>
      </div>
    </div>
  );
}

export default SignUp;