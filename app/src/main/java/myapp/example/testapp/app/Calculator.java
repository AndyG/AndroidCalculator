package myapp.example.testapp.app;
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

        //if the result is an integer, trim off the '.0'
        //remember this as the last result
        if(res.doubleValue()==res.intValue()){
            resultDisplayState = 2;
            String result = Integer.toString(res.intValue());
            lastResult = result;
            return("="+result);
        }else{
            resultDisplayState = 2;
            lastResult = res.toString();
            return("="+res.toString());
        }
    }

    /**
     * Handles pressing of number keys (and '.').
     * If there is a decimal in the currentExpression already, ignore the button press.
     * Otherwise, append this number to the currentExpression.
     */
    public String pressNumber(String num){
        if(resultDisplayState==2){
            currentStructuredExpression = new StructuredExpression();
        }
        resultDisplayState = 0;
        //get string response from current operand
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
        if(resultDisplayState==2){
            currentStructuredExpression = new StructuredExpression();
            currentStructuredExpression.addExpressionBlock("operand", lastResult);
        }
        if(currentStructuredExpression.currentlyTypingNumber()){
            currentStructuredExpression.negateOperand();
        }
        resultDisplayState=0;
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
}