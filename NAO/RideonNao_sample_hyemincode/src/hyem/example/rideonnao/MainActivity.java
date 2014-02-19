package hyem.example.rideonnao;

import hyem.example.clientService.SocketService;
import hyem.example.clientService.socketAidl;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	//public static Object MainAct;
	private socketAidl socket_serv = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		//MainAct = MainActivity.class;		
		
	}
	
	@Override
	protected void onPause(){
		super.onPause();
	}
	
	public void Tryconnect(View view)
	{
		Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vibe.vibrate(30);
		
		Intent myIntent = new Intent(getApplicationContext(), EnterIP.class);
			
		startActivity(myIntent);	
		
		onPause();
	}
	
	public void CheckService( View view )
	{
		Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vibe.vibrate(30);
		
		if(isMyServiceRunning())
		{
			Toast.makeText(getApplicationContext(), "NAO still connected, Now disconnecting...", Toast.LENGTH_SHORT).show();
			
			stopService( new Intent ( this, SocketService.class));
		}
		else
		{
			Toast.makeText(getApplicationContext(), "NAO disconnected", Toast.LENGTH_SHORT).show();

		}
	}

	private boolean isMyServiceRunning() {
	    ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if ("hyem.example.clientService.SocketService".equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
	
	
}
