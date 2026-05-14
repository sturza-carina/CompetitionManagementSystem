package client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

public class TestClientJava {

    private static final String BASE_URL = "http://localhost:8080/probe";

    public static void main(String[] args) {

        RestClient restClient = RestClient.builder()
                .baseUrl(BASE_URL)
                .requestFactory(new BufferingClientHttpRequestFactory(
                        new SimpleClientHttpRequestFactory()))
                .requestInterceptor(new LoggingInterceptor())
                .build();


        System.out.println(">>> TEST CLIENT JAVA - REST API Probe");

        // 1. GET toate probele
        System.out.println("\n1. GET TOATE PROBELE");
        try {
            List<Map> probe = restClient.get()
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<Map>>() {});
            System.out.println("Probe gasite: " + probe.size());
            probe.forEach(p -> System.out.println("  " + p));
        } catch (Exception e) {
            System.err.println("Eroare: " + e.getMessage());
        }

        // 2. POST - adauga proba noua
        System.out.println("\n2. POST - ADAUGA PROBA NOUA");
        Long idCreat = null;
        try {
            Map<String, String> novaProba = Map.of(
                    "nume", "Pictura",
                    "categorieVarsta", "6-8"
            );

            ResponseEntity<Map> response = restClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(novaProba)
                    .retrieve()
                    .toEntity(Map.class);

            Map body = response.getBody();
            idCreat = body != null ? Long.valueOf(body.get("id").toString()) : null;
            System.out.println("Status: " + response.getStatusCode());
            System.out.println("Proba creata: " + body);
            System.out.println(">>> ID generat de server: " + idCreat);
        } catch (Exception e) {
            System.err.println("Eroare: " + e.getMessage());
        }

        // 3. GET dupa id
        System.out.println("\n3. GET DUPA ID");
        if (idCreat != null) {
            try {
                Map proba = restClient.get()
                        .uri("/{id}", idCreat)
                        .retrieve()
                        .body(Map.class);
                System.out.println("Proba gasita: " + proba);
            } catch (HttpClientErrorException.NotFound e) {
                System.err.println("404 - Proba nu a fost gasita");
            }
        }

        // 4. GET filtru dupa categorieVarsta
        System.out.println("\n4. GET FILTRU DUPA CATEGORIE");
        try {
            List<Map> filtrate = restClient.get()
                    .uri("?categorie=6-8")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<Map>>() {});
            System.out.println("Probe filtrate (6-8): " + filtrate.size());
            filtrate.forEach(p -> System.out.println("  " + p));
        } catch (Exception e) {
            System.err.println("Eroare: " + e.getMessage());
        }

        // 5. PUT - modifica proba
        System.out.println("\n5. PUT - MODIFICA PROBA");
        if (idCreat != null) {
            try {
                Map<String, String> modificata = Map.of(
                        "nume", "Pictura MODIFICATA",
                        "categorieVarsta", "9-11"
                );
                Map raspuns = restClient.put()
                        .uri("/{id}", idCreat)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(modificata)
                        .retrieve()
                        .body(Map.class);
                System.out.println("Proba modificata: " + raspuns);
            } catch (HttpClientErrorException.NotFound e) {
                System.err.println("404 - Proba nu exista");
            }
        }

        // 6. DELETE - sterge proba
        System.out.println("\n6. DELETE - STERGE PROBA");
        if (idCreat != null) {
            try {
                ResponseEntity<Void> del = restClient.delete()
                        .uri("/{id}", idCreat)
                        .retrieve()
                        .toBodilessEntity();
                System.out.println("Status: " + del.getStatusCode());
                System.out.println("Proba " + idCreat + " stearsa cu succes.");
            } catch (HttpClientErrorException.NotFound e) {
                System.err.println("404 - Proba nu exista");
            }
        }

        // 7. GET id inexistent - asteptat 404
        System.out.println("\n7. GET ID INEXISTENT (asteptat 404)");
        try {
            restClient.get()
                    .uri("/{id}", 99999L)
                    .retrieve()
                    .body(Map.class);
        } catch (HttpClientErrorException.NotFound e) {
            System.out.println("Primit corect 404 pentru id inexistent.");
        }

        System.out.println(">>> TEST INCHEIAT");
    }
}