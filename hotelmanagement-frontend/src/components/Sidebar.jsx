import React from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faUserCircle } from '@fortawesome/free-solid-svg-icons';

const Sidebar = ({ managerUsername, assignedManager, hotelName }) => {
    return (
        <aside className="w-64 h-full bg-white shadow rounded-md p-6 flex-shrink-0">
            <div className="flex flex-col items-center">
                <FontAwesomeIcon icon={faUserCircle} className="text-indigo-500 text-8xl mb-4" />
                {managerUsername && <h2 className="text-xl font-semibold text-gray-800 mb-1">{managerUsername}</h2>}
                {assignedManager ? (
                    <p className="text-gray-600 text-sm mb-2">({assignedManager.managerName})</p>
                ) : (
                    <p className="text-gray-600 italic mb-2"></p>
                )}
                {hotelName && <p className="text-gray-600 text-sm">Managing {hotelName}</p>}
            </div>
        </aside>
    );
};

export default Sidebar;