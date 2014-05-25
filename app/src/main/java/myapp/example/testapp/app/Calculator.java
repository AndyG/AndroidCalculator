package myapp.example.testapp.app;
import net.sourceforge.jeval.*;


/**
 * This is the class for the calculator's functionality and members.
 */
public class Calculator {
    private String currentExpression;
    private String lastResult;

    /**
     * Evaluates the current expression in the calculator and returns a string representing the answer.
     */
    public String evaluateExpression(){
        Evaluator brain = new Evaluator();
        Double res = null;

        //attempt to evaluate -- if the expression is invalid, return null.
        try {
            res = brain.getNumberResult(currentExpression);
        }catch(EvaluationException e){
            e.printStackTrace();
            lastResult = null;
            return null;
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
}
