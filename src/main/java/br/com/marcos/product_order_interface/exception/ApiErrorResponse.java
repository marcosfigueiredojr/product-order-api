package br.com.marcos.product_order_interface.exception;

import java.time.Instant;
import java.util.List;

public class ApiErrorResponse {

    private Instant timestamp;
    private Integer status;
    private String error;
    private List<String> messages;
    private String path;

    public ApiErrorResponse(
            Instant timestamp,
            Integer status,
            String error,
            List<String> messages,
            String path
    ) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.messages = messages;
        this.path = path;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public Integer getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public List<String> getMessages() {
        return messages;
    }

    public String getPath() {
        return path;
    }
}
