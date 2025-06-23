import React from "react";
import { useNavigate ,Link} from "react-router-dom";
const UnauthorizedPage = () => {
  const navigate = useNavigate();
  return (
    <div className="flex flex-col items-center justify-center min-h-[calc(100vh-64px)] bg-gray-50 p-4">
      <h2 className="text-3xl font-bold text-red-600 mb-4">401 - Unauthorized Access</h2>
      <p className="text-gray-700 text-lg mb-6">You do not have permission to view this page.</p>
      <div className="space-x-4">
        <button
          onClick={() => { authService.logout(); navigate('/login'); }}
          className="bg-red-500 hover:bg-red-600 text-white font-bold py-2 px-4 rounded transition duration-300"
        >
          Logout
        </button>
        <Link to="/" className="bg-blue-500 hover:bg-blue-600 text-white font-bold py-2 px-4 rounded transition duration-300">
          Go to Home
        </Link>
      </div>
    </div>
  );
};
export default UnauthorizedPage;
