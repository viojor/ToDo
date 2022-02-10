package model;

public class Task {

    public enum TaskState {

        TO_DO, IN_PROGRESS, COMPLETED
    }

    private final int _taskId;
    private final int _projectId;
    private final String _name;
    private final String _description;
    private final String _currentState;

    public Task(int id, int projectId, String name, String description, String currentState){

        _taskId = id;
        _projectId = projectId;
        _name = name;
        _description = description;
        _currentState = currentState;
    }

    public int getTaskId() {

        return _taskId;
    }

    public int getProjectId() {

        return _projectId;
    }

    public String getName() {

        return _name;
    }

    public String getDescription() {

        return _description;
    }

    public String getCurrentState() {

        return _currentState;
    }
}
