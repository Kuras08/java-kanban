package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import model.Epic;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStream;

public class EpicHandler extends BaseHttpHandler {

    public EpicHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }

    @Override
    protected void handleGet(HttpExchange exchange, String[] pathParts) throws IOException {
        if (isEpicPath(pathParts)) {
            String jsonResponse = gson.toJson(manager.getAllEpics());
            sendText(exchange, jsonResponse, 200);

        } else if (isEpicPathWithId(pathParts)) {
            final int epicId = Integer.parseInt(pathParts[2]);
            String jsonResponse = gson.toJson(manager.getEpicById(epicId));
            sendText(exchange, jsonResponse, 200);

        } else if (isEpicPathWithIdAndSubtasks(pathParts)) {
            final int epicId = Integer.parseInt(pathParts[2]);
            String jsonResponse = gson.toJson(manager.getAllSubtasksEpic(epicId));
            sendText(exchange, jsonResponse, 200);
        } else {
            sendText(exchange, "Invalid Request", 400);
        }
    }

    @Override
    protected void handlePost(HttpExchange exchange, String[] pathParts) throws IOException {
        final Epic requestEpic = getRequestEpic(exchange);
        if (isEpicPath(pathParts)) {
            manager.createEpic(requestEpic);
            String jsonResponse = gson.toJson(requestEpic);
            sendText(exchange, jsonResponse, 201);
        } else {
            sendText(exchange, "Invalid Request", 400);
        }
    }

    @Override
    protected void handleDelete(HttpExchange exchange, String[] pathParts) throws IOException {
        if (isEpicPath(pathParts)) {
            manager.removeAllEpicsFromManager();
            sendText(exchange, "", 204);

        } else if (isEpicPathWithId(pathParts)) {
            final int epicId = Integer.parseInt(pathParts[2]);
            manager.removeEpicById(epicId);
            sendText(exchange, "", 204);
        } else {
            sendText(exchange, "Invalid Request", 400);
        }
    }

    private Epic getRequestEpic(HttpExchange exchange) throws IOException {
        try (InputStream requestBody = exchange.getRequestBody()) {
            String body = new String(requestBody.readAllBytes(), DEFAULT_CHARSET);
            return gson.fromJson(body, Epic.class);
        }
    }

    private boolean isEpicPath(String[] pathParts) {
        return pathParts.length == 2 && pathParts[1].equals("epics");
    }

    private boolean isEpicPathWithId(String[] pathParts) {
        return pathParts.length == 3 && pathParts[1].equals("epics");
    }

    private boolean isEpicPathWithIdAndSubtasks(String[] pathParts) {
        return pathParts.length == 4 && pathParts[1].equals("epics") && pathParts[3].equals("subtasks");
    }
}
