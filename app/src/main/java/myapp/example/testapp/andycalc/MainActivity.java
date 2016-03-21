package myapp.example.testapp.andycalc;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.TypedValue;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private int BUTTON_VIB_LENGTH=1;
    private Vibrator myVib;
    private TextView resultDisplay;
    private TextView currentExpressionDisplay;
    private Calculator calculator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //don't try and load the basic buttons as a fragment if we're in the landscape view.
        //there SEEMS to be a bug with this where sometimes the landscape view will still try and
        //load these buttons. This try/catch keeps the app from crashing, but is still ungraceful.
        //TODO: ensure landscape mode never tries to load portrait buttons.
        if(isPortrait()){
            try {
                BasicFragment basic = new BasicFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.add(R.id.portrait_bottomhalf, basic);
                ft.commit();
            }catch(Exception e){
                e.printStackTrace();
                Toast.makeText(this,"error loading buttons, try again.",Toast.LENGTH_LONG).show();
            }
        }

        //this is kind of a hack that works because there is only one fragment ever on the backstack.
        //without this line, if you open the advanced tray, change orientation and change back,
        //the advanced tray button will no longer work.
        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        myVib = (Vibrator)this.getSystemService(VIBRATOR_SERVICE);
        currentExpressionDisplay = (TextView)findViewById(R.id.currentExpressionDisplay);
        resultDisplay = (TextView)findViewById(R.id.resultDisplay);
        resultDisplay.setVisibility(View.GONE);
        calculator = new Calculator();
        updateDisplays();
    }

    //called after every button press
    //updates the result display visibility(and text) and the expression text.
    public void updateDisplays() {
        String[] currentCalculatorState = calculator.getStringValues();
        //0 is current expression string
        //1 is last result
        //2 is result display state (0, 1, or 2)

        //there is something to display in current expression
        if (currentCalculatorState[0] != null) {
            String res = currentCalculatorState[0];
            //this is okay because for now there is only one special operand (constant).
            //in the future (e, etc.) we would want to replace all special operands with their presentation values.
            res = res.replace("#{PI}", "π");
            currentExpressionDisplay.setText(res);
        }

        //there is no result to display
        if(currentCalculatorState[2].equals("0")){
            resultDisplay.setVisibility(View.GONE);
        }

        //there is a successful result to display
        if (currentCalculatorState[2].equals("2") && currentCalculatorState[1] != null) {
            resultDisplay.setText(currentCalculatorState[1]);
            resultDisplay.setVisibility(View.VISIBLE);
        }
        //there is an unsuccessful result to display
        else if (currentCalculatorState[2].equals("1")) {
            resultDisplay.setText("Invalid");
            resultDisplay.setVisibility(View.VISIBLE);
        }
        setFontSizes();
    }

    //Eventually needs to be updated to set text size dynamically based on the size of the screen
    //and not just the orientation.
    private void setFontSizes(){
        //set font size of displays
        Integer curLength = currentExpressionDisplay.getText().toString().length();

        //portrait
        if(isPortrait()){
            if (curLength < 11) {
                currentExpressionDisplay.setTextSize(TypedValue.COMPLEX_UNIT_SP,50);
                resultDisplay.setTextSize(TypedValue.COMPLEX_UNIT_SP,60);
            } else {
                currentExpressionDisplay.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
                resultDisplay.setTextSize(TypedValue.COMPLEX_UNIT_SP,60);
            }
        }
        //landscape
        else{
            if (curLength < 50) {
                currentExpressionDisplay.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
                resultDisplay.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
            } else {
                currentExpressionDisplay.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
                resultDisplay.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
            }
        }
    }

    public void onNumberButtonClicked(View v){
        if(filterOnExpressionLength("number")) {
            myVib.vibrate(BUTTON_VIB_LENGTH);
            Button buttonPressed = (Button) v;
            String responseCode = calculator.pressNumber(buttonPressed.getText().toString());
            if (responseCode != null) {
                Toast.makeText(this, responseCode, Toast.LENGTH_SHORT).show();
            }
            resultDisplay.setVisibility(View.GONE);
            updateDisplays();
        }
    }

    public void onSpecialOperandButtonClicked(View v){
        if(filterOnExpressionLength("specialOperand")) {
            myVib.vibrate(BUTTON_VIB_LENGTH);
            Button buttonPressed = (Button) v;
            calculator.pressSpecialOperand(buttonPressed.getText().toString());
            resultDisplay.setVisibility(View.GONE);
            updateDisplays();
        }
    }

    public void onOperationButtonClicked(View v){
        if(filterOnExpressionLength("operator")) {
            myVib.vibrate(BUTTON_VIB_LENGTH);
            Button buttonPressed = (Button) v;
            calculator.pressOperation(buttonPressed.getText().toString());
            resultDisplay.setVisibility(View.GONE);
            updateDisplays();
        }
    }

    public void onParensButtonClicked(View v){
        if(filterOnExpressionLength("paren")) {
            myVib.vibrate(BUTTON_VIB_LENGTH);
            calculator.pressParens();
            resultDisplay.setVisibility(View.GONE);
            updateDisplays();
        }
    }

    public void onFunctionButtonClicked(View v){
        if(filterOnExpressionLength("function")) {
            Button buttonPressed = (Button) v;
            myVib.vibrate(BUTTON_VIB_LENGTH);
            calculator.pressFunction(buttonPressed.getText().toString());
            resultDisplay.setVisibility(View.GONE);
            updateDisplays();
        }
    }

    public void onNegateButtonClicked(View v){
        myVib.vibrate(BUTTON_VIB_LENGTH);
        calculator.pressNegate();
        resultDisplay.setVisibility(View.GONE);
        updateDisplays();
    }

    public void onEqualsButtonClicked(View v){
        myVib.vibrate(BUTTON_VIB_LENGTH);
        //equals sign with no expression doesn't send signal to calculator.
        if(currentExpressionDisplay.getText().toString().isEmpty()){
            return;
        }
        calculator.pressEquals();
        resultDisplay.setVisibility(View.VISIBLE);
        updateDisplays();
    }

    public void onClearButtonClicked(View v){
        myVib.vibrate(BUTTON_VIB_LENGTH);
        calculator.pressClear();
        resultDisplay.setVisibility(View.GONE);
        updateDisplays();
    }

    public void onBackButtonClicked(View v){
        myVib.vibrate(BUTTON_VIB_LENGTH);
        calculator.pressBack();
        resultDisplay.setVisibility(View.GONE);
        updateDisplays();
    }

    //return false if current expression is too long.
    //this is called on buttons which extend the length of the expression.
    public Boolean filterOnExpressionLength(String buttonType){
        Integer currentExpressionLength = currentExpressionDisplay.getText().toString().length();

        //special case: we're displaying a result and we type an operator. We are still allowed to do this.
        if(buttonType.equals("operator")){
            if(resultDisplay.getVisibility()==View.VISIBLE){
                return true;
            }
        }
        if(currentExpressionLength >= 30){
            Toast.makeText(this,"Current expression too long.",Toast.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }
    }

    //loads the scientific tray into view.
    //makes use of addFragmentOnlyOnce so spamming the button doesn't add multiple fragments.
    public void loadAdvancedFragment(View v){
        System.out.println("Pressed load advanced fragment.");

        AdvancedFragment af = new AdvancedFragment();
        FragmentManager fm = getFragmentManager();
        String tag = "advancedFragment";
        addFragmentOnlyOnce(fm,af,tag);
    }

    //stolen and modified from stackoverflow user Tom anMoney :)
    //makes that spamming the button doesn't add multiple fragments.
    public  void addFragmentOnlyOnce(FragmentManager fragmentManager, Fragment fragment, String tag) {
        // Make sure the current transaction finishes first
        fragmentManager.executePendingTransactions();

        // If there is no fragment yet with this tag...
        if (fragmentManager.findFragmentByTag(tag) == null) {
            // Add it
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.none, R.anim.none, R.anim.slide_out_left);
            FrameLayout frame = (FrameLayout) findViewById(R.id.portrait_bottomhalf);
            transaction.add(frame.getId(),fragment,tag);
            transaction.addToBackStack(tag);
            transaction.commit();
        }
    }

    private Boolean isPortrait(){
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int curRotation = display.getRotation();
        //portrait
        if(curRotation== Surface.ROTATION_0 || curRotation==Surface.ROTATION_180)
            return true;
        else
            return false;
    }
}