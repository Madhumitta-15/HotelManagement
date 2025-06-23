import React, { useState, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import PageContainer from "../../components/ui/PageContainer";
import axios from "axios";
import Card from "../../components/ui/Card";

function Explore() {
  const location = useLocation();
  const navigate = useNavigate();
  const params = new URLSearchParams(location.search);

  const [filteredHotels, setFilteredHotels] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const searchData = {
    location: params.get("location"),
    checkIn: params.get("checkIn"),
    checkOut: params.get("checkOut"),
    guests: params.get("guests"),
  };

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

    const fetchHotelsByLocation = async () => {
      setLoading(true);
      setError(null);
      try {
        if (searchData.location) {
          const response = await axios.get(
            `http://localhost:8095/hotel/findhotel/by-location?location=${searchData.location}`
          );

          const hotels = response.data;

          // Fetch ratings in parallel
          const hotelsWithRatings = await Promise.all(
            hotels.map(async (hotel) => {
              const rating = await getHotelReviewSummary(hotel.hotelId);
              return { ...hotel, rating };
            })
          );

          setFilteredHotels(hotelsWithRatings);
        } else {
          setFilteredHotels([]);
        }
        setLoading(false);
      } catch (err) {
        console.error("Error fetching hotels by location:", err);
        setError("Failed to find hotels for this location. Please try a different search.");
        setLoading(false);
      }
    };

    fetchHotelsByLocation();
  }, [searchData.location]);

  const handleViewDetails = (hotelId) => {
    navigate(`/hotel/${hotelId}`);
  };

  if (loading) {
    return (
      <PageContainer>
        <div className="p-6 max-w-5xl mx-auto text-center">
          <p className="text-xl text-gray-700">
            Searching for hotels in {searchData.location || "all locations"}...
          </p>
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
        <h1 className="text-3xl font-bold mb-4">
          Explore Hotels in {searchData.location || "All Locations"}
        </h1>
        {searchData.checkIn && (
          <p className="mb-6 text-gray-600">
            {searchData.guests} guest(s) | {searchData.checkIn} to {searchData.checkOut}
          </p>
        )}

        {filteredHotels.length === 0 ? (
          <div className="text-gray-500 italic">
            No hotels found in {searchData.location || "the specified criteria"}.
          </div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
            {filteredHotels.map((hotel) => (
              <Card key={hotel.hotelId} hotel={hotel} onViewDetails={handleViewDetails} />
            ))}
          </div>
        )}
      </div>
    </PageContainer>
  );
}

export default Explore;
