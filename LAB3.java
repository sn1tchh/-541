import java.util.Scanner;

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
        // Перевірка рядків і стовпців
        for (int i = 0; i < size; i++) {
            if ((board[i][0] == symbol && board[i][1] == symbol && board[i][2] == symbol) ||
                (board[0][i] == symbol && board[1][i] == symbol && board[2][i] == symbol)) {
                return true;
            }
        }
        // Перевірка діагоналей
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

    // Отримання імені гравця
    public String getName() {
        return name;
    }

    // Отримання символу гравця ('X' або 'O')
    public char getSymbol() {
        return symbol;
    }

    // Запит на хід гравця
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
        GameBoard gameBoard = new GameBoard();
        Player player1 = new Player("Гравець 1", 'X');
        Player player2 = new Player("Гравець 2", 'O');
        Player currentPlayer = player1;

        boolean gameWon = false;
        while (!gameWon && !gameBoard.isFull()) {
            gameBoard.displayBoard();
            int[] move = currentPlayer.makeMove();

            // Перевірка коректності ходу
            while (!gameBoard.updateBoard(move[0], move[1], currentPlayer.getSymbol())) {
                System.out.println("Неправильний хід! Спробуйте знову.");
                move = currentPlayer.makeMove();
            }

            // Перевірка на перемогу
            if (gameBoard.checkWin(currentPlayer.getSymbol())) {
                gameWon = true;
                gameBoard.displayBoard();
                System.out.println("Переміг " + currentPlayer.getName() + "!");
            } else if (gameBoard.isFull()) {
                gameBoard.displayBoard();
                System.out.println("Нічия!");
            } else {
                // Перемикаємо поточного гравця
                currentPlayer = (currentPlayer == player1) ? player2 : player1;
            }
        }
    }
}
