package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import service.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler {
    public HistoryHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }

    @Override
    protected void handleGet(HttpExchange exchange, String[] pathParts) throws IOException {
        if (isHistoryPath(pathParts)) {
            String jsonResponse = gson.toJson(manager.getHistory());
            sendText(exchange, jsonResponse, 200);
        } else {
            sendText(exchange, "Invalid Request", 400);
        }
    }

    @Override
    protected void handlePost(HttpExchange exchange, String[] pathParts) throws IOException {

    }

    @Override
    protected void handleDelete(HttpExchange exchange, String[] pathParts) throws IOException {

    }

    private boolean isHistoryPath(String[] pathParts) {
        return pathParts.length == 2 && pathParts[1].equals("history");
    }
}
