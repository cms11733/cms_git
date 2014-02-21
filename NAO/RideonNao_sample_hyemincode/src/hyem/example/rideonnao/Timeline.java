package hyem.example.rideonnao;

import hyem.example.clientService.socketAidl;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class Timeline extends Activity {

	private socketAidl socket_serv = Camera.socket_serv;
	protected String angles[];
	protected String angles_str; 
	ListView listView;
	AngleListAdapter angleListAdapter;
	ArrayList<String> anglelist;
	ArrayList<NameAngle> NameAngleList;
	public final String[] angle_names = new String[26];
	boolean isGet = false;
	///////////////추후에 constant class를 새로 만들어서 저장해둘것!
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		
		listView = (ListView) findViewById(R.id.anglelist);
		anglelist = new ArrayList<String>();
		NameAngleList = new ArrayList<NameAngle>();
		angleListAdapter = new AngleListAdapter(this, R.layout.angles_seekbar, NameAngleList);
		listView.setAdapter(angleListAdapter);
		
		angles = new String[26];
		
	}
	
	public void getBtn(View v) {
		try{
			
			angles_str =  socket_serv.Send_recvMsg("ALMotion^getAngles^Body^true");
			Log.i("CMS",angles_str);
			angles = angles_str.split("_");
			anglelist.clear();
			NameAngleList.clear();
			for(int i=0; i<26; i++){
				NameAngleList.add(new NameAngle(
						//angle_names[i]
						Integer.toString(i), Float.parseFloat(angles[i])));
//				anglelist.add(angles[i]);
////				anglelist.add(angle_names[i] + " : " + Math.toDegrees(Float.parseFloat(angles[i])) + "Degree");
			}
			angleListAdapter.notifyDataSetChanged();
			
			isGet = true;
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
	
	private class AngleListAdapter extends ArrayAdapter<NameAngle> {
		LayoutInflater Inflater;
		int TextViewResourceID;
		
		public AngleListAdapter(Context context, int TextViewResourceID, ArrayList<NameAngle> NameAngleList) {
			super(context, TextViewResourceID, NameAngleList);
			this.Inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.TextViewResourceID = TextViewResourceID;
		}
		
		public View getView(int position, View convertView, ViewGroup parant){
			final ViewHolder nameAngleHolder;
			View v = convertView;
			final NameAngle item = this.getItem(position);
			if(v == null){
				v = Inflater.inflate(TextViewResourceID,null);
				nameAngleHolder = new ViewHolder();
				nameAngleHolder.sb = (SeekBar) v.findViewById(R.id.seekbar_angle);
				nameAngleHolder.tv = (TextView) v.findViewById(R.id.value_angle);
				nameAngleHolder.sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					
					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onProgressChanged(SeekBar seekBar, int progress,
							boolean fromUser) {
						// TODO Auto-generated method stub
						nameAngleHolder.tv.setText(item.Name + " = " + Integer.toString(progress-90)+"Degress");
						
					}
				});
				v.setTag(nameAngleHolder);
			}else{
				nameAngleHolder = (ViewHolder) v.getTag();
			}
			int value_angle = (int)Math.toDegrees(item.Angle);
			nameAngleHolder.tv.setText(item.Name + " = " + Integer.toString(value_angle)+" Degree");
			Log.i("CMS",item.Name);
			
			nameAngleHolder.sb.setProgress(value_angle+90);
			
			
			return v;
		}

		
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
	
	private class NameAngle{
		NameAngle(String name, float angle){
			this.Name = name;
			this.Angle = angle;
		}
		String Name;
		float Angle;
	}
	
	private class ViewHolder{
		public SeekBar sb;
		public TextView tv;
	}
	
}