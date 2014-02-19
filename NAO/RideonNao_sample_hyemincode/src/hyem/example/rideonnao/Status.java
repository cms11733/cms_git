package hyem.example.rideonnao;

import hyem.example.clientService.SocketService;
import hyem.example.clientService.socketAidl;
import hyem.example.rideonnao.R.drawable;
import hyem.example.rideonnao.R.id;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;

public class Status extends Activity {
	private socketAidl socket_serv = Camera.socket_serv;
	protected Intent myintent;
	protected boolean isbound;

	protected Thread thread_info;
	protected Handler handler_info = new Handler();

	protected float bat_charge;
	protected int[] motortemp = new int[25];
	protected String pose;
	protected boolean StandOrSit = Camera.StandOrSit;
	protected String[] instruction = Camera.instruction;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_status);

		ImageView nao_posture = (ImageView) findViewById(id.naopose_image);
		TextView posture_description = (TextView) findViewById(id.nao_posture);
		
		if(StandOrSit)
		{
			nao_posture.setImageResource(drawable.nao_stand);
			posture_description.setText("NAO is Standing");
		} else {
			
			nao_posture.setImageResource(drawable.nao_sit);
			posture_description.setText("NAO is Sitting");
		}
		getting_Data();

	}

	private void getting_Data() {
		thread_info = new Thread(null, startCollecting, "DataCollect");

		thread_info.start();
	}

	private Runnable startCollecting = new Runnable() {
		@Override
		public void run() {
			do_Collect();
		}

	};

	private void do_Collect() {
		int length = instruction.length;

		for (int i = 0; i < length; i++) {
			if (i == 0) {
				try {
					bat_charge = Float.parseFloat(socket_serv
							.Send_recvMsg(instruction[25 - i]));
					handler_info.post(toast_info);
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (i >= 1) {
				try {
					motortemp[i - 1] = Integer.parseInt(socket_serv
							.Send_recvMsg(instruction[i - 1]));
					handler_info.post(toast_info);
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	private Runnable toast_info = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			start_toasting_data();
		}
	};

	private void start_toasting_data() {
		TextView battery = (TextView) findViewById(id.BatteryCharged);
		TextView HeadPitch = (TextView) findViewById(id.HeadPitch_temp);
		TextView HeadYaw = (TextView) findViewById(id.HeadYaw_temp);

		TextView LAnklePitch = (TextView) findViewById(id.LAnklePitch_temp);
		TextView LAnkleRoll = (TextView) findViewById(id.LAnkleRoll_temp);
		TextView LElbowRoll = (TextView) findViewById(id.LElbowRoll_temp);
		TextView LElbowYaw = (TextView) findViewById(id.LElbowYaw_temp);
		TextView LHand = (TextView) findViewById(id.LHand_temp);
		TextView LHipPitch = (TextView) findViewById(id.LHipPitch_temp);
		TextView LHipRoll = (TextView) findViewById(id.LHipRoll_temp);
		TextView LHipYawPitch = (TextView) findViewById(id.LHipYawPitch_temp);
		TextView LKneePitch = (TextView) findViewById(id.LKneePitch_temp);
		TextView LShoulderPitch = (TextView) findViewById(id.LShoulderPitch_temp);
		TextView LShoulderRoll = (TextView) findViewById(id.LShoulderRoll_temp);
		TextView LWristYaw = (TextView) findViewById(id.LWristYaw_temp);

		TextView RAnklePitch = (TextView) findViewById(id.RAnklePitch_temp);
		TextView RAnkleRoll = (TextView) findViewById(id.RAnkleRoll_temp);
		TextView RElbowRoll = (TextView) findViewById(id.RElbowRoll_temp);
		TextView RElbowYaw = (TextView) findViewById(id.RElbowYaw_temp);
		TextView RHand = (TextView) findViewById(id.RHand_temp);
		TextView RHipPitch = (TextView) findViewById(id.RHipPitch_temp);
		TextView RHipRoll = (TextView) findViewById(id.RHipRoll_temp);
		TextView RKneePitch = (TextView) findViewById(id.RKneePitch_temp);
		TextView RShoulderPitch = (TextView) findViewById(id.RShoulderPitch_temp);
		TextView RShoulderRoll = (TextView) findViewById(id.RShoulderRoll_temp);
		TextView RWristYaw = (TextView) findViewById(id.RWristYaw_temp);

		if (bat_charge > 0)
			battery.setText(bat_charge * 100 + " % ");

		if (motortemp[0] > 0)
			HeadPitch.setText(motortemp[0] + " 'C ");

		if (motortemp[1] > 0)
			HeadYaw.setText(motortemp[1] + " 'C ");

		if (motortemp[2] > 0)
			LAnklePitch.setText(motortemp[2] + " 'C ");

		if (motortemp[3] > 0)
			LAnkleRoll.setText(motortemp[3] + " 'C ");

		if (motortemp[4] > 0)
			LElbowRoll.setText(motortemp[4] + " 'C ");

		if (motortemp[5] > 0)
			LElbowYaw.setText(motortemp[5] + " 'C ");

		if (motortemp[6] > 0)
			LHand.setText(motortemp[6] + " 'C ");

		if (motortemp[7] > 0)
			LHipPitch.setText(motortemp[7] + " 'C ");

		if (motortemp[8] > 0)
			LHipRoll.setText(motortemp[8] + " 'C ");

		if (motortemp[9] > 0)
			LHipYawPitch.setText(motortemp[9] + " 'C ");

		if (motortemp[10] > 0)
			LKneePitch.setText(motortemp[10] + " 'C ");

		if (motortemp[11] > 0)
			LShoulderPitch.setText(motortemp[11] + " 'C ");

		if (motortemp[12] > 0)
			LShoulderRoll.setText(motortemp[12] + " 'C ");

		if (motortemp[13] > 0)
			LWristYaw.setText(motortemp[13] + " 'C ");

		if (motortemp[14] > 0)
			RAnklePitch.setText(motortemp[14] + " 'C ");

		if (motortemp[15] > 0)
			RAnkleRoll.setText(motortemp[15] + " 'C ");

		if (motortemp[16] > 0)
			RElbowRoll.setText(motortemp[16] + " 'C ");

		if (motortemp[17] > 0)
			RElbowYaw.setText(motortemp[17] + " 'C ");

		if (motortemp[18] > 0)
			RHand.setText(motortemp[18] + " 'C ");

		if (motortemp[19] > 0)
			RHipPitch.setText(motortemp[19] + " 'C ");

		if (motortemp[20] > 0)
			RHipRoll.setText(motortemp[20] + " 'C ");

		if (motortemp[21] > 0)
			RKneePitch.setText(motortemp[21] + " 'C ");

		if (motortemp[22] > 0)
			RShoulderPitch.setText(motortemp[22] + " 'C ");

		if (motortemp[23] > 0)
			RShoulderRoll.setText(motortemp[23] + " 'C ");

		if (motortemp[24] > 0)
			RWristYaw.setText(motortemp[24] + " 'C ");

	}

	public void BackToMenu(View view) {
		finish();
	}

	@Override
	public void finish() {

		super.finish();

	}

}

