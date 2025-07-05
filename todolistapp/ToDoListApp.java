import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.Date;

class Task extends JPanel {
    JLabel index;
    JTextField taskName;
    JButton done, delete;
    private boolean checked;
    private Point initialClick;

    Task() {
        this.setPreferredSize(new Dimension(500, 40));
        this.setBackground(Color.WHITE);
        this.setLayout(new BorderLayout());

        checked = false;
        index = new JLabel("");
        index.setPreferredSize(new Dimension(30, 40));
        index.setHorizontalAlignment(JLabel.CENTER);
        index.setOpaque(true); // Important for background color
        index.setBackground(Color.WHITE); // Always white background
        this.add(index, BorderLayout.WEST);

        taskName = new JTextField();
        taskName.setBorder(BorderFactory.createEmptyBorder());
        taskName.setBackground(Color.WHITE);
        taskName.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        taskName.setToolTipText("Write something...");
        this.add(taskName, BorderLayout.CENTER);

        done = new JButton("✔");
        done.setPreferredSize(new Dimension(60, 40));
        done.setBackground(new Color(233, 119, 119));
        done.setBorder(BorderFactory.createEmptyBorder());

        delete = new JButton("X");
        delete.setPreferredSize(new Dimension(60, 40));
        delete.setBackground(new Color(255, 255, 153));
        delete.setBorder(BorderFactory.createEmptyBorder());

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(delete);
        buttonPanel.add(done);
        this.add(buttonPanel, BorderLayout.EAST);

        done.addActionListener(e -> toggleState());
        delete.addActionListener(e -> removeTask());

        addDragFeature();


    }

    private void addDragFeature() {
        this.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
                getParent().setComponentZOrder(Task.this, 0);
            }
        });

        this.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (getParent() instanceof JPanel panel) {
                    Component[] components = panel.getComponents();
                    int currentIndex = -1, swapIndex = -1;
                    for (int i = 0; i < components.length; i++) {
                        if (components[i] == Task.this) {
                            currentIndex = i;
                        }
                    }
                    if (currentIndex != -1) {
                        int y = Task.this.getLocation().y + e.getY() - initialClick.y;
                        for (int i = 0; i < components.length; i++) {
                            if (i != currentIndex && components[i].getBounds().contains(new Point(20, y))) {
                                swapIndex = i;
                                break;
                            }
                        }
                        if (swapIndex != -1) {
                            panel.remove(Task.this);
                            panel.add(Task.this, swapIndex);
                            panel.revalidate();
                            panel.repaint();
                            reorderTaskNumbers(panel);
                        }
                    }
                }
            }
        });
    }

    public String getTaskText() {
        return taskName.getText();
    }

    public void changeIndex(int num) {
        this.index.setText(num + "");
        this.revalidate();
    }

    public boolean getState() {
        return checked;
    }

    public void toggleState() {
        checked = !checked;
        updateTaskColor();
        revalidate();
    }
    public void updateTaskColor() {
        Color bgColor = this.getParent() != null ? this.getParent().getBackground() : Color.WHITE;
        this.setBackground(checked ? new Color(188, 226, 158) : bgColor);
        taskName.setBackground(checked ? new Color(188, 226, 158) : Color.WHITE);
        index.setBackground(Color.WHITE); // Ensure index stays white
    }

    private void removeTask() {
        Container parent = this.getParent();
        if (parent != null) {
            parent.remove(this);
            parent.revalidate();
            parent.repaint();
            reorderTaskNumbers(parent);
        }
    }

    public void updateThemeColors(Color btnColor, Color hoverColor){
        updateTaskColor(); // Update colors when theme changes
        done.setBackground(new Color(233, 119, 119)); // Keep red
        delete.setBackground(new Color(255, 255, 153));

        done.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e){
                done.setBackground(new Color(200, 80, 80)); // Darker red on hover
            }
            public void mouseExited(MouseEvent e) {
                done.setBackground(new Color(233, 119, 119)); // Normal red
            }
        });

        delete.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                delete.setBackground(new Color(255, 255, 100)); // Brighter yellow on hover
            }
            public void mouseExited(MouseEvent e) {
                delete.setBackground(new Color(255, 255, 153)); // Normal yellow
            }
        });
    
    }

    private void reorderTaskNumbers(Container parent) {
        Component[] components = parent.getComponents();
        int count = 1;
        for (Component c : components) {
            if (c instanceof Task) {
                ((Task) c).changeIndex(count++);
            }
        }
    }
}

