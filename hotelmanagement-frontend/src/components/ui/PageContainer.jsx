import React from "react";

 function PageContainer({ children }) {
  return (
   <div className="min-h-screen w-full px-4 py-10 sm:px-6 lg:px-8 bg-white">
      <div className="max-w-6xl mx-auto">{children}</div>
    </div>
  );
}
export default PageContainer;
