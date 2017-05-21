package zti;

import java.io.IOException;
import java.io.PrintWriter;

import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class FormPost
 */
@WebServlet("/FormPost")
public class FormPost extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FormPost() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		// response.getWriter().append("Served at:
		// ").append(request.getContextPath());
		PrintWriter out = response.getWriter();
		out.print("<a href=\"/JMS/\"> Home </a>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();

		QueueConnectionFactory connectionFactory;
		try {
			// Tworzenie po³¹czenia
			connectionFactory = (QueueConnectionFactory) new InitialContext().lookup("java:comp/env/jndi_JMS_BASE_QCF");

			// Tworzenie kolejki z JNDI
			Queue queue = (Queue) new InitialContext().lookup("java:comp/env/jndi_INPUT_Q");

			// Tworzenie kontekst JMS
			JMSContext jmsContext = connectionFactory.createContext();

			// Tworzenie producera do wiadomosci
			JMSProducer producer = jmsContext.createProducer();

			// Tworzenie wiadomosci
			TextMessage message = jmsContext.createTextMessage();

			// Ustawianie property
			message.setStringProperty("COLOR", "BLUE");
			message.setText(request.getParameter("msg"));

			out.println("<br> queue: " + queue.getQueueName());

			// Wysylanie wiadomosci do kolejki
			producer.send(queue, message);

			out.print("Message send sucesfully");

			if (jmsContext != null) {
				jmsContext.close();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		doGet(request, response);
	}

}
