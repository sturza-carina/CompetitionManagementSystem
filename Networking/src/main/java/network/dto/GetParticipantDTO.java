package network.dto;

public class GetParticipantDTO {
    private long idProba;
    private String categorie;

    public GetParticipantDTO(long idProba, String categorie) {
        this.idProba = idProba;
        this.categorie = categorie;
    }

    public long getIdProba() {
        return idProba;
    }

    public String getCategorie() {
        return categorie;
    }
}
