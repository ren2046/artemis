package src.java.main;

import java.util.HashMap;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;

import org.apache.activemq.artemis.api.core.DiscoveryGroupConfiguration;
import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.api.core.UDPBroadcastEndpointFactory;
import org.apache.activemq.artemis.api.jms.ActiveMQJMSClient;
import org.apache.activemq.artemis.api.jms.JMSFactoryType;
import org.apache.activemq.artemis.core.remoting.impl.netty.NettyConnectorFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ArtemisApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArtemisApplication.class, args);

		InitialContext initialContext = null;

		Connection connection0 = null;

		Connection connection1 = null;

		try {
			initialContext = new InitialContext();

			//Queue queue = (Queue) initialContext.lookup("queue/exampleQueue");

			// Step 3. Look-up a JMS Connection Factory object from JNDI on server 0
			 //ConnectionFactory connectionFactory = (ConnectionFactory) initialContext.lookup("ConnectionFactory");

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

			Thread.sleep(2000);

			connection1 = connectionFactory.createConnection("admin", "admin");

			Thread.sleep(6000);

			Session session0 = connection0.createSession(false, Session.AUTO_ACKNOWLEDGE);
			
			Thread.sleep(2000);

			Session session1 = connection1.createSession(false, Session.AUTO_ACKNOWLEDGE);
			
			Thread.sleep(2000);
			
			Queue queue = session0.createQueue("exampleQueue::exampleQueue");

			MessageConsumer consumer0 = session0.createConsumer(queue);
			
			Thread.sleep(2000);

			MessageConsumer consumer1 = session1.createConsumer(queue);
			
			Thread.sleep(2000);

			connection0.start();

			connection1.start();
			
			Thread.sleep(1000);

			// MessageProducer producer0 = session0.createProducer(queue);

			final int numMessages = 18;

			// for (int i = 0; i < numMessages; i++) {
			//
			// TextMessage message0 = session0.createTextMessage("Queue message: " + i);
			//
			// producer0.send(message0);
			// System.out.println(message0.getText());
			//
			// }
			while (true) {
				for (int i = 0; i < numMessages / 2; i++) {

					TextMessage received0 = (TextMessage) consumer0.receive(200);

					if (received0 == null) {
						System.out.println("consumer0:null");
						// throw new IllegalStateException("Message is null!");
					} else {
						System.out.println("consumer0:" + received0.getText());

					}
				}
				Thread.sleep(500);
				for (int i = 0; i < numMessages / 2; i++) {
					TextMessage received1 = (TextMessage) consumer1.receive(200);

					if (received1 == null) {
						System.out.println("consumer1:null");
						// throw new IllegalStateException("Message is null!");
					} else {
						System.out.println("consumer1:" + received1.getText());
					}

				}
				Thread.sleep(2000);
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
			try {
				connection1.close();
			} catch (JMSException e) {
				e.printStackTrace();
			}

		}
	}

}
