package network.jsonprotocol;

import com.google.gson.JsonElement;

public class Response {
    private ResponseType type;
    private JsonElement data;
    private String errorMessage;

    public Response() {}

    public Response(ResponseType type, JsonElement data) {
        this.type = type;
        this.data = data;
    }

    public Response(ResponseType type, String errorMessage) {
        this.type = type;
        this.errorMessage = errorMessage;
    }

    public ResponseType getType() { return type; }
    public JsonElement getData() { return data; }
    public String getErrorMessage() { return errorMessage; }
}
