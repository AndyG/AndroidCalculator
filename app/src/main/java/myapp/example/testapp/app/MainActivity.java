package myapp.example.testapp.app;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
            resultDisplay.setText(currentCalculatorState[1]);
            resultDisplay.setVisibility(View.VISIBLE);
        } else if (currentCalculatorState[2].equals("1")) {
            resultDisplay.setText("Invalid");
            resultDisplay.setVisibility(View.VISIBLE);
        }else{
            System.out.println("Got here somehow...");
            System.out.println("result display state: "+currentCalculatorState[1]);
        }
    }

    public void onNumberButtonClicked(View v){
        myVib.vibrate(BUTTON_VIB_LENGTH);
        Button buttonPressed = (Button)v;
        calculator.pressNumber(buttonPressed.getText().toString());
        resultDisplay.setVisibility(View.GONE);
        updateDisplays();
    }

    public void onOperationButtonClicked(View v){
        myVib.vibrate(BUTTON_VIB_LENGTH);
        Button buttonPressed = (Button)v;
        calculator.pressOperation(buttonPressed.getText().toString());
        resultDisplay.setVisibility(View.GONE);
        updateDisplays();
    }

    public void onParensButtonClicked(View v){
        myVib.vibrate(BUTTON_VIB_LENGTH);
        calculator.pressParens();
        resultDisplay.setVisibility(View.GONE);
        updateDisplays();
    }

    public void onFunctionButtonClicked(View v){
        Button buttonPressed = (Button)v;
        myVib.vibrate(BUTTON_VIB_LENGTH);
        calculator.pressFunction(buttonPressed.getText().toString());
        resultDisplay.setVisibility(View.GONE);
        updateDisplays();
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
}