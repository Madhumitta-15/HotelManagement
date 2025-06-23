import React from 'react';
import {  useNavigate } from 'react-router-dom';
import { Button } from '../../components/ui/Button';

function BookingSuccess() {
  const navigate = useNavigate();
  const defaultMessage = "Your booking was successful!";
  return (
    <div className="max-w-xl mx-auto my-12 p-8 bg-white shadow-lg rounded-lg text-center border-t-8 border-green-500">
      <div className="text-green-500 text-6xl mb-4">
        <i className="fas fa-check-circle"></i>
      </div>
      <h2 className="text-4xl font-bold text-green-700 mb-4">Booking Successful!</h2>
      <p className="text-lg text-gray-700 mb-6">{defaultMessage}</p>
      <div className="flex flex-col space-y-4">
        <Button onClick={() => navigate('/')} className="w-full bg-gray-200 hover:bg-gray-300 text-gray-800">
          Back to Home
        </Button>
      </div>
    </div>
  );
}

export default BookingSuccess;