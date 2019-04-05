package activeMQ;

import com.infinityworks.google.model.LibraryModel;
import org.apache.activemq.ActiveMQConnectionFactory;
import progress.LibraryUtils;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;


import javax.jms.*;
import java.util.Optional;

public class App {

    public static void main(String[] args) {

        try {
            thread(new HelloWorldProducer(), false);
            thread(new HelloWorldProducer(), false);
            Thread.sleep(1000);
            thread(new HelloWorldConsumer(), false);
            thread(new HelloWorldProducer(), false);
            thread(new HelloWorldConsumer(), false);
            thread(new HelloWorldProducer(), false);
            Thread.sleep(1000);
            thread(new HelloWorldConsumer(), false);
            thread(new HelloWorldProducer(), false);
            thread(new HelloWorldConsumer(), false);
            thread(new HelloWorldConsumer(), false);
            thread(new HelloWorldProducer(), false);
            thread(new HelloWorldProducer(), false);
            Thread.sleep(1000);
            thread(new HelloWorldProducer(), false);
            thread(new HelloWorldConsumer(), false);
            thread(new HelloWorldConsumer(), false);
            thread(new HelloWorldProducer(), false);
            thread(new HelloWorldConsumer(), false);
            thread(new HelloWorldProducer(), false);
            thread(new HelloWorldConsumer(), false);
            thread(new HelloWorldProducer(), false);
            thread(new HelloWorldConsumer(), false);
            thread(new HelloWorldConsumer(), false);
            thread(new HelloWorldProducer(), false);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void thread(Runnable runnable, boolean daemon) {
        Thread brokerThread = new Thread(runnable);
        brokerThread.setDaemon(daemon);
        brokerThread.start();
    }

    public static class HelloWorldProducer implements Runnable {
        public void run() {
            try {
                // Create a ConnectionFactory
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost");

                // Create a Connection
                Connection connection = connectionFactory.createConnection();
                connection.start();

                // Create a Session
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

                // Create the destination (Topic or Queue)
                Destination destination = session.createQueue("bytes.foo");

                // Create a MessageSender from the Session to the Topic or Queue
                MessageProducer producer = session.createProducer(destination);
                producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

                // Create a messages
                BytesMessage message = session.createBytesMessage();
                byte[] serialization = LibraryUtils.serialization(buildingLibraryInstance());
                message.writeBytes(serialization);

                //Tell the producer to send the message
                System.out.println("Sent message " + message.hashCode());
                producer.send(message);

                // Clean up
                producer.close();
                session.close();
                connection.close();
            } catch (Exception e) {
                System.out.println("Caught: " + e);
                e.printStackTrace();
            }
        }
    }

    public static class HelloWorldConsumer implements Runnable, ExceptionListener {
        public void run() {
            try {

                // Create a ConnectionFactory
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost");

                // Create a Connection
                Connection connection = connectionFactory.createConnection();
                connection.start();

                connection.setExceptionListener(this);

                // Create a Session
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

                // Create the destination (Topic or Queue)
                Destination destination = session.createQueue("bytes.foo");

                // Create a MessageConsumer from the Session to the Topic or Queue
                MessageConsumer consumer = session.createConsumer(destination);

                // Wait for a message
                Message message = consumer.receive(1000);

                if (message instanceof BytesMessage) {
                    BytesMessage bytesMessage = (BytesMessage) message;
                    byte[] data = new byte[(int) bytesMessage.getBodyLength()];
                    bytesMessage.readBytes(data);
                    Optional <LibraryModel.Library> library = LibraryUtils.deserialization(data);
                    System.out.println("Received: " + library);
                } else {
                    System.out.println("Received: " + message);
                }


                consumer.close();
                session.close();
                connection.close();
            } catch (Exception e) {
                System.out.println("Caught: " + e);
                e.printStackTrace();
            }
        }

        public synchronized void onException(JMSException ex) {
            System.out.println("JMS Exception occured.  Shutting down client.");
        }
    }


    public static LibraryModel.Library buildingLibraryInstance() {
        //1. BUILDING INSTANCE OF A PROTOBUFFER DEFINED MESSAGE
        LibraryModel.Book book = LibraryModel.Book.newBuilder()
                .setIsbn("bla bla isbn")
                .setTitle("Title 1")
                .setPublishDate("Date XY")
                .addAuthor(LibraryModel.Book.Author.newBuilder().setName("Name Author 1").build())
                .setGenre(LibraryModel.Book.Genre.TECHNOLOGY)
                .build();

        LibraryModel.Library library = LibraryModel.Library.newBuilder().addBooks(book).build();
        return library;
    }

}



