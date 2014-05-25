package myapp.example.testapp.app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends Activity {

    private Boolean startedTypingNumber;
    private Boolean startedBuildingExpression;
    private TextView currentOperandDisplay;
    private TextView resultDisplay;
    private TextView currentExpressionDisplay;
    private Calculator calculator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.w("debugMessage", "created main activity");
        startedTypingNumber = false;
        startedBuildingExpression=false;
        currentExpressionDisplay = (TextView)findViewById(R.id.currentExpressionDisplay);
        resultDisplay = (TextView)findViewById(R.id.resultDisplay);
        resultDisplay.setVisibility(View.GONE);
        calculator = new Calculator();
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

    public void updateDisplays(){
        String[] currentCalculatorState = calculator.getStringValues();
        if(currentCalculatorState[0]!=null)
            currentExpressionDisplay.setText(currentCalculatorState[0]);
        if(currentCalculatorState[1]!=null){
            resultDisplay.setText(currentCalculatorState[1]);
        }else{
            resultDisplay.setText("");
        }
    }

    public void onNumberButtonClicked(View v){
        Button buttonPressed = (Button)v;
        calculator.pressNumber(buttonPressed.getText().toString());
        resultDisplay.setVisibility(View.GONE);
        updateDisplays();
    }

    public void onOperationButtonClicked(View v){
        Button buttonPressed = (Button)v;
        calculator.pressOperation(buttonPressed.getText().toString());
        resultDisplay.setVisibility(View.GONE);
        updateDisplays();
    }

    public void onEqualsButtonClicked(View v){
        calculator.pressEquals();
        resultDisplay.setVisibility(View.VISIBLE);
        updateDisplays();
    }

    public void onClearButtonClicked(View v){
        calculator.pressClear();
        resultDisplay.setVisibility(View.GONE);
        updateDisplays();
    }

    public void onBackButtonClicked(View v){
        calculator.pressBack();
        resultDisplay.setVisibility(View.GONE);
        updateDisplays();
    }
}