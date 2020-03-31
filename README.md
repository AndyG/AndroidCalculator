AndroidCalculator
=================

This is a basic scientific calculator for modern Android phones and tablets.

The meat of the code is in the three files MainActivity.java, Calculator.java, and StructuredExpression.java.

I stuck to a Model-View-Controller architecture for this, where the view is MainActivity, the controller is Calculator, and the model is Structured Expression.

![alt text](http://i59.tinypic.com/1zwlyxh.jpg "Main Activity")
![alt text](http://i62.tinypic.com/2ldg6jr.jpg "Main Activity with Scientific Drawer out")
![alt text](http://i58.tinypic.com/jhd6ra.jpg "Landscape Main Activity")


## Structured Expression

Structured Expression is the meat of this calculator. As the name suggests, it provides structure to the expressions that the user builds, so that the calculator can safely make decisions about how to handle new input. At its core, it is an array of ExpressionBlocks, each of which has a String value and a String "blockType." The blockType is the tag that defines an ExpressionBlock as either an operator, an operand, an openParen, a closeParen, a function, or a "specialOperand" (constant, like pi).

StructuredExpression consists of functions which return the expressionArray as a human-readable string for display, as well as all the functions to handle new input. A simplifying assumption in this calculator is that the user can only append to the expression, not move a cursor to within it. This made StructuredExpression very simple to test: all we need to do is ensure that for each possible blockType that could be pressed. SturedExpression handles seven cases: one for an empty expressionArray, and one for each of the six possible blockTypes that could be serving as the preceding ExpressionBlock!

## Calculator

The Calculator consists of three things: the current StructuredExpression that the user is building, the last result that was computed, and the current resultDisplayState. It passes information from the MainActivity to the StructuredExpression in the form of button handlers, and passes information from the StructuredExpression to the MainActivity after doing the necessary processing (truncation of results, etc.)

## MainActivity

The MainActivity is the viewport for the user into the Calculator, which interfaces with the StructuredExpression model. It doesn't "do" much other than pass button presses to the Calculator and update the displays afterward.

There are a few things I would like to fix about the MainActivity. One, in portrait mode, the button to bring out the scientific tray is... a button. I'd really like it to be a slide-out -- that's high on my list of things to learn how to do. Second, there appears to be a bug sometimes where the Activity tries to load the portrait buttons into a container that doesn't exist in landscape mode. There are cheap, obvious fixes to this, but I'd rather come up with a more elegant solution. It doesn't help that this is extremely rare and is hard to capture on the debugger.
