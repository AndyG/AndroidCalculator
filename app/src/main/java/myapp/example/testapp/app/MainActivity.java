package myapp.example.testapp.app;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private int BUTTON_VIB_LENGTH=1;
    private Vibrator myVib;
    private Boolean startedTypingNumber;
    private Boolean startedBuildingExpression;
    private TextView currentOperandDisplay;
    private TextView resultDisplay;
    private TextView currentExpressionDisplay;
    private Calculator calculator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myVib = (Vibrator)this.getSystemService(VIBRATOR_SERVICE);

        setContentView(R.layout.activity_main);
        Log.w("debugMessage", "created main activity");
        startedTypingNumber = false;
        startedBuildingExpression=false;
        currentExpressionDisplay = (TextView)findViewById(R.id.currentExpressionDisplay);
        resultDisplay = (TextView)findViewById(R.id.resultDisplay);
        resultDisplay.setVisibility(View.GONE);
        calculator = new Calculator();
        updateDisplays();
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        outState.putParcelable("calculator",calculator);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        calculator = savedInstanceState.getParcelable("calculator");
        updateDisplays();
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Log.w("menuClick","clicked on the menu");
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateDisplays() {
        String[] currentCalculatorState = calculator.getStringValues();
        //0 is current expression string
        //1 is last result
        //2 is result display state (0, 1, or 2)
        if (currentCalculatorState[0] != null)
            currentExpressionDisplay.setText(currentCalculatorState[0]);
        if(currentCalculatorState[2].equals("0")){
            resultDisplay.setVisibility(View.GONE);
        }
        if (currentCalculatorState[2].equals("2") && currentCalculatorState[1] != null) {
            resultDisplay.setText(currentCalculatorState[1].substring(0,Math.min(currentCalculatorState[1].length(),7)));
            resultDisplay.setVisibility(View.VISIBLE);
        } else if (currentCalculatorState[2].equals("1")) {
            resultDisplay.setText("Invalid");
            resultDisplay.setVisibility(View.VISIBLE);
        }else{
            System.out.println("Got here somehow...");
            System.out.println("result display state: "+currentCalculatorState[1]);
        }

        setFontSizes();
    }

    private void setFontSizes(){
        //set font size of displays
        Integer curLength = currentExpressionDisplay.getText().toString().length();

        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int curRotation = display.getRotation();
        //portrait
        if(curRotation== Surface.ROTATION_0 || curRotation==Surface.ROTATION_180) {
            if (curLength < 12) {
                currentExpressionDisplay.setTextSize(TypedValue.COMPLEX_UNIT_SP,50);
                resultDisplay.setTextSize(TypedValue.COMPLEX_UNIT_SP,70);
            } else {
                currentExpressionDisplay.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
                resultDisplay.setTextSize(TypedValue.COMPLEX_UNIT_SP,70);
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

    //return false if current expression is too long or
    public Boolean filterOnExpressionLength(String buttonType){
        Integer currentExpressionLength = currentExpressionDisplay.getText().toString().length();

        //special case: we're displaying a result and we type an operator. We are still allowed to do this.
        if(buttonType.equals("operator")){
            if(resultDisplay.getVisibility()==View.VISIBLE){
                return true;
            }
        }
        if(currentExpressionLength >= 35){
            Toast.makeText(this,"Current expression too long.",Toast.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }
    }
}