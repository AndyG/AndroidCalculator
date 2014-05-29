package myapp.example.testapp.app;

/**
 * Created by root on 5/25/14.
 */


import java.util.ArrayList;

/**
 * Defines an "expression block".
 * blockType will at runtime be one of five values:
 * -openParen
 * -closeParen
 * -operator
 * -operand
 * -specialOperand (constant like PI)
 * -function (sin, cos, etc.)
 *
 *
 *
 * the blockType of the latest block will be used to decide how to handle each button press.
 */
class ExpressionBlock{
    public String blockType;
    public String value;


    public ExpressionBlock(String blockType, String value){
        this.blockType=blockType;
        this.value=value;
    }
}

//TODO: add addOpenParen, addCloseParen functions instead of having to manually manage openParensCount
public class StructuredExpression {
    private ArrayList<ExpressionBlock> expressionArray;
    private Integer openParensCount;

    public StructuredExpression(){
        expressionArray = new ArrayList<ExpressionBlock>();
        openParensCount=0;
    }


    //this is the only way other classes can access the expressionArray.
    //calls to this function are only made when the Calculator needs add the last result as a new
    //operand. this function will soon be deprecated.
    public Boolean addExpressionBlock(String blockType, String value){
        ExpressionBlock lastBlock = null;
        if(!expressionArray.isEmpty()) {
            lastBlock = expressionArray.get(expressionArray.size() - 1);
        }

        //empty expressionArray, can only accept operands.
        if(lastBlock==null && blockType.equals("operand")){
            expressionArray.add(new ExpressionBlock(blockType,value));
            return true;
        }else{
            System.out.println("GOT TO BOTTOM OF ADDEXPRESSIONBLOCK");
            return false;
        }
    }


    //TODO: consider, is it really necessary to multiply for normal operands either?
    //decides whether the thing to negate is a specialOperand or operand and proceeds accordingly.
    public Boolean negateOperand(){
        if(expressionArray.isEmpty()){
            System.out.println("can't negate empty expression");
            return false;
        }
        ExpressionBlock lastBlock = expressionArray.get(expressionArray.size()-1);

        //in negating a specialOperand, we don't want to actually multiply -- prepending or removing
        //the negative sign is good enough.
        if(lastBlock.blockType.equals("specialOperand")){
            String val = lastBlock.value;
            if(val.startsWith("-")){
                val = val.substring(1);
            }else{
                val = "-"+val;
            }
            lastBlock.value = val;
            expressionArray.set(expressionArray.size()-1,lastBlock);
            return true;
        }else if(lastBlock.blockType.equals("operand")){
            //multiply this operand by -1
            Double val = null;
            try {
                //this call to removeParentheses is not necessary at this point.
                //TODO: remove after stable release (interview :))
                val = Double.parseDouble(removeParentheses(lastBlock.value));
            }catch(NumberFormatException e){
                //failed to convert operand to double, return false
                e.printStackTrace();
                return false;
            }
            Double negatedVal = val*(-1);

            //this just keeps "1" from turning into "-1.0"
            if(negatedVal.doubleValue()==negatedVal.intValue()){
                lastBlock.value = Integer.toString(negatedVal.intValue());
            }else{
                lastBlock.value = negatedVal.toString();
            }
            expressionArray.set(expressionArray.size()-1,lastBlock);
            return true;
        }else{
            System.out.println("Can't negate non-operand.");
            return false;
        }
    }

    private String removeParentheses(String s){
        s = s.replace("(","");
        return(s.replace(")",""));
    }


