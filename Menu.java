public class Menu {
    public void print() {
        System.out.printf("%36s\n", "CURRENCY CALCULATOR");
        System.out.println("-------------------------------------------------------");
        System.out.printf("%37s\n", "SUPPORTED CURRENCIES");
        System.out.println("-------------------------------------------------------");
        System.out.printf("%3s %13s %26s\n", "#", "CURRENCY", "DESCRIPTION");
        System.out.println("-------------------------------------------------------");
        System.out.printf("%3s %10s %40s\n", "1", "$1", "$ before the number");
        System.out.printf("%3s %10s %40s\n", "2", "1p", "p  after the number");
        System.out.println("-------------------------------------------------------");
        System.out.println("-------------------------------------------------------");
        System.out.printf("%38s\n", "SUPPORTED TRANSACTIONS");
        System.out.println("-------------------------------------------------------");
        System.out.printf("%3s %14s %25s\n", "#", "TRANSACTION", "DESCRIPTION");
        System.out.println("-------------------------------------------------------");
        System.out.printf("%3s %9s %41s\n",  "1", "+",          "adding      values of one currency");
        System.out.printf("%3s %9s %41s\n",  "2", "-",          "subtracting values of one currency");
        System.out.printf("%3s %13s %37s\n", "3", "toDollars",  "converting    rubles  into dollars");
        System.out.printf("%3s %12s %38s\n", "4", "toRubles",   "converting    dollars into  rubles");
        System.out.println("-------------------------------------------------------");
        System.out.println();
    }
}