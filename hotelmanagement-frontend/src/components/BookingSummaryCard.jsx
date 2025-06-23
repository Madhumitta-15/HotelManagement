import React from 'react';
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";

const BookingSummaryCard = ({
    calculatedPrice,
    totalPrice,
    checkIn,
    checkOut,
    guests,
    selectedRoomName,
    onContinueToBook,
    isContinueToBookDisabled,
    onCheckInChange,
    onCheckOutChange,
    onGuestsChange,
    minGuests = 1,
    maxGuests = 10,
    availabilityMessage,
    showAvailabilityInfo
}) => {
    const handleGuestsChange = (e) => {
        let value = parseInt(e.target.value, 10);
        if (isNaN(value) || value < minGuests) {
            value = minGuests;
        }
        if (value > maxGuests) {
            value = maxGuests;
        }
        if (onGuestsChange) {
            onGuestsChange(value);
        }
    };
    return (
        <div className="bg-white p-6 rounded-lg shadow-md border border-gray-200">
            <div className="mb-4">
                <div className="flex items-baseline mb-1">
                    <span className="text-3xl font-bold text-gray-900">₹{calculatedPrice}</span>
                </div>
            </div>

            <div className="border border-gray-300 rounded mb-4">
                <div className="flex border-b border-gray-300">
                    <div className="w-1/2 p-2 text-sm">
                        <p className="font-semibold text-gray-700">Check-in</p>
                        <DatePicker
                            selected={checkIn}
                            onChange={onCheckInChange}
                            minDate={new Date()}
                            dateFormat="EEE, MMM d"
                            className="w-full text-gray-500 cursor-pointer focus:outline-none"
                            wrapperClassName="w-full"
                        />
                    </div>
                    <div className="w-1/2 p-2 text-sm border-l border-gray-300">
                        <p className="font-semibold text-gray-700">Check-out</p>
                        <DatePicker
                            selected={checkOut}
                            onChange={onCheckOutChange}
                            minDate={checkIn || new Date()}
                            dateFormat="EEE, MMM d"
                            className="w-full text-gray-500 cursor-pointer focus:outline-none"
                            wrapperClassName="w-full"
                        />
                    </div>
                </div>
                <div className="p-2 text-sm flex justify-between items-center">
                    <label htmlFor="num-guests" className="font-semibold text-gray-700">
                        Rooms:
                    </label>
                    <input
                        type="number"
                        id="num-guests"
                        value={guests}
                        onChange={handleGuestsChange}
                        min={minGuests}
                        max={maxGuests}
                        className="w-20 px-2 py-1 border border-gray-300 rounded-md text-gray-700 focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                    />
                </div>
            </div>
 
            <div className="mb-4 text-sm text-gray-700">
                <span className="font-medium">Selected Room:</span> {selectedRoomName}
            </div>
 
            {showAvailabilityInfo && (
                <div className="text-sm text-gray-600 mb-4">
                    {availabilityMessage}
                </div>
            )}
 
            <div className="mb-4 pt-4 border-t border-gray-200">
                <div className="flex justify-between items-center text-lg font-bold text-gray-900 mt-2">
                    <span>Total price</span>
                    <span>₹{totalPrice}</span>
                </div>
                <p className="text-xs text-gray-500 flex items-center mt-1">
                    Including taxes & fees
                    <svg xmlns="http://www.w3.org/2000/svg" className="h-3 w-3 ml-1 text-gray-400" viewBox="0 0 20 20" fill="currentColor">
                        <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clipRule="evenodd" />
                    </svg>
                </p>
            </div>
 
            <button
                onClick={onContinueToBook}
                disabled={isContinueToBookDisabled}
                className={`w-full py-3 rounded text-white font-semibold transition
                    ${isContinueToBookDisabled
                        ? "bg-gray-400 cursor-not-allowed"
                        : "bg-green-600 hover:bg-green-700"}`}
            >
                Continue to Book
            </button>
            <div className="text-xs text-gray-500 mt-4">
                <p className="mb-1">
                    <span className="font-semibold text-blue-600 flex items-center">
                        Cancellation Policy
                        <svg xmlns="http://www.w3.org/2000/svg" className="h-3 w-3 ml-1 text-blue-600" viewBox="0 0 20 20" fill="currentColor">
                            <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clipRule="evenodd" />
                        </svg>
                    </span>
                    Follow safety measures advised at the hotel.
                </p>
                <p>By proceeding, you agree to our <span className="text-blue-600 font-semibold cursor-pointer">Guest Policies</span>.</p>
            </div>
        </div>
    );
};

export default BookingSummaryCard;













  // const formatDateForDisplay = (date) => {
    //     if (!date) return "";
    //     const options = { weekday: 'short', month: 'short', day: 'numeric' };
    //     return date.toLocaleDateString('en-US', options);
    // };