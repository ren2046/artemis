package src.java.main;

import java.util.HashMap;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;

import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.api.jms.ActiveMQJMSClient;
import org.apache.activemq.artemis.api.jms.JMSFactoryType;
import org.apache.activemq.artemis.core.remoting.impl.netty.NettyConnectorFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ArtemisProducer0Application {

	public static void main(String[] args) {
		SpringApplication.run(ArtemisProducer0Application.class, args);

		InitialContext initialContext = null;

		Connection connection0 = null;

		try {
			initialContext = new InitialContext();

			HashMap<String, Object> map = new HashMap<String, Object>();
			 map.put("host", "0.0.0.0");
			 map.put("port", "61616");
			 TransportConfiguration server1 = new TransportConfiguration(NettyConnectorFactory.class.getName(), map);
			 HashMap<String, Object> map2 = new HashMap<String, Object>();
			 map2.put("host", "0.0.0.0");
			 map2.put("port", "61617");
			 TransportConfiguration server2 = new TransportConfiguration(NettyConnectorFactory.class.getName(), map2);

			 ActiveMQConnectionFactory connectionFactory = ActiveMQJMSClient.createConnectionFactoryWithHA(JMSFactoryType.CF, server1, server2);

			Thread.sleep(2000);

			connection0 = connectionFactory.createConnection("admin", "admin");
			
			Thread.sleep(6000);
					
			connection0.start();
			
			System.out.println("connection0 is created: "+connection0.getClientID());
			
			System.out.println("connection0 is started.");

			Session session0 = connection0.createSession(false, Session.AUTO_ACKNOWLEDGE);
			
			Thread.sleep(2000);
			
			Queue queue = session0.createQueue("exampleQueue::exampleQueue");
			
			System.out.println("session0 is created: " + session0.toString());

			MessageProducer producer0 = session0.createProducer(queue);
			
			Thread.sleep(2000);

			final int numMessages = 18;
			while (true) {
				for (int i = 0; i < numMessages; i++) {

					TextMessage message0 = session0.createTextMessage("Queue message: " + i);

					producer0.send(message0);
					System.out.println(message0.getText());

				}
				Thread.sleep(5000);
			}
		} catch (Exception e) {
			System.out.println("Error.");
			e.printStackTrace();
		} finally {
			try {
				connection0.close();
			} catch (JMSException e) {
				e.printStackTrace();
			}

		}
	}

}
