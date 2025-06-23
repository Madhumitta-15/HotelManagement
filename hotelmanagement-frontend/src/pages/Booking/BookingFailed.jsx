import React from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { Button } from '../../components/ui/Button';

function BookingFailed() {
  const location = useLocation();
  const navigate = useNavigate();

  const { message } = location.state || {}; 

  const defaultMessage = "We encountered an issue processing your booking. Please try again.";

  return (
    <div className="max-w-xl mx-auto my-12 p-8 bg-white shadow-lg rounded-lg text-center border-t-8 border-red-500">
      <div className="text-red-500 text-6xl mb-4">
        <i className="fas fa-times-circle"></i> 
      </div>
      <h2 className="text-4xl font-bold text-red-700 mb-4">Booking Failed!</h2>
      <p className="text-lg text-gray-700 mb-6">{message || defaultMessage}</p>

      <p className="text-md text-gray-600 mb-8">
        We apologize for the inconvenience. This might be due to:
      </p>
      <ul className="list-disc list-inside text-left text-gray-600 mb-8 space-y-2">
        <li>The selected room becoming unavailable.</li>
        <li>An issue with your payment method.</li>
        <li>A temporary system error.</li>
        <li>Authentication issues (e.g., session expired).</li>
      </ul>

      <div className="flex flex-col space-y-4">
        <Button onClick={() => navigate('/')} className="w-full bg-gray-200 hover:bg-gray-300 text-gray-800">
          Back to Home
        </Button>
      </div>
    </div>
  );
}

export default BookingFailed;