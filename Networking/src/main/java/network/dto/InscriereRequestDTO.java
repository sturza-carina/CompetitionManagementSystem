package network.dto;

import java.util.List;

public class InscriereRequestDTO {
    private String nume;
    private String cnp;
    private List<Long> probeIds;

    public InscriereRequestDTO(String nume, String cnp, List<Long> probeIds) {
        this.nume = nume;
        this.cnp = cnp;
        this.probeIds = probeIds;
    }

    public String getNume() { return nume; }
    public String getCnp() { return cnp; }
    public List<Long> getProbeIds() { return probeIds; }
}
