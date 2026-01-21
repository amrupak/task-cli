import java.util.List;
public class TaskData {
    private int nextId;
    private List<Task> tasks;

    public TaskData(int nextId, List<Task> tasks){
        this.nextId = nextId;
        this.tasks = tasks;
    }
    public int getNextId(){
        return nextId;
    }
    public void setNextId(int nextId){
        this.nextId = nextId;
    }
    public List<Task> getTasks(){
        return tasks;
    }


}
