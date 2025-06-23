import React from "react";

function Features({ icon, title, description }){
    const IconComponent = icon;
    return (
    <div className="rounded-2xl shadow-md p-6 text-center bg-white">
      <IconComponent className="text-4xl mx-auto text-blue-600 mb-4" />
      <h2 className="text-2xl font-semibold mb-2">{title}</h2>
      <p>{description}</p>
    </div>
  );
    
}

export default Features;