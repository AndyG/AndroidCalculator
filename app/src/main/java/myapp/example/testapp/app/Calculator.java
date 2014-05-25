package myapp.example.testapp.app;
import net.sourceforge.jeval.*;


/**
 * This is the class for the calculator's functionality and members.
 */
public class Calculator {
    private String currentExpression;
    private String currentOperand;
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
            res = brain.getNumberResult(currentExpression);
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
        if(num.equals(".")){
            if(currentOperand.contains(".")){
                System.out.println("Pressed decimal point when there was already a decimal in the current operand.");
                return;
            }
        }

        if(!startedTypingNumber){
            if(num.equals(".")){
                currentOperand = ("0.");
            }else{
                currentOperand=num;
            }
            startedTypingNumber=true;
        }else{
            currentOperand=currentOperand+num;
        }
    }

    public void pressOperation(String op){
        if(startedTypingNumber){
            //if there is no current expression, set the current expression to the current operand and the selected operator.
            if(!startedBuildingExpression){
                currentExpression = currentOperand+op;
                currentOperand="0";
                startedTypingNumber=false;
                startedBuildingExpression=true;
            }
            //if there IS a current expression, we append current operand and chosen operator to it.
            else {
                currentExpression = currentExpression + currentOperand + op;
                currentOperand = "0";
                startedTypingNumber = false;
                startedBuildingExpression = true;
            }
        }
        //if we have not started typing a number but have a previous result, set current expression to previous result plus op
        else if(lastResult!=null){
            currentExpression = lastResult+op;
            startedBuildingExpression=true;
            startedTypingNumber = false;
            currentOperand = "0";
        }
        //have no previous result AND have no current operand
        else{
            System.out.println("Typed operator before a number was available.");
        }
    }

    public void pressEquals(){
        //if we have started typing a number and we have an expression, append it to the current expression
        if(startedTypingNumber && startedBuildingExpression){
            currentExpression = currentExpression + currentOperand;
        }
        //if we have started typing a number and we haven't started building an expression, set expression to operand
        else if(startedTypingNumber){
            currentExpression = currentOperand;
        }


        System.out.println("Evaluating: "+currentExpression);
        lastResult = evaluateCurrentExpression();
        startedTypingNumber=false;
        currentOperand = "0";
        startedBuildingExpression=false;
    }

    public void pressBack(){
        if(!currentOperand.equals("0") && !currentOperand.equals("")){
            String newText = currentOperand.substring(0,currentOperand.length()-1);
            if(newText.length()==0) {
                currentOperand="0";
                startedTypingNumber=false;
            }else{
                currentOperand=newText;
                startedTypingNumber=true;
            }
        }
    }

    public void pressClear(){
        currentExpression = "0";
        currentOperand = "0";
        startedTypingNumber = false;
        startedBuildingExpression = false;
        lastResult = null;
    }

    /**
     * Get String values to populate UI
     */
    public String[] getStringValues(){
        String[] result = new String[3];
        result[0]=currentOperand;
        result[1]=currentExpression;
        result[2]=lastResult;
        return result;
    }


    /**
     * Default constructor -- current expression = 0, last result = null
     */
    public Calculator(){
        currentExpression = null;
        lastResult=null;
        currentOperand="0";
        startedBuildingExpression = false;
        startedTypingNumber = false;
    }
}
