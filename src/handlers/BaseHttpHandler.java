package handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.ManagerSaveException;
import exceptions.NotFoundException;
import exceptions.ValidationException;
import server.MethodType;
import service.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {

    protected static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    protected final TaskManager manager;
    protected final Gson gson;

    public BaseHttpHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    protected abstract void handleGet(HttpExchange exchange, String[] pathParts) throws IOException;

    protected abstract void handlePost(HttpExchange exchange, String[] pathParts) throws IOException;

    protected abstract void handleDelete(HttpExchange exchange, String[] pathParts) throws IOException;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            try {
                final MethodType method = getMethod(exchange);
                final String[] pathParts = getPathParts(exchange);

                switch (method) {
                    case GET -> handleGet(exchange, pathParts);
                    case POST -> handlePost(exchange, pathParts);
                    case DELETE -> handleDelete(exchange, pathParts);
                    default -> sendText(exchange, "Method Not Allowed", 405);
                }
            } catch (Exception e) {
                handleException(exchange, e);
            }
        }
    }

    protected void sendText(HttpExchange exchange, String text, int statusCode) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");

        if (statusCode == 204) {
            exchange.sendResponseHeaders(statusCode, -1);
        } else {
            byte[] response = text.getBytes(DEFAULT_CHARSET);
            exchange.sendResponseHeaders(statusCode, response.length);
            OutputStream os = exchange.getResponseBody();
            os.write(response);
        }
    }

    private MethodType getMethod(HttpExchange exchange) {
        return MethodType.valueOf(exchange.getRequestMethod());
    }

    private String[] getPathParts(HttpExchange exchange) {
        return exchange.getRequestURI().getPath().split("/");
    }

    private void handleException(HttpExchange exchange, Exception e) throws IOException {
        switch (e) {
            case JsonSyntaxException exception ->
                    sendText(exchange, "Invalid JSON Syntax: " + exception.getMessage(), 400);

            case NumberFormatException exception ->
                    sendText(exchange, "Invalid Number Format: " + exception.getMessage(), 400);

            case NotFoundException exception -> sendText(exchange, "Not Found: " + exception.getMessage(),
                    404);

            case ValidationException exception -> sendText(exchange, "Not Acceptable: " + exception.getMessage(),
                    406);

            case ManagerSaveException exception ->
                    sendText(exchange, "Error Saving Data: " + exception.getMessage(), 500);

            default -> sendText(exchange, "Internal Server Error: " + e.getMessage(), 500);
        }
    }
}
