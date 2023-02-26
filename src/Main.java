import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner =new Scanner(System.in);
        Calculator calculator= new Calculator();
        System.out.print("Enter an arithmetic expression : ");
        String input = scanner.nextLine().replaceAll("\\s","");
        while (!input.isEmpty()){
            String s = calculator.Calculate(input);
            System.out.println(s);
            System.out.print("\n=======================================\nEnter another arithmetic expression : ");
            input = scanner.nextLine().replaceAll("\\s","");
        }
        System.exit(0);
    }
}
// √(36)-2*(5+1)