class List extends JPanel {
    List() {
        GridLayout layout = new GridLayout(10, 1);
        layout.setVgap(5);
        this.setLayout(layout);
        this.setPreferredSize(new Dimension(400, 560));
        this.setBackground(new Color(252, 221, 176));
    }

    public void updateNumbers() {
        Component[] listItems = this.getComponents();
        for (int i = 0; i < listItems.length; i++) {
            if (listItems[i] instanceof Task) {
                ((Task) listItems[i]).changeIndex(i + 1);
            }
        }
    }
}
class Footer extends JPanel {
    JButton addTask, backButton;
    private Color currentButtonColor;
    private Color currentHoverColor;

    // Colors
    Color buttonColor = new Color(236, 69, 112); // Base button color
    Color hoverColor = new Color(200, 50, 90); // Darker pink on hover
    Color backgroundColor = new Color(252, 221, 176); // Footer background

    Footer() {

         // Initialize components first
         this.setPreferredSize(new Dimension(400, 70));
         this.setBackground(new Color(252, 221, 176));
         this.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 15));

        addTask = new JButton("Add Task");
        backButton = new JButton("Close");

        this.currentButtonColor = new Color(236, 69, 112);
        this.currentHoverColor = new Color(200, 50, 90);

        // Style buttons
        styleButton(addTask, buttonColor);
        styleButton(backButton, buttonColor);

        // Add buttons to the panel
        this.add(addTask);
        this.add(backButton);
    }

    //updating button colors
    public void updateThemeColors(Color buttonColor, Color hoverColor) {
        this.currentButtonColor = buttonColor;
        this.currentHoverColor = hoverColor;
    
        styleButton(addTask, buttonColor);
        styleButton(backButton, buttonColor);
        
        // Update hover effects
        addTask.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                addTask.setBackground(hoverColor); 
            }
            public void mouseExited(MouseEvent evt) {
                addTask.setBackground(buttonColor);
            }
        });
    
        backButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                backButton.setBackground(hoverColor);
            }
            public void mouseExited(MouseEvent evt) {
                backButton.setBackground(buttonColor);
            }
        });
    }

    // Button styling method
    private void styleButton(JButton button, Color color) {
        button.setBorder(BorderFactory.createEmptyBorder()); // No border
        button.setFont(new Font("forte", Font.BOLD, 16)); // Stylish font
        button.setBackground(color); // Matching color
        button.setForeground(Color.WHITE); // White text for contrast
        button.setFocusPainted(false); // Remove focus border
        button.setBorderPainted(false); // Remove button border
        button.setContentAreaFilled(true); // Keep background filled
        button.setPreferredSize(new Dimension(140, 40)); // Standard button size
        

        // Rounded border effect
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(buttonColor.darker(), 2, true), // Outer border
                BorderFactory.createEmptyBorder(5, 15, 5, 15) // Inner padding
        ));

        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(currentHoverColor); 
            }

            public void mouseExited(MouseEvent evt) {
                button.setBackground(currentButtonColor);
            }
        });

    }

    // Getters for buttons
    public JButton getNewTask() {
        return addTask;
    }

    public JButton getBackButton() {
        return backButton;
    }
}

class TitleBar extends JPanel {
    private static final String THEME_PREF_FILE = "theme.pref";
    private String currentTheme;
    private JButton addTask;
    private JButton closeButton;
    private ToDoListPage toDoListPage;

