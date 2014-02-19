package hyem.example.rideonnao;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
//import android.widget.Button;



public class MenuTab extends Activity {
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_menu);
			
		
	
	}
	
	public void backToCamera(View view){
		
		Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vibe.vibrate(30);
		
		finish();
			
	}
	
	
	public void backToMain(View view){
		
		Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vibe.vibrate(30);
		
		finish();
		
		Camera.CameraAct.finish();	
		
	}
	

	
	public void gotoSetting(View view){
		
		Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vibe.vibrate(30);
		
		Intent myIntent = new Intent(getApplicationContext(), Status.class);
		
		startActivity(myIntent);	
		
		onPause();
		
	}
	
	public void gotoTimeline(View view){
		
		Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vibe.vibrate(30);
		
		Intent myIntent = new Intent(getApplicationContext(), Timeline.class);
		
		startActivity(myIntent);	
		
		onPause();
		
	}


}
