import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;

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

    private String extractTaskArray(String json){
        String key = "\"tasks\":";
        int idx = json.indexOf(key);

        if(idx == -1){
            throw new RuntimeException("Invalid tasks file :  missing tasks");

        }
        int arrayStart = json.indexOf('[', idx);
            if(arrayStart == -1){
                throw new RuntimeException("Invalid task file : tasks array start not found");

            }
            int arrayEnd = json.indexOf(']', arrayStart);
    if (arrayEnd == -1) {
        throw new RuntimeException("Invalid tasks file: tasks array end not found");
    }

    // returns what's inside the brackets:  {..},{..}
    return json.substring(arrayStart + 1, arrayEnd).trim();

        }
    private String[] splitTaskObjects(String tasksInner){
        if(tasksInner.isBlank()){
            return new String[0];
        }

        //split between objects : "},{"

        String[] parts = tasksInner.split("\\},\\s*\\{");

        //add braces back because split removes them

        for(int i = 0; i< parts.length; i++){
            String p = parts[i].trim();
            if(!p.startsWith("{")) p = "{" + p;
            if(!p.endsWith("}")) p = p+ "}";
            parts[i] = p;
        }
        return parts;
    }
 }

