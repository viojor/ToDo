package model;

import database.DBConnector;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class ProjectDAO {

    public static final String PROJECTS_TABLE_NAME = "PROJECTS";
    public static final int STRINGS_MAX_SIZE = 255;
    public static final String PROJECTS_ID_COLUMN_NAME = "id";

    public ProjectDAO(){

        Connection connection = DBConnector.getConnection();
        if (DBConnector.isTableNotCreated(PROJECTS_TABLE_NAME, connection)) {

            createTable();
        }
    }

    public void createTable() {

        try {

            Connection connection = DBConnector.getConnection();

            Statement st = connection.createStatement();
            st.executeUpdate("CREATE TABLE " + PROJECTS_TABLE_NAME + " (" + PROJECTS_ID_COLUMN_NAME + " INT PRIMARY KEY auto_increment, name VARCHAR(" + STRINGS_MAX_SIZE + "))");

            st.close();
            DBConnector.disconnectDB(connection);
        } catch (SQLException e) {

            System.out.println("Error creating the table '" + PROJECTS_TABLE_NAME + "': " + e);
        }
    }

    public void insert(Project newProject) {

        try {

            Connection connection = DBConnector.getConnection();

            String name = newProject.getName();

            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO " + PROJECTS_TABLE_NAME +
                    " (name) VALUES (?)");
            preparedStatement.setString(1, name);

            preparedStatement.execute();

            preparedStatement.close();
            DBConnector.disconnectDB(connection);
        } catch (SQLException e) {

            System.out.println("Error inserting a new element on '" + PROJECTS_TABLE_NAME + "': " + e);
        }
    }

    public void delete(int idProjectRemove) {

        try {

            Connection connection = DBConnector.getConnection();

            Statement st = connection.createStatement();
            st.executeUpdate("DELETE FROM " + PROJECTS_TABLE_NAME + " WHERE " + PROJECTS_ID_COLUMN_NAME + " = " + idProjectRemove);

            st.close();
            DBConnector.disconnectDB(connection);
        } catch (SQLException e) {

            System.out.println("Error removing an entry from '" + PROJECTS_TABLE_NAME + "': " + e);
        }
    }

    public Project getByOrder(int positionProjectSelected) {

        Project projectDesired = null;
        try {

            Connection connection = DBConnector.getConnection();

            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM " + PROJECTS_TABLE_NAME);
            int numItemsProcessed = 0;
            while (rs.next()) {
                if (numItemsProcessed == positionProjectSelected) {

                    int id = rs.getInt(PROJECTS_ID_COLUMN_NAME);
                    String name = rs.getString("name");

                    projectDesired = new Project(id, name);
                }
                numItemsProcessed++;
            }

            st.close();
            rs.close();
            DBConnector.disconnectDB(connection);
        } catch (SQLException e) {

            System.out.println("Error getting the project in the row " + positionProjectSelected + " from '" + PROJECTS_TABLE_NAME + "': " + e);
        }
        return projectDesired;
    }

    public List<Project> getAllProjects(){

        List<Project> projectsList = new LinkedList<>();
        try{

            Connection connection = DBConnector.getConnection();
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM " + PROJECTS_TABLE_NAME);
            while (rs.next()) {

                int id = rs.getInt(PROJECTS_ID_COLUMN_NAME);
                String name = rs.getString("name");

                Project dbProject = new Project(id, name);
                projectsList.add(dbProject);
            }

            rs.close();
            st.close();
            DBConnector.disconnectDB(connection);
        } catch (SQLException e){

            System.out.println("Error getting all the data from '" + PROJECTS_TABLE_NAME + "': " + e);
        }
        return projectsList;
    }

    public int getCurrentMaxId() {

        int maxId = 0;
        try {

            Connection connection = DBConnector.getConnection();

            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("SELECT " + PROJECTS_ID_COLUMN_NAME + " FROM " + PROJECTS_TABLE_NAME);
            while (rs.next()) {
                if (rs.getInt(1) > maxId) {

                    maxId = rs.getInt(1);
                }
            }

            rs.close();
            st.close();
            DBConnector.disconnectDB(connection);
        } catch (SQLException e) {

            System.out.println("Error getting the max id from '" + PROJECTS_TABLE_NAME + "': " + e);
        }
        return maxId;
    }
}
