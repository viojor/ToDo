package view;

import javax.swing.*;

public class ProjectsListView extends JFrame{

    public JTable projectsTable;
    public JButton addProjectButton;
    public JPanel mainPanel;
    private JScrollPane projectsScrollPane;

    public ProjectsListView(String windowsTitle){

        super(windowsTitle);

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setContentPane(mainPanel);
        this.pack();
    }
}
