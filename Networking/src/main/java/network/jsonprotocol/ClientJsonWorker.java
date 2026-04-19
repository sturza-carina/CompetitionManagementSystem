package network.jsonprotocol;

import com.google.gson.Gson;
import network.dto.*;
import model.Inscriere;
import model.Operator;
import model.Participant;
import model.Proba;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.IObserver;
import services.IServices;
import services.InscriereException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class ClientJsonWorker implements Runnable, IObserver {
    private IServices server;
    private Socket connection;

    private BufferedReader input;
    private PrintWriter output;
    private Gson gson;
    private volatile boolean connected;

    private static Logger logger = LogManager.getLogger(ClientJsonWorker.class);

    public ClientJsonWorker(IServices server, Socket connection) {
        this.server = server;
        this.connection = connection;
        gson = new Gson();

        try{
            output = new PrintWriter(connection.getOutputStream());
            input = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            connected = true;
        } catch (IOException e) {
            logger.error(e);
            logger.error(e.getStackTrace());
        }
    }

    public void run() {
        logger.info("Worker pornit pentru client: {}", connection);

        while (connected) {
            try{
                logger.debug("Astept request de la client...");

                String requestLine = input.readLine();
                if (requestLine == null) {
                    logger.info("Clientul a inchis conexiunea.");
                    connected = false;
                    break;
                }

                Request request = gson.fromJson(requestLine, Request.class);
                Response response = handleRequest(request);

                if(response != null) {
                    sendResponse(response);
                }
            } catch (IOException e) {
                logger.error(e);
                logger.error(e.getStackTrace());
                connected = false;
            }
        }

        try{
            input.close();
            output.close();
            connection.close();
        } catch (IOException e) {
            logger.error("Error "+e);
        }
    }


    private Response handleRequest(Request request) {
        Response response = null;

        switch (request.getType()) {
            case LOGIN:
                OperatorDTO operatorDTO = gson.fromJson(gson.toJson(request.getData()), OperatorDTO.class);
                Operator operator = new Operator(operatorDTO.getUsername(), operatorDTO.getPassword());

                try{
                    server.login(operator, this);
                    response = JsonProtocolUtils.createOkResponse(null);
                } catch (InscriereException e) {
                    response = JsonProtocolUtils.createErrorResponse(e.getMessage());
                }
                break;

            case LOGOUT:
                OperatorDTO opDTO = gson.fromJson(gson.toJson(request.getData()), OperatorDTO.class);
                Operator op = new Operator(opDTO.getUsername(), opDTO.getPassword());

                try{
                    server.logout(op, this);
                    response = JsonProtocolUtils.createOkResponse(null);
                } catch (InscriereException e) {
                    response = JsonProtocolUtils.createErrorResponse(e.getMessage());
                }
                break;

            case GET_PROBE:
                try{
                    List<Proba> probe = server.getAllProbe(); // lista din service
                    List<ProbaDTO> probeDTO = probe.stream()
                            .map(DTOUtils::toDTO)
                            .toList();

                    response = JsonProtocolUtils.createOkResponse(probeDTO);
                } catch (InscriereException e) {
                    response = JsonProtocolUtils.createErrorResponse(e.getMessage());
                }
                break;

            case ADAUGA_INSCRIERE:
                InscriereRequestDTO inscriereDTO  = gson.fromJson(gson.toJson(request.getData()), InscriereRequestDTO.class);

                try{
                    server.inscriereParticipant(
                            inscriereDTO.getNume(),
                            inscriereDTO.getCnp(),
                            inscriereDTO.getProbeIds()
                    );


                    response = JsonProtocolUtils.createOkResponse(null);
                } catch (InscriereException e) {
                    response = JsonProtocolUtils.createErrorResponse(e.getMessage());
                }
                break;

            case STERGE_INSCRIERE:
                StergeInscriereDTO stergeDTO = gson.fromJson(
                        gson.toJson(request.getData()),
                        StergeInscriereDTO.class
                );

                try {
                    server.stergeProbaParticipant(stergeDTO.getCnp(), stergeDTO.getIdProba());
                    response = JsonProtocolUtils.createOkResponse(null);
                } catch (InscriereException e) {
                    response = JsonProtocolUtils.createErrorResponse(e.getMessage());
                }
                break;

            case GET_INSCRIERI:
                try {
                    List<Inscriere> inscrieri = server.getAllInscrieri();

                    List<InscriereDTO> dtos = inscrieri.stream()
                            .map(DTOUtils::toDTO)
                            .toList();

                    response = JsonProtocolUtils.createOkResponse(dtos);

                } catch (InscriereException e) {
                    response = JsonProtocolUtils.createErrorResponse(e.getMessage());
                }
                break;

            case GET_PARTICIPANT_BY_PROBA:
                GetParticipantDTO dto = gson.fromJson(
                        gson.toJson(request.getData()),
                        GetParticipantDTO.class
                );

                try {
                    List<Participant> list = server.getParticipantByProbaSiCategorie(
                            dto.getIdProba(),
                            dto.getCategorie()
                    );

                    List<ParticipantDTO> listDTO = list.stream()
                            .map(DTOUtils::toDTO)
                            .toList();

                    response = JsonProtocolUtils.createOkResponse(listDTO);
                } catch (InscriereException e) {
                    response = JsonProtocolUtils.createErrorResponse(e.getMessage());
                }
                break;

            case GET_PARTICIPANT:
                Long idParticipant = gson.fromJson(
                        gson.toJson(request.getData()),
                        Long.class
                );

                try {
                    Participant p = server.getParticipantById(idParticipant);

                    ParticipantDTO pDto = DTOUtils.toDTO(p);

                    response = JsonProtocolUtils.createOkResponse(pDto);

                } catch (InscriereException e) {
                    response = JsonProtocolUtils.createErrorResponse(e.getMessage());
                }
                break;

            case GET_PROBA: {
                try {
                    long idProba = ((Number) request.getData()).longValue();
                    Proba proba = server.getProbaById(idProba);

                    ProbaDTO prDto = new ProbaDTO(proba.getId(), proba.getNume(), proba.getCategorie());

                    response = JsonProtocolUtils.createOkResponse(prDto);

                } catch (Exception e) {
                    response = JsonProtocolUtils.createErrorResponse(e.getMessage());
                }
                break;
            }

            default:
                response = JsonProtocolUtils.createErrorResponse("Unknown request type " + request.getType());
                break;

        }

        return response;
    }

    private void sendResponse(Response response) {
        output.println(gson.toJson(response));
        output.flush();
    }

    @Override
    public void inscriereAdded(Inscriere inscriere) throws InscriereException {
        try {
            InscriereDTO dto = DTOUtils.toDTO(inscriere);
            Response response = JsonProtocolUtils.createNewInscriereResponse(dto);

            sendResponse(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void inscriereDeleted(Inscriere inscriere) throws InscriereException {
        try {
            InscriereDTO dto = DTOUtils.toDTO(inscriere);
            Response response = JsonProtocolUtils.createDeleteInscriereResponse(dto);

            sendResponse(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
