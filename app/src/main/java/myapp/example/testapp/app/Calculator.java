package myapp.example.testapp.app;
import net.sourceforge.jeval.*;


/**
 * This is the class for the calculator's functionality and members.
 */
public class Calculator {
    private StructuredExpression currentStructuredExpression;
    private String lastResult;

    //state of calculator
    private Boolean startedTypingNumber;
    private Boolean startedBuildingExpression;

    /**
     * Evaluates the current expression in the calculator and returns a string representing the answer.
     */
    private String evaluateCurrentExpression(){
        Evaluator brain = new Evaluator();
        Double res = null;

        //attempt to evaluate -- if the expression is invalid, return null.
        try {
            res = brain.getNumberResult(currentStructuredExpression.toString());
        }catch(EvaluationException e){
            e.printStackTrace();
            lastResult = "Invalid Expression";
            return lastResult;
        }

        //if the result is an integer, trim off the '.0'
        //remember this as the last result
        if(res.equals(res.intValue())){
            String result = Integer.toString(res.intValue());
            lastResult = result;
            return(result);
        }else{
            lastResult = res.toString();
            return(res.toString());
        }
    }

    /**
     * Handles pressing of number keys (and '.').
     * If there is a decimal in the currentExpression already, ignore the button press.
     * Otherwise, append this number to the currentExpression.
     */
    public void pressNumber(String num){
        if(startedTypingNumber && currentStructuredExpression.currentlyTypingNumber()){
            currentStructuredExpression.updateLastOperand(num);
        }else{
            currentStructuredExpression.addExpressionBlock("operand",num);
            startedTypingNumber = true;
        }
    }

    public void pressOperation(String op){
        currentStructuredExpression.addExpressionBlock("operator",op);
        startedTypingNumber = false;
    }

    public void pressEquals(){
        String expressionToEvaluate = currentStructuredExpression.toString();
        System.out.println("Evaluating: "+expressionToEvaluate);
        lastResult = evaluateCurrentExpression();
        startedTypingNumber=false;
        startedBuildingExpression=false;
    }

    public void pressNegate(){
        if(currentStructuredExpression.currentlyTypingNumber()){
            currentStructuredExpression.negateOperand();
        }
    }

    public void pressBack(){
        currentStructuredExpression.handleBackspace();
    }

    public void pressClear(){
        currentStructuredExpression = new StructuredExpression();
        lastResult = null;
        startedTypingNumber=false;
        startedBuildingExpression=false;
    }

    /**
     * Get String values to populate UI
     */
    public String[] getStringValues(){
        String[] result = new String[2];
        result[0]=currentStructuredExpression.toString();
        result[1]=lastResult;
        return result;
    }


    /**
     * Default constructor -- current expression = 0, last result = null
     */
    public Calculator(){
        lastResult=null;
        startedBuildingExpression = false;
        startedTypingNumber = false;
        currentStructuredExpression = new StructuredExpression();
    }
}
