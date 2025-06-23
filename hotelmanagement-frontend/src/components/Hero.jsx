import { motion } from "framer-motion";
import React, { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { Button } from "./ui/Button";

function Hero({ isLoggedIn, userName, userId }) {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    location: "",
    checkIn: "",
    checkOut: "",
    guests: 1,
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSearch = () => {
    const params = new URLSearchParams(formData).toString();
    navigate(`/explore?${params}`);
  };

  return (
    <section className="text-center py-20 px-4 bg-gradient-to-r from-blue-100 to-indigo-100 text-black">
      {isLoggedIn ? (
        <motion.div
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          className="mb-10" 
        >
          <h1 className="text-5xl font-bold mb-4">Welcome back, {userName}!</h1>
          <p className="text-xl max-w-2xl mx-auto">
            Ready to explore new destinations? Use the search below.
          </p>
    </motion.div>
      ) : (
        <motion.div
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
        >
          <h1 className="text-5xl font-bold mb-4">Welcome to LuxeStay</h1>
          <p className="text-xl mb-8 max-w-2xl mx-auto">
            Discover luxury stays tailored for comfort and elegance
          </p>
        </motion.div>
      )}

      
      <div className="bg-white p-6 rounded-xl shadow-md max-w-4xl mx-auto grid grid-cols-1 md:grid-cols-4 gap-4 items-end">
        <div>
          <label className="block text-left font-semibold mb-1 text-sm sm:text-base text-gray-800">Location</label>
          <input
            name="location"
            value={formData.location}
            onChange={handleChange}
            type="text"
            placeholder="City or Area"
            className="w-full border border-gray-300 rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-blue-500 text-gray-800"
          />
        </div>

        <div>
          <label className="block text-left font-semibold mb-1 text-sm sm:text-base text-gray-800">Check-In</label>
          <input
            name="checkIn"
            value={formData.checkIn}
            onChange={handleChange}
            type="date"
            className="w-full border border-gray-300 rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-blue-500 text-gray-800"
          />
        </div>

        <div>
          <label className="block text-left font-semibold mb-1 text-sm sm:text-base text-gray-800">Check-Out</label>
          <input
            name="checkOut"
            value={formData.checkOut}
            onChange={handleChange}
            type="date"
            className="w-full border border-gray-300 rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-blue-500 text-gray-800"
          />
        </div>

        <div>
          <label className="block text-left font-semibold mb-1 text-sm sm:text-base text-gray-800">Guests</label>
          <input
            name="guests"
            value={formData.guests}
            onChange={handleChange}
            type="number"
            min="1"
            className="w-full border border-gray-300 rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-blue-500 text-gray-800"
          />
        </div>

        <div className="md:col-span-4">
          <Button onClick={handleSearch} className="w-full md:w-auto mt-4 md:mt-0">
            Search Hotels
          </Button>
        </div>
      </div>
    </section>
  );
}

export default Hero;