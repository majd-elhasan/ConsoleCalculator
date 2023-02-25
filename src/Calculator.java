import java.util.Stack;

public class Calculator {
    String exp ;
    Double previousNum ;
    Double nextNum;
    boolean isNegative = false;

    boolean isDigit(char c){
        return switch (c) {
            case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> true;
            default -> false;
        };
    }
    boolean isSign(char c){
        return switch (c){
            case '/','*','+','-' -> true;
            default -> false;
            };
    }
    String Calculate(String exp){
        this.exp = exp;
        checkParenthesesSets(this.exp);
        // 1. parentheses
        while (this.exp.contains("(")){
            this.exp = parentheses(this.exp);
        }
        // 2. powers and roots
        this.exp = multiplyOrDivide(this.exp);
        this.exp = addAndSubtract(this.exp);
        return this.exp;
    }
    void checkParenthesesSets(String expression){
        Stack<Character> parentheses = new Stack<>();
        for (int i=0;i<expression.length();i++){
            if (expression.charAt(i)=='(' || expression.charAt(i)==')'){
                if (i==0 && expression.charAt(i)==')')
                    throw new UnKnownSignException("you can't type ')' at the beginning of the expression !");
                else if (parentheses.empty() || expression.charAt(i) == parentheses.peek())
                    parentheses.push(expression.charAt(i));
                else if(expression.charAt(i) != parentheses.peek()){
                    parentheses.pop();
                }
            }
        }
    }
    String parentheses(String exp){
        if (exp.contains("(")) {
            int i = exp.indexOf('(');
            int j = i+1;
            int c = 1;
            for (;;j++){
                if (exp.charAt(j)=='(') c++;
                else if (exp.charAt(j)==')') {
                    c--;
                    if (c==0)
                        break;
                }
            }
            boolean IsMultiplied = false;
            String innerExp = exp.substring(i+1, j);
            StringBuilder sb = new StringBuilder(exp);
            if (i>0 && isDigit(exp.charAt(i-1))) {
                IsMultiplied = true;
            }else if ( i>0 && (exp.charAt(i-1)=='-' || exp.charAt(i-1)=='+')){
                int x;
                for (x = i-1;x>=0;x--){
                    if (isDigit(exp.charAt(x)))
                        break;
                    if (exp.charAt(x)=='-') {
                        isNegative = !isNegative;
                        sb.deleteCharAt(x);
                    }
                    else if (exp.charAt(x)=='+')
                        sb.deleteCharAt(x);
                }
                if (isNegative) {
                    sb.insert(i-1,'-');
                }else{
                    sb.insert(i-1,'+');
                }
            }
            isNegative = false;
            String result = parentheses(innerExp);
            sb.delete(i,innerExp.length()+i+2);
            sb.insert(i,result);
            if (IsMultiplied) sb.insert(i,'*');
            exp = sb.toString();
            return exp;
        }
        // powers and roots
        exp = multiplyOrDivide(exp);
        exp = addAndSubtract(exp);
        return exp;
    }
    String multiplyOrDivide(String exp){
        char multiply = '*';char divide = '/';
        exp = exceptionAssistant(multiply,divide,exp);
        if (!exp.contains("*") && !exp.contains("/"))
            return exp;
        return multiplyOrDivide(exp);

    }
    boolean hasTwoNumber(String exp){
        boolean passedFirstNum = false;
        boolean passedFirstSign = false;
        for (int i = 0;i<exp.length();i++){
            if (isDigit(exp.charAt(i)) || exp.charAt(i)=='.'){
                passedFirstNum = true;
                if (passedFirstSign) return true;
            }
            else if (isSign(exp.charAt(i)) && passedFirstNum)
                passedFirstSign = true;
        }
        return false;
    }
    String addAndSubtract(String exp){
        char plus = '+';char minus='-';
        exp = exceptionAssistant(plus,minus,exp);
        // if length of exp is equal to 1 ,no need to check for '-' sign after the first index , anyway we can't because of the substring method working mechanism
        if (!hasTwoNumber(exp))
            return exp;
        return addAndSubtract(exp);
    }
    String exceptionAssistant(char a,char b,String exp){
        if((a=='*' || a =='/') && exp.charAt(0)==a)
            throw new UnKnownSignException("you can not type '"+exp.charAt(0)+"' at the beginning of the expression !");
        if (exp.contains(a+"") || exp.contains(b+"")) {
            int i;
            boolean passedDigit =false;
            for (i = 0; i < exp.length(); i++) {
                if ((exp.charAt(i)==a || exp.charAt(i)==b) && passedDigit)
                    break;
                if(isDigit(exp.charAt(i)))
                    passedDigit = true;
            }
            if(i<exp.length()) {
                if (( exp.charAt(i) == '-' || exp.charAt(i) == '+') &&( exp.charAt(i + 1) == '*' || exp.charAt(i + 1) == '/' || exp.charAt(i + 1) == '^'))
                    throw new UnKnownSignException("you can not type '" + exp.charAt(i) + "' directly before '" + exp.charAt(i + 1) + "' !");
                exp = assistant(exp,i);
            }
        }
        return exp;
    }
    String assistant(String exp,int signIndicator){
        char sign = exp.charAt(signIndicator);
        int p =signIndicator;
        while (p!=0 && (isDigit(exp.charAt(p-1)) || exp.charAt(p-1) == '.')){
            p--;
        }

        StringBuilder num= new StringBuilder();
        for (int n=p;n<exp.length() && (isDigit(exp.charAt(n) ) || exp.charAt(n) == '.');n++){
                num.append(exp.charAt(n));
        }
        if (!(num.toString().contains(".".repeat(0)) || num.toString().contains(".".repeat(1)))) throw new RuntimeException("the number should not have two dots in it !");
        previousNum = Double.parseDouble(num.toString());
        while (p!=0 && (exp.charAt(p-1) =='-' || exp.charAt(p-1) =='+') ) {
            if(exp.charAt(p-1) =='-' )
                isNegative = !isNegative;
            p--;
        }
        int lowerIndex = p;
        if (isNegative) previousNum = -previousNum;
        // return isNegative to false for using it later.
        isNegative = false;
        //  until here we got the number before the * or / sign and assigned the negativity if exists .
        int n;
        for (n =signIndicator+1 ;n<exp.length() && (exp.charAt(n) =='-' || exp.charAt(n) =='+');n++){
            if(exp.charAt(n) =='-' )
                isNegative = !isNegative;
        }
        StringBuilder num2 = new StringBuilder();
        int ii;
        for ( ii = n;ii<exp.length() && (isDigit(exp.charAt(ii)) || exp.charAt(ii) == '.');ii++){
             num2.append(exp.charAt(ii));
        }
        nextNum = Double.parseDouble(num2.toString());
        StringBuilder sb = new StringBuilder(exp);
        sb.delete(lowerIndex,ii);
        String calculated;
        double D_Calculated=0;
        switch (sign){
            case '*' -> D_Calculated = (previousNum*nextNum);
            case '/' -> D_Calculated = (previousNum/nextNum);
            case '+' -> D_Calculated = (previousNum+nextNum);
            case '-' -> D_Calculated = (previousNum-nextNum);
        }
        calculated = D_Calculated+"";
        String signToBeInserted = D_Calculated<0 && (exp.charAt(lowerIndex)=='+')?"":"+";
        sb.insert(lowerIndex,signToBeInserted + calculated);
        exp = sb.toString();
        return exp;
    }
}
