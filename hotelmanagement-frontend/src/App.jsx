import React, { useState, useEffect } from 'react';
import { Routes, Route, Link, useNavigate, useLocation } from 'react-router-dom';
import { Toaster } from 'react-hot-toast'; 

// Services
import authService from './services/AuthServices';

// Router component
import ProtectedRoute from './router/ProtectedRoute';

// Page Components
import RegisterPage from './pages/login/SignUp';
import Login from './pages/login/Login';
import LandingPageLayout from './pages/landingPage/LandingPage';
import Explore from './pages/searchHotels/Explore';
import About from './pages/landingPage/About';
import ContactUs from './pages/landingPage/ContactUs'; 
import SignUp from './pages/login/SignUp'; 

//Exception
import UnauthorizedPage from './pages/Exception/UnauthorizedPage';
import NotFoundPage from './pages/Exception/NotFound';

// User Dashboard Pages
import EditProfile from './pages/userDashboard/EditProfile';
import LoyaltyPoints from './pages/userDashboard/LoyaltyPoints';
import MyBookings from './pages/userDashboard/MyBookings'; 

// Booking Pages 
import BookingPage from './pages/Booking/BookingPage';
import PaymentPage from './pages/Booking/PaymentPage';
import BookingSuccess from './pages/Booking/BookingSuccess';
import BookingFailed from './pages/Booking/BookingFailed';
import HotelDetailsPage from './pages/searchHotels/HotelDetailsPage';
import AddReviewPage from './pages/searchHotels/AddReviewPage'; 

// Dashboard Components for specific roles 
import AdminDashboard from './pages/userDashboard/AdminDashboard';
import ManagerDashboard from './pages/userDashboard/ManagerDashboard';

import Navbar from './components/Navbar'; 
import './App.css';




// --- Main App Component ---
function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [userName, setUserName] = useState('');
  const [userId, setUserId] = useState('');
  const navigate = useNavigate();
  const location = useLocation();
  useEffect(() => {
    const storedIsLoggedIn = localStorage.getItem('isLoggedIn');
    const storedUserName = localStorage.getItem('username');
    const storedUserId = localStorage.getItem('userId');

    if (storedIsLoggedIn === 'true' && storedUserName && storedUserId) {
      setIsLoggedIn(true);
      setUserName(storedUserName);
      setUserId(storedUserId);
    } else {
      setIsLoggedIn(false);
      setUserName('');
      setUserId('');
    }
  }, []); 

  const handleLoginSuccess = (username, id) => {
    setIsLoggedIn(true);
    setUserName(username);
    setUserId(id);
    
  };

  const handleLogout = () => {
    authService.logout();
    setIsLoggedIn(false);
    setUserName('');
    setUserId('');
    navigate('/');
  };

  const isNavbarVisible = (
    location.pathname === '/' || 
    location.pathname === '/about' ||
    location.pathname === '/contact' ||
    location.pathname === '/explore' ||
    location.pathname === '/login' ||
    location.pathname === '/register' ||
    location.pathname === '/signup' ||
    location.pathname === '/unauthorized' ||
    location.pathname.startsWith('/hotel/') ||
    location.pathname.startsWith('/booking/') ||
    location.pathname.startsWith('/payment') || 
    (!location.pathname.startsWith('/admin/dashboard') && !location.pathname.startsWith('/manager/dashboard')) &&
    (userId && location.pathname.startsWith(`/${userId}`))
  );

  return (
    <div className="app-container">
      {isNavbarVisible && (
        <Navbar
          isLoggedIn={isLoggedIn}
          userName={userName}
          userId={userId}
          handleLogout={handleLogout}
        />
      )}
      <Toaster richColors position="top-center" />

      <main className="app-main flex-grow">
        <Routes>
          <Route
            path="/"
            element={<LandingPageLayout isLoggedIn={isLoggedIn} userName={userName} userId={userId} />}
          />
          <Route path="/about" element={<About />} />
          <Route path="/contact" element={<ContactUs />} />
          <Route path="/explore" element={<Explore />} />
          <Route path="/login" element={<Login onLoginSuccess={handleLoginSuccess} />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/signup" element={<SignUp />} />
          <Route path="/unauthorized" element={<UnauthorizedPage />} />
          <Route path="/booking-success" element={<BookingSuccess />} />
          <Route path="/booking-failed" element={<BookingFailed />} />

          
          <Route path="/hotel/:hotelId" element={<HotelDetailsPage />} />
          <Route path="/booking/:hotelId" element={<BookingPage />} />
          <Route path="/payment" element={<PaymentPage />} />
          <Route path="/add-review/:hotelId" element={<AddReviewPage/>}/>


          
          <Route element={<ProtectedRoute allowedRoles={['ADMIN']} />}>
            <Route path="/admin/dashboard" element={<AdminDashboard />} />
          </Route>

          
          <Route element={<ProtectedRoute allowedRoles={['MANAGER']} />}>
            <Route path="/manager/dashboard" element={<ManagerDashboard />} />
          </Route>

          
          <Route element={<ProtectedRoute allowedRoles={['GUEST']} />}>
            <Route
              path="/:userId/edit-profile"
              element={<EditProfile />}
            />
            <Route
              path="/:userId/my-bookings"
              element={<MyBookings />}
            />
            <Route
              path="/:userId/my-loyalty-points"
              element={<LoyaltyPoints />}
            />
          </Route>
          <Route path="*" element={<NotFoundPage />} />
        </Routes>
      </main>
    </div>
  );
}
export default App;