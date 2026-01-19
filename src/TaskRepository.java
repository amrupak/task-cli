import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;
public class TaskRepository {
    private final Path filePath;

    public TaskRepository(Path filePath){
        this.filePath = filePath;
        ensureFileExists();
    }

    private void ensureFileExists(){
        if (Files.exists(filePath)){
            return;
        }
        try{
            Files.createFile(filePath);
            String initialJson= """
                {
            "nextId": 1,
            "tasks": []
            }
                    """;
        Files.writeString(filePath, initialJson);
        }catch(IOException e){
            throw new RuntimeException("Failed to Initialize tasks File", e);
        }

    }
}
