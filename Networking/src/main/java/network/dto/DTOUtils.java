package network.dto;

import model.Inscriere;
import model.Operator;
import model.Participant;
import model.Proba;
import services.IServices;
import services.InscriereException;

import java.util.ArrayList;
import java.util.List;

public class DTOUtils {

    public static ProbaDTO toDTO(Proba p) {
        return new ProbaDTO(
                p.getId(),
                p.getNume(),
                p.getCategorie()
        );
    }

    public static Proba fromDTO(ProbaDTO dto) {
        Proba p = new Proba(dto.getNume(), dto.getCategorieVarsta());
        p.setId(dto.getId());
        return p;
    }


    public static ParticipantDTO toDTO(Participant p) {
        return new ParticipantDTO(
                p.getId(),
                p.getNume(),
                p.getCnp(),
                p.getVarsta()
        );
    }

    public static Participant fromDTO(ParticipantDTO dto) {
        Participant p = new Participant(dto.getNume(), dto.getCnp(), dto.getVarsta());
        p.setId(dto.getId());
        return p;
    }


    public static InscriereDTO toDTO(Inscriere i) {
        return new InscriereDTO(
                i.getParticipant().getId(),
                i.getProba().getId()
        );
    }

    public static Inscriere fromDTO(InscriereDTO dto,
                                    Participant p,
                                    Proba pr) {
        return new Inscriere(p, pr);
    }

    public static List<Proba> fromDTOList(ProbaDTO[] dtos) {
        List<Proba> list = new ArrayList<>();
        for (ProbaDTO dto : dtos) {
            Proba p = new Proba(dto.getNume(), dto.getCategorieVarsta());
            p.setId(dto.getId());
            list.add(p);
        }
        return list;
    }

    public static List<Participant> fromDTOList(ParticipantDTO[] dtos) {
        List<Participant> list = new ArrayList<>();
        for (ParticipantDTO dto : dtos) {
            Participant p = new Participant(dto.getNume(), dto.getCnp(), dto.getVarsta());
            p.setId(dto.getId());
            list.add(p);
        }
        return list;
    }


    public static List<Inscriere> fromDTOList(InscriereDTO[] dtos, IServices service) throws InscriereException {
        List<Inscriere> list = new ArrayList<>();
        for (InscriereDTO dto : dtos) {
            Participant p = service.getParticipantById(dto.getParticipantId());
            Proba pr = service.getProbaById(dto.getProbaId());
            Inscriere i = new Inscriere(p, pr);
            list.add(i);
        }
        return list;
    }
}
