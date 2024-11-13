import java.util.Scanner;
import java.sql.*;

// Класс, представляющий игровое поле
class GameBoard {
    private char[][] board; // Двумерный массив для представления клеток игрового поля
    private final int size = 3; // Размер поля 3x3

    // Конструктор, инициализирует поле
    public GameBoard() {
        board = new char[size][size];
        initializeBoard();
    }

    // Метод для заполнения поля пустыми ячейками
    private void initializeBoard() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = '-'; // '-' обозначает пустую ячейку
            }
        }
    }
    
    // Метод для отображения текущего состояния игрового поля в консоли
    public void displayBoard() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }

    // Метод для обновления состояния поля
    public boolean updateBoard(int row, int col, char symbol) {
        // Проверка: если выбранные координаты в пределах поля и ячейка пуста, обновляем её символом игрока
        if (row >= 0 && row < size && col >= 0 && col < size && board[row][col] == '-') {
            board[row][col] = symbol;
            return true;
        }
        return false; // Возвращает false, если ячейка занята
    }

    // Метод для проверки, выиграл ли текущий игрок
    public boolean checkWin(char symbol) {
        // Проверка всех строк и столбцов на совпадение символов
        for (int i = 0; i < size; i++) {
            if ((board[i][0] == symbol && board[i][1] == symbol && board[i][2] == symbol) ||
                (board[0][i] == symbol && board[1][i] == symbol && board[2][i] == symbol)) {
                return true;
            }
        }
        // Проверка диагоналей
        if ((board[0][0] == symbol && board[1][1] == symbol && board[2][2] == symbol) ||
            (board[0][2] == symbol && board[1][1] == symbol && board[2][0] == symbol)) {
            return true;
        }
        return false; // Возвращает false, если нет выигрышной комбинации
    }

    // Метод для проверки, заполнено ли поле (на случай ничьей)
    public boolean isFull() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == '-') { // Если есть пустая ячейка, возвращаем false
                    return false;
                }
            }
        }
        return true; // Если поле заполнено, возвращаем true
    }
}

// Класс, представляющий игрока
class Player {
    private String name; // Имя игрока
    private char symbol; // Символ игрока ('X' или 'O')
    private Scanner scanner;

    // Конструктор, инициализирующий имя и символ игрока
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

    // Метод для запроса хода от игрока
    public int[] makeMove() {
        System.out.println(name + ", введите номер строки и столбца (0, 1 или 2): ");
        int row = scanner.nextInt();
        int col = scanner.nextInt();
        return new int[]{row, col}; // Возвращает выбранные координаты
    }
}

// Основной класс для управления игрой
public class LAB3 {
    public static void main(String[] args) {
        Db db = new Db(); // Создание объекта для работы с базой данных
        Scanner scanner = new Scanner(System.in);

        System.out.println("Регистрация или вход для двух игроков:");

        // Регистрация или вход для двух игроков
        Player player1 = registerOrLoginPlayer(db, scanner, "Игрок 1", 'X');
        Player player2 = registerOrLoginPlayer(db, scanner, "Игрок 2", 'O');

        GameBoard gameBoard = new GameBoard(); // Создание игрового поля
        Player currentPlayer = player1; // Установка первого игрока как текущего

        boolean gameWon = false; // Флаг для отслеживания состояния игры
        while (!gameWon && !gameBoard.isFull()) { // Цикл, пока не определится победитель или ничья
            gameBoard.displayBoard(); // Отображение текущего состояния поля
            int[] move = currentPlayer.makeMove(); // Получение хода текущего игрока

            // Проверка допустимости хода
            while (!gameBoard.updateBoard(move[0], move[1], currentPlayer.getSymbol())) {
                System.out.println("Неправильный ход! Попробуйте снова.");
                move = currentPlayer.makeMove();
            }

            // Проверка победителя или ничьи
            if (gameBoard.checkWin(currentPlayer.getSymbol())) {
                gameWon = true;
                gameBoard.displayBoard();
                System.out.println("Победил " + currentPlayer.getName() + "!");
            } else if (gameBoard.isFull()) {
                gameBoard.displayBoard();
                System.out.println("Ничья!");
            } else {
                // Смена текущего игрока
                currentPlayer = (currentPlayer == player1) ? player2 : player1;
            }
        }

        db.close(); // Закрытие соединения с базой данных
        scanner.close(); // Закрытие сканера
    }

    // Метод для регистрации или входа игрока
    private static Player registerOrLoginPlayer(Db db, Scanner scanner, String playerName, char symbol) {
        while (true) {
            System.out.println(playerName + ": Введите имя пользователя: ");
            String username = scanner.nextLine();
            System.out.print(playerName + ": Введите пароль: ");
            String password = scanner.nextLine();

            // Проверка существования пользователя в базе данных
            if (db.isUserExists(username)) {
                if (db.checkPassword(username, password)) {
                    System.out.println("Вход успешен для " + playerName + ".");
                    return new Player(username, symbol); // Создание объекта Player
                } else {
                    System.out.println("Неправильный пароль. Попробуйте снова.");
                }
            } else {
                db.registerUser(username, password); // Регистрация нового пользователя
                System.out.println("Регистрация успешна для " + playerName + ".");
                return new Player(username, symbol);
            }
        }
    }
}

// Класс для работы с базой данных
class Db {
    String dbUrl = "jdbc:mysql://localhost:3306/myGAME?useSSL=false";
    String user = "root"; // Имя пользователя базы данных
    String password = "1234"; // Пароль базы данных
    Connection con; // Объект для соединения с базой данных

    // Конструктор для установки соединения с базой данных
    public Db() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Загрузка драйвера JDBC
            this.con = DriverManager.getConnection(dbUrl, user, password); // Установка соединения с базой данных
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // Метод для закрытия соединения с базой данных
    public void close() {
        try {
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // Метод для проверки, существует ли пользователь
    public boolean isUserExists(String username) {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT count(*) FROM users WHERE username='" + username + "';");
            while (rs.next())
                if (rs.getInt(1) == 1) return true; // Если пользователь найден, возвращает true
        } catch (Exception e) {
            System.out.println(e);
        }
        return false;
    }

    // Метод для проверки пароля пользователя
    public boolean checkPassword(String username, String password) {
        try {
            PreparedStatement stmt = con.prepareStatement("SELECT password FROM users WHERE username=?");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                return storedPassword.equals(password); // Сравнивает введённый и хранящийся пароли
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return false;
    }

    // Метод для регистрации нового пользователя
    public void registerUser(String username, String password) {
        try {
            PreparedStatement stmt = con.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)");
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate(); // Выполняет вставку пользователя в базу данных
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
