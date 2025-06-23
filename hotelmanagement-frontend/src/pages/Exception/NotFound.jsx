import React from "react";
import { Link } from "react-router-dom";
const NotFoundPage = () => (
    <div className="flex flex-col items-center justify-center min-h-[calc(100vh-64px)] bg-gray-50 p-4">
      <h2 className="text-3xl font-bold text-gray-800 mb-4">404 - Page Not Found</h2>
      <p className="text-gray-700 text-lg mb-6">The page you are looking for does not exist.</p>
      <Link to="/" className="bg-blue-500 hover:bg-blue-600 text-white font-bold py-2 px-4 rounded transition duration-300">
        Go to Home
      </Link>
    </div>
  );
  export default NotFoundPage;