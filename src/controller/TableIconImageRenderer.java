package controller;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;

public class TableIconImageRenderer extends DefaultTableCellRenderer {

    private static final int WIDTH_CELL_ICON = 30;
    private static final int HEIGHT_CELL_ICON = 30;

    private ImageIcon _imageIcon;
    private final String _iconRoute;

    public TableIconImageRenderer(String iconRoute){

        super();
        _iconRoute = iconRoute;
    }

    public void setValue(Object value){

        if(_imageIcon == null){

            try {
                _imageIcon = new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass().getResource(_iconRoute)))
                        .getScaledInstance(WIDTH_CELL_ICON, HEIGHT_CELL_ICON, Image.SCALE_SMOOTH));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        setHorizontalAlignment(CENTER);
        setIcon(_imageIcon);
    }
}
