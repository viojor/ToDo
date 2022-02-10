package controller;

import model.Project;
import model.ProjectDAO;
import view.ProjectsListView;
import view.TasksListView;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

public class ProjectController {

    private static class TableProjectNameRenderer extends DefaultTableCellRenderer {

        private static final int TOP_BORDER = 5;
        private static final int LEFT_BORDER = 10;
        private static final int BOTTOM_BORDER = 5;
        private static final int RIGHT_BORDER = 10;

        public TableProjectNameRenderer(){

            super();
        }

        public void setValue(Object value){

            Project project = (Project) value;

            setBorder(BorderFactory.createEmptyBorder(TOP_BORDER, LEFT_BORDER, BOTTOM_BORDER, RIGHT_BORDER));
            setText(project.getName());
        }
    }

    private static final double WIDTH_SCREEN_CONST = 2.0/3.0;
    private static final double HEIGHT_SCREEN_CONST = 3.0/4.0;

    private static final String DELETE_ICON_ROUTE = "/resources/deleteIcon.png";

    private static final String WHITESPACE_REGEX = "\\s+";

    private static final String COLUMN_PROJECT_NAME = "Project";

    private static final String COLUMN_PROJECT_DELETE = "";
    private static final int COLUMN_DELETE_INDEX = 1;

    private static final Object[] columns = {COLUMN_PROJECT_NAME, COLUMN_PROJECT_DELETE};

    private static final int ROWS_HEIGHT = 60;

    private Project _projectModel;
    private final ProjectDAO _projectDAO;
    private final ProjectsListView _projectsListView;

    private final DefaultTableModel _tableModel;

    public ProjectController(ProjectsListView projectsListView){

        this._projectDAO = new ProjectDAO();
        this._projectsListView = projectsListView;

        this._tableModel = new DefaultTableModel(columns, 0){

            public boolean isCellEditable(int rowIndex, int columnIndex) {

                return columnIndex == 1;
            }
        };

        this._projectsListView.addProjectButton.addActionListener(e -> openAddProjectDialog());

        addMouseListenerToTable(this._projectsListView.projectsTable);
        loadProjectsTable();
        configureTable(_projectsListView.projectsTable);
    }

    public void openAddProjectDialog(){

        String projectName = (String) JOptionPane.showInputDialog(null, "Indicate project's name", "Add new project",
                JOptionPane.PLAIN_MESSAGE, null, null, "0");
        if(isStringInvalid(projectName)){

            JOptionPane.showMessageDialog(null,"The input field cant be empty",
                    "Add new project", JOptionPane.ERROR_MESSAGE);
        }
        else{

            createProject(projectName);
            loadProjectsTable();
        }
    }

    private boolean isStringInvalid(String stringToCheck){

        return stringToCheck == null || stringToCheck.isEmpty() || Pattern.matches(WHITESPACE_REGEX, stringToCheck);
    }

    private void createProject(String _projectName){

        int id = _projectDAO.getCurrentMaxId() + 1;
        _projectModel = new Project(id, _projectName);

        addProjectToDB();
    }

    private void addProjectToDB(){

        _projectDAO.insert(_projectModel);
    }

    private void addMouseListenerToTable(JTable table){

        table.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                int rowSelected = _projectsListView.projectsTable.getSelectedRow();
                if(rowSelected != -1){

                    if(isDeleteOptionSelected(e.getPoint())){ // Delete project

                        deleteProject(rowSelected);
                    }
                    else{ // Project name clicked

                        openProjectInfo(rowSelected);
                    }
                }
            }
        });
    }

    private boolean isDeleteOptionSelected(Point pointClicked){

        return _projectsListView.projectsTable.columnAtPoint(pointClicked) == COLUMN_DELETE_INDEX;
    }

    private void deleteProject(int rowSelected){

        int optionSelected = JOptionPane.showConfirmDialog(null, "Are you sure you want to remove the project?",
                "Deleting a project", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if(optionSelected == JOptionPane.YES_OPTION){

            _tableModel.removeRow(rowSelected);

            Project projectSelected = _projectDAO.getByOrder(rowSelected);
            _projectDAO.delete(projectSelected.getId());

            loadProjectsTable();
        }
    }

    private void openProjectInfo(int rowSelected){

        Project projectSelected = _projectDAO.getByOrder(rowSelected);

        TasksListView projectTasksView = new TasksListView(projectSelected.getName());
        new TaskController(projectTasksView, projectSelected.getId());

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        projectTasksView.setSize((int) (screenSize.getWidth() * WIDTH_SCREEN_CONST), (int) (screenSize.getHeight() * HEIGHT_SCREEN_CONST));

        projectTasksView.setLocationRelativeTo(null);
        projectTasksView.setVisible(true);
    }

    private void loadProjectsTable(){

        clearTableModel();
        loadDataInModel();
    }

    private void clearTableModel(){

        if (_tableModel.getRowCount() > 0) {
            for (int i = _tableModel.getRowCount() - 1; i > -1; i--) {

                _tableModel.removeRow(i);
            }
        }
    }

    private void loadDataInModel(){

        List<Project> projectsList = _projectDAO.getAllProjects();
        for(Project dbProject : projectsList){

            Vector<Project> dbProjectDataArray = new Vector<>(1);
            dbProjectDataArray.add(dbProject);
            this._tableModel.addRow(dbProjectDataArray);
        }
    }

    private void configureTable(JTable table){

        table.setModel(_tableModel);
        table.setRowHeight(ROWS_HEIGHT);

        table.getColumn(COLUMN_PROJECT_NAME).setCellRenderer(new TableProjectNameRenderer());
        table.getColumn(COLUMN_PROJECT_DELETE).setCellRenderer(new TableIconImageRenderer(DELETE_ICON_ROUTE));
    }
}
