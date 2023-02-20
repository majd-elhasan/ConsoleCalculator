public class Calculator {
    String expression ;
    Double previousNum ;
    Double nextNum;
    boolean isNegative = false;

    boolean isNumber(char c){
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
    String Calculate(String expression){
        this.expression = expression;
        while (this.expression.contains("*") || this.expression.contains("/") || this.expression.contains("+")){
            // 1. parentheses
            // 2. powers and roots
            this.expression = multiplyOrDivide(expression); // 3.
            // 4. addition and subtract
        }
        return this.expression;
    }
    String multiplyOrDivide(String expression){
        if (expression.contains("*") || expression.contains("/")){
            int i = expression.indexOf('*');
            int j = expression.indexOf("/");
            if (i!=-1 && (i<j || j==-1 )){
                if (i ==0)
                    throw new UnKnownSignException("you can not type '*' at the beginning of the expression !");
                if(isSign(expression.charAt(i-1)))
                    throw new UnKnownSignException("you can not type '"+ expression.charAt(i-1) +"' directly before '*' !");
                multiplyOrDivide('*',i);
            }else if (j == 0) throw new UnKnownSignException("you can not type '/' at the beginning of the expression !");
            else if (j>0) {
                if(isSign(expression.charAt(j-1)))
                    throw new UnKnownSignException("you can not type '"+ expression.charAt(j-1) +"' directly before '/' !");
                multiplyOrDivide('/',j);
            }
        }
        if (!this.expression.contains("*") && !this.expression.contains("/"))
            return this.expression;
        return multiplyOrDivide(this.expression);
    }
    void multiplyOrDivide(char sign ,int indicator){
        int p;
        for (p = indicator-1;p>0;p--){
            if (!isNumber(expression.charAt(p)) && expression.charAt(p) != '.')
                break;
        }
        StringBuilder num= new StringBuilder();
        for (int n=p;n<expression.length();n++){
            if (isNumber(expression.charAt(n) ) || expression.charAt(n) == '.')
                num.append(expression.charAt(n));
            else if(n==p) continue;
            else break;
        }
        if (num.toString().contains(".".repeat(2))) throw new RuntimeException("the number should not have two dots in it !");
        previousNum = Double.parseDouble(num.toString());
        while (true) {
            if (p==-1) {p++;break;}
            if (expression.charAt(p) !='-') break;
            else {isNegative = !isNegative;p--;}
        }
        int lowerIndex = p;
        if (isNegative) previousNum = -previousNum;
        // return isNegative to false for using it later.
        isNegative = false;
        //  until here we got the number before the * or / sign and assigned the negativity if exists .
        int n ;
        for (n =indicator+1 ;n<expression.length();n++){
            if (expression.charAt(n)=='-')
                isNegative = !isNegative;
            else break;
        }
        StringBuilder stringBuilder = new StringBuilder();
        int ii;
        for ( ii = n;ii<expression.length();ii++){
            if (!isNumber(expression.charAt(ii)) && expression.charAt(ii) != '.')
                break;
            else stringBuilder.append(expression.charAt(ii));
        }
        nextNum = Double.parseDouble(stringBuilder.toString());
        StringBuilder sb = new StringBuilder(expression);
        sb.delete(lowerIndex,ii);
        String calculated="";
        if (sign =='*')
            calculated = (previousNum*nextNum)+"";
        else if (sign =='/')
            calculated = (previousNum/nextNum)+"";

        sb.insert(lowerIndex,calculated);
        expression = sb.toString();
    }
}
