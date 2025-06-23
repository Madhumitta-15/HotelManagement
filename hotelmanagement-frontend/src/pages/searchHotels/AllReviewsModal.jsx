import React, { useEffect, useState } from "react";
import axios from "axios";
import authService from '../../services/AuthServices'; 
const API_BASE_URL = 'http://localhost:8095';
 
const parseCustomDate = (dateString) => {
    if (!dateString) return null;
    const [datePart, timePart] = dateString.split(' ');
    if (!datePart || !timePart) return null;
 
    const [year, month, day] = datePart.split('-').map(Number);
    const [hours, minutes, seconds] = timePart.split(':').map(Number);
 
    return new Date(Date.UTC(year, month - 1, day, hours, minutes, seconds));
};
 
// AllReviewsModal component
function AllReviewsModal({ hotelId, reviewSummary, onClose, hotelName, isOpen, initialReviews }) {
    const [allReviewsData, setAllReviewsData] = useState([]);
    const [sortBy, setSortBy] = useState("recent");
    const [selectedCategory, setSelectedCategory] = useState("all");
    const [loadingReviews, setLoadingReviews] = useState(true);
    const [error, setError] = useState(""); 
 
    const categories = ["all", "single", "couples", "Tourist"];
    const fetchAndSetReviews = async () => {
        setLoadingReviews(true);
        setError(""); 
        try {
            authService.setAuthHeader(); 
 
            let fetchedData;
            if (selectedCategory === "all") {
                const res = await axios.get(`${API_BASE_URL}/review/filter/${hotelId}?sortBy=${sortBy}`);
                fetchedData = res.data.reviews || [];
            } else {
                const res = await axios.get(`${API_BASE_URL}/review/getbycategory/${hotelId}/${selectedCategory}`);
                fetchedData = res.data || [];
 
                fetchedData = fetchedData.slice().sort((a, b) => {
                    if (sortBy === "highToLow") {
                        return b.rating - a.rating;
                    } else if (sortBy === "lowToHigh") {
                        return a.rating - b.rating;
                    } else {
                        const dateA = parseCustomDate(a.reviewDate);
                        const dateB = parseCustomDate(b.reviewDate);
                        if (dateA === null || isNaN(dateA.getTime())) return 1;
                        if (dateB === null || isNaN(dateB.getTime())) return -1;
                        return dateB.getTime() - dateA.getTime();
                    }
                });
            }
            setAllReviewsData(fetchedData);
        } catch (err) {
            console.error("Failed to fetch all reviews:", err);
            if (err.response && (err.response.status === 401 || err.response.status === 403)) {
                setError("You need to be logged in to view all reviews or access this feature.");
            } else {
                setError("Failed to load reviews. Please try again.");
            }
            setAllReviewsData([]);
        } finally {
            setLoadingReviews(false);
        }
    };

    useEffect(() => {
        if (!isOpen) {
            setAllReviewsData([]);
            setSortBy("recent");
            setSelectedCategory("all");
            setLoadingReviews(true);
            setError("");
            return;
        }
 
        if (isOpen && sortBy === "recent" && selectedCategory === "all" && initialReviews && initialReviews.length > 0) {
            setAllReviewsData(initialReviews);
            setLoadingReviews(false);
        } else if (isOpen) {
            fetchAndSetReviews();
        }
    }, [hotelId, sortBy, selectedCategory, isOpen, initialReviews]);
 
 
    const displayOverallRating = reviewSummary?.overallRating;
    const displayTotalReviews = reviewSummary?.totalReviews;
 
    if (!isOpen) return null;
 
    return (
        <div className="fixed inset-0 bg-gray-600 bg-opacity-75 flex items-center justify-center z-50 p-4">
            <div className="bg-white rounded-lg shadow-xl w-full max-w-3xl h-[90vh] flex flex-col">
                <div className="flex justify-between items-center p-4 border-b">
                    <h2 className="text-2xl font-bold text-gray-800">Reviews for {hotelName}</h2>
                    <button onClick={onClose} className="text-gray-500 hover:text-gray-700 text-3xl">&times;</button>
                </div>
 
                {/* Summary and Sort/Category Filters */}
                <div className="p-4 border-b flex justify-between items-center flex-wrap gap-4">
                    <div className="flex items-center">
                        {displayOverallRating !== undefined && (
                            <span className={`text-lg font-bold px-3 py-1 rounded mr-2 ${displayOverallRating >= 4 ? 'bg-green-500 text-white' : displayOverallRating >= 3 ? 'bg-yellow-500 text-white' : 'bg-red-500 text-white'}`}>
                                {displayOverallRating.toFixed(1)}
                            </span>
                        )}
                        <div className="flex flex-col">
                            {displayOverallRating !== undefined && (
                                <span className="text-lg font-semibold text-gray-800">
                                    {displayOverallRating >= 4.0 ? "EXCELLENT" : displayOverallRating >= 3.0 ? "VERY GOOD" : "GOOD"}
                                </span>
                            )}
                            {displayTotalReviews !== undefined && (
                                <span className="text-sm text-gray-600">{displayTotalReviews} ratings</span>
                            )}
                        </div>
                    </div>
                    {/* Sort By Dropdown */}
                    <div className="flex items-center">
                        <label htmlFor="sortBy" className="text-gray-700 mr-2 text-sm">Sort By:</label>
                        <select
                            id="sortBy"
                            className="border rounded p-2 text-sm"
                            value={sortBy}
                            onChange={(e) => setSortBy(e.target.value)}
                        >
                            <option value="recent">Recent</option>
                            <option value="highToLow">Rating: High to Low</option>
                            <option value="lowToHigh">Rating: Low to High</option>
                        </select>
                    </div>
                    {/* Category Dropdown */}
                    <div className="flex items-center">
                        <label htmlFor="categoryFilter" className="text-gray-700 mr-2 text-sm">Category:</label>
                        <select
                            id="categoryFilter"
                            className="border rounded p-2 text-sm"
                            value={selectedCategory}
                            onChange={(e) => setSelectedCategory(e.target.value)}
                        >
                            {categories.map((cat) => (
                                <option key={cat} value={cat}>
                                    {cat.charAt(0).toUpperCase() + cat.slice(1)}
                                </option>
                            ))}
                        </select>
                    </div>
                </div>
 
                {/* All Reviews List */}
                <div className="flex-1 overflow-y-auto p-4">
                    {loadingReviews ? (
                        <p className="text-center text-gray-700">Loading reviews...</p>
                    ) : error ? (
                        <p className="text-center text-red-600">{error}</p>
                    ) : allReviewsData.length === 0 ? (
                        <p className="italic text-gray-500">No reviews found for this selection.</p>
                    ) : (
                        <ul className="space-y-4">
                            {allReviewsData.map((review) => (
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
                                                    console.error("Error parsing review date in modal:", e);
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
                </div>
            </div>
        </div>
    );
}
 
export default AllReviewsModal;