package AAsterick;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class AAsterickVisualization extends JPanel {
    private static final int ROWS = 30; // Grid size
    private static final int COLS = 30;
    private static final int CELL_SIZE = 20; // Cell size
    private int[][] grid;
    private Node start;
    private Node goal;
    private PriorityQueue<Node> openSet;
    private Set<Node> closedSet;
    private List<Node> path;
    private Timer timer;
    private Random random;

    public AAsterickVisualization() {
        random = new Random();
        grid = new int[ROWS][COLS];
        start = new Node(0, 0);
        goal = generateRandomGoal(); // Random goal node
        generateRandomObstacles();
        initializeAStar();
        startVisualization();
    }

    private Node generateRandomGoal() {
        Node randomGoal;
        do {
            int x = random.nextInt(ROWS);
            int y = random.nextInt(COLS);
            randomGoal = new Node(x, y);
        } while (randomGoal.equals(start) || grid[randomGoal.x][randomGoal.y] == 1); // Ensure goal is not start or obstacle
        return randomGoal;
    }

    private void generateRandomObstacles() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (random.nextDouble() < 0.3) { // 30% chance of obstacle
                    grid[i][j] = 1;
                }
            }
        }
        grid[start.x][start.y] = 0; // Ensure start is clear
        grid[goal.x][goal.y] = 0;   // Ensure goal is clear
    }

    private void initializeAStar() {
        openSet = new PriorityQueue<>();
        closedSet = new HashSet<>();
        path = new ArrayList<>();

        start.g = 0;
        start.h = heuristic(start, goal);
        openSet.add(start);
    }

    private void startVisualization() {
        timer = new Timer(500, new ActionListener() { // 250ms delay for smoother animation
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!runAStarStep()) {
                    timer.stop(); // Stop the timer when the algorithm is done
                }
                repaint(); // Refresh the display
            }
        });
        timer.start(); // Start the animation
    }

    private boolean runAStarStep() {
        if (openSet.isEmpty()) {
            return false; // No path found
        }

        Node current = openSet.poll();

        if (current.equals(goal)) {
            reconstructPath(current);
            return false; // Path found, stop the algorithm
        }

        closedSet.add(current);

        for (int[] dir : AAsterick.dirs) {
            int newX = current.x + dir[0];
            int newY = current.y + dir[1];

            if (newX < 0 || newX >= ROWS || newY < 0 || newY >= COLS || grid[newX][newY] == 1) {
                continue;
            }

            Node neighbor = new Node(newX, newY);

            if (closedSet.contains(neighbor)) {
                continue;
            }

            double tentativeG = current.g + 1;

            if (!openSet.contains(neighbor) || tentativeG < neighbor.g) {
                neighbor.parent = current;
                neighbor.g = tentativeG;
                neighbor.h = heuristic(neighbor, goal);
                openSet.add(neighbor);
            }
        }

        return true; // Continue the algorithm
    }

    private void reconstructPath(Node node) {
        path.clear();
        while (node != null) {
            path.add(node);
            node = node.parent;
        }
        Collections.reverse(path);
    }

    private double heuristic(Node a, Node b) {
        return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the grid
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (grid[i][j] == 1) {
                    g.setColor(Color.BLACK); // Obstacle
                } else {
                    g.setColor(Color.WHITE); // Free cell
                }
                g.fillRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                g.setColor(Color.GRAY);
                g.drawRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }

        // Draw open set nodes
        g.setColor(Color.YELLOW);
        for (Node node : openSet) {
            g.fillRect(node.y * CELL_SIZE, node.x * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }

        // Draw closed set nodes
        g.setColor(Color.CYAN);
        for (Node node : closedSet) {
            g.fillRect(node.y * CELL_SIZE, node.x * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }

        // Draw the start node
        g.setColor(Color.GREEN);
        g.fillRect(start.y * CELL_SIZE, start.x * CELL_SIZE, CELL_SIZE, CELL_SIZE);

        // Draw the goal node
        g.setColor(Color.RED);
        g.fillRect(goal.y * CELL_SIZE, goal.x * CELL_SIZE, CELL_SIZE, CELL_SIZE);

        // Draw the path
        g.setColor(Color.BLUE);
        for (Node node : path) {
            g.fillRect(node.y * CELL_SIZE, node.x * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("A* Algorithm Visualization");
        AAsterickVisualization panel = new AAsterickVisualization();
        frame.add(panel);
        frame.setSize(COLS * CELL_SIZE, ROWS * CELL_SIZE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

class AAsterick {
    static final int[][] dirs = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1},
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
    };
}