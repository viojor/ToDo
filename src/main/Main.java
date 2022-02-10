package main;

import controller.ProjectController;
import database.DBConnector;
import view.ProjectsListView;

import javax.swing.*;
import java.awt.*;

public class Main {

    private static final String WINDOWS_TITLE = "ToDo_List";

    private static final double WIDTH_SCREEN_CONST = 2.0/3.0;
    private static final double HEIGHT_SCREEN_CONST = 3.0/4.0;

    public static void main(String[] args) {

        if(DBConnector.getConnection() == null){

            JOptionPane.showMessageDialog(null, "Cant connect with the database. Look if MySQL service is running",
                    "Cant connect with the database", JOptionPane.ERROR_MESSAGE);
        }
        else{

            ProjectsListView projectsListView = new ProjectsListView(WINDOWS_TITLE);
            new ProjectController(projectsListView);

            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            projectsListView.setSize((int) (screenSize.getWidth() * WIDTH_SCREEN_CONST), (int) (screenSize.getHeight() * HEIGHT_SCREEN_CONST));

            projectsListView.setLocationRelativeTo(null);
            projectsListView.setVisible(true);
        }
    }
}
