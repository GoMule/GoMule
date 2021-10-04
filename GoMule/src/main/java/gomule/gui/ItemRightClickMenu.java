package gomule.gui;

import gomule.item.D2Item;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class ItemRightClickMenu extends JPopupMenu {

    private final JMenuItem deleteItemOption;
    private final JMenuItem viewItemOption;

    public ItemRightClickMenu(D2Item item, Consumer<D2Item> deleteMenuItemAction) {
        add(deleteItemOption = new JMenuItem("Delete?"));
        add(viewItemOption = new JMenuItem("View Item"));
        add(new JPopupMenu.Separator());
        add("Cancel");

        deleteItemOption.addActionListener(event -> {
            int check = JOptionPane.showConfirmDialog(null, "Delete " + item.getName() + "?");
            if (check == 0) deleteMenuItemAction.accept(item);
        });

        viewItemOption.addActionListener(event -> {
            JFrame itemPanel = new JFrame();
            JEditorPane report = new JEditorPane();
            report.setContentType("text/html");
            JScrollPane SP = new JScrollPane(report);
            report.setBackground(Color.black);
            report.setForeground(Color.white);
            report.setText("<html><font size=3 face=Dialog>" + item.itemDumpHtml(true) + "</font></html>");
            report.setCaretPosition(0);
            itemPanel.add(SP);
            Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
            itemPanel.setLocation(mouseLocation.x - 20, mouseLocation.y - 20);
            itemPanel.setSize(200, 400);
            itemPanel.setVisible(true);
            itemPanel.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        });
    }
}
