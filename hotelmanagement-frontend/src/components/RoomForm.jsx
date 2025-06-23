import React from 'react';

const RoomForm = ({ title, formData, onFormChange, onSubmit, submitButtonText, onCancel }) => {
    return (
        <div className="bg-white shadow rounded-md p-6">
            <h2 className="text-xl font-semibold text-gray-800 mb-4">{title}</h2>
            <form onSubmit={onSubmit} className="space-y-4">
                <div>
                    <label htmlFor="roomType" className="block text-gray-700 text-sm font-bold mb-2">Room Type:</label>
                    <select
                        id="roomType"
                        name="roomType"
                        value={formData.roomType}
                        onChange={onFormChange}
                        required
                        className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                    >
                        <option value="REGULAR">Regular</option>
                        <option value="DELUXE">Deluxe</option>
                        <option value="ULTRADELUXE">Ultra Deluxe</option>
                    </select>
                </div>
                <div>
                    <label htmlFor="price" className="block text-gray-700 text-sm font-bold mb-2">Price (per night):</label>
                    <input
                        type="number"
                        id="price"
                        name="price"
                        value={formData.price}
                        onChange={onFormChange}
                        step="500"
                        required
                        className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                    />
                </div>
                <div>
                    <label htmlFor="features" className="block text-gray-700 text-sm font-bold mb-2">Features (comma-separated):</label>
                    <input
                        type="text"
                        id="features"
                        name="features"
                        value={formData.features}
                        onChange={onFormChange}
                        placeholder="e.g., Wi-Fi, Balcony, Minibar"
                        className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                    />
                </div>
                <div>
                    <label htmlFor="availability" className="block text-gray-700 text-sm font-bold mb-2">Availability:</label>
                    <input
                        type="number"
                        id="availability"
                        name="availability"
                        value={formData.availability}
                        onChange={onFormChange}
                        required
                        className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                    />
                </div>
                <div>
                    <label htmlFor="imageUrl" className="block text-gray-700 text-sm font-bold mb-2">Image URL:</label>
                    <input
                        type="text" 
                        id="imageUrl"
                        name="imageUrl"
                        value={formData.imageUrl}
                        onChange={onFormChange}
                        placeholder="e.g., https://example.com/room-image.jpg"
                        className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                    />
                </div>

                <div className="flex space-x-2">
                    <button type="submit" className="bg-indigo-500 hover:bg-indigo-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline">
                        {submitButtonText}
                    </button>
                    {onCancel && (
                        <button
                            type="button"
                            onClick={onCancel}
                            className="bg-gray-300 hover:bg-gray-400 text-gray-800 font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
                        >
                            Cancel
                        </button>
                    )}
                </div>
            </form>
        </div>
    );
};

export default RoomForm;