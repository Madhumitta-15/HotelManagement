import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import authService from '../services/AuthServices';

function ProtectedRoute({ allowedRoles }) {
  const isAuthenticated = authService.getCurrentUserToken();
  const userRole = authService.getCurrentUserRole();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (allowedRoles && !allowedRoles.includes(userRole)) {
    return <Navigate to="/unauthorized" replace />;
  }

  return <Outlet />;
}

export default ProtectedRoute;