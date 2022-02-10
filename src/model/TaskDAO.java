package model;

import database.DBConnector;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class TaskDAO {

    public static final String TASKS_TABLE_NAME = "TASKS";
    public static final int STRINGS_MAX_SIZE = 255;

    private static final String FOREIGN_KEY_CONSTRAINT_NAME = "fk_project";

    public TaskDAO(){

        Connection connection = DBConnector.getConnection();
        if (DBConnector.isTableNotCreated(TASKS_TABLE_NAME, connection)) {

            createTable();
        }
    }

    public void createTable() {

        try {

            Connection connection = DBConnector.getConnection();

            Statement st = connection.createStatement();
            st.executeUpdate("CREATE TABLE " + TASKS_TABLE_NAME + " (id INT PRIMARY KEY auto_increment, projectId INT," +
                    " name VARCHAR(" + STRINGS_MAX_SIZE + "), description VARCHAR(" + STRINGS_MAX_SIZE + "), currentState VARCHAR(" + STRINGS_MAX_SIZE + ")," +
                    " CONSTRAINT " + FOREIGN_KEY_CONSTRAINT_NAME + " FOREIGN KEY(projectId) REFERENCES " + ProjectDAO.PROJECTS_TABLE_NAME.toLowerCase(Locale.ROOT) + "(" +
                    ProjectDAO.PROJECTS_ID_COLUMN_NAME + ") ON DELETE CASCADE ON UPDATE CASCADE)");

            st.close();
            DBConnector.disconnectDB(connection);
        } catch (SQLException e) {

            System.out.println("Error creating the table '" + TASKS_TABLE_NAME + "': " + e);
        }
    }

    public void insert(Task newTask) {

        try {

            Connection connection = DBConnector.getConnection();

            int projectId = newTask.getProjectId();
            String name = newTask.getName();
            String description = newTask.getDescription();
            String currentState = newTask.getCurrentState();

            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO " + TASKS_TABLE_NAME +
                    " (projectId, name, description, currentState) VALUES (?, ?, ?, ?)");
            preparedStatement.setInt(1, projectId);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, description);
            preparedStatement.setString(4, currentState);

            preparedStatement.execute();

            preparedStatement.close();
            DBConnector.disconnectDB(connection);
        } catch (SQLException e) {

            System.out.println("Error inserting a new element on '" + TASKS_TABLE_NAME + "': " + e);
        }
    }

    public void delete(int idTaskRemove) {

        try {

            Connection connection = DBConnector.getConnection();

            Statement st = connection.createStatement();
            st.executeUpdate("DELETE FROM " + TASKS_TABLE_NAME + " WHERE id = " + idTaskRemove);

            st.close();
            DBConnector.disconnectDB(connection);
        } catch (SQLException e) {

            System.out.println("Error removing an entry from '" + TASKS_TABLE_NAME + "': " + e);
        }
    }

    public void updateTaskState(String newState, int taskId){

        try {

            Connection connection = DBConnector.getConnection();

            Statement st = connection.createStatement();
            st.executeUpdate("UPDATE " + TASKS_TABLE_NAME +
                    " SET currentState = '" + newState + "'"  +
                    " WHERE id = " + taskId);

            st.close();
            DBConnector.disconnectDB(connection);
        } catch (SQLException e) {

            System.out.println("Error updating the state of the task with the id( " + taskId + "):" + e);
        }
    }

    public Task getByOrder(int positionTaskSelected, String stateTaskSelected) {

        Task taskDesired = null;
        try {

            Connection connection = DBConnector.getConnection();

            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM " + TASKS_TABLE_NAME + " WHERE currentState = '" + stateTaskSelected + "'");
            int numItemsProcessed = 0;
            while (rs.next()) {
                if (numItemsProcessed == positionTaskSelected) {

                    int id = rs.getInt("id");
                    int projectId = rs.getInt("projectId");
                    String name = rs.getString("name");
                    String description = rs.getString("description");
                    String currentState = rs.getString("currentState");

                    taskDesired = new Task(id, projectId, name, description, currentState);
                }
                numItemsProcessed++;
            }

            st.close();
            rs.close();
            DBConnector.disconnectDB(connection);
        } catch (SQLException e) {

            System.out.println("Error getting the task in the row " + positionTaskSelected + " from '" + TASKS_TABLE_NAME + "': " + e);
        }
        return taskDesired;
    }

    public List<Task> getAllTasksByState(String state){

        List<Task> tasksList = new LinkedList<>();
        try{

            Connection connection = DBConnector.getConnection();
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM " + TASKS_TABLE_NAME + " WHERE currentState = '" + state + "'");
            while (rs.next()) {

                int id = rs.getInt("id");
                int projectId = rs.getInt("projectId");
                String name = rs.getString("name");
                String description = rs.getString("description");
                String currentState = rs.getString("currentState");

                Task dbTask = new Task(id, projectId, name, description, currentState);
                tasksList.add(dbTask);
            }

            rs.close();
            st.close();
            DBConnector.disconnectDB(connection);
        } catch (SQLException e){

            System.out.println("Error getting all the data from '" + TASKS_TABLE_NAME + "' with the state '" + state + "': " + e);
        }
        return tasksList;
    }

    public int getCurrentMaxId() {

        int maxId = 0;
        try {

            Connection connection = DBConnector.getConnection();

            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT id FROM " + TASKS_TABLE_NAME);
            while (rs.next()) {
                if (rs.getInt(1) > maxId) {

                    maxId = rs.getInt(1);
                }
            }

            rs.close();
            st.close();
            DBConnector.disconnectDB(connection);
        } catch (SQLException e) {

            System.out.println("Error getting the max id from '" + TASKS_TABLE_NAME + "': " + e);
        }
        return maxId;
    }
}
