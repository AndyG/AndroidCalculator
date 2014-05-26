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
    private Integer openParensCount;

    public StructuredExpression(){
        expressionArray = new ArrayList<ExpressionBlock>();
        openParensCount=0;
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
        //empty expressionArray but got operator
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
            //putting in operator when last block was an open paren
            else if(lastBlock.blockType.equals("openParen")){
                return false;
            }
            //putting in operator when last block was a closeParen
            else if(lastBlock.blockType.equals("closeParen")){
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
            //putting in operand but the last ExpressionBlock was an openParen.
            else if(lastBlock.blockType.equals("openParen")){
                expressionArray.add(new ExpressionBlock(blockType,value));
                return true;
            }
            //putting in operand but the last ExpressionBlock was a closeParen.
            else if(lastBlock.blockType.equals("closeParen")){
                expressionArray.add(new ExpressionBlock("operator","*"));
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

    public Boolean negateOperand(){
        if(expressionArray.isEmpty()){
            System.out.println("can't negate empty expression");
            return false;
        }

        ExpressionBlock lastBlock = expressionArray.get(expressionArray.size()-1);
        if(lastBlock.blockType.equals("operand")){
            //multiply this operand by -1
            Double val = Double.parseDouble(removeParentheses(lastBlock.value));
            Double negatedVal = val*(-1);
            lastBlock.value = negatedVal.toString();
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

    @Override
    public String toString(){
        StringBuilder buf = new StringBuilder();
        for(ExpressionBlock b : expressionArray){
            buf.append(b.value);
        }
        return buf.toString();
    }

    //for use when I have parentheses
    public Boolean currentlyTypingNumber(){
        if(expressionArray.isEmpty()){
            return false;
        }

        ExpressionBlock lastBlock = expressionArray.get(expressionArray.size()-1);

        //if the last block is not an operand or is an operand and ends with ),
        //then we are not currently typing a number
        if(lastBlock.blockType!="operand"){
            return false;
        }else if(lastBlock.value.endsWith(")")){
            return false;
        }else{
            return true;
        }
    }

    public void handleBackspace(){
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
            expressionArray.remove(expressionArray.size()-1);
        }else{
            lastBlock.value=val;
            expressionArray.set(expressionArray.size()-1,lastBlock);
        }
    }

    public Boolean handleParens(){
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
        }else if(lastBlockType.equals("operand")||lastBlockType.equals("closeParen")){
            if(openParensCount>0){
                expressionArray.add(new ExpressionBlock("closeParen",")"));
                openParensCount--;
            }else{
                expressionArray.add(new ExpressionBlock("operator","*"));
                expressionArray.add(new ExpressionBlock("openParen","("));
                openParensCount++;
            }
            return true;
        }else{
            System.out.println("REACHED END OF HANDLEPARENS");
            return false;
        }
    }
}
