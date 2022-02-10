package view;

import javax.swing.*;

public class TasksListView extends JFrame{

    public static final String TO_DO_TABLE_NAME = "toDoTable";
    public static final String DOING_TABLE_NAME = "doingTable";
    public static final String DONE_TABLE_NAME = "doneTable";

    public JTable toDoTable;
    public JTable doingTable;
    public JTable doneTable;
    public JPanel mainPanel;
    public JButton addTaskButton;
    private JScrollPane toDoScrollPane;
    private JScrollPane doingScrollPane;
    private JScrollPane doneScrollPane;

    public TasksListView(String windowsTitle){

        super(windowsTitle);

        toDoTable.setName(TO_DO_TABLE_NAME);
        doingTable.setName(DOING_TABLE_NAME);
        doneTable.setName(DONE_TABLE_NAME);

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setContentPane(mainPanel);
        this.pack();
    }
}
