import java.util.Scanner;
import java.sql.*;

// Клас, що представляє ігрове поле
class GameBoard {
    private char[][] board;
    private final int size = 3; // Розмір поля 3x3

    public GameBoard() {
        board = new char[size][size];
        initializeBoard();
    }

    // Ініціалізація порожнього поля
    private void initializeBoard() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = '-';
            }
        }
    }
    
    // Виведення поля на екран
    public void displayBoard() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }

    // Зміна стану ігрового поля
    public boolean updateBoard(int row, int col, char symbol) {
        if (row >= 0 && row < size && col >= 0 && col < size && board[row][col] == '-') {
            board[row][col] = symbol;
            return true;
        }
        return false;
    }

    // Перевірка на перемогу
    public boolean checkWin(char symbol) {
        for (int i = 0; i < size; i++) {
            if ((board[i][0] == symbol && board[i][1] == symbol && board[i][2] == symbol) ||
                (board[0][i] == symbol && board[1][i] == symbol && board[2][i] == symbol)) {
                return true;
            }
        }
        if ((board[0][0] == symbol && board[1][1] == symbol && board[2][2] == symbol) ||
            (board[0][2] == symbol && board[1][1] == symbol && board[2][0] == symbol)) {
            return true;
        }
        return false;
    }

    // Перевірка на заповнене поле (нічия)
    public boolean isFull() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == '-') {
                    return false;
                }
            }
        }
        return true;
    }
}

// Клас, що представляє гравця
class Player {
    private String name;
    private char symbol;
    private Scanner scanner;

    public Player(String name, char symbol) {
        this.name = name;
        this.symbol = symbol;
        this.scanner = new Scanner(System.in);
    }

    public String getName() {
        return name;
    }

    public char getSymbol() {
        return symbol;
    }

    public int[] makeMove() {
        System.out.println(name + ", введіть номер рядка та стовпця (0, 1 або 2): ");
        int row = scanner.nextInt();
        int col = scanner.nextInt();
        return new int[]{row, col};
    }
}

// Основний клас для управління грою
public class LAB3 {
    public static void main(String[] args) {
        Db db = new Db();
        Scanner scanner = new Scanner(System.in);

        System.out.print("Введіть ім'я користувача: ");
        String username = scanner.nextLine();
        System.out.print("Введіть пароль: ");
        String password = scanner.nextLine();

        if (!db.isUserExists(username) || !db.checkPassword(username, password)) {
            System.out.println("Неправильне ім'я користувача або пароль.");
            db.close();
            scanner.close();
            return;
        }

        GameBoard gameBoard = new GameBoard();
        Player player1 = new Player("Гравець 1", 'X');
        Player player2 = new Player("Гравець 2", 'O');
        Player currentPlayer = player1;

        boolean gameWon = false;
        while (!gameWon && !gameBoard.isFull()) {
            gameBoard.displayBoard();
            int[] move = currentPlayer.makeMove();

            while (!gameBoard.updateBoard(move[0], move[1], currentPlayer.getSymbol())) {
                System.out.println("Неправильний хід! Спробуйте знову.");
                move = currentPlayer.makeMove();
            }

            if (gameBoard.checkWin(currentPlayer.getSymbol())) {
                gameWon = true;
                gameBoard.displayBoard();
                System.out.println("Переміг " + currentPlayer.getName() + "!");
            } else if (gameBoard.isFull()) {
                gameBoard.displayBoard();
                System.out.println("Нічия!");
            } else {
                currentPlayer = (currentPlayer == player1) ? player2 : player1;
            }
        }

        db.close();
        scanner.close();
    }
}

// Клас для роботи з базою даних
class Db {
    String dbUrl = "jdbc:mysql://localhost:3306/myGAME?useSSL=false";
    String user = "root";
    String password = "1234";
    Connection con;

    public Db() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.con = DriverManager.getConnection(dbUrl, user, password);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void close() {
        try {
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public boolean isUserExists(String username) {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT count(*) FROM users WHERE username='" + username + "';");
            while (rs.next())
                if (rs.getInt(1) == 1) return true;
                else return false;
        } catch (Exception e) {
            System.out.println(e);
        }
        return true;
    }
    public boolean checkPassword(String username, String password) {
        try {
            PreparedStatement stmt = con.prepareStatement("SELECT password FROM users WHERE username=?");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                return storedPassword.equals(password);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return false;
    }
}
