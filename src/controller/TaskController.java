package controller;

import model.Task;
import model.TaskDAO;
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

public class TaskController {

    private static class TableTasksNameRenderer  extends DefaultTableCellRenderer {

        private static final int TOP_BORDER = 5;
        private static final int LEFT_BORDER = 10;
        private static final int BOTTOM_BORDER = 5;
        private static final int RIGHT_BORDER = 10;

        public TableTasksNameRenderer(){

            super();
        }

        public void setValue(Object value){

            Task task = (Task) value;

            setBorder(BorderFactory.createEmptyBorder(TOP_BORDER, LEFT_BORDER, BOTTOM_BORDER, RIGHT_BORDER));
            setText(task.getName());
        }
    }

    private static final String MOVE_ICON_ROUTE = "/resources/moveIcon.png";
    private static final String DELETE_ICON_ROUTE = "/resources/deleteIcon.png";

    private static final String WHITESPACE_REGEX = "\\s+";

    private static final String COLUMN_TASKS_NAME = "Tasks";

    private static final String COLUMN_TASKS_MOVE = "Move Task";
    private static final int COLUMN_MOVE_INDEX = 1;

    private static final String COLUMN_TASKS_DELETE = "Delete Task";
    private static final int COLUMN_DELETE_INDEX = 2;

    private static final int ROWS_HEIGHT = 60;

    private static final Object[] columns = {COLUMN_TASKS_NAME, COLUMN_TASKS_MOVE, COLUMN_TASKS_DELETE};

    private Task _taskModel;
    private final TaskDAO _taskDAO;
    private final TasksListView _tasksListView;

    private DefaultTableModel _toDoTableModel;
    private DefaultTableModel _doingTableModel;
    private DefaultTableModel _doneTableModel;

    private final int _projectId;

    public TaskController(TasksListView tasksListView, int projectId){

        this._projectId = projectId;

        this._taskDAO = new TaskDAO();
        this._tasksListView = tasksListView;

        this._tasksListView.addTaskButton.addActionListener(e -> openAddTaskDialog());

        initializeTableModel(this._tasksListView.toDoTable);
        setUpTable(this._tasksListView.toDoTable, this._toDoTableModel);

        initializeTableModel(this._tasksListView.doingTable);
        setUpTable(this._tasksListView.doingTable, this._doingTableModel);

        initializeTableModel(this._tasksListView.doneTable);
        setUpTable(this._tasksListView.doneTable, this._doneTableModel);
    }

    private void openAddTaskDialog(){

        String taskName = getStringWithJOptionPane("Indicate task's name", "Add new task");
        if(isStringInvalid(taskName)){

            JOptionPane.showMessageDialog(null,"The input field cant be empty", "Add new task", JOptionPane.ERROR_MESSAGE);
        }
        else{

            String taskDescription = getStringWithJOptionPane("What's the purpose of the task?", "Add task description");
            if(isStringInvalid(taskDescription)){

                taskDescription = "No description specified";
            }

            createTask(_projectId, taskName, taskDescription, Task.TaskState.TO_DO);
            loadTable(_toDoTableModel, Task.TaskState.TO_DO.toString());
        }
    }

    private String getStringWithJOptionPane(String message, String title){

        return (String) JOptionPane.showInputDialog(null, message, title, JOptionPane.PLAIN_MESSAGE, null, null, "0");
    }

    private void setUpTable(JTable table, DefaultTableModel tableModel){

        loadTable(tableModel, getTypeTaskTable(table));
        configureTable(table, tableModel);
        addMouseListenerToTable(table, tableModel);
    }

    private void initializeTableModel(JTable table){

        Object[] columnsAux = columns.clone();
        if(table.getName().equals(TasksListView.DONE_TABLE_NAME)){

            columnsAux = eliminateMoveColumnFromTableModel(columnsAux);
        }
        DefaultTableModel tableModel = new DefaultTableModel(columnsAux, 0){

            public boolean isCellEditable(int rowIndex, int columnIndex) {

                return columnIndex == COLUMN_MOVE_INDEX || columnIndex == COLUMN_DELETE_INDEX;
            }
        };

        switch (table.getName()) {
            case TasksListView.TO_DO_TABLE_NAME -> _toDoTableModel = tableModel;
            case TasksListView.DOING_TABLE_NAME -> _doingTableModel = tableModel;
            case TasksListView.DONE_TABLE_NAME -> _doneTableModel = tableModel;
        }
    }

    private Object[] eliminateMoveColumnFromTableModel(Object[] columnsTable){

        Object[] aux = new Object[columnsTable.length - 1];
        System.arraycopy(columnsTable, 0, aux, 0, columnsTable.length - 1);
        System.arraycopy(columnsTable, COLUMN_MOVE_INDEX + 1, aux, COLUMN_MOVE_INDEX, columnsTable.length - COLUMN_MOVE_INDEX - 1);
        columnsTable = aux;

        return columnsTable;
    }

