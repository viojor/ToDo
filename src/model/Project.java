package model;

public class Project {

    private final int _id;
    private final String _name;

    public Project(int id, String projectName){

        this._id = id;
        this._name = projectName;
    }

    public int getId() {

        return _id;
    }

    public String getName() {

        return _name;
    }
}
