import React from 'react';

const Alerts = ({ loading, error, formSuccess, formError }) => {
    return (
        <>
            {loading && (
                <div className="bg-blue-100 border border-blue-400 text-blue-700 px-4 py-3 rounded relative mb-4" role="alert">
                    <strong className="font-bold">Loading...</strong>
                </div>
            )}
            {error && (
                <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative mb-4" role="alert">
                    <strong className="font-bold">Error!</strong>
                    <span className="block sm:inline">{error}</span>
                </div>
            )}
            {formSuccess && (
                <div className="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded relative mb-4" role="alert">
                    <strong className="font-bold">Success!</strong>
                    <span className="block sm:inline">Operation completed successfully.</span>
                </div>
            )}
            {formError && (
                <div className="bg-orange-100 border border-orange-400 text-orange-700 px-4 py-3 rounded relative mb-4" role="alert">
                    <strong className="font-bold">Warning!</strong>
                    <span className="block sm:inline">{formError}</span>
                </div>
            )}
        </>
    );
};

export default Alerts;