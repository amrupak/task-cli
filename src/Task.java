import java.time.LocalDateTime;
public class Task {
    private int id;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    

    public Task( int id, String description){
        this.id = id;
        this.description = description;
        this.status = "todo";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    public void touchUpdatedAt(){
        this.updatedAt = LocalDateTime.now();
    }

    public int getId(){
        return id;
    }
    public String getDescription(){
        return description;
    }
    public String getStatus(){
        return status;
    }
    public LocalDateTime getCreatedAt(){
        return createdAt;
    }
    public LocalDateTime getUpdatedAt(){
        return updatedAt;
    }

    public void markInProgress(){
        this.status ="in-progress";
        touchUpdatedAt();
    }
    public void markDone(){
        this.status = "done";
        touchUpdatedAt();
    }
              
}