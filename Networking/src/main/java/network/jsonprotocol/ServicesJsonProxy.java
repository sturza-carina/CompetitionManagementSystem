package network.jsonprotocol;

import com.google.gson.Gson;
import network.dto.*;
import model.Inscriere;
import model.Operator;
import model.Participant;
import model.Proba;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;
import services.IObserver;
import services.IServices;
import services.InscriereException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ServicesJsonProxy implements IServices {
    private String host;
    private int port;

    private IObserver client;

    private BufferedReader input;
    private PrintWriter output;
    private Gson gson;
    private Socket connection;

    private BlockingQueue<Response> qresponses;
    private volatile boolean finished;

    private static Logger logger = LogManager.getLogger(ServicesJsonProxy.class);


    public ServicesJsonProxy(String host, int port) {
        this.host = host;
        this.port = port;

        qresponses = new LinkedBlockingQueue<Response>();
    }


    @Override
    public void login(Operator operator, IObserver client) throws InscriereException {
        initializeConnection();

        Request request = JsonProtocolUtils.createLoginRequest(operator);
        sendRequest(request);

        Response response = readResponse();

        if(response.getType().equals(ResponseType.OK)) {
            this.client=client;
            return;
        }

        if(response.getType().equals(ResponseType.ERROR)) {
            String err = response.getErrorMessage();;
            closeConnection();
            throw new InscriereException(err);
        }

    }

    @Override
    public void logout(Operator operator, IObserver client) throws InscriereException {
        ensureConnection();

        Request request = JsonProtocolUtils.createLogoutRequest(operator);
        sendRequest(request);

        Response response = readResponse();
        closeConnection();

        if(response.getType().equals(ResponseType.ERROR)) {
            String err = response.getErrorMessage();
            throw new InscriereException(err);
        }
    }

    @Override
    public List<Proba> getAllProbe() throws InscriereException {
        ensureConnection();

        Request request = JsonProtocolUtils.createGetProbeRequest();
        sendRequest(request);

        Response response = readResponse();
        if(response.getType().equals(ResponseType.OK)) {
            ProbaDTO[] probeDTO = gson.fromJson(response.getData().toString(), ProbaDTO[].class);
            return DTOUtils.fromDTOList(probeDTO);

        } else {
            throw new InscriereException(response.getErrorMessage());
        }
    }

    @Override
    public List<Inscriere> getAllInscrieri() throws InscriereException {
        ensureConnection();

        Request request = JsonProtocolUtils.createGetInscrieriRequest();
        sendRequest(request);

        Response response = readResponse();
        if(response.getType().equals(ResponseType.OK)) {
            InscriereDTO[] inscrieriDTO = gson.fromJson(gson.toJson(response.getData()), InscriereDTO[].class);

            List<Inscriere> list = DTOUtils.fromDTOList(inscrieriDTO, (IServices) this);
            return list;
        } else {
            throw new InscriereException(response.getErrorMessage());
        }
    }

    @Override
    public List<Participant> getParticipantByProbaSiCategorie(long idProba, String categorieVarsta) throws InscriereException {
        ensureConnection();

        Request request = JsonProtocolUtils.createParticipantByProbaRequest(idProba, categorieVarsta);
        sendRequest(request);

        Response response = readResponse();
        if(response.getType().equals(ResponseType.OK)) {
            ParticipantDTO[] participantsDTO = gson.fromJson(response.getData().toString(), ParticipantDTO[].class);
            return DTOUtils.fromDTOList(participantsDTO);

        } else {
            throw new InscriereException(response.getErrorMessage());
        }
    }

    @Override
    public void inscriereParticipant(String nume, String cnp, List<Long> probeIds) throws InscriereException {
        ensureConnection();

        InscriereRequestDTO dto = new InscriereRequestDTO(nume, cnp, probeIds);

        Request request = JsonProtocolUtils.createInscriereRequest(dto.getNume(), dto.getCnp(), dto.getProbeIds());
        sendRequest(request);

        Response response = readResponse();
        if(response.getType().equals(ResponseType.ERROR)) {
            throw new InscriereException(response.getErrorMessage());
        }

    }

    @Override
    public void stergeProbaParticipant(String cnp, long idProba) throws InscriereException {
        ensureConnection();

        Request request = JsonProtocolUtils.createDeleteInscriereRequest(cnp, idProba);
        sendRequest(request);

        Response response = readResponse();
        if(response.getType().equals(ResponseType.ERROR)) {
            throw new InscriereException(response.getErrorMessage());
        }

    }

    @Override
    public Participant getParticipantById(long idParticipant) throws InscriereException {
        ensureConnection();

        Request request = JsonProtocolUtils.createGetParticipantRequest(idParticipant);
        sendRequest(request);

        Response response = readResponse();
        if(response.getType().equals(ResponseType.OK)) {
            ParticipantDTO participantDTO = gson.fromJson(response.getData(), ParticipantDTO.class);
            return DTOUtils.fromDTO(participantDTO);

        } else {
            throw new InscriereException(response.getErrorMessage());
        }

    }

    @Override
    public Proba getProbaById(long idProba) throws InscriereException {
        ensureConnection();

        Request request = JsonProtocolUtils.createGetProbaRequest(idProba);
        sendRequest(request);

        Response response = readResponse();
        if(response.getType().equals(ResponseType.OK)) {
            ProbaDTO probaDTO = gson.fromJson(response.getData(), ProbaDTO.class);
            return DTOUtils.fromDTO(probaDTO);

        } else {
            throw new InscriereException(response.getErrorMessage());
        }
    }


    private void initializeConnection() throws InscriereException {
        try {
            gson = new Gson();
            connection = new Socket(host, port);

            output = new PrintWriter(connection.getOutputStream());
            output.flush();
            input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            finished = false;

            startReader();
        } catch (IOException e) {
            logger.error(e);
            logger.error(e.getStackTrace());
            throw new InscriereException("Nu s-a putut conecta la server: " + e.getMessage());
        }
    }

    private void startReader() {
        Thread thread = new Thread(new ReaderThread());
        thread.start();
    }

    private void closeConnection() {
        finished = true;

        try{
            input.close();
            output.close();
            connection.close();
            client = null;
        } catch (IOException e) {
            logger.error(e);
            logger.error(e.getStackTrace());
        }
    }

    private synchronized void sendRequest(Request request) throws InscriereException {
        String reqLine = gson.toJson(request);

        try{
            output.println(reqLine);
            output.flush();
        } catch (Exception e) {
            throw new InscriereException("Error sending object "+e);
        }
    }

    private Response readResponse() throws InscriereException {
        Response response = null;

        try {
            response = qresponses.take();

        } catch (InterruptedException e) {
            logger.error(e);
            logger.error(e.getStackTrace());
        }
        return response;
    }


    private class ReaderThread implements Runnable {
        public void run() {
            while (!finished) {

                try{

                    System.out.println("READER: astept linie de la server...");
                    String respLine = input.readLine();

                    if (respLine == null) {
                        logger.debug("Connection closed by server");
                        finished = true;
                        break;
                    }

                    logger.debug("response received {}" , respLine);
                    Response response = gson.fromJson(respLine, Response.class);

                    if (response == null) {
                        logger.error("Could not deserialize response: {}", respLine);
                        continue;
                    }

                    if(isUpdated(response)) {
                        Thread t = new Thread(() -> handleUpdate(response));
                        t.setDaemon(true);
                        t.start();

                    } else {
                        try{
                            qresponses.put(response);

                        } catch (InterruptedException e) {
                            logger.error(e);
                            logger.error(e.getStackTrace());
                        }
                    }
                } catch (IOException e) {
                    logger.error("Reading error "+e);
                }
            }
        }
    }

    private boolean isUpdated(Response response) {
        return response.getType() == ResponseType.NEW_INSCRIERE
            || response.getType() == ResponseType.DELETE_INSCRIERE;
    }

    private void handleUpdate(Response response){
        if (response.getType() == ResponseType.NEW_INSCRIERE) {
            InscriereDTO dto = gson.fromJson(
                    response.getData().toString(),
                    InscriereDTO.class
            );

            try {
                Inscriere inscriere = DTOUtils.fromDTO(
                        dto,
                        getParticipantById(dto.getParticipantId()),
                        getProbaById(dto.getProbaId())
                );

                client.inscriereAdded(inscriere);

            } catch (InscriereException e) {
                logger.error(e);
                logger.error(e.getStackTrace());
            }
        }

        if (response.getType() == ResponseType.DELETE_INSCRIERE){
            Gson gson = new Gson();
            InscriereDTO dto = gson.fromJson(
                    gson.toJson(response.getData()),
                    InscriereDTO.class
            );

            try{
                Inscriere inscriere = DTOUtils.fromDTO(
                        dto,
                        getParticipantById(dto.getParticipantId()),
                        getProbaById(dto.getProbaId())
                );

                client.inscriereDeleted(inscriere);

            } catch(InscriereException e){
                logger.error(e);
            }
        }
    }

    private void ensureConnection() throws InscriereException {
        if (connection == null || connection.isClosed()) {
            initializeConnection();
        }
    }
}
