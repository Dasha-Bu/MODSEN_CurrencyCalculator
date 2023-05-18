import java.util.*;
import java.io.*;

public class Main {
    //checking symbols to separate currency from transactions
    static boolean isCurrency(char c) {
        return (c >= '0' && c <= '9'                //numbers
             || c == ',' || c == '.'                //decimal separators
             || c == '$' || c == 'p' || c == 'р');  //currencies
    }
    
    //checking a string for a number
    static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } 
        catch (NumberFormatException e) {
            return false;
        }
    }

    //prioritizing transactions
    static int priority(char c) {      
        switch(c) {
            case '(':           return 1;
            case '+': case '-': return 2;
            case 'R': case 'D': return 3;
            default: return 0;
        }
    }

    //converting a string to reverse polish notation
    static String convert(String input) {
        String output = "";
        int chekingBrackets = 0; //variable for checking brackets
        Stack<Character> transactionsStack = new Stack<>();
        
        try {
            for (int index = 0; index < input.length(); index++) {
                char c = input.charAt(index); //reading a character
                
                if(isCurrency(c)) 
                    output += c; //adding each currency separately
                    
                else {
                    output += " "; //separating the currency with a space
                    
                    switch (c) {
                        case ' ': //if the character is a space
                            break; //skiping
                        
                        case '(': //if the character is a left bracket
                            if(index != input.length()-1 && input.charAt(index+1) == ')')
                                throw new EmptyBracketsException();
                            transactionsStack.push(c); //saving to stack
                            ++chekingBrackets;         //adding to check brackets
                            break;
                           
                        case 'D': case 'R': case '+': case '-': //if the character is a transaction
                            if(index == input.length()-1) //if the operation is at the end of the string
                               throw new IncorrectOperationException(); //sending an error
                           
                            if(!transactionsStack.empty()) { //if the stack is not empty
                                while(priority(c) <= priority(transactionsStack.peek())) { //if the transaction priority is less than or equal to the transaction priority at the top of the stack
                                    output += transactionsStack.pop(); //adding the last transaction from stack
                                    if(transactionsStack.empty()) break;
                        	    }
                            }
                           
                            transactionsStack.push(c); //adding the transaction to the stack
                            break;
                        
                        case ')': //if the character is a right bracket
                            while((c = transactionsStack.pop()) != '(' && chekingBrackets > 0) { //adding all transactions up to the left bracket
                    	        output += c;
                    	    } 
                            --chekingBrackets; //removing the paired bracket
                            break;
                           
                        default: //if the character does not satisfy the condition
                            throw new IncorrectSyntaxException(); //sending an error
                    }
                }
            }
            if (chekingBrackets != 0) //if there are still unclosed brackets
                throw new UnclosedBracketsException(); //sending an error
                
            while(!transactionsStack.empty())
                output += transactionsStack.pop(); //adding the remaining transactions to the stack
        }
        catch (IncorrectSyntaxException e)    { return "E1"; }
        catch (IncorrectOperationException e) { return "E2"; }
        catch (EmptyBracketsException e)      { return "E3"; }
        catch (UnclosedBracketsException e)   { return "E4"; }   
        
        return output;
    }
    
    //counting the result using reverse polish notation
    static String calculate(String input, double rate) {
        double number1, number2;
        String currency1 = "", currency2 = "";
        String number = "";
        String result = "";
        Stack<String> temp = new Stack<>();
        
        try {
            if(input.charAt(0) == 'E') { //conversion error handling
                switch(input.charAt(1)) {
                    case '1': throw new IncorrectSyntaxException(); 
                    case '2': throw new IncorrectOperationException(); 
                    case '3': throw new EmptyBracketsException(); 
                    case '4': throw new UnclosedBracketsException(); 
                }
            }
            
            for(int i = 0; i < input.length(); ++i) {
                char c = input.charAt(i); //reading a character
                
                if(c == ' ') //if the character is a space
                    continue; //skiping
                
                else {
                    switch(c) {
                        case '+': case '-':
                            if(temp.size() >= 2) { //checking for binary operation
                                currency2 = temp.pop();
                                currency1 = temp.pop();
                                
                                if(currency1.charAt(0) == '$' && currency2.charAt(0) == '$') { //if both currencies are dollars
                                    currency1 = currency1.substring(1, currency1.length());
                                    number1 = Double.parseDouble(currency1); //separating the number from the currency
                                    currency2 = currency2.substring(1, currency2.length());
                                    if(c == '+') number2 = number1 + Double.parseDouble(currency2); //if the character is plus  add
                                    else         number2 = number1 - Double.parseDouble(currency2); //if the character is minus substract
                                    temp.push("$"+number2); //saving as a dollar
                                }
                                
                                else if ((currency1.charAt(currency1.length()-1) == 'p'
                                       || currency1.charAt(currency1.length()-1) == 'р')
                                       &&(currency2.charAt(currency2.length()-1) == 'p'
                                       || currency2.charAt(currency2.length()-1) == 'р')) { //if both currencies are rubles
                                    currency1 = currency1.substring(0, currency1.length()-1);
                                    number1 = Double.parseDouble(currency1); //separating the number from the currency
                                    currency2 = currency2.substring(0, currency2.length()-1);
                                    if(c == '+') number2 = number1 + Double.parseDouble(currency2); //if the character is plus  add
                                    else         number2 = number1 - Double.parseDouble(currency2); //if the character is minus substract
                                    temp.push(number2+"p"); //saving as a ruble
                                }
                                
                                else throw new DifferentCurrencyException(); //else sending an error
                            }
                            
                            else throw new BinaryOperationException(); //else sending an error
                            break;
                            
                        case 'D': //converting to dollars
                            if(!temp.empty()) {
                                currency1 = temp.pop();
                                if(currency1.charAt(currency1.length()-1) == 'p'
                                || currency1.charAt(currency1.length()-1) == 'р') { //if the currency is the ruble
                                    currency1 = currency1.substring(0, currency1.length()-1);
                                    number1 = Double.parseDouble(currency1) / rate; //separating the number from the currency and converting
                                    temp.push("$"+number1); //saving as a dollar
                                }
                                else if(currency1.charAt(0) == '$') //if the currency is the dollar
                                    temp.push(currency1); //leaving the same
                                break;
                            }
                            else throw new IncorrectSyntaxException();
                            
                        case 'R': //converting to rubles
                            if(!temp.empty()) {
                                currency1 = temp.pop();
                                if(currency1.charAt(0) == '$') { //if the currency is the dollar
                                    currency1 = currency1.substring(1, currency1.length());
                                    number1 = Double.parseDouble(currency1) * rate; //separating the number from the currency and converting
                                    temp.push(number1+"p"); //saving as a ruble
                                }
                                else if(currency1.charAt(currency1.length()-1) == 'p'
                                     || currency1.charAt(currency1.length()-1) == 'р') //if the currency is the ruble
                                    temp.push(currency1); //leaving the same
                                break;
                            }
                            else throw new IncorrectSyntaxException();
                                
                        default: //processing currencies
                            if((c >= '0' && c <= '9') || c == '.' || c == ',') {
                                while((c >= '0' && c <= '9') || c == '.' || c == ',') { //separating a number from a currency
                                    if(c == ',') number += '.'; //replacing a comma with a dot for a separator
                                    else number += c;
                                    if(i < input.length()-1) c = input.charAt(++i);
                                    else break;
                                }
                                
                                c = input.charAt(i);
                                
                                if(number.charAt(0) != '$' && (c == 'p' || c == 'р')) { //saving a ruble
                                    number += c;
                                    temp.push(number);
                                    number = "";
                                }
                                
                                else if(number.charAt(0) == '$' && c != 'p' && c != 'р') { // saving a dollar
                                    temp.push(number);
                                    number = "";
                                    if(c == '+' || c == '-') i -= 1;
                                }
                                
                                else throw new IncorrectSyntaxException(); //sending an error of wrong currency
                            }
                            
                            else if(c == '$') number += c;
                            break;
                    }
                }
            }
            
            currency1 = temp.pop();
            
            if(currency1.charAt(0) == '$') { //if the currency is the dollar
                currency1 = currency1.substring(1, currency1.length());
                number1 = Double.parseDouble(currency1);
                result = String.format("Result: $%.2f", number1); //formatting the result
            }
            
            else if(currency1.charAt(currency1.length()-1) == 'p'
                 || currency1.charAt(currency1.length()-1) == 'р') { //if the currency is the ruble
                currency1 = currency1.substring(0, currency1.length()-1);
                number1 = Double.parseDouble(currency1);
                result = String.format("Result: %.2fp", number1); //formatting the result
            }
            
            if(!temp.empty()) throw new IncorrectSyntaxException();
        }
        catch (NumberFormatException e){
            return "ERROR: Invalid double format.";
        }
        catch (DifferentCurrencyException e){
            return "ERROR: Different types of currencies.";
        }
        catch (IncorrectSyntaxException e){
            return "ERROR: Invalid characters in the expression.";
        }
        catch (IncorrectOperationException e){
            return "ERROR: Incorrect location of the transaction.";
        }
        catch (EmptyBracketsException e){
            return "ERROR: The expression inside the brackets is empty.";
        }
        catch (UnclosedBracketsException e){
            return "ERROR: Brackets are not closed in the expression.";
        }
        catch (BinaryOperationException e){
            return "ERROR: Invalid operands to binary expression.";
        }
        
        return result;
    }
    
    public static void main(String[] args) {
	File input  = new File("rate.txt");
	try{
	    Scanner file = new Scanner(input);              
	    double rate = Double.parseDouble(file.nextLine());
	    System.out.print("Enter expression: ");
	    Scanner in = new Scanner(System.in);

	    String text = in.nextLine();
	    text = text.replaceAll("toDollars", "D");
	    text = text.replaceAll("toRubles", "R");
	    System.out.println(calculate(convert(text), rate));
	}
	catch (FileNotFoundException e){
	    System.out.println("ERROR: The file is missing.");
	}
	catch (NumberFormatException e){
	    System.out.println("ERROR: Invalid double format.");
	}
    }
}
