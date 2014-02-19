package hyem.example.rideonnao;

import hyem.example.clientService.SocketService;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
//import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

public class EnterIP extends Activity {

	public static String IP = "192.168.0.0";
	public static int PORT = 0;
	
	public static Activity EnterAct;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_enter_ip);
		
		EnterAct = this;
	}

	public void gotoMain(View view)
	{
		Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vibe.vibrate(30);
		
		finish();
	
	}
	
	public void gotoCamera(View view) //여기서 서비스를 스타트시켜주어야 할 것이다.
	{
		Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vibe.vibrate(30);
		
		EditText enterIP = (EditText) findViewById(R.id.enterIP);
		EditText enterPORT = (EditText) findViewById(R.id.enterPORT);
		
		if ( ( enterIP.getText().length() == 0 ) | ( enterPORT.getText().length() == 0 ) )
		{
			Toast.makeText(getApplicationContext(), "Invalid IP/PORT", Toast.LENGTH_SHORT).show();
			
		} else {
			IP = enterIP.getText().toString();
			PORT = Integer.parseInt(enterPORT.getText().toString());
			
			Toast.makeText(getApplicationContext(), "Start Connecting...", Toast.LENGTH_SHORT).show();
			startService( new Intent ( getApplicationContext(), SocketService.class));
			//finish();
		}
		
		//finish();
	}
	
	

	
}
