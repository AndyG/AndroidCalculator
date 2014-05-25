package myapp.example.testapp.app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import net.sourceforge.jeval.*;


public class MainActivity extends Activity {

    private Boolean startedTypingNumber;
    private Boolean startedBuildingExpression;
    private TextView mainDisplay;
    private TextView currentExpressionDisplay;
    private Evaluator brain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.w("debugMessage", "created main activity");
        startedTypingNumber = false;
        startedBuildingExpression=false;
        mainDisplay = (TextView)findViewById(R.id.mainDisplay);
        currentExpressionDisplay = (TextView)findViewById(R.id.currentExpressionDisplay);
        brain = new Evaluator();
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

    public void onNumberButtonClicked(View v){
        Button clickedButton = (Button)v;
        if(clickedButton.getText().toString().equals(".")){
            if(!mainDisplay.getText().toString().contains(".")){
                mainDisplay.append(".");
            }
            return;
        }


        if(!startedTypingNumber){
            mainDisplay.setText(clickedButton.getText());
            startedTypingNumber=true;
        }else{
            mainDisplay.append(clickedButton.getText());
        }
    }

    public void onOperationButtonClicked(View v){
        Button clickedButton = (Button)v;
        if(startedTypingNumber){
            if(!startedBuildingExpression){
                currentExpressionDisplay.setText(mainDisplay.getText().toString()+clickedButton.getText().toString());
                mainDisplay.setText("0");
                startedTypingNumber=false;
                startedBuildingExpression=true;
            }else {
                currentExpressionDisplay.append(mainDisplay.getText().toString() + clickedButton.getText().toString());
                mainDisplay.setText("0");
                startedTypingNumber = false;
            }
        }else{
            Toast toast = Toast.makeText(this, "Pressed operator before pressing number", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void onEqualsButtonClicked(View v){
        Button clickedButton = (Button)v;
        try{
            currentExpressionDisplay.append(mainDisplay.getText());
            mainDisplay.setText(brain.evaluate(currentExpressionDisplay.getText().toString()));
            currentExpressionDisplay.setText("0");
            startedTypingNumber=false;
            startedBuildingExpression=false;
        }catch(Exception e){
            System.out.println("Error in evaluation");
            e.printStackTrace();
        }
    }

    public void onClearButtonClicked(View v){
        currentExpressionDisplay.setText("0");
        mainDisplay.setText("0");
        startedBuildingExpression=false;
        startedTypingNumber=false;
    }

    public void onBackButtonClicked(View v){
        String curText = mainDisplay.getText().toString();

        if(!curText.equals("0") && !curText.equals("")){
            String newText = curText.substring(0,curText.length()-1);
            if(newText.length()==0) {
                mainDisplay.setText("0");
                startedTypingNumber=false;
            }else{
                mainDisplay.setText(newText);
                startedTypingNumber=true;
            }
        }
    }
}