import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        Scanner scanner =new Scanner(System.in);
        Calculator calculator= new Calculator();
        System.out.print("Enter an arithmetic expression : ");
        String input = "((5(1))---0.1*12.5*2-2)";//scanner.nextLine();// "---0.1*25/2*2-2";  // +(5---0.1*(25/2)*2-2)  //((5)---0.1*12.5*2-2)
        //System.out.println(input);
        String s = calculator.Calculate(input);
        System.out.println(s);
    }
}