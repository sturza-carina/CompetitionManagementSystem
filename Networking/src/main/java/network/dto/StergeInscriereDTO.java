package network.dto;

public class StergeInscriereDTO {
    private String cnp;
    private long idProba;

    public StergeInscriereDTO(String cnp, long idProba) {
        this.cnp = cnp;
        this.idProba = idProba;
    }

    public String getCnp() { return cnp; }
    public long getIdProba() { return idProba; }
}
