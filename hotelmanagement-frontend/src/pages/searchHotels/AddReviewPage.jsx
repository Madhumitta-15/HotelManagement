import React, { useState, useEffect } from 'react';
import axios from "axios";
import authService from '../../services/AuthServices';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import { FaStar } from 'react-icons/fa';
import toast from 'react-hot-toast';
 
const API_BASE_URL = 'http://localhost:8095';
 
function AddReviewPage() {
    const { hotelId: routeHotelId } = useParams();
    const navigate = useNavigate();
    const location = useLocation();
 
    const hotelId = routeHotelId || location.state?.hotelId;
    const hotelName = location.state?.hotelName || "Unknown Hotel";
 
    const [rating, setRating] = useState(0);
    const [hover, setHover] = useState(0);
    const [comment, setComment] = useState('');
    const [category, setCategory] = useState(''); 
    const [message, setMessage] = useState('');
    const [submitting, setSubmitting] = useState(false);
    const [checkingEligibility, setCheckingEligibility] = useState(true);
    const [canUserAddReview, setCanUserAddReview] = useState(false);
    const isLoggedIn = authService.getCurrentUserToken() !== null;
 
    
    const reviewCategories = [
        { value: '', label: 'Select Category' },
        { value: 'single', label: 'Single-' },   
        { value: 'couples', label: 'Couples' },  
        { value: 'Tourist', label: 'Tourist' },  
    ];
    
 
    useEffect(() => {
        const checkEligibility = async () => {
            setCheckingEligibility(true);
            setCanUserAddReview(false);
            setMessage('');
 
            if (!isLoggedIn) {
                setMessage("You must be logged in to add a review.");
                setCheckingEligibility(false);
                return;
            }
            if (!hotelId) {
                setMessage("Hotel ID is missing. Cannot check eligibility.");
                setCheckingEligibility(false);
                return;
            }
 
            try {
                authService.setAuthHeader();
                const response = await axios.get(`${API_BASE_URL}/bookings/check-review-eligibility/${hotelId}`);
                setCanUserAddReview(response.data.canReviewHotel);
                if (!response.data.canReviewHotel) {
                    setMessage("You must have a confirmed booking at this hotel to add a review.");
                }
            } catch (err) {
                console.error("Failed to check review eligibility:", err.response?.data || err.message || err);
                if (err.response?.status === 401) {
                    authService.logout();
                    setMessage("Your session expired. Please log in again.");
                } else if (err.response?.status === 403) {
                    setMessage("You are not authorized to perform this action or not eligible to add review.");
                } else {
                    setMessage("Failed to determine review eligibility. Please try again.");
                }
                setCanUserAddReview(false);
            } finally {
                setCheckingEligibility(false);
            }
        };
 
        checkEligibility();
    }, [hotelId, isLoggedIn]);
 
    const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage('');
        setSubmitting(true);
 
        if (!isLoggedIn) {
            setMessage('You must be logged in to submit a review.');
            setSubmitting(false);
            return;
        }
        if (!canUserAddReview) {
            setMessage('You are not eligible to add a review for this hotel (must have a confirmed booking).');
            setSubmitting(false);
            return;
        }
        if (rating === 0 || !comment || !category) {
            setMessage('Please provide a rating (at least one star), comment, AND select a category.');
            setSubmitting(false);
            return;
        }
        if (!hotelId) {
            setMessage('Hotel ID is missing. Cannot submit review.');
            setSubmitting(false);
            return;
        }
 
        try {
            authService.setAuthHeader();
 
            const response = await axios.post(
                `${API_BASE_URL}/review/add/${hotelId}`,
                {
                    rating: rating,
                    category: category, 
                    comment: comment,
                }
            );
            setMessage('Review added successfully!');
            setRating(0);
            setComment('');
            setCategory('');
            navigate(`/hotel/${hotelId}`);
        } catch (err) {
            console.error("Error submitting review:", err.response?.data || err.message || err);
            const backendMessage = err.response?.data?.message;
 
            if (err.response?.status === 403) {
                setMessage(backendMessage || 'You must have a confirmed booking at this hotel to add a review.');
            } else if (err.response?.status === 401) {
                setMessage('Your session expired. Please log in again.');
                authService.logout();
            } else if (err.response?.status === 404 && backendMessage && backendMessage.includes("hotel not found")) {
                setMessage('Hotel not found for review submission.');
            }
            else {
                setMessage(backendMessage || 'Failed to submit review. Please try again.');
            }
        } finally {
            setSubmitting(false);
        }
        toast.success("Review added Successfully!")
    };
 
    return (
        <div className="flex items-center justify-center min-h-screen bg-gray-100 p-4">
            <div className="bg-white p-8 rounded-lg shadow-md w-full max-w-md">
                <h2 className="text-2xl font-bold text-center mb-6">Submit a Review for {hotelName}</h2>
 
                {checkingEligibility ? (
                    <p className="text-center text-gray-600 mb-4">Checking if you can add a review...</p>
                ) : !isLoggedIn ? (
                    <p className="text-center text-red-600 mb-4">Please log in to add a review.</p>
                ) : !canUserAddReview ? (
                    <p className="text-center text-red-600 mb-4">You must have a confirmed booking at this hotel to add a review.</p>
                ) : (
                    <form onSubmit={handleSubmit} className="space-y-4">
                        <input type="hidden" name="hotelId" value={hotelId || ''} />
 
                        <div>
                            <label htmlFor="rating" className="block text-gray-700 text-sm font-semibold mb-2">Rating:</label>
                            <div className="flex">
                                {[...Array(5)].map((star, index) => {
                                    const currentRating = index + 1;
                                    return (
                                        <label key={index} className="cursor-pointer">
                                            <input
                                                type="radio"
                                                name="rating"
                                                value={currentRating}
                                                onClick={() => setRating(currentRating)}
                                                className="hidden"
                                            />
                                            <FaStar
                                                className="star"
                                                size={30}
                                                color={currentRating <= (hover || rating) ? "#ffc107" : "#e4e5e9"}
                                                onMouseEnter={() => setHover(currentRating)}
                                                onMouseLeave={() => setHover(0)}
                                            />
                                        </label>
                                    );
                                })}
                            </div>
                        </div>
 
                        <div>
                            <label htmlFor="category" className="block text-gray-700 text-sm font-semibold mb-2">Category:</label>
                            <select
                                id="category"
                                className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500"
                                value={category}
                                onChange={(e) => setCategory(e.target.value)} 
                                required
                            >
                                {reviewCategories.map((cat) => (
                                    <option key={cat.value} value={cat.value} disabled={cat.value === ''}>
                                        {cat.label}
                                    </option>
                                ))}
                            </select>
                        </div>
 
                        <div>
                            <label htmlFor="comment" className="block text-gray-700 text-sm font-semibold mb-2">Comment:</label>
                            <textarea
                                id="comment"
                                className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:ring-2 focus:ring-blue-500 h-32 resize-none"
                                value={comment}
                                onChange={(e) => setComment(e.target.value)}
                                required
                            ></textarea>
                        </div>
 
                        <button
                            type="submit"
                            className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded transition duration-300 w-full disabled:opacity-50 disabled:cursor-not-allowed"
                            disabled={submitting || checkingEligibility || !canUserAddReview || !isLoggedIn || rating === 0 || !comment || !category}
                        >
                            {submitting ? 'Submitting...' : 'Submit Review'}
                        </button>
 
                        {message && (
                            <p className={`text-center text-sm mt-3 ${message.includes('successfully') ? 'text-green-500' : 'text-red-500'}`}>
                                {message}
                            </p>
                        )}
                    </form>
                )}
            </div>
        </div>
    );
}
 
export default AddReviewPage;