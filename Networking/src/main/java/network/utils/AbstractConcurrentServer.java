package network.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.Socket;

public abstract class AbstractConcurrentServer extends AbstractServer {
    private static Logger logger = LogManager.getLogger(AbstractConcurrentServer.class);

    public AbstractConcurrentServer(int port) {
        super(port);
        logger.debug("Concurrent AbstractServer");
    }

    protected void processRequest(Socket client) {
        Thread thread = createWorker(client);
        thread.start();
    }

    protected abstract Thread createWorker(Socket client);
}
