import React, { useState } from "react";
import { Button } from "./ui/Button";
import { Link, useNavigate } from "react-router-dom";
import { FaUserCircle } from "react-icons/fa";

function Navbar({ isLoggedIn, userName, userId, handleLogout }) {
  const [showUserMenu, setShowUserMenu] = useState(false);

  const toggleUserMenu = () => {
    setShowUserMenu(!showUserMenu);
  };

  return (
    <header className="w-full shadow-md bg-white sticky top-0 z-50">
      <nav className="max-w-6xl mx-auto flex justify-between items-center px-4 py-3">
        {/* Logo */}
        <Link to="/" className="text-2xl font-extrabold text-blue-600 hover:text-blue-800">
          LuxeStay
        </Link>

        {/* Navigation Links */}
        <div className="flex items-center gap-4 relative">
          <Link
            to="/about"
            className="text-gray-700 hover:text-blue-600 transition-colors duration-200"
          >
            About
          </Link>
          <Link
            to="/contact"
            className="text-gray-700 hover:text-blue-600 transition-colors duration-200"
          >
            Contact
          </Link>

          {/* Auth Buttons or User Icon */}
          {!isLoggedIn ? (
            <>
              <Link to="/login">
                <Button variant="outline">Login</Button>
              </Link>
              <Link to="/signup">
                <Button variant="primary">Sign Up</Button>
              </Link>
            </>
          ) : (
            <div className="relative flex items-center gap-4 min-w-[150px] h-12">
              <button
                onClick={toggleUserMenu}
                className="text-gray-700 hover:text-blue-600 focus:outline-none flex flex-row-reverse items-center gap-3 text-base h-full"
                aria-label="User menu"
              >
                <span className="text-gray-700 font-medium text-base leading-none">{userName}</span>
                <FaUserCircle className="text-xl" />
              </button>
              {showUserMenu && (
                <div className="absolute right-0 top-full mt-1 w-48 bg-white border border-gray-200 rounded-md shadow-lg z-50 flex flex-col">
                  <Link
                    to={`/${userId}/edit-profile`}
                    className="px-4 py-2 text-gray-700 hover:bg-blue-100"
                    onClick={() => setShowUserMenu(false)}
                  >
                    Edit Profile
                  </Link>
                  <Link
                    to={`/${userId}/my-bookings`}
                    className="px-4 py-2 text-gray-700 hover:bg-blue-100"
                    onClick={() => setShowUserMenu(false)}
                  >
                    My Bookings
                  </Link>
                  <Link
                    to={`/${userId}/my-loyalty-points`}
                    className="px-4 py-2 text-gray-700 hover:bg-blue-100"
                    onClick={() => setShowUserMenu(false)}
                  >
                    My Loyalty Points
                  </Link>
                  <button
                    onClick={() => {
                      setShowUserMenu(false);
                      handleLogout();
                    }}
                    className="text-left px-4 py-2 text-gray-700 hover:bg-blue-100 focus:outline-none"
                  >
                    Logout
                  </button>
                </div>
              )}
            </div>
          )}
        </div>
      </nav>
    </header>
  );
}

export default Navbar;