    public Boolean handleOperand(String operandText){
        if(expressionArray.isEmpty()){
            expressionArray.add(new ExpressionBlock("operand",operandText));
            return true;
        }

        //nonempty expressionArray
        //handle lastBlock cases: operand, operator, openParen, closeParen, function
        ExpressionBlock lastBlock = expressionArray.get(expressionArray.size()-1);
        String lastBlockType = lastBlock.blockType;

        //putting in operand, last block was an operator. append.
        if(lastBlockType.equals("operator")){
            expressionArray.add(new ExpressionBlock("operand",operandText));
            return true;
        }
        //putting in operand but the last ExpressionBlock was an openParen.
        else if(lastBlockType.equals("openParen")){
            expressionArray.add(new ExpressionBlock("operand",operandText));
            return true;
        }
        //putting in operand but the last ExpressionBlock was a closeParen.
        else if(lastBlockType.equals("closeParen")){
            expressionArray.add(new ExpressionBlock("operator","*"));
            expressionArray.add(new ExpressionBlock("operand",operandText));
            return true;
        }
        //putting in operand but the last ExpressionBlock was an operand.
        //append this operand to that operand
        //handle case where typed decimal but the operand already has a decimal
        else if(lastBlockType.equals("operand")){
            if(operandText.equals(".") && lastBlock.value.contains(".")){
                return false;
            }
            //can append
            lastBlock.value = lastBlock.value+operandText;
            expressionArray.set(expressionArray.size()-1,lastBlock);
            return true;
        }
        //putting in operand, last block was a function
        else if(lastBlockType.equals("function")){
            expressionArray.add(new ExpressionBlock("openParen","("));
            openParensCount++;
            expressionArray.add(new ExpressionBlock("operand", operandText));
            return true;
        }else if(lastBlockType.equals("specialOperand")){
            expressionArray.add(new ExpressionBlock("operator","*"));
            expressionArray.add(new ExpressionBlock("operand",operandText));
            return true;
        }
        //shouldn't get down here!
        else{
            System.out.println("Somehow reached end of handleOperand!");
            return false;
        }
    }

    //user pressed operator
    //handle last block cases: empty, operator, operand, open paren, close paren, function, specialOperand
    public Boolean handleOperator(String operatorText){
        //handle empty case
        if(expressionArray.isEmpty()){
            //do nothing
            return false;
        }

        //nonempty expression array

        String lastBlockType = expressionArray.get(expressionArray.size()-1).blockType;


        //putting in operator but the last ExpressionBlock was an operator.
        //replace last ExpressionBlock with the new operator
        if(lastBlockType.equals("operator")){
            expressionArray.set(expressionArray.size() - 1, new ExpressionBlock("operator", operatorText));
            return true;
        }
        //putting in operator when last block was an operand.
        else if(lastBlockType.equals("operand")){
            expressionArray.add(new ExpressionBlock("operator",operatorText));
            return true;
        }
        //putting in operator when last block was an open paren. do nothing
        else if(lastBlockType.equals("openParen")){
            return false;
        }
        //putting in operator when last block was a closeParen
        else if(lastBlockType.equals("closeParen")){
            expressionArray.add(new ExpressionBlock("operator",operatorText));
            return true;
        }
        //putting in an operator when last block was a function. do nothing (for now?)
        else if(lastBlockType.equals("function")){
            return false;
        }
        //operator -- last block was specialOperand
        else if(lastBlockType.equals("specialOperand")){
            expressionArray.add(new ExpressionBlock("operator",operatorText));
            return true;
        }
        //shouldn't get here
        else{
            System.out.println("Somehow got to end of handleOperator.");
            return false;
        }
    }


    public void handleBackspace(){
        //empty case, backspace does nothing
        if(expressionArray.isEmpty()){
            return;
        }
        ExpressionBlock lastBlock = expressionArray.get(expressionArray.size()-1);

        String val = lastBlock.value;
        System.out.println("Before backspace: "+val);
        val = val.substring(0,val.length()-1);
        System.out.println("After backspace: "+val);
        if(val.length()==0){
            if(lastBlock.blockType.equals("closeParen")){
                openParensCount++;
            }
            if(lastBlock.blockType.equals("openParen")){
                openParensCount--;
            }
            expressionArray.remove(expressionArray.size() - 1);
        }
        //get rid of whole function if we're backspacing on a function
        else if(lastBlock.blockType.equals("function")){
            expressionArray.remove(expressionArray.size()-1);
        }
        //backspaced on operand and just have negative sign left, get rid of whole operand
        else if(lastBlock.blockType.equals("operand") && val.equals("-")){
            expressionArray.remove(expressionArray.size() - 1);
        }
        //get rid of entire specialOperand (including negative sign, if it's there)
        else if(lastBlock.blockType.equals("specialOperand")){
            expressionArray.remove(expressionArray.size() - 1);
        }
        else{
            lastBlock.value=val;
            expressionArray.set(expressionArray.size()-1,lastBlock);
        }
    }

