package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import model.Subtask;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStream;

public class SubtaskHandler extends BaseHttpHandler {

    public SubtaskHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }

    @Override
    protected void handleGet(HttpExchange exchange, String[] pathParts) throws IOException {
        if (isSubtaskPath(pathParts)) {
            String jsonResponse = gson.toJson(manager.getAllSubtasks());
            sendText(exchange, jsonResponse, 200);

        } else if (isSubtaskPathWithId(pathParts)) {
            final int subtaskId = Integer.parseInt(pathParts[2]);
            String jsonResponse = gson.toJson(manager.getSubtaskById(subtaskId));
            sendText(exchange, jsonResponse, 200);
        } else {
            sendText(exchange, "Invalid Request", 400);
        }
    }

    @Override
    protected void handlePost(HttpExchange exchange, String[] pathParts) throws IOException {
        final Subtask requestSubtask = getRequestSubtask(exchange);
        if (isSubtaskPath(pathParts)) {
            manager.createSubtask(requestSubtask);
            String jsonResponse = gson.toJson(requestSubtask);
            sendText(exchange, jsonResponse, 201);

        } else if (isSubtaskPathWithId(pathParts)) {
            final int subtaskId = Integer.parseInt(pathParts[2]);
            requestSubtask.setId(subtaskId);
            manager.updateSubtask(requestSubtask);
            String jsonResponse = gson.toJson(requestSubtask);
            sendText(exchange, jsonResponse, 201);
        } else {
            sendText(exchange, "Invalid Request", 400);
        }
    }

    @Override
    protected void handleDelete(HttpExchange exchange, String[] pathParts) throws IOException {
        if (isSubtaskPath(pathParts)) {
            manager.removeAllSubtasksFromManager();
            sendText(exchange, "", 204);

        } else if (isSubtaskPathWithId(pathParts)) {
            final int subtaskId = Integer.parseInt(pathParts[2]);
            manager.removeSubtaskById(subtaskId);
            sendText(exchange, "", 204);
        } else {
            sendText(exchange, "Invalid Request", 400);
        }
    }

    private Subtask getRequestSubtask(HttpExchange exchange) throws IOException {
        try (InputStream requestBody = exchange.getRequestBody()) {
            String body = new String(requestBody.readAllBytes(), DEFAULT_CHARSET);
            return gson.fromJson(body, Subtask.class);
        }
    }

    private boolean isSubtaskPath(String[] pathParts) {
        return pathParts.length == 2 && pathParts[1].equals("subtasks");
    }

    private boolean isSubtaskPathWithId(String[] pathParts) {
        return pathParts.length == 3 && pathParts[1].equals("subtasks");
    }
}
