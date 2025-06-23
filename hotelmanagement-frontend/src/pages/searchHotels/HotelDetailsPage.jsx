import React, { useEffect, useState, useMemo } from "react";
import { useParams, useLocation, useNavigate } from "react-router-dom";
import axios from "axios";
import RoomCard from "../../components/RoomCard";
import BookingSummaryCard from "../../components/BookingSummaryCard";
import AllReviewsModal from "./AllReviewsModal";
import authService from '../../services/AuthServices';

const parseCustomDate = (dateString) => {
    if (!dateString) return null;
    const [datePart, timePart] = dateString.split(' ');
    if (!timePart) {
        const [year, month, day] = datePart.split('-').map(Number);
        return new Date(Date.UTC(year, month - 1, day));
    }
    const [year, month, day] = datePart.split('-').map(Number);
    const [hours, minutes, seconds] = timePart.split(':').map(Number);
    return new Date(Date.UTC(year, month - 1, day, hours, minutes, seconds));
};

function HotelDetailsPage() {
    const { hotelId } = useParams();
    const location = useLocation();
    const navigate = useNavigate();

    const initialCheckIn = new URLSearchParams(location.search).get("checkIn");
    const initialCheckOut = new URLSearchParams(location.search).get("checkOut");

    const [hotel, setHotel] = useState(null);
    const [rooms, setRooms] = useState([]);
    const [selectedRoom, setSelectedRoom] = useState(null);
    const [loadingHotel, setLoadingHotel] = useState(true);
    const [loadingRooms, setLoadingRooms] = useState(true);
    const [error, setError] = useState("");
    const [reviews, setReviews] = useState([]);
    const [reviewSummary, setReviewSummary] = useState(null);
    const [showAllReviewsModal, setShowAllReviewsModal] = useState(false);
    const [allReviewsForModal, setAllReviewsForModal] = useState([]);
    const [loadingReviews, setLoadingReviews] = useState(true);
    const [loadingReviewSummary, setLoadingReviewSummary] = useState(true);
    const [loadingAllReviewsForModal, setLoadingAllReviewsForModal] = useState(false);

    const [checkInDate, setCheckInDate] = useState(initialCheckIn ? new Date(initialCheckIn) : null);
    const [checkOutDate, setCheckOutDate] = useState(initialCheckOut ? new Date(initialCheckOut) : null);
    const [numberOfGuests, setNumberOfGuests] = useState(
        parseInt(new URLSearchParams(location.search).get("guests")) || 1
    );

    useEffect(() => {
        const fetchHotel = async () => {
            try {
                authService.setAuthHeader();
                const res = await axios.get(`http://localhost:8095/hotel/gethoteldetails/${hotelId}`);
                setHotel(res.data);
            } catch (err) {
                console.error("Failed to load hotel details:", err);
                if (err.response && err.response.status === 401) {
                    setError("You need to be logged in to view hotel details.");
                } else {
                    setError("Failed to load hotel details.");
                }
            } finally {
                setLoadingHotel(false);
            }
        };
        fetchHotel();
    }, [hotelId]);

    useEffect(() => {
        const fetchHotelRooms = async () => {
            try {
                authService.setAuthHeader();
                const response = await axios.get(`http://localhost:8095/rooms/getallrooms/${hotelId}`);
                const fetchedRooms = response.data;
                const availableRooms = fetchedRooms.filter(room => room.availability > 0);
                setRooms(fetchedRooms);

                if (availableRooms.length > 0 && !selectedRoom) {
                    setSelectedRoom(availableRooms[0]);
                } else if (availableRooms.length === 0) {
                    setSelectedRoom(null);
                }

                if (!checkInDate && !checkOutDate) {
                    const today = new Date();
                    const tomorrow = new Date(today);
                    tomorrow.setDate(today.getDate() + 1);
                    const dayAfterTomorrow = new Date(tomorrow);
                    dayAfterTomorrow.setDate(tomorrow.getDate() + 1);
                    setCheckInDate(tomorrow);
                    setCheckOutDate(dayAfterTomorrow);
                }

            } catch (err) {
                console.error("Failed to load rooms:", err);
                if (err.response && err.response.status === 401) {
                    setError("You need to be logged in to view room details.");
                } else {
                    setError("Failed to load room details.");
                }
                setRooms([]);
            } finally {
                setLoadingRooms(false);
            }
        };
        fetchHotelRooms();
    }, [hotelId, checkInDate, checkOutDate, selectedRoom]);

    useEffect(() => {
        const fetchReviews = async () => {
            try {
                const res = await axios.get(`http://localhost:8095/review/${hotelId}`);
                setReviews(res.data.reviews.slice(0, 2) || []);
            } catch (err) {
                console.error("Failed to load initial reviews:", err);
                setReviews([]);
            } finally {
                setLoadingReviews(false);
            }
        };
        fetchReviews();
    }, [hotelId]);

    useEffect(() => {
        const fetchReviewSummary = async () => {
            setLoadingReviewSummary(true);
            try {
                const res = await axios.get(`http://localhost:8095/review/summary/${hotelId}`);
                setReviewSummary(res.data);
            } catch (err) {
                console.error("Failed to load review summary:", err);
                setReviewSummary(null);
            } finally {
                setLoadingReviewSummary(false);
            }
        };
        fetchReviewSummary();
    }, [hotelId]);

    const handleSeeAllReviews = async () => {
        setLoadingAllReviewsForModal(true);
        try {
            const res = await axios.get(`http://localhost:8095/review/${hotelId}`);
            setAllReviewsForModal(res.data.reviews || []);
        } catch (err) {
            console.error("Failed to load all reviews for modal:", err);
            setAllReviewsForModal([]);
            setError("Failed to load all reviews for the modal. Please try again.");
        } finally {
            setLoadingAllReviewsForModal(false);
            setShowAllReviewsModal(true);
        }
    };
    const handleAddReviewClick = () => {
        navigate(`/add-review/${hotelId}`, { state: { hotelName: hotel.hotelName, hotelId: hotelId } });
    };
    const { roomPricePerNight, subtotalAmount, numberOfNights } = useMemo(() => {
        const roomPrice = selectedRoom ? selectedRoom.price : 0;
        const nights = checkInDate && checkOutDate ?
            Math.ceil((checkOutDate.getTime() - checkInDate.getTime()) / (1000 * 60 * 60 * 24)) : 0;

        const pricePerNight = roomPrice;
        const subtotal = pricePerNight * numberOfGuests * Math.max(1, nights);

        return {
            roomPricePerNight: parseFloat(pricePerNight.toFixed(2)),
            subtotalAmount: parseFloat(subtotal.toFixed(2)),
            numberOfNights: nights
        };
    }, [selectedRoom, checkInDate, checkOutDate, numberOfGuests]);

    const handleContinueToBook = () => {
        const token = localStorage.getItem("jwtToken");
        if (!selectedRoom || !checkInDate || !checkOutDate || numberOfGuests <= 0 || numberOfNights <= 0) {
            alert("Please select a room, valid check-in/check-out dates, and number of guests to continue.");
            return;
        }
        const bookingDetails = {
            hotelId: hotelId,
            hotelName: hotel.hotelName,
            roomId: selectedRoom.roomId,
            roomType: selectedRoom.type,
            checkIn: checkInDate.toISOString(),
            checkOut: checkOutDate.toISOString(),
            guests: numberOfGuests,
            roomPricePerNight: roomPricePerNight,
            numberOfNights: numberOfNights,
            subtotalBeforeLoyalty: subtotalAmount,
        }
        if (!token) {
            alert("Please login to continue.");
            navigate("/login", { state: { from: `/booking/${hotelId}`, bookingDetails: bookingDetails } });
        } else {
            navigate(`/booking/${hotelId}`, { state: { bookingDetails: bookingDetails } });
        }
    };
    if (loadingHotel || loadingRooms) {
        return <p className="p-6 text-center text-gray-700">Loading hotel details and rooms...</p>;
    }
    if (error) {
        return <p className="p-6 text-center text-red-600">{error}</p>;
    }
    if (!hotel) {
        return <p className="p-6 text-center text-red-600">Hotel not found.</p>;
    }

    const isContinueToBookDisabled = !selectedRoom || selectedRoom.availability <= 0 || !checkInDate || !checkOutDate || numberOfGuests <= 0 || numberOfNights <= 0;
    return (
        <div className="min-h-screen bg-gray-100 font-sans">
            <div className="max-w-6xl mx-auto p-4 flex flex-col lg:flex-row gap-6">
                <div className="flex-1 bg-white p-6 rounded-lg shadow-md">
                    <div className="flex items-center justify-between mb-4">
                        <div>
                            <h1 className="text-3xl font-bold text-gray-900">{hotel.hotelName}</h1>
                            <p className="text-sm text-gray-600">{hotel.location}</p>
                            <div className="flex items-center text-green-600 text-sm mt-1">
                                <span className="font-semibold mr-1">5.0</span> Check-in rating: Delightful experience
                            </div>
                        </div>
                        <span className="bg-red-500 text-white text-xs px-2 py-1 rounded">NEW</span>
                    </div>

                    <img
                        src={hotel.imageUrl}
                        alt={hotel.hotelName}
                        className="w-full h-96 object-cover rounded-lg mb-6"
                    />

                    <div className="mb-6">
                        <h2 className="text-xl font-semibold text-gray-800 mb-2">Description</h2>
                        <p className="text-gray-700">
                            {hotel.description}
                        </p>
                    </div>
                    <div className="mb-6">
                        <h2 className="text-xl font-semibold text-gray-800 mb-2">Choose your room</h2>
                        <div className="space-y-4">
                            {loadingRooms ? (
                                <p>Loading rooms...</p>
                            ) : rooms.length > 0 ? (
                                rooms.map((room) => (
                                    <RoomCard
                                        key={room.roomId}
                                        room={room}
                                        isSelected={selectedRoom && selectedRoom.roomId === room.roomId}
                                        onSelect={() => setSelectedRoom(room)}
                                        isAvailable={room.availability > 0}
                                    />
                                ))
                            ) : (
                                <p className="text-gray-500">No rooms currently available at this hotel.</p>
                            )}
                        </div>
                    </div>
                    <div className="mb-6">
                        <h2 className="text-xl font-semibold text-gray-800 mb-2">Amenities</h2>
                        <p className="text-sm text-gray-600">{hotel.amenities}</p>
                    </div>
                    <div className="mb-6 pt-6 border-t border-gray-200">
                        <h2 className="text-2xl font-semibold text-gray-800 mb-4">Ratings and reviews</h2>
                        {loadingReviewSummary ? (
                            <p>Loading review summary...</p>
                        ) : reviewSummary && reviewSummary.overallRating !== undefined ? (
                            <>
                                <div className="flex items-center mb-4">
                                    <span className={`text-lg font-bold px-3 py-1 rounded mr-3 ${reviewSummary.overallRating >= 4 ? 'bg-green-500 text-white' : reviewSummary.overallRating >= 3 ? 'bg-yellow-500 text-white' : 'bg-red-500 text-white'}`}>
                                        {reviewSummary.overallRating.toFixed(1)}
                                    </span>
                                    <div className="flex flex-col">
                                        <span className="text-lg font-semibold text-gray-800">
                                            {reviewSummary.overallRating >= 4.0 ? "EXCELLENT" : reviewSummary.overallRating >= 3.0 ? "VERY GOOD" : "GOOD"}
                                        </span>
                                        <span className="text-sm text-gray-600">{reviewSummary.totalReviews} ratings</span>
                                    </div>
                                </div>
                                <div className="mb-6">
                                    {reviewSummary?.ratingPercentages &&
                                        [5, 4, 3, 2, 1].map((star) => {
                                            const percentage = reviewSummary.ratingPercentages[String(star)] || 0;
                                            const displayPercentage = Math.round(percentage);
                                            return (
                                                <div key={star} className="flex items-center text-sm mb-1">
                                                    <span className="w-8 text-right text-gray-600 mr-2">{star} ★</span>
                                                    <div className="w-full bg-gray-200 rounded-full h-2.5">
                                                        <div
                                                            className="bg-blue-500 h-2.5 rounded-full"
                                                            style={{ width: `${displayPercentage}%` }}
                                                        ></div>
                                                    </div>
                                                    <span className="ml-2 text-gray-600">{displayPercentage}%</span>
                                                </div>
                                            );
                                        })}
                                    {!reviewSummary?.ratingPercentages && (
                                        <p className="italic text-gray-500">Rating distribution data not available.</p>
                                    )}
                                </div>
                            </>
                        ) : (
                            <p className="italic text-gray-500">No review summary available yet.</p>
                        )}
                        {loadingReviews ? (
                            <p>Loading reviews...</p>
                        ) : reviews.length === 0 ? (
                            <p className="italic text-gray-500">No reviews yet.</p>
                        ) : (
                            <ul className="space-y-4 max-h-96 overflow-y-auto pr-2">
                                {reviews.map((review) => (
                                    <li key={review.reviewId} className="border-b border-gray-200 pb-4">
                                        <div className="flex justify-between items-center mb-1">
                                            <span className="font-semibold text-gray-800">{review.guestUserName || "Anonymous"}</span>
                                            <span className="text-sm text-gray-500">
                                                {(() => {
                                                    try {
                                                        const date = parseCustomDate(review.reviewDate);
                                                        return date && !isNaN(date.getTime()) ?
                                                            date.toLocaleDateString("en-US", { day: 'numeric', month: 'short', year: 'numeric' }) :
                                                            'N/A';
                                                    } catch (e) {
                                                        console.error("Error parsing review date:", e);
                                                        return 'N/A';
                                                    }
                                                })()}
                                            </span>
                                            <div className="text-yellow-500">⭐ {review.rating}</div>
                                        </div>
                                        <p className="text-gray-700">{review.comment}</p>
                                        {review.imageUrl && (
                                            <img src={review.imageUrl} alt="Review" className="w-24 h-24 object-cover rounded mt-2" />
                                        )}
                                    </li>
                                ))}
                            </ul>
                        )}
                        <div className="flex justify-between items-center mt-3">
                        <button
                            onClick={handleSeeAllReviews}
                            className="text-blue-600 text-sm mt-3 hover:underline"
                            disabled={loadingAllReviewsForModal}
                        >
                            {loadingAllReviewsForModal ? "Loading all reviews..." : "See all reviews"}
                        </button>
                        {authService.getCurrentUserToken() ? (
                                <button
                                    onClick={handleAddReviewClick}
                                    className="bg-green-500 hover:bg-green-600 text-white font-bold py-2 px-4 rounded-md text-sm transition duration-300"
                                >
                                    Add Your Review
                                </button>
                            ) : (
                                <p className="text-gray-500 text-sm">Log in to add a review.</p>
                            )}
                        </div>
                    </div>
                        <div className="mb-6 pt-6 border-t border-gray-200">
                        <h2 className="text-2xl font-semibold text-gray-800 mb-4">Hotel policies</h2>
                        <div className="flex space-x-8 mb-4">
                            <div>
                                <p className="font-medium text-gray-700">Check-in</p>
                                <p className="text-gray-800">12:00 PM</p>
                            </div>
                            <div>
                                <p className="font-medium text-gray-700">Check-out</p>
                                <p className="text-gray-800">11:00 AM</p>
                            </div>
                        </div>
                        <ul className="list-disc list-inside text-gray-700 space-y-1">
                            <li>Guests are welcome</li>
                            <li>Guests can check in using any local or outstation ID proof (PAN card not accepted).</li>
                            <li>This hotel is serviced under the trade name of Sunrise Residency as per quality standards of LuxeStay.</li>
                        </ul>
                        <button className="text-blue-600 text-sm mt-3 hover:underline">View Guest Policy</button>
                    </div>
                </div>
                <div className="lg:w-96">
                    <div className="sticky top-4">
                        <BookingSummaryCard
                            calculatedPrice={subtotalAmount}
                            totalPrice={subtotalAmount}
                            checkIn={checkInDate}
                            checkOut={checkOutDate}
                            guests={numberOfGuests}
                            onGuestsChange={setNumberOfGuests}
                            selectedRoomName={selectedRoom ? selectedRoom.type : "Please select a room"}
                            onContinueToBook={handleContinueToBook}
                            isContinueToBookDisabled={isContinueToBookDisabled}
                            onCheckInChange={setCheckInDate}
                            onCheckOutChange={setCheckOutDate}
                            availabilityMessage={
                                selectedRoom && selectedRoom.availability > 0
                                    ? `${selectedRoom.availability} rooms of this type currently available for ${numberOfNights} night(s).`
                                    : 'No rooms of this type currently available.'
                            }
                            showAvailabilityInfo={!!selectedRoom}
                            maxGuests={selectedRoom ? selectedRoom.availability : 10}
                            minGuests={1}
                        />
                    </div>
                </div>
            </div>
            <AllReviewsModal
                hotelId={hotelId}
                reviewSummary={reviewSummary}
                onClose={() => setShowAllReviewsModal(false)}
                hotelName={hotel.hotelName}
                isOpen={showAllReviewsModal}
                initialReviews={allReviewsForModal}
            />
        </div>
    );
}
export default HotelDetailsPage;