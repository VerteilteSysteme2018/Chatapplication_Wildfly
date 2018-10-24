import javax.annotation.Resource;
import javax.jms.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = "/send")
public class DummyServlet extends HttpServlet {
    @Resource(lookup = "java:/jms/chatFactory")
    ConnectionFactory connectionFactory;

    @Resource(lookup = "java:/jms/queue/chatQueue")
    Destination destination;


    @Override
    protected void doGet(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();

        try {
            QueueConnection connection = (QueueConnection)
                    connectionFactory.createConnection(
                            "user",
                            "user"
                    );

            try {
                QueueSession session =
                        connection.createQueueSession(
                                false,
                                Session.AUTO_ACKNOWLEDGE
                        );

                try {
                    MessageProducer producer =
                            session.createProducer(destination);

                    try {
                        TextMessage message =
                                session.createTextMessage(
                                        "Nachricht"
                                );

                        producer.send(message);

                        writer.println(
                                "Message an die Queue gesendet"
                        );
                    } finally {
                        producer.close();
                    }
                } finally {
                    session.close();
                }

            } finally {
                connection.close();
            }

        } catch (Exception ex) {
            ex.printStackTrace(writer);
        }
    }
}