package network.utils;

import network.jsonprotocol.ClientJsonWorker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.IServices;

import java.net.Socket;

public class JsonConcurrentServer extends AbstractConcurrentServer {
    private IServices services;
    private static Logger logger = LogManager.getLogger(JsonConcurrentServer.class);

    public JsonConcurrentServer(int port, IServices services) {
        super(port);
        this.services = services;

        logger.info("Init JsonConcurrentServer");
    }


    @Override
    protected Thread createWorker(Socket client) {
        ClientJsonWorker worker = new ClientJsonWorker(services, client);

        Thread thread = new Thread(worker);
        return thread;
    }
}
