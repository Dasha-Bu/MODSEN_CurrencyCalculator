import java.util.*;
import java.io.*;

class IncorrectSyntaxException extends Exception{
}
class DifferentCurrencyException extends Exception{
}

public class Main {
    static boolean isCurrency(char c) {
        return (c >= '0' && c <= '9' || c == ',' || c == '.' 
                         || c == '$' || c == 'p' || c == 'р');
    }
    
    static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    static int priority(char c) {      
        switch(c) {
            case '(': return 1;
            case '+': case '-': return 2;
            case 'R': case 'D': return 3;
            default: return 0;
        }
    }

    static String convert(String text) {
        try {
            int bracket = 0, index = 0;         
            String str_in = text, str_out = "";
            Stack<Character> op_stack = new Stack<>();    
            while (index < str_in.length()) {
                char c = str_in.charAt(index);
                if(isCurrency(c))
                   str_out += c;
                else {
                    str_out += " ";
                    switch (c) {
                        case ' ':
                            break;
                        case '(': 
                           op_stack.push(c); 
                           ++bracket; 
                           break;
                        case 'D': case 'R': case '+': case '-':
                           if(index == str_in.length())
                               throw new IncorrectSyntaxException();
                           if(!op_stack.empty()) {
                               while(priority(c) <= priority(op_stack.peek())) {
                                   str_out += op_stack.pop();
                                   if(op_stack.empty()) break;
                        	   }
                        	   op_stack.push(c);
                           }
                           else op_stack.push(c);
                           break;
                        
                        case ')':
                           while((c = op_stack.pop()) != '(' && bracket > 0) {
                    	       str_out += c;
                    	   } 
                           --bracket; 
                           break;
                           
                        default:
                            throw new IncorrectSyntaxException();
                    }
                }
                index++;
            }
            while(!op_stack.empty())
                str_out += op_stack.pop();
            if(bracket != 0) 
                throw new IncorrectSyntaxException();
                
            return str_out;
        }
        catch (IncorrectSyntaxException e){
            return "Incorrect Syntax";
        }
    }
    
