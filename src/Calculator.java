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
    boolean isRootSign(char c){
        return switch (c){
            case 'v','V','√' -> true;
            default -> false;
        };
    }
    boolean containsRootSign(String exp){
        for (int i = 0;i<exp.length();i++){
            if (isRootSign(exp.charAt(i)))
                return true;
        }
        return false;
    }
    boolean isPlusOrMinus(char c){
        return switch (c){
            case '+','-' -> true;
            default -> false;
            };
    }
    String Calculate(String exp){
        this.exp = exp;
        checkParenthesesSets(this.exp);
        double roundingNum = Math.pow(10,9);
        try {
            this.exp = calculateTheExpression(this.exp);
            if (Double.parseDouble(this.exp)!= Double.POSITIVE_INFINITY &&
                    Double.parseDouble(this.exp)!= Double.NEGATIVE_INFINITY &&
                    !Double.isNaN(Double.parseDouble(this.exp)))
                this.exp = String.valueOf(Math.round(Double.parseDouble(this.exp)*roundingNum)/roundingNum);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        if (this.exp.charAt(0) =='+')
            this.exp = this.exp.substring(1);
        if (this.exp.substring(this.exp.length()-2).equals(".0"))
            this.exp = this.exp.substring(0,this.exp.length()-2);

        return this.exp;
    }
    String calculateTheExpression(String exp){
        while (exp.contains("(")){
            exp = parentheses(exp);
        }
        exp = powerAndRoot(exp);
        exp = multiplyOrDivide(exp);
        exp = addAndSubtract(exp);
        return exp;
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
            boolean IsMultipliedByPrev = false;
            String innerExp = exp.substring(i+1, j);
            StringBuilder sb = new StringBuilder(exp);
            if (i>0 && isDigit(exp.charAt(i-1))) {
                IsMultipliedByPrev = true;
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

            StringBuilder result = new StringBuilder(calculateTheExpression(innerExp));
            sb.delete(i,innerExp.length()+i+2);
            if (i>0 && sb.charAt(i-1)=='+' && result.charAt(0)=='+')
                sb.insert(i,result.substring(1));
            else
                sb.insert(i,result);
            if (IsMultipliedByPrev) sb.insert(i,'*');
            if (i+result.length()<sb.length() && isDigit(sb.charAt(i+result.length()))) {
                sb.insert(i+result.length(),'*');
            }
            exp = sb.toString();
        }
        return exp;
    }
    String powerAndRoot(String exp){
        char power = '^';char squareRoot = 'v';
        exp = exceptionAssistant(power,squareRoot,exp);
        if (!exp.contains("^") && !containsRootSign(exp))
            return exp;
        return powerAndRoot(exp);
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
            else if (isPlusOrMinus(exp.charAt(i)) && passedFirstNum)
                passedFirstSign = true;
        }
        return false;
    }
    String addAndSubtract(String exp){
        char plus = '+';char minus='-';
        exp = exceptionAssistant(plus,minus,exp);

        // if length of exp is equal to 1 ,no need to check for '-' sign after the first index
        if (!hasTwoNumber(exp)){
            // here if we have more than one sign ('+' and '-') we manage them to have - if sum of - signs is odd
            int i=0;
            for (;i<exp.length();i++){
                if (isPlusOrMinus(exp.charAt(i))){
                    if (exp.charAt(i) =='-')
                        isNegative = !isNegative;
                }else break;
            }
            if (isNegative) {
                isNegative = false;
                return "-" + exp.substring(i);
            }
            return exp.substring(i);
        }
        return addAndSubtract(exp);
    }
    boolean isSignsSequenceCorrect(char s , char s2){
        return (s != '-' && s != '+' && !isRootSign(s) && s != '*' && s != '/') || (s2 != '*' && s2 != '/' && s2 != '^');
    }
    String exceptionAssistant(char a,char b,String exp){
        if((a=='*' || a =='/' || a=='^') && exp.charAt(0)==a)
            throw new UnKnownSignException("you can not type '"+exp.charAt(0)+"' at the beginning of the expression !");
        if (exp.contains(a+"") || exp.contains(b+"") || ((isRootSign(a) || isRootSign(b)))) {  // TODO -> contain 'V' ?
            int i;
            boolean passedDigit =false;
            for (i = 0; i < exp.length(); i++) {
                if ((exp.charAt(i)==a || exp.charAt(i)==b) && passedDigit)
                    break;
                if(isDigit(exp.charAt(i)))
                    passedDigit = true;
            }
            if (isRootSign(exp.charAt(0)))
                i = 0;
            if(i<exp.length()) {
                if (!isSignsSequenceCorrect(exp.charAt(i),exp.charAt(i+1)))
                    throw new UnKnownSignException("you can not type '" + exp.charAt(i) + "' directly before '" + exp.charAt(i + 1) + "' !");
                if (isRootSign(exp.charAt(i)) && isRootSign(exp.charAt(i+1)))
                    exp ="√"+ powerAndRoot(exp.substring(i+1));
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
        // for root sign we do not need previous number
        if(num.toString().isEmpty())
            previousNum = 0d;
        else
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
        if(exp.startsWith("Infinity", n)){
            num2.append("Infinity");
            ii += "Infinity".length();
        }
        nextNum = Double.parseDouble(num2.toString());
        if (isNegative) nextNum = -nextNum;
        StringBuilder sb = new StringBuilder(exp);
        sb.delete(lowerIndex,ii);
        String calculated;
        double D_Calculated=0;
        switch (sign){
            case '*' -> D_Calculated = (previousNum*nextNum);
            case '/' -> D_Calculated = (previousNum/nextNum);
            case '+' -> D_Calculated = (previousNum+nextNum);
            case '-' -> D_Calculated = (previousNum-nextNum);
            case '^' -> D_Calculated = Math.pow(previousNum,nextNum);
        }
        if (isRootSign(sign)){
            D_Calculated = Math.sqrt(nextNum);
            if (Double.isNaN(D_Calculated))
                throw new ArithmeticException("you can not take the root of a negative number !");
        }

        calculated = D_Calculated+"";
        String signToBeInserted = D_Calculated<0 && (exp.charAt(lowerIndex)=='+')?"":"+";
        sb.insert(lowerIndex,signToBeInserted + calculated);
        exp = sb.toString();
        return exp;
    }
}
