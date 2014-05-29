package myapp.example.testapp.andycalc;
import android.os.Parcel;
import android.os.Parcelable;

import net.sourceforge.jeval.*;


/**
 * This is the class for the calculator's functionality and members.
 */
public class Calculator implements Parcelable {


    private String lastResult;
    private Integer resultDisplayState = 0;//0 for none, 1 for invalid, 2 for valid
    private StructuredExpression currentStructuredExpression;

    /**
     * Evaluates the current expression in the calculator and returns a string representing the answer.
     */
    private String evaluateCurrentExpression(){
        Evaluator brain = new Evaluator();
        Double res = null;

        //attempt to evaluate -- if the expression is invalid, return null.
        try {
            //first, close all parens in the expression
            currentStructuredExpression.closeParens();
            res = brain.getNumberResult(currentStructuredExpression.toString());
        }catch(EvaluationException e){
            e.printStackTrace();
            lastResult = "Invalid Expression";
            resultDisplayState = 1;
            return lastResult;
        }

        //catch NaN
        if(res.toString().contains("NaN")){
            resultDisplayState=1;
            lastResult="NaN";
            return(res.toString());
        }

        //if the result is an integer, trim off the '.0'
        //remember this as the last result
        if(res.doubleValue()==res.intValue()){
            resultDisplayState = 2;
            String result = Integer.toString(res.intValue());
            lastResult = result;
            return("="+result);
        }else{
            resultDisplayState = 2;
            lastResult = truncate(res.toString());
            return("="+res.toString());
        }
    }

    /**
     * Handles pressing of number keys (and '.').
     * If there is a decimal in the currentExpression already or the operand is too long, ignore the button press.
     * Otherwise, append this number to the currentExpression.
     */
    public String pressNumber(String num){
        //create new expression if user types specialOperand after evaluation
        if(resultDisplayState==2){
            currentStructuredExpression = new StructuredExpression();
        }
        resultDisplayState = 0;
        //get string response from current operand
        //ensures we don't make an operand too long.
        String response = currentStructuredExpression.operandState();
        if(response==null) {
            currentStructuredExpression.handleOperand(num);
            return null;
        }else{
            //return to MainActivity the code from operandState()
            return response;
        }
    }

    public void pressOperation(String op){
        //if we are displaying a result and press an operator, use the
        //old result as the new operand
        if(resultDisplayState==2){
            currentStructuredExpression = new StructuredExpression();
            currentStructuredExpression.addExpressionBlock("operand",lastResult);
        }
        currentStructuredExpression.handleOperator(op);
        resultDisplayState=0;
    }

    public void pressParens(){
        currentStructuredExpression.handleParens();
        resultDisplayState=0;
    }

    public void pressFunction(String functionText){
        //if we are displaying a result and press a function, use the
        //old result as a multiplicand with this function
        if(resultDisplayState==2){
            currentStructuredExpression = new StructuredExpression();
            currentStructuredExpression.addExpressionBlock("operand",lastResult);
        }
        currentStructuredExpression.handleFunction(functionText);
        resultDisplayState=0;
    }

    public void pressEquals(){
        String expressionToEvaluate = currentStructuredExpression.toString();
        System.out.println("Evaluating: "+expressionToEvaluate);
        evaluateCurrentExpression();
    }


    public void pressNegate(){
        //if we are displaying a result and press negate, use the
        //old result as a the thing we are negating
        if(resultDisplayState==2){
            currentStructuredExpression = new StructuredExpression();
            currentStructuredExpression.addExpressionBlock("operand", lastResult);
        }
        currentStructuredExpression.negateOperand();
        resultDisplayState=0;
    }

    //expand to handle other specialOperands later (constants)
    public void pressSpecialOperand(String opText){
        //create new expression if user types specialOperand after evaluation
        if(resultDisplayState==2){
            currentStructuredExpression = new StructuredExpression();
        }
        resultDisplayState = 0;
        if(opText.equals("π")){
            currentStructuredExpression.handleSpecialOperand("#{PI}");
        }else{
            System.out.println("Unhandled special operand: "+opText);
        }
    }

    public void pressBack(){
        resultDisplayState=0;
        currentStructuredExpression.handleBackspace();
    }

    public void pressClear(){
        currentStructuredExpression = new StructuredExpression();
        lastResult = null;
        resultDisplayState=0;
    }

    /**
     * Get String values to populate UI
     */
    public String[] getStringValues(){
        String[] result = new String[3];
        result[0]=currentStructuredExpression.toString();
        result[1]=lastResult;
        result[2]=(resultDisplayState.toString());
        return result;
    }

    /**
     * Default constructor -- current expression = 0, last result = null
     */
    public Calculator(){
        resultDisplayState=0;
        lastResult=null;
        currentStructuredExpression = new StructuredExpression();
    }

    //required by Parcelable
    public int describeContents(){
       return 0;
    }

    public void writeToParcel(Parcel out, int flags)
    {
        System.out.println("Writing to parcel. resultDisplayState: "+resultDisplayState);
        out.writeString(lastResult);
        out.writeInt(resultDisplayState);
        out.writeStringArray(currentStructuredExpression.toStringArray());
    }


    public class MyCreator implements Parcelable.Creator<Calculator> {
        public Calculator createFromParcel(Parcel source) {
            return new Calculator(source);
        }
        public Calculator[] newArray(int size) {
            return new Calculator[size];
        }
    }
    /**
     * This will be used only by the MyCreator
     * @param source
     */
    public Calculator(Parcel source){
        /*
         * Reconstruct from the Parcel
         */
        lastResult = source.readString();
        resultDisplayState=source.readInt();
        String [] structuredExprArray = source.createStringArray();
        currentStructuredExpression = new StructuredExpression(structuredExprArray);
        System.out.println("Read from parcel. resultDisplayState: "+resultDisplayState);
    }



    //truncate AFTER decimal on non-scientific notation input
    //this function is here instead of MainActivity so the truncated values can be used as the operands
    //to subsequent expressions
    private String truncate(String in){
        //infinity or large integer, don't want to truncate
        if(in.contains("Infinity") || !in.contains(".")){
            return(in);
        }

        //scientific notation, move "E" back to 5 points after decimal
        if(in.contains("E")){
            //if somehow the input does not contain a decimal, return in. that shouldn't happen.
            if(!in.contains(".")){
                return(in);
            }

            Integer decimalIndex = in.indexOf(".");
            Integer E_Index = in.indexOf("E");

            //want to put E 4 points after decimal.
            if(E_Index-decimalIndex<=4){
                return(in);
            }

            //E is more than 4 points away from the decimal. Put it exactly 4 points away.
            String resultString = in.substring(0,decimalIndex+4);
            resultString = resultString+in.substring(E_Index);
            return(resultString);
        }

        //in contains "." and isn't scientific notation

        String[] split_in = in.split("\\.");
        System.out.println(split_in.length);
        try {
            String pre_decimal = split_in[0];
            String post_decimal = split_in[1];
            if(post_decimal.length()>6){
                return(pre_decimal+"."+post_decimal.substring(0,6));
            }else{
                return(in);
            }
        }catch(Exception e){
            e.printStackTrace();
            return(in);
        }

    }

}