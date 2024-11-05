public class LAB2 {

    // Метод для виведення ялинки у формі рівнобедреного трикутника
    public static void printTree(int levels) {
        for (int i = 1; i <= levels; i++) {
            // Виведення пробілів перед зірочками для симетрії
            for (int j = 0; j < levels - i; j++) {
                System.out.print(" ");
            }
            // Виведення зірочок
            for (int k = 0; k < (2 * i - 1); k++) {
                System.out.print("*");
            }
            System.out.println();
        }
    }

    // Інші методи залишаються незмінними
    public static void print2DArray(int rows, int cols) {
        int[][] array = new int[rows][cols];
        int value = 1;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                array[i][j] = value;
                System.out.print(array[i][j] + "\t");
                value += 3;
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        // Виклик методу для виведення ялинки
        int levels = 6; // кількість рівнів ялинки
        System.out.println("Ялинка:");
        printTree(levels);

        // Виклик методу для виведення двовимірного масиву
        int rows = 4; // кількість рядків масиву
        int cols = 5; // кількість стовпців масиву
        System.out.println("\nДвовимірний масив:");
        print2DArray(rows, cols);
    }
}
