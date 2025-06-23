import React from "react";

const HotelDetails = ({ assignedHotel, loading, error, managerId }) => {
    if (loading) return <p className="text-center text-lg text-blue-500">Loading...</p>;
    if (error) return <p className="text-center text-lg text-red-500">Error: {error}</p>;

    return (
        <div className="p-6 border rounded-lg max-w-md mx-auto bg-white shadow-lg">
            <h2 className="text-xl font-bold text-center text-gray-800 mb-4">🏨 Hotel Overview</h2>
            {assignedHotel ? (
                <div className="space-y-3">
                    <p className="text-gray-700"><strong className="text-blue-600">Name:</strong> {assignedHotel.hotelName}</p>
                    <p className="text-gray-700"><strong className="text-blue-600">Location:</strong> {assignedHotel.location}</p>
                    {assignedHotel.assignedManager && (
                        <p className="text-gray-700"><strong className="text-blue-600">Manager:</strong> {assignedHotel.assignedManager.managerName} (ID: {assignedHotel.assignedManager.managerId})</p>
                    )}
                </div>
            ) : (
                managerId && <p className="text-center text-yellow-600 font-semibold">No hotel assigned. Contact administrator.</p>
            )}
        </div>
    );
};

export default HotelDetails;
