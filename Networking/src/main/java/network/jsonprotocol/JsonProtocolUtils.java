package network.jsonprotocol;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import network.dto.*;
import model.Operator;

import java.util.List;

public class JsonProtocolUtils {

    /// requests
    public static Request createLoginRequest(Operator operator) {
        OperatorDTO dto = new OperatorDTO(operator.getId(), operator.getUsername(), operator.getPassword());
        return new Request(RequestType.LOGIN, dto);
    }

    public static Request createInscriereRequest(String nume, String cnp, List<Long> probeIds) {
        InscriereRequestDTO dto = new InscriereRequestDTO(nume, cnp, probeIds);
        return new Request(RequestType.ADAUGA_INSCRIERE, dto);
    }

    public static Request createLogoutRequest(Operator operator) {
        OperatorDTO dto = new OperatorDTO(operator.getId(), operator.getUsername(), operator.getPassword());
        return new Request(RequestType.LOGOUT, dto);
    }

    public static Request createGetInscrieriRequest() {
        return new Request(RequestType.GET_INSCRIERI, null);
    }

    public static Request createGetProbeRequest() {
        return new Request(RequestType.GET_PROBE, null);
    }

    public static Request createGetParticipantRequest(long idParticipant) {
        return new Request(RequestType.GET_PARTICIPANT, idParticipant);
    }

    public static Request createParticipantByProbaRequest(long idProba, String categorieVarsta) {
        GetParticipantDTO dto = new GetParticipantDTO(idProba, categorieVarsta);
        return new Request(RequestType.GET_PARTICIPANT_BY_PROBA, dto);
    }


    public static Request createGetProbaRequest(long idProba) {
        return new Request(RequestType.GET_PROBA, idProba);
    }

    public static Request createDeleteInscriereRequest(String cnp, long idProba) {
        StergeInscriereDTO dto = new StergeInscriereDTO(cnp, idProba);
        return new Request(RequestType.STERGE_INSCRIERE, dto);
    }


    /// response
    public static Response createOkResponse(Object data) {
        JsonElement jsonData = data != null ? new Gson().toJsonTree(data) : JsonNull.INSTANCE;
        return new Response(ResponseType.OK, jsonData);
    }

    public static Response createErrorResponse(String msg) {
        return new Response(ResponseType.ERROR, msg);
    }


    public static Response createNewInscriereResponse(InscriereDTO inscriere) {
        JsonElement jsonData = new Gson().toJsonTree(inscriere);
        return new Response(ResponseType.NEW_INSCRIERE, jsonData);
    }

    public static Response createDeleteInscriereResponse(InscriereDTO inscriere) {
        JsonElement jsonData = new Gson().toJsonTree(inscriere);
        return new Response(ResponseType.DELETE_INSCRIERE, jsonData);
    }
}
