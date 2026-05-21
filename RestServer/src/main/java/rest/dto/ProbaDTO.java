package rest.dto;


/**
 * DTO folosit la CREARE (POST) - fara id.
 * La returnare, id-ul e inclus in ProbaResponseDTO.
 */
public class ProbaDTO {
    private String nume;
    private String categorieVarsta;

    public ProbaDTO() {}

    public ProbaDTO(String nume, String categorieVarsta) {
        this.nume = nume;
        this.categorieVarsta = categorieVarsta;
    }

    public String getNume() { return nume; }
    public void setNume(String nume) { this.nume = nume; }

    public String getCategorieVarsta() { return categorieVarsta; }
    public void setCategorieVarsta(String categorieVarsta) { this.categorieVarsta = categorieVarsta; }
}
