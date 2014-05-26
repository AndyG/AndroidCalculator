package myapp.example.testapp.app;
import android.os.Parcel;
import android.os.Parcelable;

import net.sourceforge.jeval.*;


/**
 * This is the class for the calculator's functionality and members.
 */
public class Calculator implements Parcelable {

    private String lastResult;
    private Boolean displayingSuccessfulResult;
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
            displayingSuccessfulResult = false;
            return lastResult;
        }

        //if the result is an integer, trim off the '.0'
        //remember this as the last result
        if(res.doubleValue()==res.intValue()){
            displayingSuccessfulResult = true;
            String result = Integer.toString(res.intValue());
            lastResult = result;
            return("="+result);
        }else{
            displayingSuccessfulResult = true;
            lastResult = res.toString();
            return("="+res.toString());
        }
    }

    /**
     * Handles pressing of number keys (and '.').
     * If there is a decimal in the currentExpression already, ignore the button press.
     * Otherwise, append this number to the currentExpression.
     */
    public void pressNumber(String num){
        if(displayingSuccessfulResult){
            currentStructuredExpression = new StructuredExpression();
        }
        if(currentStructuredExpression.currentlyTypingNumber()){
            currentStructuredExpression.updateLastOperand(num);
        }else{
            currentStructuredExpression.addExpressionBlock("operand",num);
        }
        displayingSuccessfulResult = false;
    }

    public void pressOperation(String op){
        //if we are displaying a result and press an operator, use the
        //old result as the new operand
        if(displayingSuccessfulResult){
            currentStructuredExpression = new StructuredExpression();
            currentStructuredExpression.addExpressionBlock("operand",lastResult);
        }
        currentStructuredExpression.addExpressionBlock("operator",op);
        displayingSuccessfulResult = false;
    }

    public void pressParens(){
        currentStructuredExpression.handleParens();
        displayingSuccessfulResult = false;
    }

    public void pressEquals(){
        String expressionToEvaluate = currentStructuredExpression.toString();
        System.out.println("Evaluating: "+expressionToEvaluate);
        evaluateCurrentExpression();
    }

    public void pressNegate(){
        if(displayingSuccessfulResult){
            currentStructuredExpression = new StructuredExpression();
            currentStructuredExpression.addExpressionBlock("operand", lastResult);
        }
        if(currentStructuredExpression.currentlyTypingNumber()){
            currentStructuredExpression.negateOperand();
        }
        displayingSuccessfulResult = false;
    }

    public void pressBack(){
        displayingSuccessfulResult = false;
        currentStructuredExpression.handleBackspace();
    }

    public void pressClear(){
        currentStructuredExpression = new StructuredExpression();
        lastResult = null;
        displayingSuccessfulResult = false;
    }

    /**
     * Get String values to populate UI
     */
    public String[] getStringValues(){
        String[] result = new String[3];
        result[0]=currentStructuredExpression.toString();
        result[1]=lastResult;
        result[2]=(displayingSuccessfulResult ? "1" : null);
        return result;
    }


    /**
     * Default constructor -- current expression = 0, last result = null
     */
    public Calculator(){
        displayingSuccessfulResult = false;
        lastResult=null;
        currentStructuredExpression = new StructuredExpression();
    }

    //required by Parcelable
    public int describeContents(){
        return 0;
    }

    public void writeToParcel(Parcel out, int flags)
    {
        out.writeString(lastResult);
        out.writeInt(displayingSuccessfulResult ? 1 : 0);
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
        displayingSuccessfulResult = (source.readInt()==1);
        String [] structuredExprArray = source.createStringArray();
        currentStructuredExpression = new StructuredExpression(structuredExprArray);
    }
}