import PageContainer from "../../components/ui/PageContainer";
function About(){
    return(
         <PageContainer>
          <div className="w-full min-h-screen flex items-center justify-center px-4">   
         <section id="about" className="max-w-4xl w-full">
        <h2 className="text-4xl font-bold mb-6 text-center">About LuxeStay</h2>
        <p className="text-gray-700 leading-relaxed text-center">
          LuxeStay is your gateway to luxury accommodations with exceptional service, beautiful interiors,
          and prime locations. Whether it's a weekend getaway or a long stay, we ensure your comfort is a top priority.
        </p>
      </section>
    </div>
    </PageContainer>
          )
}

export default About;