    public Boolean handleFunction(String functionText){

        //empty case, add the function
        if(expressionArray.isEmpty()){
            expressionArray.add(new ExpressionBlock("function",functionText));
            expressionArray.add(new ExpressionBlock("openParen","("));
            openParensCount++;
            return true;
        }

        //expression array not empty
        ExpressionBlock lastBlock = expressionArray.get(expressionArray.size()-1);
        String lastBlockType = lastBlock.blockType;

        if(lastBlockType.equals("openParen")){
            expressionArray.add(new ExpressionBlock("function",functionText));
            expressionArray.add(new ExpressionBlock("openParen","("));
            openParensCount++;
            return true;
        }else if(lastBlockType.equals("operator")){
            expressionArray.add(new ExpressionBlock("function",functionText));
            expressionArray.add(new ExpressionBlock("openParen","("));
            openParensCount++;
            return true;
        }else if(lastBlockType.equals("operand")||lastBlockType.equals("closeParen")){
            expressionArray.add(new ExpressionBlock("operator","*"));
            expressionArray.add(new ExpressionBlock("function",functionText));
            expressionArray.add(new ExpressionBlock("openParen","("));
            openParensCount++;
            return true;
        }else if(lastBlockType.equals("function")){
            expressionArray.add(new ExpressionBlock("openParen","("));
            expressionArray.add(new ExpressionBlock("function",functionText));
            expressionArray.add(new ExpressionBlock("openParen","("));
            openParensCount+=2;
            return true;
        }else if(lastBlockType.equals("specialOperand")) {
            expressionArray.add(new ExpressionBlock("operator","*"));
            expressionArray.add(new ExpressionBlock("function",functionText));
            expressionArray.add(new ExpressionBlock("openParen","("));
            openParensCount++;
            return true;
        }
        else{
            System.out.println("REACHED END OF HANDLEPARENS");
            return false;
        }
    }

    public Boolean handleSpecialOperand(String opText){

        //empty case
        if(expressionArray.isEmpty()){
            expressionArray.add(new ExpressionBlock("specialOperand",opText));
            return true;
        }

        //expression array not empty
        ExpressionBlock lastBlock = expressionArray.get(expressionArray.size()-1);
        String lastBlockType = lastBlock.blockType;

        if(lastBlockType.equals("openParen")){
            expressionArray.add(new ExpressionBlock("specialOperand",opText));
            return true;
        }else if(lastBlockType.equals("operator")){
            expressionArray.add(new ExpressionBlock("specialOperand",opText));
            return true;
        }else if(lastBlockType.equals("operand")||lastBlockType.equals("closeParen")){
            expressionArray.add(new ExpressionBlock("operator","*"));
            expressionArray.add(new ExpressionBlock("specialOperand",opText));
            return true;
        }else if(lastBlockType.equals("function")){
            expressionArray.add(new ExpressionBlock("openParen","("));
            expressionArray.add(new ExpressionBlock("specialOperand",opText));
            openParensCount++;
            return true;
        }else if(lastBlockType.equals("specialOperand")) {
            expressionArray.add(new ExpressionBlock("operator","*"));
            expressionArray.add(new ExpressionBlock("specialOperand",opText));
            return true;
        }
        else{
            System.out.println("REACHED END OF HANDLESPECIALOPERAND");
            return false;
        }
    }

