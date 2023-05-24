import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		File input  = new File("rate.txt");
        try{
            Menu menu = new Menu();
            menu.print();
            
            Scanner file = new Scanner(input);              
            Double rate = Double.parseDouble(file.nextLine());
            System.out.println("Exchange RATE obtained from the file: "+rate+"\n");
            
            boolean isEnd = false;
            while(!isEnd) {
                System.out.print("Enter EXPRESSION: ");
                Scanner in = new Scanner(System.in);
                String text = in.nextLine();
                
                CurrencyCalculator calculator = new CurrencyCalculator(text);
                System.out.println(calculator.calculate(rate));
                
                System.out.print("Do you want to continue? (y/n) ");
                String answer = in.nextLine().toLowerCase();
                if(!answer.equals("y") && !answer.equals("yes")) isEnd = true;
            }
        }
        catch (FileNotFoundException e){
            System.out.println("ERROR: The file is missing.");
        }
        catch (NumberFormatException e){
            System.out.println("ERROR: Invalid double format.");
        }
	}
}
