public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        Calculator calculator= new Calculator();
        System.out.print("Enter an arithmetic expression : ");
        String input = "---0.1*25/2*2";
        System.out.println(input);
        String s = calculator.Calculate(input);
        System.out.println(s);
    }
}