    public Boolean handleParens(){
        //empty case
        if(expressionArray.isEmpty()){
            expressionArray.add(new ExpressionBlock("openParen","("));
            openParensCount++;
            return true;
        }

        //expression array not empty
        ExpressionBlock lastBlock = expressionArray.get(expressionArray.size()-1);
        String lastBlockType = lastBlock.blockType;

        if(lastBlockType.equals("openParen")){
            expressionArray.add(new ExpressionBlock("openParen","("));
            openParensCount++;
            return true;
        }else if(lastBlockType.equals("operator")){
            expressionArray.add(new ExpressionBlock("openParen","("));
            openParensCount++;
            return true;
        }
        //on operands, operators, and specialOperands, if there is already an open paren, close it.
        //otherwise, create a new open parenthesis with a multiplication operator preceding it.
        else if(lastBlockType.equals("operand")||lastBlockType.equals("closeParen")||lastBlockType.equals("specialOperand")){
            if(openParensCount>0){
                expressionArray.add(new ExpressionBlock("closeParen",")"));
                openParensCount--;
            }else{
                expressionArray.add(new ExpressionBlock("operator","*"));
                expressionArray.add(new ExpressionBlock("openParen","("));
                openParensCount++;
            }
            return true;
        }else if(lastBlockType.equals("function")){
            expressionArray.add(new ExpressionBlock("openParen","("));
            openParensCount++;
            return true;
        }
        else{
            System.out.println("REACHED END OF HANDLEPARENS");
            return false;
        }
    }

    //closes all open parentheses if need be. called when equals sign pressed.
    public void closeParens(){
        while(openParensCount!=0){
            expressionArray.add(new ExpressionBlock("closeParen",")"));
            openParensCount--;
        }
    }

    //simple function which turns the expressionArray into a human-readable mathematical expression.
    @Override
    public String toString(){
        StringBuilder buf = new StringBuilder();
        for(int i=0;i<expressionArray.size();i++){
            ExpressionBlock b = expressionArray.get(i);
            if(i!=expressionArray.size()-1 && b.blockType.equals("operand")){
                b.value = formatDecimalOperand(b.value);
                expressionArray.set(i,b);
            }
            buf.append(b.value);
        }
        return buf.toString();
    }

    //returns as string array
    public String[] toStringArray(){
        //get length
        Integer arrayLen = expressionArray.size()*2;
        String [] res = new String[arrayLen];
        for(int i=0;i<expressionArray.size();i++){
            res[i]=expressionArray.get(i).blockType;
            res[i+1]=expressionArray.get(i).value;
        }
        return res;
    }

    //create StructuredExpression from string array
    //for use when creating StructuredExpression from Calculator Parcel
    public StructuredExpression(String[] src){
            expressionArray = new ArrayList<ExpressionBlock>();
            openParensCount=0;
            for(int i=0;i<src.length;i+=2){
                expressionArray.add(new ExpressionBlock(src[i],src[i+1]));
                if(src[i]=="openParen"){
                    openParensCount++;
                }
                if(src[i]=="closeParen"){
                    openParensCount--;
                }
            }
    }

    //fix operands which have either nothing 0 pre-decimal point or nothing after the decimal point
    //this is what makes something like ".+" turn into "0.0+"
    private String formatDecimalOperand(String num) {
        //no decimal, no problem
        if (!num.contains(".")) {
            return num;
        }

        if (num.equals(".")) {
            return "0.0";
        }

        //split string on decimal. length 1 implies no post-decimal info.
        //length 2 implies either no pre-decimal info or info on both sides (hence test for emptiness of left side).
        String[] splitNum = num.split("\\.");

        if (splitNum.length == 1) {
            return (num + "0");
        } else if (splitNum.length == 2) {
            if(splitNum[0].isEmpty())
                return ("0" + num);
            else
                return(num);
        }
        //we should never get here, but just in case...
        else{
            return(num);
        }
    }

    //returns null if it's okay to press an operand.
    //otherwise, returns a message saying the operand is too long or has too many after-decimal digits
    public String operandState(){
        if(expressionArray.isEmpty()){
            return null;
        }

        //nonempty expression array
        ExpressionBlock lastBlock = expressionArray.get(expressionArray.size()-1);
        //not typing an operand, we're fine
        if(!lastBlock.blockType.equals("operand")){
            return null;
        }

        //typing an operand
        String operandValue = lastBlock.value;
        if(operandValue.length()>=10){
            return("Current operand at maximum length.");
        }else if(getDigitsAfterDecimal(operandValue) >=7){
            return("Current operand has too many digits after decimal.");
        }else{
            return null;
        }
    }

    private Integer getDigitsAfterDecimal(String operandValue){
        //no decimal = no digits after decimal
        if(!operandValue.contains(".")){
            return 0;
        }
        int indexOfDecimal = operandValue.indexOf(".");
        return((operandValue.length()-indexOfDecimal)-1);
    }
}