import React, { useEffect, useState, useRef, useCallback } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import axios from "axios";
import { toast } from "react-toastify";
import LoadingSpinner from "../../components/LoadingSpinner";

function PaymentPage() {
    const navigate = useNavigate();
    const location = useLocation()
    const { bookingPayload } = location.state || {};
    const [status, setStatus] = useState("PROCESSING");
    const [displayErrorMessage, setDisplayErrorMessage] = useState("");
    const paymentInitiatedRef = useRef(false);
    const redirectScheduledRef = useRef(false);
    const handleFailedRedirect = useCallback((message) => {
        if (redirectScheduledRef.current) {
            console.log("Failed redirect already scheduled/executed. Skipping duplicate.");
            return;
        }
        redirectScheduledRef.current = true;
        setTimeout(() => {
            console.log("Executing redirect to /booking-failed...");
            navigate("/booking-failed", {
                state: {
                    message: message,
                },
            });
        }, 100);
    }, [navigate]);

    useEffect(() => { 
        if (!bookingPayload) {
            toast.error("No booking data found. Redirecting to home.");
            navigate("/");
            return;
        }

        if (paymentInitiatedRef.current) {
            console.log("Payment process already initiated for this instance. Skipping re-initiation.");
            return;
        }
        paymentInitiatedRef.current = true;

        const simulatePaymentAndBook = async () => {
            try {
                setStatus("PROCESSING");
                console.log("Starting simulated payment delay...");
                await new Promise((resolve) => setTimeout(resolve, 3000));

                const token = localStorage.getItem("jwtToken");
                if (!token) {
                    const authErrorMessage = "You are not logged in. Please log in to continue.";
                    toast.error(authErrorMessage);
                    setDisplayErrorMessage(authErrorMessage);
                    setStatus("FAILED");
                    setTimeout(() => navigate("/login", { state: { from: location.pathname } }), 2000);
                    return;
                }

                console.log("Sending booking request to backend...");
                const response = await axios.post("http://localhost:8095/bookings/bookroom", bookingPayload, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                        "Content-Type": "application/json",
                    },
                });
                console.log("Booking API call successful:", response.data);
                setStatus("SUCCESS");
                toast.success("Booking and Payment Successful!");
                setDisplayErrorMessage("");
                navigate("/booking-success");

            } catch (err) {
                console.error("Payment or Booking failed:", err.response?.data || err.message, err);
                const apiErrorMessage = err.response?.data?.message || "Payment or Booking Failed. Please try again.";

                setDisplayErrorMessage(apiErrorMessage);
                setStatus("FAILED");
                toast.error(apiErrorMessage);
                handleFailedRedirect(apiErrorMessage);
            }
        };
        simulatePaymentAndBook();

    }, [bookingPayload, navigate, location.pathname, handleFailedRedirect]);

    return (
        <div className="text-center p-10">
            <h1 className="text-2xl font-bold text-blue-700">
                {status === "PROCESSING" ? "Processing Payment..." :status === "SUCCESS" ? "Payment Successful!" :"Payment Failed!"}
            </h1>
            <p className="mt-2 text-gray-600">
                {status === "PROCESSING" ? "This may take a few seconds. Please wait." :(status === "SUCCESS" || status === "FAILED") && "Redirecting shortly..."}
            </p>
            <div className="mt-8">
                {status === "PROCESSING" && <LoadingSpinner />}
                <p className="mt-6 text-lg font-semibold text-gray-700">
                    {status === "PROCESSING" ? "Transaction in progress..." : `Payment ${status}`}
                    {status === "FAILED" && displayErrorMessage && (
                        <span className="block text-red-500 text-sm mt-2">{displayErrorMessage}</span>
                    )}
                </p>
            </div>
        </div>
    );
}

export default PaymentPage;