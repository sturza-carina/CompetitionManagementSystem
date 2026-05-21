package rest.controller;

import model.Proba;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import persistence.interfaces.IProbaRepository;
import rest.dto.ProbaDTO;
import rest.dto.ProbaResponseDTO;
import services.InscriereException;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Controller REST pentru entitatea Proba.
 *
 * Endpoint-uri:
 *   GET    /probe                          - toate probele
 *   GET    /probe/{id}                     - proba dupa id
 *   GET    /probe/filtru?categorie=...     - probe filtrate dupa categorieVarsta
 *   POST   /probe                          - adauga proba (fara id in body, returneaza id-ul creat)
 *   PUT    /probe/{id}                     - modifica proba existenta
 *   DELETE /probe/{id}                     - sterge proba
 */
// @CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/probe")
public class ProbaRestController {

    private static final Logger logger = LogManager.getLogger(ProbaRestController.class);

    private final IProbaRepository probaRepository;

    public ProbaRestController(IProbaRepository probaRepository) {
        this.probaRepository = probaRepository;
    }

    // GET /probe  ->  returneaza toate probele
    // GET /probe?categorie=6-8  ->  filtru dupa categorieVarsta
    @GetMapping
    public ResponseEntity<?> getProbe(
            @RequestParam(value = "categorie", required = false) String categorie) {

        if (categorie != null && !categorie.isBlank()) {
            logger.info("GET /probe?categorie={} - filtrare", categorie);
            List<ProbaResponseDTO> result = StreamSupport
                    .stream(probaRepository.findByCategorieVarsta(categorie).spliterator(), false)
                    .map(this::toResponseDTO)
                    .collect(Collectors.toList());
            logger.info("Gasite {} probe pentru categoria {}", result.size(), categorie);
            return ResponseEntity.ok(result);
        }

        logger.info("GET /probe - afisare toate probele");
        List<ProbaResponseDTO> result = StreamSupport
                .stream(probaRepository.findAll().spliterator(), false)
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
        logger.info("Returnate {} probe", result.size());
        return ResponseEntity.ok(result);
    }


    // GET /probe/{id}  ->  cauta proba dupa id
    @GetMapping("/{id}")
    public ResponseEntity<ProbaResponseDTO> getProbaById(@PathVariable("id") Long id) {
        logger.info("GET /probe/{} - cautare dupa id", id);

        Proba proba = probaRepository.findById(id);
        if (proba == null) {
            logger.warn("Proba cu id {} nu a fost gasita", id);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(toResponseDTO(proba));
    }

    // POST /probe  ->  adauga proba noua
    //   - body: { "nume": "...", "categorieVarsta": "..." }  (fara id!)
    //   - response 201 Created + body cu id-ul generat
    @PostMapping
    public ResponseEntity<ProbaResponseDTO> addProba(@RequestBody ProbaDTO dto) {
        logger.info("POST /probe - adaugare proba: nume={}, categorie={}",
                dto.getNume(), dto.getCategorieVarsta());

        if (dto.getNume() == null || dto.getNume().isBlank() ||
                dto.getCategorieVarsta() == null || dto.getCategorieVarsta().isBlank()) {
            logger.warn("Date invalide pentru adaugare proba");
            return ResponseEntity.badRequest().build();
        }

        Proba proba = new Proba(dto.getNume(), dto.getCategorieVarsta());
        probaRepository.add(proba);

        // dupa persist(), Hibernate populeaza id-ul automat (GenerationType.IDENTITY)
        logger.info("Proba adaugata cu id={}", proba.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDTO(proba));
    }


    // PUT /probe/{id}  ->  modifica proba existenta
    //   - body: { "nume": "...", "categorieVarsta": "..." }
    @PutMapping("/{id}")
    public ResponseEntity<ProbaResponseDTO> updateProba(
            @PathVariable("id") Long id,
            @RequestBody ProbaDTO dto) {

        logger.info("PUT /probe/{} - modificare proba", id);

        Proba existing = probaRepository.findById(id);
        if (existing == null) {
            logger.warn("Proba cu id {} nu exista, nu se poate modifica", id);
            return ResponseEntity.notFound().build();
        }

        Proba updated = new Proba(dto.getNume(), dto.getCategorieVarsta());
        probaRepository.update(updated, id);

        // construim raspunsul cu datele actualizate
        existing.setNume(dto.getNume());
        existing.setCategorie(dto.getCategorieVarsta());
        logger.info("Proba {} modificata cu succes", id);
        return ResponseEntity.ok(toResponseDTO(existing));
    }


    // DELETE /probe/{id}  ->  sterge proba
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProba(@PathVariable("id") Long id) {
        logger.info("DELETE /probe/{} - stergere proba", id);

        Proba proba = probaRepository.findById(id);
        if (proba == null) {
            logger.warn("Proba cu id {} nu exista, nu se poate sterge", id);
            return ResponseEntity.notFound().build();
        }

        probaRepository.delete(proba);
        logger.info("Proba {} stearsa cu succes", id);
        return ResponseEntity.noContent().build();  // 204 No Content
    }

    // Helper: Proba -> ProbaResponseDTO
    private ProbaResponseDTO toResponseDTO(Proba proba) {
        return new ProbaResponseDTO(proba.getId(), proba.getNume(), proba.getCategorie());
    }

    @ExceptionHandler(InscriereException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleInscriereException(InscriereException e) {
        logger.error("InscriereException: {}", e.getMessage());
        return e.getMessage();
    }

}
