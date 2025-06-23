import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import PageContainer from "./ui/PageContainer";
import axios from "axios";
import Card from "./ui/Card"; 

function HomeHotel() {
  const navigate = useNavigate();
  const [hotels, setHotels] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const getHotelReviewSummary = async (hotelId) => {
      try {
        const response = await axios.get(`http://localhost:8095/review/summary/${hotelId}`);
        return response.data.overallRating || 0;
      } catch (error) {
        console.error(`Failed to fetch summary for hotel ${hotelId}:`, error);
        return 0;
      }
    };

    const fetchAllHotels = async () => {
      setLoading(true);
      setError(null);
      try {
        const response = await axios.get("http://localhost:8095/hotel/gethotelslist");
        const hotelsData = response.data;
        const hotelsWithRatings = await Promise.all(
          hotelsData.map(async (hotel) => {
            const rating = await getHotelReviewSummary(hotel.hotelId);
            return { ...hotel, rating };
          })
        );
        const sortedHotels = hotelsWithRatings.sort((a, b) => (b.rating || 0) - (a.rating || 0));
        setHotels(sortedHotels);
      } catch (err) {
        console.error("Error fetching hotels:", err);
        setError("Failed to load hotels. Please try again later.");
      } finally {
        setLoading(false);
      }
    };

    fetchAllHotels();
  }, []);

  const handleViewDetails = (hotelId) => {
    navigate(`/hotel/${hotelId}`);
  };

  if (loading) {
    return (
      <PageContainer>
        <div className="p-6 max-w-5xl mx-auto text-center">
          <p className="text-xl text-gray-700">Loading hotels...</p>
        </div>
      </PageContainer>
    );
  }

  if (error) {
    return (
      <PageContainer>
        <div className="p-6 max-w-5xl mx-auto text-center">
          <p className="text-xl text-red-600">{error}</p>
        </div>
      </PageContainer>
    );
  }

  return (
    <PageContainer>
      <div className="p-6 max-w-5xl mx-auto">
        <h2 className="text-3xl font-bold text-center text-blue-700 mb-8">Top LuxeStay Hotels</h2>
        {hotels.length === 0 ? (
          <div className="text-gray-500 italic text-center">
            No hotels available at the moment.
          </div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
            {hotels.map((hotel) => (
              <Card key={hotel.hotelId} hotel={hotel} onViewDetails={handleViewDetails} />
            ))}
          </div>
        )}
      </div>
    </PageContainer>
  );
}

export default HomeHotel;