    static String calculate(String str_in, double rate) {
        try {
            Stack<String> val_stack = new Stack<>();
            double n1, n2;
            String temp1 = "";
            String temp2 = "";
            String number = "";
            String result = "";
            for(int i = 0; i < str_in.length(); ++i) {
                if(str_in.charAt(i) == ' ') continue;
                else {
                    char c = str_in.charAt(i);
                    switch(c) {
                        case '+': 
                            if(val_stack.size() >= 2) {
                                temp2 = val_stack.pop();
                                temp1 = val_stack.pop();
                                if(temp1.charAt(0) == '$' && temp2.charAt(0) == '$') {
                                    temp1 = temp1.substring(1, temp1.length());
                                    n1 = Double.parseDouble(temp1);
                                    temp2 = temp2.substring(1, temp2.length());
                                    n2 = Double.parseDouble(temp2) + n1;
                                    val_stack.push("$"+n2);
                                }
                                
                                else if ((temp1.charAt(temp1.length()-1) == 'p'
                                       || temp1.charAt(temp1.length()-1) == 'р')
                                      && (temp2.charAt(temp2.length()-1) == 'p'
                                       || temp2.charAt(temp2.length()-1) == 'р')) {
                                    temp1 = temp1.substring(0, temp1.length()-1);
                                    n1 = Double.parseDouble(temp1);
                                    temp2 = temp2.substring(0, temp2.length()-1);
                                    n2 = Double.parseDouble(temp2) + n1;
                                    val_stack.push(n2+"p");
                                }
                                
                                else throw new DifferentCurrencyException();
                            }
                            else throw new IncorrectSyntaxException();
                            break;
                        case '-': 
                            if(val_stack.size() >= 2) {
                                temp2 = val_stack.pop();
                                temp1 = val_stack.pop();
                                if(temp1.charAt(0) == '$' && temp2.charAt(0) == '$') {
                                    temp1 = temp1.substring(1, temp1.length());
                                    n1 = Double.parseDouble(temp1);
                                    temp2 = temp2.substring(1, temp2.length());
                                    n2 = n1 - Double.parseDouble(temp2);
                                    val_stack.push("$"+n2);
                                }
                                
                                else if ((temp1.charAt(temp1.length()-1) == 'p'
                                       || temp1.charAt(temp1.length()-1) == 'р')
                                      && (temp2.charAt(temp2.length()-1) == 'p'
                                       || temp2.charAt(temp2.length()-1) == 'р')) {
                                    temp1 = temp1.substring(0, temp1.length()-1);
                                    n1 = Double.parseDouble(temp1);
                                    temp2 = temp2.substring(0, temp2.length()-1);
                                    n2 = n1 - Double.parseDouble(temp2);
                                    val_stack.push(n2+"p");
                                }
                            }
                            else throw new IncorrectSyntaxException();
                            break;
                        case 'D': 
                            if(!val_stack.empty()) {
                                temp1 = val_stack.pop();
                                if(temp1.charAt(temp1.length()-1) == 'p'
                                || temp1.charAt(temp1.length()-1) == 'р') {
                                    temp1 = temp1.substring(0, temp1.length()-1);
                                    n1 = Double.parseDouble(temp1) / rate;
                                    val_stack.push("$"+n1);
                                }
                                else if(temp1.charAt(0) == '$') val_stack.push(temp1);
                                break;
                            }
                            else throw new IncorrectSyntaxException();
                        case 'R':
                            if(!val_stack.empty()) {
                                temp1 = val_stack.pop();
                                if(temp1.charAt(0) == '$') {
                                    temp1 = temp1.substring(1, temp1.length());
                                    n1 = Double.parseDouble(temp1) * rate;
                                    val_stack.push(n1+"p");
                                }
                                else if(temp1.charAt(temp1.length()-1) == 'p'
                                     || temp1.charAt(temp1.length()-1) == 'р')
                                    val_stack.push(temp1);
                                break;
                            }
                            else throw new IncorrectSyntaxException();
                        default: 
                            if((c >= '0' && c <= '9') || c == '.' || c == ',') {
                                while((c >= '0' && c <= '9') || c == '.' || c == ',') {
                                    if(c == ',') number += '.';
                                    else number += c;
                                    if(i < str_in.length()-1) c = str_in.charAt(++i);
                                    else break;
                                }
                                c = str_in.charAt(i);
                                if(number.charAt(0) != '$' && (c == 'p' || c == 'р')) {
                                    number += c;
                                    val_stack.push(number);
                                    number = "";
                                }
                                else if(number.charAt(0) == '$' && c != 'p' && c != 'р') {
                                    val_stack.push(number);
                                    number = "";
                                    if(c == '+' || c == '-') i -= 1;
                                }
                                else throw new IncorrectSyntaxException();
                            }
                            else if(c == '$') number += c;
                            else throw new IncorrectSyntaxException();
                            break;
                    }
                }
            }
            temp1 = val_stack.pop();
            if(temp1.charAt(0) == '$') {
                temp1 = temp1.substring(1, temp1.length());
                n1 = Double.parseDouble(temp1);
                result = String.format("Result: $%.2f", n1);
            }
            else if(temp1.charAt(temp1.length()-1) == 'p'
                 || temp1.charAt(temp1.length()-1) == 'р') {
                temp1 = temp1.substring(0, temp1.length()-1);
                n1 = Double.parseDouble(temp1);
                result = String.format("Result: %.2fp", n1);
            }
            if(!val_stack.empty()) throw new IncorrectSyntaxException();
            return result;
        }
        catch (NumberFormatException e){
            return "Invalid double format";
        }
        catch (IncorrectSyntaxException e){
            return "Incorrect Syntax";
        }
        catch (DifferentCurrencyException e){
            return "Different currency";
        }
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
            System.out.println("The file is missing");
        }
        catch (NumberFormatException e){
            System.out.println("Invalid number");
        }
	}
}
