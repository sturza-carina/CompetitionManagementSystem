package rest.dto;

/**
 * DTO folosit in RASPUNSURI - include intotdeauna id-ul resursei.
 */
public class ProbaResponseDTO {
    private Long id;
    private String nume;
    private String categorieVarsta;

    public ProbaResponseDTO() {}

    public ProbaResponseDTO(Long id, String nume, String categorieVarsta) {
        this.id = id;
        this.nume = nume;
        this.categorieVarsta = categorieVarsta;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNume() { return nume; }
    public void setNume(String nume) { this.nume = nume; }

    public String getCategorieVarsta() { return categorieVarsta; }
    public void setCategorieVarsta(String categorieVarsta) { this.categorieVarsta = categorieVarsta; }
}
