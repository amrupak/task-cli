import java.nio.file.Path;
public class Main {
    public static void main(String[] args) {
        TaskRepository repo = new TaskRepository(Path.of("tasks.json"));
        if(args.length == 0){
            System.out.println("Usage : task-cli <command> [args]");
        }
        String command = args[0];
        switch (command) {
            case "add":{
                if(args.length<2){
                    System.out.println("Usage: task-cli add \"description\"");
                    return;
                }
                String description = args[1];
                TaskData data = repo.load();
                Task task = new Task(data.getNextId(), description);
                data.getTasks().add(task);
                data.setNextId(data.getNextId()+1);
                repo.save(data);
                System.out.println("Task added successfully (ID : "+task.getId()+ ")");            
                break;
            }
            case "list":{

                TaskData data = repo.load();
                if(data.getTasks().isEmpty()){
                    System.out.println(" List is Empty");
                    return;
                }
                for(Task t : data.getTasks()){
                    System.out.println(
                        t.getId() + "[" + t.getStatus() + "]" + t.getDescription()
                    );
                }
                break;
            }
            default:
                break;
        }
    }
}