    TitleBar(JButton addTask, JButton closeButton, ToDoListPage toDoListPage) {
        this.addTask = addTask;
        this.closeButton = closeButton;
        this.toDoListPage = toDoListPage;
        this.currentTheme = loadThemePreference();
        
        this.setPreferredSize(new Dimension(400, 80));
        this.setLayout(new BorderLayout());

        // Title label
        JLabel titleText = new JLabel("To-Do List");
        titleText.setFont(new Font("Forte", Font.BOLD, 30));
        titleText.setHorizontalAlignment(JLabel.CENTER);
        titleText.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));

        // Date label
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMM dd, yyyy");
        JLabel dateLabel = new JLabel(sdf.format(new Date()));
        dateLabel.setFont(new Font("forte", Font.PLAIN, 18));
        dateLabel.setHorizontalAlignment(JLabel.LEFT);
        dateLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20));

        JPanel labelPanel = new JPanel(new BorderLayout());
        labelPanel.add(dateLabel, BorderLayout.WEST);
        labelPanel.add(titleText, BorderLayout.CENTER);

        // Create styled theme menu
        JPopupMenu themeMenu = createThemeMenu();
        
        // Settings button with gear icon
        JButton settingsButton = new JButton("⚙");
        settingsButton.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        settingsButton.setBorder(BorderFactory.createEmptyBorder());
        settingsButton.setContentAreaFilled(false);
        settingsButton.setFocusPainted(false);
        settingsButton.setPreferredSize(new Dimension(50, 50));
        settingsButton.addActionListener(e -> 
            themeMenu.show(settingsButton, 0, settingsButton.getHeight()));

        this.add(labelPanel, BorderLayout.CENTER);
        this.add(settingsButton, BorderLayout.EAST);

        // Apply saved theme
        setTheme(currentTheme);
    }

    private JPopupMenu createThemeMenu() {
        JPopupMenu themeMenu = new JPopupMenu();
        
        // Menu styling
        themeMenu.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        themeMenu.setBackground(new Color(250, 250, 250));
        
        // Pink theme item
        JMenuItem pinkTheme = new JMenuItem("Pink Theme");
        styleMenuItem(pinkTheme, new Color(252, 221, 176), new Color(236, 69, 112));
        pinkTheme.addActionListener(e -> setTheme("Pink"));
        
        // Blue theme item
        JMenuItem blueTheme = new JMenuItem("Blue Theme");
        styleMenuItem(blueTheme, new Color(157, 249, 238), new Color(0, 105, 180));
        blueTheme.addActionListener(e -> setTheme("Blue"));
        
        themeMenu.add(pinkTheme);
        themeMenu.add(blueTheme);
        
        return themeMenu;
    }

    private void styleMenuItem(JMenuItem item, Color bgColor, Color textColor) {
        item.setFont(new Font("Segoe UI", Font.BOLD, 14));
        item.setOpaque(true);
        item.setBackground(bgColor);
        item.setForeground(textColor);
        item.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        
        // Hover effect
        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                item.setBackground(textColor.darker());
                item.setForeground(bgColor);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                item.setBackground(bgColor);
                item.setForeground(textColor);
            }
        });
    }

    private void setTheme(String theme) {
        currentTheme = theme;
        saveThemePreference(theme);
        
        if (theme.equals("Pink")) {
            updateColors(
                new Color(252, 221, 176), // bg
                new Color(236, 69, 112),  // btn
                new Color(200, 50, 90)    // hover
            );
        } 
        else if (theme.equals("Blue")) {
            updateColors(
                new Color(157, 249, 238), // bg
                new Color(0, 105, 180),   // btn
                new Color(0, 80, 150)     // hover
            );
        }
    }

    private void updateColors(Color bgColor, Color btnColor, Color hoverColor) {
        this.setBackground(bgColor);
        ((JPanel)this.getComponent(0)).setBackground(bgColor);
        
        toDoListPage.setBackground(bgColor);
        toDoListPage.taskList.setBackground(bgColor);
        toDoListPage.footer.setBackground(bgColor);
        
        toDoListPage.footer.updateThemeColors(btnColor, hoverColor);
        
        for (Component comp : toDoListPage.taskList.getComponents()) {
            if (comp instanceof Task) {
                Task task = (Task) comp;
                task.setBackground(task.getState() ? new Color(188, 226, 158) : bgColor);
                task.taskName.setBackground(Color.WHITE);
                task.updateThemeColors(btnColor, hoverColor);
            }
        }
        repaint();
    }

    private void saveThemePreference(String theme) {
        try (PrintWriter writer = new PrintWriter(THEME_PREF_FILE)) {
            writer.println(theme);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private String loadThemePreference() {
        try (Scanner scanner = new Scanner(new File(THEME_PREF_FILE))) {
            return scanner.nextLine();
        } catch (FileNotFoundException e) {
            return "Pink"; // Default theme
        }
    }
}


class ToDoListPage extends JPanel {
    List taskList;
    Footer footer;
    TitleBar titleBar;
    private static final String FILE_PATH = "tasks.dat";

    ToDoListPage(AppFrame appFrame) {
    this.setLayout(new BorderLayout());
    this.setBackground(new Color(252, 221, 176)); // Default pink

    footer = new Footer();
    taskList = new List();
    taskList.setBackground(new Color(252, 221, 176)); // Default pink

    titleBar = new TitleBar(footer.getNewTask(), footer.getBackButton(), this);

    this.add(titleBar, BorderLayout.NORTH);
    this.add(taskList, BorderLayout.CENTER);
    this.add(footer, BorderLayout.SOUTH);

    loadTasksFromFile();

    footer.backButton.addActionListener(e -> {
        saveTasksToFile();
        appFrame.dispose();
    });

    footer.addTask.addActionListener(e -> addNewTask());
}

    private void addNewTask() {
        Task task = new Task();

        // Set proper colors for new task
        task.taskName.setBackground(Color.WHITE);
        task.done.setBackground(new Color(233, 119, 119));
        task.delete.setBackground(new Color(255, 255, 153));

        taskList.add(task);
        taskList.updateNumbers();
        taskList.revalidate(); // Ensures UI refresh
        taskList.repaint(); // Ensures UI refresh
        saveTasksToFile(); // Now, tasks are saved correctly
    }

    private void clearCompletedTasks() {
        ArrayList<Component> toRemove = new ArrayList<>();
        for (Component c : taskList.getComponents()) {
            if (c instanceof Task && ((Task) c).getState()) {
                toRemove.add(c);
            }
        }

        for (Component c : toRemove) {
            taskList.remove(c);
        }

        taskList.updateNumbers();
        taskList.revalidate();
        taskList.repaint();
        saveTasksToFile();
    }

    public void saveTasksToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            ArrayList<TaskData> tasks = new ArrayList<>();
            for (Component c : taskList.getComponents()) {
                if (c instanceof Task) {
                    Task task = (Task) c;
                    tasks.add(new TaskData(task.getTaskText(), task.getState()));
                }
            }
            oos.writeObject(tasks);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void loadTasksFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            ArrayList<TaskData> tasks = (ArrayList<TaskData>) ois.readObject();
            for (TaskData taskData : tasks) {
                Task task = new Task();
                task.taskName.setText(taskData.text);
                if (taskData.completed) {
                    task.toggleState();
                }
                taskList.add(task);
            }
            taskList.updateNumbers();
        } catch (IOException | ClassNotFoundException ex) {
            // File doesn't exist yet or is empty - that's okay
        }
    }

    private static class TaskData implements Serializable {
        String text;
        boolean completed;

        TaskData(String text, boolean completed) {
            this.text = text;
            this.completed = completed;
        }
    }
}

class AppFrame extends JFrame {
    private ToDoListPage toDoListPage;

    AppFrame() {
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Prevents direct exit
        this.setLayout(new BorderLayout());

        toDoListPage = new ToDoListPage(this);
        this.add(toDoListPage, BorderLayout.CENTER);

        // Add window listener to handle X button close event
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                toDoListPage.saveTasksToFile(); // Save tasks before exit
                dispose(); // Close the app properly
            }
        });

        this.setVisible(true);
    }
}

public class ToDoListApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(AppFrame::new);
    }
}
