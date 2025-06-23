import React, { useState, useEffect, useMemo, useCallback } from "react";
import { useNavigate, useParams, useLocation } from "react-router-dom";
import axios from "axios";
import { Button } from "../../components/ui/Button";
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import { v4 as uuidv4 } from 'uuid';

const Modal = ({ isOpen, onClose, children, title }) => {
    if (!isOpen) return null;
    return (
        <div className="fixed inset-0 bg-gray-600 bg-opacity-50 flex justify-center items-center z-50">
            <div className="bg-white p-6 rounded-lg shadow-xl max-w-md w-full mx-auto">
                <div className="flex justify-between items-center mb-4">
                    <h3 className="text-xl font-bold">{title}</h3>
                    <button onClick={onClose} className="text-gray-500 hover:text-gray-700 text-2xl">&times;</button>
                </div>
                <div>{children}</div>
            </div>
        </div>
    );
};

function BookingPage() {
    const { hotelId } = useParams();
    const location = useLocation();
    const navigate = useNavigate();
    const bookingDetails = useMemo(() => location.state?.bookingDetails, [location.state?.bookingDetails]);
    const [hotelName, setHotelName] = useState("");
    const [selectedRoomId, setSelectedRoomId] = useState("");
    const [selectedRoomType, setSelectedRoomType] = useState("");
    const [roomPricePerNight, setRoomPricePerNight] = useState(0); 
    const [checkInDate, setCheckInDate] = useState(null);
    const [checkOutDate, setCheckOutDate] = useState(null);
    const [numberOfRooms, setNumberOfRooms] = useState(1);
    const [paymentMethod, setPaymentMethod] = useState("CARD");
    const [useLoyaltyPoints, setUseLoyaltyPoints] = useState(false);
    const [loyaltyPointsAvailable, setLoyaltyPointsAvailable] = useState(0);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [isConfirmModalOpen, setIsConfirmModalOpen] = useState(false);

    useEffect(() => {
        const fetchBookingInfo = async () => {
            setLoading(true);
            setError("");
            try {
                if (bookingDetails) {
                    setHotelName(bookingDetails.hotelName || "");
                    setSelectedRoomId(String(bookingDetails.roomId));
                    setSelectedRoomType(bookingDetails.roomType || "");
                    setCheckInDate(bookingDetails.checkIn ? new Date(bookingDetails.checkIn) : null);
                    setCheckOutDate(bookingDetails.checkOut ? new Date(bookingDetails.checkOut) : null);
                    setNumberOfRooms(bookingDetails.guests || 1); 
                    setRoomPricePerNight(bookingDetails.roomPricePerNight || 0); 
                } else if (hotelId) {
                    
                    const hotelRes = await axios.get(`http://localhost:8095/hotel/gethotel/${hotelId}`);
                    setHotelName(hotelRes.data.hotelName);
                    const roomRes = await axios.get(`http://localhost:8095/rooms/getallrooms/${hotelId}`);
                    const availableRoom = roomRes.data.find(room => room.availability > 0);
                    if (availableRoom) {
                        setSelectedRoomId(String(availableRoom.roomId));
                        setSelectedRoomType(availableRoom.type);
                        setRoomPricePerNight(availableRoom.price); 
                    } else {
                        setError("No available rooms for this hotel.");
                        toast.error("No available rooms found for this hotel.");
                    }
                    const today = new Date();
                    const tomorrow = new Date(today);
                    tomorrow.setDate(today.getDate() + 1);
                    const dayAfterTomorrow = new Date(tomorrow);
                    dayAfterTomorrow.setDate(tomorrow.getDate() + 1);
                    setCheckInDate(tomorrow);
                    setCheckOutDate(dayAfterTomorrow);
                    setNumberOfRooms(1); 
                } else {
                    setError("Hotel ID or booking details are missing.");
                    toast.error("Booking information is incomplete. Please go back and select a hotel.");
                }
            } catch (err) {
                console.error("Failed to load booking details:", err);
                const message = err.response?.data?.message || "Failed to load hotel or room details. Please try again.";
                setError(message);
                toast.error(message);
            } finally {
                setLoading(false);
            }
        };
        fetchBookingInfo();
    }, [hotelId, bookingDetails]);

    useEffect(() => {
        const fetchPoints = async () => {
            try {
                const token = localStorage.getItem("jwtToken");
                if (!token) {
                    setLoyaltyPointsAvailable(0);
                    return;
                }
                const res = await axios.get("http://localhost:8095/loyalty/balance", {
                    headers: { Authorization: `Bearer ${token}` }
                });
                setLoyaltyPointsAvailable(res.data || 0);
            } catch (err) {
                console.error("Could not fetch loyalty points:", err);
                toast.error("Failed to fetch loyalty points.");
                setLoyaltyPointsAvailable(0);
            }
        };
        fetchPoints();
    }, []);

    const {
        finalCalculatedPrice,
        payableAmount,
        redeemedPoints,
        numberOfNights
    } = useMemo(() => {
        const nights = checkInDate && checkOutDate && checkOutDate > checkInDate
            ? Math.ceil((checkOutDate.getTime() - checkInDate.getTime()) / (1000 * 60 * 60 * 24))
            : 0;
        const calculated = roomPricePerNight * numberOfRooms * nights;
        const redeemable = useLoyaltyPoints ? Math.min(loyaltyPointsAvailable, calculated) : 0;
        const payable = calculated - redeemable;
        return {
            finalCalculatedPrice: calculated,
            payableAmount: payable,
            redeemedPoints: redeemable,
            numberOfNights: nights
        };
    }, [roomPricePerNight, numberOfRooms, checkInDate, checkOutDate, useLoyaltyPoints, loyaltyPointsAvailable]);

    const handleInitiateBooking = useCallback((e) => {
        e.preventDefault();
        setError("");
        if (!selectedRoomId || !checkInDate || !checkOutDate || numberOfRooms <= 0 || !hotelName || !selectedRoomType || roomPricePerNight <= 0) {
            toast.error("Booking details are incomplete. Please ensure all information is loaded.");
            return;
        }
        if (checkInDate && checkOutDate && checkInDate >= checkOutDate) {
            toast.error("Check-out date must be after check-in date.");
            return;
        }
        setIsConfirmModalOpen(true);
    }, [selectedRoomId, checkInDate, checkOutDate, numberOfRooms, hotelName, selectedRoomType, roomPricePerNight]);

    const confirmAndRedirectToPayment = useCallback(() => {
        setIsConfirmModalOpen(false);
        setLoading(true);
        const token = localStorage.getItem("jwtToken");
        if (!token) {
            toast.error("You are not logged in. Please log in to complete your booking.");
            navigate("/login", { state: { from: location.pathname } });
            setLoading(false);
            return;
        }

        const requestId = uuidv4();
        const bookingPayloadForBackend = {
            roomId: parseInt(selectedRoomId),
            checkInDate: checkInDate.toISOString().split("T")[0],
            checkOutDate: checkOutDate.toISOString().split("T")[0],
            numberOfRooms: parseInt(numberOfRooms),
            paymentMethod: paymentMethod,
            useLoyaltyPoints: useLoyaltyPoints,
            requestId: requestId,
            frontendCalculatedBaseAmount: parseFloat(finalCalculatedPrice.toFixed(2)),
        };

        navigate("/payment", {
            state: {
                bookingPayload: bookingPayloadForBackend,
                hotelName: hotelName,
                finalPayableAmount: parseFloat(payableAmount.toFixed(2)),
                totalBeforeLoyalty: parseFloat(finalCalculatedPrice.toFixed(2)),
                loyaltyRedeemedAmount: parseFloat(redeemedPoints.toFixed(2)),
            }
        });
    }, [selectedRoomId, checkInDate, checkOutDate, numberOfRooms, paymentMethod, useLoyaltyPoints, payableAmount, finalCalculatedPrice, redeemedPoints, navigate, location.pathname, hotelName, hotelId]);

    if (loading) {
        return (
            <div className="flex justify-center items-center min-h-screen flex-col">
                <div className="loader ease-linear rounded-full border-8 border-t-8 border-gray-200 h-20 w-20 animate-spin"></div>
                <p className="text-xl ml-4 mt-4 text-gray-700">Loading booking details...</p>
            </div>
        );
    }

    if (error && !loading) {
        return (
            <div className="max-w-xl mx-auto my-8 p-6 bg-white shadow rounded-lg text-center">
                <h2 className="text-3xl font-bold text-red-600 mb-4">Error</h2>
                <p className="text-red-500 mb-4">{error}</p>
                <Button onClick={() => navigate(-1)} className="bg-blue-600 text-white hover:bg-blue-700">Go Back</Button>
            </div>
        );
    }

    return (
        <div className="max-w-xl mx-auto my-8 p-6 bg-white shadow rounded-lg">
            <h2 className="text-3xl font-bold text-blue-600 mb-6">Confirm Your Booking at {hotelName}</h2>
            <form onSubmit={handleInitiateBooking} className="space-y-5">
                <div className="p-4 bg-gray-50 rounded-md border border-gray-200">
                    <p className="text-lg font-semibold text-gray-800 mb-2">Selected Room:</p>
                    <p className="text-gray-700">{selectedRoomType} - ₹{roomPricePerNight.toFixed(0)} / night</p>
                </div>
                <div className="grid grid-cols-2 gap-4 bg-gray-50 p-4 rounded-md border border-gray-200">
                    <div>
                        <p className="block text-lg font-semibold text-gray-800 mb-2">Check-In Date:</p>
                        <p className="text-gray-700">{checkInDate?.toLocaleDateString("en-US", { day: 'numeric', month: 'short', year: 'numeric' }) || "N/A"}</p>
                    </div>
                    <div>
                        <p className="block text-lg font-semibold text-gray-800 mb-2">Check-Out Date:</p>
                        <p className="text-gray-700">{checkOutDate?.toLocaleDateString("en-US", { day: 'numeric', month: 'short', year: 'numeric' }) || "N/A"}</p>
                    </div>
                </div>
                <div className="p-4 bg-gray-50 rounded-md border border-gray-200">
                    <p className="text-lg font-semibold text-gray-800 mb-2">Number of Rooms:</p>
                    <p className="text-gray-700">{numberOfRooms}</p>
                </div>
                <div className="pt-4 border-t border-gray-200 space-y-2">
                    <div className="flex justify-between text-base font-medium text-gray-700">
                        <span>Base Price per room per night:</span>
                        <span>₹{roomPricePerNight.toFixed(0)}</span>
                    </div>
                    <div className="flex justify-between text-base font-medium text-gray-700">
                        <span>Total Nights:</span>
                        <span>{numberOfNights}</span>
                    </div>
                    <div className="flex justify-between text-base font-semibold text-gray-800">
                        <span>Subtotal (before points):</span>
                        <span>₹{finalCalculatedPrice.toFixed(0)}</span>
                    </div>
                    {useLoyaltyPoints && redeemedPoints > 0 && (
                        <div className="flex justify-between text-sm text-green-600">
                            <span>Loyalty Points Redeemed:</span>
                            <span>- ₹{redeemedPoints.toFixed(0)}</span>
                        </div>
                    )}
                    <div className="flex justify-between text-xl font-bold text-blue-700 pt-2 border-t border-gray-100">
                        <span>Final Payable Amount:</span>
                        <span>₹{payableAmount.toFixed(0)}</span>
                    </div>
                </div>
                <div>
                    <label htmlFor="payment-method" className="block text-lg font-semibold text-gray-800 mb-2">Payment Method</label>
                    <select
                        id="payment-method"
                        value={paymentMethod}
                        onChange={(e) => setPaymentMethod(e.target.value)}
                        className="w-full border border-gray-300 rounded-md px-3 py-2 text-gray-700 focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                    >
                        <option value="CARD">Card</option>
                        <option value="WALLET">Wallet</option>
                        <option value="CASH">Cash at Hotel</option>
                    </select>
                </div>
                {loyaltyPointsAvailable > 0 ? (
                    <div className="flex items-center p-3 bg-blue-50 rounded-md border border-blue-200">
                        <input
                            type="checkbox"
                            id="loyalty"
                            checked={useLoyaltyPoints}
                            onChange={(e) => setUseLoyaltyPoints(e.target.checked)}
                            className="mr-3 h-5 w-5 text-blue-600 focus:ring-blue-500 border-gray-300 rounded cursor-pointer"
                        />
                        <label htmlFor="loyalty" className="text-base text-gray-900 font-medium">
                            Use Loyalty Points: <span className="text-blue-700 font-bold">{loyaltyPointsAvailable}</span> available
                        </label>
                    </div>
                ) : (
                    <p className="text-sm text-gray-600 italic">No loyalty points available.</p>
                )}
                <Button
                    type="submit"
                    className="w-full py-3 text-lg"
                    disabled={loading || !selectedRoomId || !checkInDate || !checkOutDate || numberOfRooms <= 0 || payableAmount < 0}
                >
                    {loading ? "Processing..." : "Pay and Book"}
                </Button>
            </form>
            <Modal isOpen={isConfirmModalOpen} onClose={() => setIsConfirmModalOpen(false)} title="Confirm Your Booking">
                <p className="mb-4 text-gray-800">Please review your booking details before confirming:</p>
                <div className="text-sm text-gray-700 space-y-2">
                    <p><strong>Hotel:</strong> <span className="font-medium">{hotelName}</span></p>
                    <p><strong>Room Type:</strong> <span className="font-medium">{selectedRoomType}</span></p>
                    <p><strong>Check-in:</strong> <span className="font-medium">{checkInDate?.toLocaleDateString("en-US", { day: 'numeric', month: 'short', year: 'numeric' })}</span></p>
                    <p><strong>Check-out:</strong> <span className="font-medium">{checkOutDate?.toLocaleDateString("en-US", { day: 'numeric', month: 'short', year: 'numeric' })}</span></p>
                    <p><strong>Number of Rooms:</strong> <span className="font-medium">{numberOfRooms}</span></p>
                    <p><strong>Number of Nights:</strong> <span className="font-medium">{numberOfNights}</span></p>
                    <p><strong>Payment Method:</strong> <span className="font-medium">{paymentMethod}</span></p>
                    <p><strong>Total (before points):</strong> <span className="font-medium">₹{finalCalculatedPrice.toFixed(0)}</span></p>
                    {useLoyaltyPoints && redeemedPoints > 0 && <p><strong>Loyalty Used:</strong> <span className="font-medium">- ₹{redeemedPoints.toFixed(0)}</span></p>}
                    <p className="font-bold text-green-700 text-lg">Payable: ₹{payableAmount.toFixed(0)}</p>
                </div>
                <div className="flex justify-end space-x-3 mt-6">
                    <Button onClick={() => setIsConfirmModalOpen(false)} className="bg-gray-300 text-gray-800 hover:bg-gray-400">Cancel</Button>
                    <Button onClick={confirmAndRedirectToPayment} className="bg-blue-600 text-white hover:bg-blue-700" disabled={loading}>
                        {loading ? "Confirming..." : "Confirm Booking"}
                    </Button>
                </div>
            </Modal>
        </div>
    );
}

export default BookingPage;