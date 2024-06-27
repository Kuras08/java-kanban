package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStream;

public class TaskHandler extends BaseHttpHandler {

    public TaskHandler(TaskManager manager, Gson gson) {
        super(manager, gson);

    }

    @Override
    protected void handleGet(HttpExchange exchange, String[] pathParts) throws IOException {
        if (isTaskPath(pathParts)) {
            String jsonResponse = gson.toJson(manager.getAllTasks());
            sendText(exchange, jsonResponse, 200);

        } else if (isTaskPathWithId(pathParts)) {
            int taskId = Integer.parseInt(pathParts[2]);
            String jsonResponse = gson.toJson(manager.getTaskById(taskId));
            sendText(exchange, jsonResponse, 200);
        } else {
            sendText(exchange, "Invalid Request", 400);
        }
    }

    @Override
    protected void handlePost(HttpExchange exchange, String[] pathParts) throws IOException {
        Task requestTask = getRequestTask(exchange);
        if (isTaskPath(pathParts)) {
            manager.createTask(requestTask);
            String jsonResponse = gson.toJson(requestTask);
            sendText(exchange, jsonResponse, 201);

        } else if (isTaskPathWithId(pathParts)) {
            int taskId = Integer.parseInt(pathParts[2]);
            requestTask.setId(taskId);
            manager.updateTask(requestTask);
            String jsonResponse = gson.toJson(requestTask);
            sendText(exchange, jsonResponse, 201);
        } else {
            sendText(exchange, "Invalid Request", 400);
        }
    }

    @Override
    protected void handleDelete(HttpExchange exchange, String[] pathParts) throws IOException {
        if (isTaskPath(pathParts)) {
            manager.removeAllTasksFromManager();
            sendText(exchange, "", 204);

        } else if (isTaskPathWithId(pathParts)) {
            int taskId = Integer.parseInt(pathParts[2]);
            manager.removeTaskById(taskId);
            sendText(exchange, "", 204);
        } else {
            sendText(exchange, "Invalid Request", 400);
        }
    }

    private Task getRequestTask(HttpExchange exchange) throws IOException {
        try (InputStream requestBody = exchange.getRequestBody()) {
            String body = new String(requestBody.readAllBytes(), DEFAULT_CHARSET);
            return gson.fromJson(body, Task.class);
        }
    }

    private boolean isTaskPath(String[] pathParts) {
        return pathParts.length == 2 && pathParts[1].equals("tasks");
    }

    private boolean isTaskPathWithId(String[] pathParts) {
        return pathParts.length == 3 && pathParts[1].equals("tasks");
    }
}
