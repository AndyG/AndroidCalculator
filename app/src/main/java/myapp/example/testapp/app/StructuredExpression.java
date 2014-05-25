    package myapp.example.testapp.app;

/**
 * Created by root on 5/25/14.
 */

import java.util.ArrayList;

/**
 * Defines an "expression block" -- a single operator, a single number,
 * or a single parenthesis (open/close).
 */
class ExpressionBlock{
    public String blockType;
    public String value;

    public ExpressionBlock(String blockType, String value){
        this.blockType=blockType;
        this.value=value;
    }
}

public class StructuredExpression {
    private ArrayList<ExpressionBlock> expressionArray;

    public StructuredExpression(){
        expressionArray = new ArrayList<ExpressionBlock>();
    }


    public Boolean addExpressionBlock(String blockType, String value){

        ExpressionBlock lastBlock = null;
        if(!expressionArray.isEmpty()) {
            lastBlock = expressionArray.get(expressionArray.size() - 1);
        }

        //empty expressionArray, can only accept open parens and operands
        if(lastBlock==null && (blockType.equals("operand") || blockType.equals("openParen"))){
            expressionArray.add(new ExpressionBlock(blockType,value));
            return true;
        }
        //empty expressionArray, but got openParen
        else if(lastBlock==null && (blockType.equals("operator") || (blockType.equals("closeParen")))){
            System.out.println("Can't append "+blockType+" to empty expression array.");
            return false;
        }
        //nonempty expressionArray: handle operator
        if(blockType.equals("operator")){
            //putting in operator but the last ExpressionBlock was an operator.
            //replace last ExpressionBlock with the new operator
            if(lastBlock.blockType.equals("operator")){
                expressionArray.set(expressionArray.size() - 1, new ExpressionBlock(blockType, value));
                return true;
            }
            //putting in operator when last block was an operand.
            else if(lastBlock.blockType.equals("operand")){
                expressionArray.add(new ExpressionBlock(blockType,value));
                return true;
            }
        }
        else if(blockType.equals("operand")){
            //putting in operand, last block was an operator. append.
            if(lastBlock.blockType.equals("operator")){
                expressionArray.add(new ExpressionBlock(blockType,value));
                return true;
            }
            //putting in operand but the last ExpressionBlock was an operand.
            //put a multiplication between them.
            else if(lastBlock.blockType.equals("operand")){
                expressionArray.add(new ExpressionBlock("operand","*"));
                expressionArray.add(new ExpressionBlock(blockType, value));
                return true;
            }
        }

        //should never get here
        System.out.println("GOT TO BOTTOM OF ADDEXPRESSIONBLOCK");
        return false;
    }

    //run when user presses a number key and they have already begun typing a number.
    public Boolean updateLastOperand(String nextNum){
        if(expressionArray.size()==0){
            System.out.println("Attempting to update in empty expressionArray");
            return false;
        }

        ExpressionBlock lastBlock = expressionArray.get(expressionArray.size()-1);
        if(!lastBlock.blockType.equals("operand")){
            System.out.println("Attempting to update something that isn't an operand");
            return false;
        }

        if(lastBlock.blockType.equals("operand")){
            if(nextNum.equals(".") && lastBlock.value.contains(".")){
                System.out.println("Attempted to add a decimal point to an operand containing a decimal point.");
                return false;
            }

            lastBlock.value = lastBlock.value + nextNum;
            return true;
        }

        System.out.println("Reached end of updateLastOperand!");
        return false;
    }

    @Override
    public String toString(){
        StringBuffer buf = new StringBuffer();
        for(ExpressionBlock b : expressionArray){
            buf.append(b.value.toString());
        }
        return buf.toString();
    }
}