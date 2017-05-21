package zti;

import java.io.IOException;
import java.io.PrintWriter;

import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class PublisherSubscriberTest
 */
@WebServlet("/Examples")
public class Examples extends HttpServlet {
	private static final int DELAY = 2000;
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Examples() {
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

		String action = request.getParameter("ACTION");

		try {
			if (action == null) {

			} else if (action.equalsIgnoreCase("topicTest")) {
				topicTest(request, response);
			} else if (action.equalsIgnoreCase("queueTest")) {
				queueTest(request, response);
			} else if (action.equalsIgnoreCase("recieveMessages")) {
				recieveMessages(request, response);
			} else if (action.equalsIgnoreCase("topicTestOld")) {
				topicTestOld(request, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void topicTest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();
		out.println("Test start <br>");

		TopicConnectionFactory connectionFactory = (TopicConnectionFactory) new InitialContext()
				.lookup("java:comp/env/jmsTCF");

		Topic topic = (Topic) new InitialContext().lookup("java:comp/env/jmsTopic");

		JMSContext jmsContext = connectionFactory.createContext();

		//JMSConsumer consumer = jmsContext.createConsumer(topic);
		JMSConsumer consumer = jmsContext.createDurableConsumer(topic, "ID");
		//JMSConsumer consumer = jmsContext.createSharedConsumer(topic);
		//JMSConsumer consumer = jmsContext.createSharedDurableConsumer(topic, "ID");
		JMSProducer producer = jmsContext.createProducer();

		TextMessage message = jmsContext.createTextMessage();

		message.setText("test Message via JMS");

		producer.send(topic, message);

		TextMessage recived = (TextMessage) consumer.receive();
		if (null == recived) {
			throw new Exception("no message");
		} else {
			out.print("message recived : " + recived.getText());
		}
		
		if (null != consumer) {
			consumer.close();
		}
		
		if (jmsContext != null) {
			jmsContext.close();
		}

		out.println("<br> Test completed <br>");
		
		
	}

	@Deprecated
	private void topicTestOld(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();
		out.println("Old test start <br>");

		TopicConnectionFactory connFactory = (TopicConnectionFactory) new InitialContext()
				.lookup("java:comp/env/jmsTCF");

		TopicConnection connection = (TopicConnection) connFactory.createConnection();

		connection.start();

		TopicSession session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);

		Topic topic = (Topic) new InitialContext().lookup("java:comp/env/jmsTopic");

		TopicSubscriber sub = session.createSubscriber(topic);

		TopicPublisher publisher = session.createPublisher(topic);

		publisher.publish(session.createTextMessage("PRZYKLADOWA WIADOMOSC"));

		TextMessage message = (TextMessage) sub.receive();

		if (null == message) {
			throw new Exception("No message");
		} else {
			out.println("Recieved :" + message.getText());
		}

		if (sub != null) {
			sub.close();
		}

		if (connection != null) {
			connection.close();
		}
		
		out.println("<br> Old test completed <br>");
	}

	private void queueTest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();
		out.println("Test start <br>");
		out.println("<form name=\"form1\" method=\"post\" action=\"FormPost\">");
		out.println("<label>Wiadomosc</label> <input type=\"text\" name=\"msg\"></input><br /> <input");
		out.println("type=\"submit\"></input>");

		out.println("<br>Test end <br>");
	}

	private void recieveMessages(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();
		out.println("Test start <br>");

		QueueConnectionFactory connectionFactory = (QueueConnectionFactory) new InitialContext()
				.lookup("java:comp/env/jndi_JMS_BASE_QCF");

		Queue queue = (Queue) new InitialContext().lookup("java:comp/env/jndi_INPUT_Q");

		JMSContext jmsContext = connectionFactory.createContext();

		JMSConsumer consumer = jmsContext.createConsumer(queue);
		// JMSConsumer consumer =
		// jmsContext.createConsumer(queue,"COLOR='BLUE'");
		// JMSConsumer consumer =
		// jmsContext.createConsumer(queue,"COLOR='RED'");
		TextMessage msg = null;
		boolean isMessageRecived = false;
		do {
			msg = (TextMessage) consumer.receive(DELAY);
			if (msg != null) {
				out.println("Recived : " + msg.getText() + "<br>");
				isMessageRecived = true;
			} else {
				if (!isMessageRecived) {
					out.println("<br> Nic nie otrzymanao");
				}
			}
		} while (msg != null);
		out.println("<br>Test end <br>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
