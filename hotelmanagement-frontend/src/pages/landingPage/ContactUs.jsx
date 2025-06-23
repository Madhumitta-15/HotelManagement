import PageContainer from "../../components/ui/PageContainer";


function ContactUs(){
    return(
        <PageContainer>
        <h1 className="text-3xl sm:text-4xl font-bold mb-4">Contact Us</h1>
      <p className="text-gray-600 mb-4">
        Have questions or feedback? We'd love to hear from you.
      </p>
      <form className="grid gap-4 max-w-lg">
        <input className="border p-2 rounded-md" placeholder="Your Name" />
        <input className="border p-2 rounded-md" placeholder="Your Email" />
        <textarea className="border p-2 rounded-md" rows="4" placeholder="Message" />
        <button className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700">
          Send
        </button>
      </form>
        </PageContainer>
          )
}

export default ContactUs;