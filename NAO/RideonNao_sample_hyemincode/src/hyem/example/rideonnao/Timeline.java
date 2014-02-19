package hyem.example.rideonnao;

import hyem.example.clientService.socketAidl;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Timeline extends Activity {

	private socketAidl socket_serv = Camera.socket_serv;
	protected String angles[];
	protected String angles_str; 
	TextView timeline_text;
	ListView listView;
	ArrayAdapter<String> arrayadapter;
	ArrayList<String> arraylist;
	public final String[] angle_names = new String[26];
	boolean isGet = false;
	///////////////추후에 constant class를 새로 만들어서 저장해둘것!
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		
		listView = (ListView) findViewById(R.id.anglelist);
		arraylist = new ArrayList<String>();
		arrayadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,arraylist);
		listView.setAdapter(arrayadapter);
		
		angles = new String[26];
		
//		timeline_text = (TextView) findViewById(R.id.timeline_text);
	}
	
	public void getBtn(View v) {
		try{
			
			angles_str =  socket_serv.Send_recvMsg("ALMotion^getAngles^Body^true");
//			timeline_text.setText(angles_str);
			Log.i("CMS",angles_str);
			angles = angles_str.split("_");
			arraylist.clear();
			for(int i=0; i<26; i++){
				arraylist.add(angle_names[i] + " : " + Math.toDegrees(Float.parseFloat(angles[i])) + "Degree");
			}
			arrayadapter.notifyDataSetChanged();
			
			
			
			isGet = true;
//			angles[0] = Float.parseFloat(socket_serv.Send_recvMsg("ALMotion^getAngles^Body^true"));
//			for(int i=1;i<25;i++){
//				angles[i] = Float.parseFloat(socket_serv.recvMsg());	
//			}
//			timeline_text.setText("HeadYaw = "+Float.toString(angles[0]) + 
//					"HeadPitch"+Float.toString(angles[1]) + 
//					"HeadPitch"+Float.toString(angles[2]) + 
//					"HeadPitch"+Float.toString(angles[3]) + 
//					"HeadPitch"+Float.toString(angles[4]) + 
//					"HeadPitch"+Float.toString(angles[5]) + 
//					"HeadPitch"+Float.toString(angles[6]) + 
//					"HeadPitch"+Float.toString(angles[7]) + 
//					"HeadPitch"+Float.toString(angles[8]) + 
//					"HeadPitch"+Float.toString(angles[9]) + 
//					"HeadPitch"+Float.toString(angles[10]) + 
//					"HeadPitch"+Float.toString(angles[11]) + 
//					"HeadPitch"+Float.toString(angles[12]) + 
//					"HeadPitch"+Float.toString(angles[13]) + 
//					"HeadPitch"+Float.toString(angles[14]) + 
//					"HeadPitch"+Float.toString(angles[15]) + 
//					"HeadPitch"+Float.toString(angles[16]) + 
//					"HeadPitch"+Float.toString(angles[17]) + 
//					"HeadPitch"+Float.toString(angles[18]) + 
//					"HeadPitch"+Float.toString(angles[19]) + 
//					"HeadPitch"+Float.toString(angles[20]) + 
//					"HeadPitch"+Float.toString(angles[21]) + 
//					"HeadPitch"+Float.toString(angles[22]) + 
//					"HeadPitch"+Float.toString(angles[23]) + 
//					"HeadPitch"+Float.toString(angles[24]) +
//					"HeadPitch"+Float.toString(angles[25]) ;
			
		}
		catch(RemoteException e){
			e.printStackTrace();
		}
		
	}
	
	public void setBtn(View v){
		if(isGet==false){
			Toast.makeText(Timeline.this, "Before SET, Get the angles", Toast.LENGTH_SHORT).show();
			return ; 
		}
		String angles_str_temp = "";
		for(int i=0;i<angles.length;i++){
			if(i!=angles.length-1){
				angles_str_temp = angles_str_temp + angles[i] + "_";
			}
			else{
				angles_str_temp = angles_str_temp + angles[i];
			}
		}
		try{
			socket_serv.sendMsg("ALMotion^setAngles^Body^"+angles_str_temp+"^1.0");
			Toast.makeText(Timeline.this, angles_str_temp, Toast.LENGTH_SHORT).show();
		}catch(RemoteException re){
			Log.d("setbtnclick",re.toString());
		}
		return ;
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.timeline, menu);
		return true;
	}
	
	{
		angle_names[0] = "HeadYaw";
		angle_names[1] = "HeadPitch";
		angle_names[2] = "LShoulderPitch";
		angle_names[3] = "LShoulderRoll";
		angle_names[4] = "LElbowYaw";
		angle_names[5] = "LElbowRoll";
		angle_names[6] = "LWristYaw2";
		angle_names[7] = "LHand2";
		angle_names[8] = "LHipYawPitch1";
		angle_names[9] = "LHipRoll";
		angle_names[10] = "LHipPitch";
		angle_names[11] = "LKneePitch";
		angle_names[12] = "LAnklePitch";
		angle_names[13] = "RAnkleRoll";
		angle_names[14] = "RHipYawPitch1";
		angle_names[15] = "RHipRoll	";
		angle_names[16] = "RHipPitch";
		angle_names[17] = "RKneePitch";
		angle_names[18] = "RAnklePitch";
		angle_names[19] = "LAnkleRoll";
		angle_names[20] = "RShoulderPitch";
		angle_names[21] = "RShoulderRoll";
		angle_names[22] = "RElbowYaw";
		angle_names[23] = "RElbowRoll";
		angle_names[24] = "RWristYaw2";
		angle_names[25] = "RHand2";
	}
	
}