package network.dto;

import java.io.Serializable;

public class InscriereDTO implements Serializable {
    private Long participantId;
    private Long probaId;

    public InscriereDTO(Long participantId, Long probaId) {
        this.participantId = participantId;
        this.probaId = probaId;
    }

    public Long getParticipantId() {
        return participantId;
    }

    public Long getProbaId() {
        return probaId;
    }

    @Override
    public String toString() {
        return "InscriereDTO[" + participantId + " " + probaId + "]";
    }
}
