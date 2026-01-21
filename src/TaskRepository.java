import java.nio.file.Path;
import java.time.LocalDateTime;
import java.nio.file.Files;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TaskRepository {
    private final Path filePath;

    public TaskRepository(Path filePath) {
        this.filePath = filePath;
        ensureFileExists();
    }

    private void ensureFileExists() {
        if (Files.exists(filePath)) {
            return;
        }
        try {
            Files.createFile(filePath);
            String initialJson = """
                        {
                    "nextId": 1,
                    "tasks": []
                    }
                            """;
            Files.writeString(filePath, initialJson);
        } catch (IOException e) {
            throw new RuntimeException("Failed to Initialize tasks File", e);
        }

    }

    private String readFile() {
        try {
            return Files.readString(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read tasks file", e);
        }
    }

    private void writeFile(String content) {
        try {
            Files.writeString(filePath, content);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write tasks file", e);
        }
    }

    private int parseNextId(String json) {
        String key = "\"nextId\":";
        int idx = json.indexOf(key);
        if (idx == -1) {
            throw new RuntimeException("Invalid tasks file : nextId  is not a number");
        }
        int start = idx + key.length();
        // skip whitespace
        while (start < json.length() && Character.isWhitespace(json.charAt(start))) {
            start++;
        }
        int end = start;
        while (end < json.length() && Character.isDigit(json.charAt(end))) {
            end++;
        }
        if (start == end) {
            throw new RuntimeException("Invalid tasks file : nextId is not a number");

        }
        return Integer.parseInt(json.substring(start, end));
    }

    private String extractTaskArray(String json) {
        String key = "\"tasks\":";
        int idx = json.indexOf(key);

        if (idx == -1) {
            throw new RuntimeException("Invalid tasks file :  missing tasks");

        }
        int arrayStart = json.indexOf('[', idx);
        if (arrayStart == -1) {
            throw new RuntimeException("Invalid task file : tasks array start not found");

        }
        int arrayEnd = json.indexOf(']', arrayStart);
        if (arrayEnd == -1) {
            throw new RuntimeException("Invalid tasks file: tasks array end not found");
        }

        // returns what's inside the brackets: {..},{..}
        return json.substring(arrayStart + 1, arrayEnd).trim();

    }

    private String[] splitTaskObjects(String tasksInner) {
        if (tasksInner.isBlank()) {
            return new String[0];
        }

        // split between objects : "},{"

        String[] parts = tasksInner.split("\\},\\s*\\{");

        // add braces back because split removes them

        for (int i = 0; i < parts.length; i++) {
            String p = parts[i].trim();
            if (!p.startsWith("{"))
                p = "{" + p;
            if (!p.endsWith("}"))
                p = p + "}";
            parts[i] = p;
        }
        return parts;
    }

    private Task parseTask(String obj) {
        int id = Integer.parseInt(extractString(obj, "\"id\":"));
        String description = extractQuoted(obj, "\"description\":");
        String status = extractQuoted(obj, "\"status\":");
        LocalDateTime createdAt = LocalDateTime.parse(extractQuoted(obj, "\"createdAt\":"));
        LocalDateTime updatedAt = LocalDateTime.parse(extractQuoted(obj, "\"updatedAt\":"));
        return Task.fromFile(id, description, status, createdAt, updatedAt);
    }

    private String extractString(String src, String key) {
        int idx = src.indexOf(key);
        if (idx == -1) {
            throw new RuntimeException("Missing Key: " + key);
        }

        int start = idx + key.length();
        while (Character.isWhitespace(src.charAt(start)))
            start++;

        int end = start;
        while (Character.isDigit(src.charAt(end)))
            end++;

        return src.substring(start, end);
    }

    private String extractQuoted(String src, String key) {
        int idx = src.indexOf(key);
        if (idx == -1)
            throw new RuntimeException("Missing key: " + key);

        int firstQuote = src.indexOf('"', idx + key.length());
        int secondQuote = src.indexOf('"', firstQuote + 1);

        return src.substring(firstQuote + 1, secondQuote);
    }

    public TaskData load(){
        String json = readFile();
        int nextId = parseNextId(json);

        String tasksInner = extractTaskArray(json);
        String[] objects = splitTaskObjects(tasksInner);
        List<Task> tasks = new ArrayList<>();
        for(String obj : objects){
            tasks.add(parseTask(obj));
        }
        return new TaskData(nextId, tasks);
    }

    public void save(TaskData data) {
        String json = toJson(data);
        writeFile(json);
    }

    // helper method
    private String toJson(TaskData data) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"nextId\": ").append(data.getNextId()).append(",\n");
        sb.append("  \"tasks\": [\n");

        var tasks = data.getTasks();
        for (int i = 0; i < tasks.size(); i++) {
            Task t = tasks.get(i);

            sb.append(taskToJson(t));
            if (i < tasks.size() - 1)
                sb.append(",");
            sb.append("\n");
        }

        sb.append("  ]\n");
        sb.append("}\n");
        return sb.toString();
    }

    private String taskToJson(Task t) {
        return "    {\n" +
                "      \"id\": " + t.getId() + ",\n" +
                "      \"description\": " + quote(escape(t.getDescription())) + ",\n" +
                "      \"status\": " + quote(t.getStatus()) + ",\n" +
                "      \"createdAt\": " + quote(t.getCreatedAt().toString()) + ",\n" +
                "      \"updatedAt\": " + quote(t.getUpdatedAt().toString()) + "\n" +
                "    }";
    }

    private String quote(String s) {
        return "\"" + s + "\"";
    }

    private String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }



}