    private void addMouseListenerToTable(JTable table, DefaultTableModel tableModel){

        table.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                int rowSelected = table.getSelectedRow();
                if(rowSelected != -1){
                    if(isMoveOptionSelected(table, e.getPoint())){ // Move task

                        moveTask(table, tableModel, rowSelected);
                    }
                    else if(isDeleteOptionSelected(table, e.getPoint())){ // Delete task

                        int optionSelected = JOptionPane.showConfirmDialog(null, "Are you sure you want to remove the task?",
                                "Deleting a task", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                        if(optionSelected == JOptionPane.YES_OPTION){

                            deleteTask(table, rowSelected);
                        }
                    }
                    else{

                        showTaskDescription(table, rowSelected);
                    }
                }
            }
        });
    }

    private boolean isMoveOptionSelected(JTable table, Point pointClicked){

        return table.columnAtPoint(pointClicked) == COLUMN_MOVE_INDEX && !table.getName().equals(TasksListView.DONE_TABLE_NAME);
    }

    private void moveTask(JTable table, DefaultTableModel tableModel, int rowSelected){

        tableModel.removeRow(rowSelected);

        Task taskSelected = _taskDAO.getByOrder(rowSelected, getTypeTaskTable(table));
        String taskNextState = getNextStateTaskSelected(taskSelected);
        _taskDAO.updateTaskState(taskNextState, taskSelected.getTaskId());

        loadTable(tableModel, taskSelected.getCurrentState());
        loadTable(getNextStateModel(taskSelected.getCurrentState()), getNextStateTaskSelected(taskSelected));
    }

    private void loadTable(DefaultTableModel tableModel, String state){

        clearTableModel(tableModel);
        loadDataInModel(tableModel, state);
    }

    private boolean isDeleteOptionSelected(JTable table, Point pointClicked){

        return table.columnAtPoint(pointClicked) == COLUMN_DELETE_INDEX ||
                (table.getName().equals(TasksListView.DONE_TABLE_NAME) && table.columnAtPoint(pointClicked) == COLUMN_MOVE_INDEX);
    }

    private void deleteTask(JTable table, int rowSelected){

        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
        tableModel.removeRow(rowSelected);

        Task taskSelected = _taskDAO.getByOrder(rowSelected, getTypeTaskTable(table));
        _taskDAO.delete(taskSelected.getTaskId());
        loadTable(tableModel, getTypeTaskTable(table));
    }

    private void showTaskDescription(JTable table, int rowSelected){

        Task taskSelected = _taskDAO.getByOrder(rowSelected, getTypeTaskTable(table));
        JOptionPane.showMessageDialog(null, taskSelected.getDescription(),
                taskSelected.getName(), JOptionPane.INFORMATION_MESSAGE);
    }

    private void clearTableModel(DefaultTableModel tableModel){

        if (tableModel.getRowCount() > 0) {
            for (int i = tableModel.getRowCount() - 1; i > -1; i--) {

                tableModel.removeRow(i);
            }
        }
    }

    private void loadDataInModel(DefaultTableModel tableModel, String state){

        List<Task> tasksList = _taskDAO.getAllTasksByState(state);
        for(Task dbTask : tasksList){

            Vector<Task> dbTaskDataArray = new Vector<>(1);
            dbTaskDataArray.add(dbTask);
            tableModel.addRow(dbTaskDataArray);
        }
    }

    private void configureTable(JTable table, DefaultTableModel tableModel){

        table.setModel(tableModel);
        table.setRowHeight(ROWS_HEIGHT);

        table.getColumn(COLUMN_TASKS_NAME).setCellRenderer(new TableTasksNameRenderer());
        loadIcons(table);
    }

    private void loadIcons(JTable table){

        if(table.getName().equals(_tasksListView.toDoTable.getName()) || table.getName().equals(_tasksListView.doingTable.getName())){

            table.getColumn(COLUMN_TASKS_MOVE).setCellRenderer(new TableIconImageRenderer(MOVE_ICON_ROUTE));
        }
        table.getColumn(COLUMN_TASKS_DELETE).setCellRenderer(new TableIconImageRenderer(DELETE_ICON_ROUTE));
    }

    private boolean isStringInvalid(String stringToCheck){

        return stringToCheck == null || stringToCheck.isEmpty() || Pattern.matches(WHITESPACE_REGEX, stringToCheck);
    }

    public void createTask(int _projectId, String _taskName, String _taskDescription, Task.TaskState _taskState){

        int taskId = _taskDAO.getCurrentMaxId() + 1;
        _taskModel = new Task(taskId, _projectId, _taskName, _taskDescription, _taskState.toString());

        addTaskToDB();
    }

    public void addTaskToDB(){

        _taskDAO.insert(_taskModel);
    }

    private String getTypeTaskTable(JTable table){

        String state;
        switch (table.getName()) {

            case TasksListView.TO_DO_TABLE_NAME -> state = Task.TaskState.TO_DO.toString();
            case TasksListView.DOING_TABLE_NAME -> state = Task.TaskState.IN_PROGRESS.toString();
            case TasksListView.DONE_TABLE_NAME -> state = Task.TaskState.COMPLETED.toString();
            default -> throw new IllegalStateException("Unexpected value: " + table.getName());
        }
        return state;
    }

    private DefaultTableModel getNextStateModel(String currentStateTask){

        if(currentStateTask.equals(Task.TaskState.TO_DO.toString())){

            return _doingTableModel;
        }
        else if(currentStateTask.equals(Task.TaskState.IN_PROGRESS.toString())){

            return _doneTableModel;
        }
        else{

            return _doneTableModel;
        }
    }

    private String getNextStateTaskSelected(Task taskSelected){

        String nextState = "noState";
        if(taskSelected.getCurrentState().equals(Task.TaskState.TO_DO.toString())){

            nextState = Task.TaskState.IN_PROGRESS.toString();
        }
        else if(taskSelected.getCurrentState().equals(Task.TaskState.IN_PROGRESS.toString())){

            nextState = Task.TaskState.COMPLETED.toString();
        }
        return nextState;
    }
}
