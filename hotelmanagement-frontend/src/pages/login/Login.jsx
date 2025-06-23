import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import authService from '../../services/AuthServices';
import toast from 'react-hot-toast'; 

function Login({ onLoginSuccess }) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loginType, setLoginType] = useState('guest'); 
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    setError(''); 

    try {
      let decodedToken;
      if (loginType === 'guest') {
        decodedToken = await authService.loginGuest(username, password);
      } else if (loginType === 'admin') {
        decodedToken = await authService.loginAdmin(username, password);
      } else { 
        decodedToken = await authService.loginManager(username, password);
      }

      if (decodedToken && decodedToken.role) {
        onLoginSuccess(username, decodedToken.userId);
        toast.success('Login successful!');
        switch (decodedToken.role) {
          case 'ADMIN':
            navigate('/admin/dashboard');
            break;
          case 'GUEST':
            navigate('/'); 
            break;
          case 'MANAGER':
            navigate('/manager/dashboard');
            break;
          default:
            setError('Unknown user role. Logging out.');
            authService.logout();
          
        }
      } else {
        setError('Authentication successful, but role not found in token.');
        authService.logout();
      }
    } catch (err) {
      console.error('Login error:', err);
      const errorMessage = err.response?.data?.message || err.message || 'Login failed. Please check your credentials.';
      setError(errorMessage);
      toast.error(errorMessage);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100">
      <div className="bg-white p-8 rounded-lg shadow-md w-full max-w-md">
        <h2 className="text-3xl font-bold text-center text-gray-800 mb-6">
          {loginType === 'guest' ? 'Guest Login' : loginType === 'admin' ? 'Admin Login' : 'Manager Login'}
        </h2>

        <div className="flex justify-center space-x-4 mb-6">
          <label className="inline-flex items-center">
            <input
              type="radio"
              className="form-radio h-5 w-5 text-blue-600"
              name="loginType"
              value="guest"
              checked={loginType === 'guest'}
              onChange={() => setLoginType('guest')}
            />
            <span className="ml-2 text-gray-700">Guest</span>
          </label>
          <label className="inline-flex items-center">
            <input
              type="radio"
              className="form-radio h-5 w-5 text-blue-600"
              name="loginType"
              value="admin"
              checked={loginType === 'admin'}
              onChange={() => setLoginType('admin')}
            />
            <span className="ml-2 text-gray-700">Admin</span>
          </label>
          <label className="inline-flex items-center">
            <input
              type="radio"
              className="form-radio h-5 w-5 text-blue-600"
              name="loginType"
              value="manager"
              checked={loginType === 'manager'}
              onChange={() => setLoginType('manager')}
            />
            <span className="ml-2 text-gray-700">Manager</span>
          </label>
        </div>

        <form onSubmit={handleLogin} className="space-y-4">
          <div>
            <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="username">
              Username:
            </label>
            <input
              type="text"
              id="username"
              className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
            />
          </div>
          <div>
            <label className="block text-gray-700 text-sm font-bold mb-2" htmlFor="password">
              Password:
            </label>
            <input
              type="password"
              id="password"
              className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 mb-3 leading-tight focus:outline-none focus:shadow-outline"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>
          <button
            type="submit"
            className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline w-full"
          >
            Login
          </button>
        </form>

        {error && <p className="text-red-500 text-sm mt-4 text-center">{error}</p>}
        {loginType === 'guest' && (
          <p className="text-center text-gray-600 mt-4">
            Don't have an account? <Link to="/register" className="text-blue-600 hover:underline">Register here</Link>
          </p>
        )}
      </div>
    </div>
  );
}

export default Login;