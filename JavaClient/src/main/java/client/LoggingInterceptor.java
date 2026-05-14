package client;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class LoggingInterceptor implements ClientHttpRequestInterceptor {

    private static final String SEP = "─".repeat(60);

    @Override
    public ClientHttpResponse intercept(
            HttpRequest request,
            byte[] body,
            ClientHttpRequestExecution execution) throws IOException {

        // LOG REQUEST
        System.out.println("\n" + SEP);
        System.out.println(">>> REQUEST");
        System.out.println("    Metoda  : " + request.getMethod());
        System.out.println("    URI     : " + request.getURI());
        if (body.length > 0)
            System.out.println("    Body    : " + new String(body, StandardCharsets.UTF_8));
        else
            System.out.println("    Body    : (gol)");
        System.out.println(SEP);

        ClientHttpResponse response = execution.execute(request, body);

        // LOG RESPONSE
        System.out.println("<<< RESPONSE");
        System.out.println("    Status  : " + response.getStatusCode());
        System.out.println(SEP + "\n");

        return response;
    }
}