import React from "react";
import Hero from "../../components/Hero"; 
import HomeHotel from "../../components/HomeHotel"; 
import Features from "../../components/Features"; 
import Footer from "../../components/Footer"; 
import { FaBed, FaMapMarkerAlt, FaStar } from "react-icons/fa";
export default function LandingPageLayout({ isLoggedIn, userName, userId }) {
  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-100 to-white text-gray-800">
     
      <Hero isLoggedIn={isLoggedIn} userName={userName} userId={userId} />

      <HomeHotel />

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 px-6 py-12 max-w-6xl mx-auto">
        <Features
          icon={FaBed}
          title="Luxury Rooms"
          description="Stay in beautifully designed rooms with top-tier amenities for every guest."
        />
        <Features
          icon={FaMapMarkerAlt}
          title="Top Locations"
          description="Choose from prime properties in the best city and vacation destinations."
        />
        <Features
          icon={FaStar}
          title="Rated 5 Stars"
          description="Trusted by thousands of guests with consistent five-star ratings."
        />
      </div>

      <Footer />
    </div>
  );
}