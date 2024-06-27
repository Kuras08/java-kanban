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

    protected abstract void handleGet(HttpExchange exchange, String[] pathParts) throws IOException;

    protected abstract void handlePost(HttpExchange exchange, String[] pathParts) throws IOException;

    protected abstract void handleDelete(HttpExchange exchange, String[] pathParts) throws IOException;

    public BaseHttpHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            try {
                MethodType method = getMethod(exchange);
                String[] pathParts = getPathParts(exchange);

                switch (method) {
                    case GET -> handleGet(exchange, pathParts);
                    case POST -> handlePost(exchange, pathParts);
                    case DELETE -> handleDelete(exchange, pathParts);
                    default -> sendText(exchange, "Method Not Allowed", 405);
                }
            } catch (JsonSyntaxException e) {
                sendText(exchange, "Invalid JSON Syntax: " + e.getMessage(), 400);
            } catch (NumberFormatException e) {
                sendText(exchange, "Invalid Number Format: " + e.getMessage(), 400);
            } catch (NotFoundException e) {
                sendText(exchange, "Not Found: " + e.getMessage(), 404);
            } catch (ValidationException e) {
                sendText(exchange, "Not Acceptable: " + e.getMessage(), 406);
            } catch (ManagerSaveException e) {
                sendText(exchange, "Error Saving Data: " + e.getMessage(), 500);
            } catch (Exception e) {
                sendText(exchange, "Internal Server Error: " + e.getMessage(), 500);
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
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        }
    }

    private MethodType getMethod(HttpExchange exchange) {
        return MethodType.valueOf(exchange.getRequestMethod());
    }

    private String[] getPathParts(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        return path.split("/");
    }
}
