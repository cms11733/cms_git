package hyem.example.rideonnao;

import hyem.example.clientService.SocketService;
import hyem.example.clientService.socketAidl;

import java.util.ArrayList;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;


public class Camera extends Activity  {

	//////////////////////
	/*codes related to Gsteramer
	 private native void nativeInit();     // Initialize native code, build pipeline, etc
	    private native void nativeFinalize(); // Destroy pipeline and shutdown native code
	    private native void nativePlay();     // Set pipeline to PLAYING
	    private native void nativePause();    // Set pipeline to PAUSED
	    private static native boolean nativeClassInit(); // Initialize native class: cache Method IDs for callbacks
	    private native void nativeSurfaceInit(Object surface);
	    private native void nativeSurfaceFinalize();
	    private long native_custom_data;      // Native code will use this to keep private data

	    private boolean is_playing_desired
	    
	//////////////////////
	 */
	
	static public String[] instruction = new String[26];

	private boolean thread_watch_birth;
	private int data_order = 0;
	
	private boolean thread_killer = false;
	private Thread thread_watch;
	private Thread thread_bind;
	private float bat_charge = (float) 0.0;
	private int motor_max_temp = 0;
	private Handler handler = new Handler();
	private Handler handler_wat = new Handler();

	///////////////////////////////
	
	static protected socketAidl socket_serv = null;
	protected Intent myintent;
	protected boolean isbound = false;

	protected long button_start_left;
	protected long button_end_left;

	protected long button_start_right;
	protected long button_end_right;

	protected long button_start_up;
	protected long button_end_up;

	protected long button_start_down;
	protected long button_end_down;

	public long last_order_start;
	public long last_order_end;

	protected float head_angle_yaw;
	protected float head_angle_pitch;

	protected float simul_angle_yaw;
	protected float simul_angle_pitch;

	protected moveBtn[] storeBtn = new moveBtn[12];

	public static Activity CameraAct;
	
	protected static boolean StandOrSit = false;

	protected boolean OperatingNow = true;

///////////////////////////20140123 modified ///////////////////////////////////////////////
	protected boolean ViewMode = true;
	protected boolean isCrouch = false;
///////////////////////////20140123 modified ///////////////////////////////////////////////
	
	protected ProgressDialog loadingDialog;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_camera);

		CameraAct = this;

		Log.i("hyem", "activity is created");

		simul_angle_yaw = 0;
		simul_angle_pitch = 0;

		last_order_end = System.currentTimeMillis();

///////////////////////////20140123 modified ///////////////////////////////////////////////
		final RelativeLayout back_layout = (RelativeLayout) findViewById(R.id.backlayout);
		final RelativeLayout back_nao_motion = (RelativeLayout) findViewById(R.id.background_nao_motion);
		final ImageButton back_view = (ImageButton) findViewById(R.id.backview);
		final ImageView nao_motion = (ImageView) findViewById(R.id.nao_motion);
		final Button backtoview = (Button) findViewById(R.id.backtoview);
		final ImageButton nao_motion_down = (ImageButton) findViewById(R.id.nao_motion_down);
		final ImageButton nao_motion_up = (ImageButton) findViewById(R.id.nao_motion_up);
		if(StandOrSit == false) {
			nao_motion_down.setVisibility(View.GONE);
			nao_motion_up.setVisibility(View.VISIBLE);
		} else{
			nao_motion_down.setVisibility(View.VISIBLE);
			nao_motion_up.setVisibility(View.GONE);
		}

///////////////////////////20140123 modified ///////////////////////////////////////////////
		
		final ImageButton forwardkey = (ImageButton) findViewById(R.id.upKey);
		final ImageButton backkey = (ImageButton) findViewById(R.id.downKey);
		final ImageButton leftkey = (ImageButton) findViewById(R.id.leftKey);
		final ImageButton rightkey = (ImageButton) findViewById(R.id.rightKey);

		final ImageButton forwardkey_lie = (ImageButton) findViewById(R.id.upKey_lie);
		final ImageButton backkey_lie = (ImageButton) findViewById(R.id.downKey_lie);
		final ImageButton leftkey_lie = (ImageButton) findViewById(R.id.leftKey_lie);
		final ImageButton rightkey_lie = (ImageButton) findViewById(R.id.rightKey_lie);

		final ImageButton leftBtn = (ImageButton) findViewById(R.id.arrowleft);
		final ImageButton rightBtn = (ImageButton) findViewById(R.id.arrowright);
		final ImageButton upBtn = (ImageButton) findViewById(R.id.arrowup);
		final ImageButton downBtn = (ImageButton) findViewById(R.id.arrowdown);
		final ImageButton menuBtn = (ImageButton) findViewById(R.id.menuBtn);

		final ImageButton leftBtn_lie = (ImageButton) findViewById(R.id.arrowleft_LIE);
		final ImageButton rightBtn_lie = (ImageButton) findViewById(R.id.arrowright_LIE);
		final ImageButton upBtn_lie = (ImageButton) findViewById(R.id.arrowup_LIE);
		final ImageButton downBtn_lie = (ImageButton) findViewById(R.id.arrowdown_LIE);
		final ImageButton menuBtn_lie = (ImageButton) findViewById(R.id.menuBtn_LIE);

		final ImageView naostatus = (ImageView) findViewById(R.id.naostatus);

		final ImageButton nao_motor = (ImageButton) findViewById(R.id.naohead);
		final ImageButton battery = (ImageButton) findViewById(R.id.battery);
		final ImageButton makepose = (ImageButton) findViewById(R.id.makepose);


		storeBtn[0] = new moveBtn(forwardkey, forwardkey_lie, "forward_move");
		storeBtn[1] = new moveBtn(backkey, backkey_lie, "back_move");
		storeBtn[2] = new moveBtn(leftkey, leftkey_lie, "left_move");
		storeBtn[3] = new moveBtn(rightkey, rightkey_lie, "right_move");
		storeBtn[4] = new moveBtn(leftBtn, leftBtn_lie, "left_shake");
		storeBtn[5] = new moveBtn(rightBtn, rightBtn_lie, "right_shake");
		storeBtn[6] = new moveBtn(upBtn, upBtn_lie, "up_shake");
		storeBtn[7] = new moveBtn(downBtn, downBtn_lie, "down_shake");
		storeBtn[8] = new moveBtn(menuBtn, menuBtn_lie, "menu");
		storeBtn[9] = new moveBtn(nao_motor, nao_motor, "motor");
		storeBtn[10] = new moveBtn(battery, battery, "battery");
		storeBtn[11] = new moveBtn(makepose, makepose, "makepose");

		myintent = new Intent(getApplicationContext(), SocketService.class);

		naostatus.setImageResource(R.drawable.busy);

		CoverAll(storeBtn);
		Disable_All(storeBtn);

		///
		loadingDialog = new ProgressDialog(Camera.this);
		loadingDialog.setTitle("");
		loadingDialog.setMessage("Loading...");
		loadingDialog.show();
		
		
		////


		isbound = getApplicationContext().bindService(myintent, servConn,
				Context.BIND_AUTO_CREATE);

		
		/*codes related to Gsteramer
		/////////////////////
		 
		
         SurfaceView sv = (SurfaceView) this.findViewById(R.id.surface_video);
         SurfaceHolder sh = sv.getHolder();
         sh.addCallback(this);

         //if (savedInstanceState != null) {
         //    is_playing_desired = savedInstanceState.getBoolean("playing");
         //    Log.i ("GStreamer", "Activity created. Saved state is playing:" + is_playing_desired);
         //} else {
         //    is_playing_desired = false;
         //    Log.i ("GStreamer", "Activity created. There is no saved state, playing: false");
        // }
         
         /////////////////////
          */
       
///////////////////////////20140123 modified ///////////////////////////////////////////////
		back_view.setOnClickListener(new OnClickListener (){
			public void onClick(View v){
				Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
 				vibe.vibrate(30);
 				
 				if(ViewMode){
 					back_layout.setVisibility(View.GONE);
 					back_nao_motion.setVisibility(View.VISIBLE);
 					if(StandOrSit){
 						nao_motion.setImageResource(R.drawable.motionstandup);
 					} else if (isCrouch == false){
 						nao_motion.setImageResource(R.drawable.motionsitdown);
 					} else {
 						nao_motion.setImageResource(R.drawable.motioncrouch);
 					}
 					ViewMode=false;
 				} else {
 					back_layout.setVisibility(View.VISIBLE);
 					back_nao_motion.setVisibility(View.GONE);
 					ViewMode=true;
 				}
			}
		});
		
		backtoview.setOnClickListener(new OnClickListener() {
 			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
 				vibe.vibrate(30);
 				
 				if(ViewMode){
 					back_layout.setVisibility(View.GONE);
 					back_nao_motion.setVisibility(View.VISIBLE); 
 					ViewMode=false;
 				} else {
 					back_layout.setVisibility(View.VISIBLE);
 					back_nao_motion.setVisibility(View.GONE);
 					ViewMode=true;
 				}
				
			}
		});
		
		nao_motion_down.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
 				vibe.vibrate(30);
 				
 				try{
 					socket_serv.sendMsg("ALRobotPosture^goToPosture^Crouch^1.0");
 				} catch(RemoteException e){
 					e.printStackTrace();
 				}
 				
 				OperatingNow = true;
 				nao_motion_down.setVisibility(View.GONE);
 				nao_motion_up.setVisibility(View.VISIBLE);
 				nao_motion.setImageResource(R.drawable.motioncrouch);
 				makepose.setImageResource(R.drawable.standup);
 				isCrouch = true;
 				StandOrSit = false;
 				
			}
		});
		
		nao_motion_up.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
 				vibe.vibrate(30);
 				
 				try{
 					socket_serv.sendMsg("ALRobotPosture^goToPosture^Stand^1.0");
 				} catch(RemoteException e){
 					e.printStackTrace();
 				}
 				
 				OperatingNow = true;
 				nao_motion_down.setVisibility(View.VISIBLE);
 				nao_motion_up.setVisibility(View.GONE);
 				nao_motion.setImageResource(R.drawable.motionstandup);
 				makepose.setImageResource(R.drawable.sit);
 				isCrouch = false;
 				StandOrSit = true;
				
			}
		});
		
///////////////////////////20140123 modified ///////////////////////////////////////////////
		
         makepose.setOnClickListener(new OnClickListener() {

 			@Override
 			public void onClick(View v) {
 				// TODO Auto-generated method stub
 				Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
 				vibe.vibrate(30);

 				OperatingNow = true;


 				if (StandOrSit)
 				{
 					try {
 						socket_serv.sendMsg("ALRobotPosture^goToPosture^Sit^1.0");
 					} catch (RemoteException e) {
 						// TODO Auto-generated catch block
 						e.printStackTrace();
 					}
 					
 					
 					makepose.setImageResource(R.drawable.standup);
 					StandOrSit = false;
///////////////////////////20140123 modified ///////////////////////////////////////////////
 					isCrouch = false;
 					nao_motion.setImageResource(R.drawable.motionsitdown);
///////////////////////////20140123 modified ///////////////////////////////////////////////

					OperatingNow = false;

 					
 				}else {
 					try {
 						socket_serv.sendMsg("ALRobotPosture^goToPosture^Stand^1.0");
 					} catch (RemoteException e) {
 						// TODO Auto-generated catch block
 						e.printStackTrace();
 					}
 					
 					
 					makepose.setImageResource(R.drawable.sit);
 					StandOrSit = true;
///////////////////////////20140123 modified ///////////////////////////////////////////////
 					isCrouch = false;
 					nao_motion.setImageResource(R.drawable.motionstandup);
///////////////////////////20140123 modified ///////////////////////////////////////////////


					OperatingNow = false;

 				}
 			}
 			
 			
 		});
 		
         
         rightkey.setOnTouchListener(new OnTouchListener() {

 			@Override
 			public boolean onTouch(View v, MotionEvent event) {
 				// TODO Auto-generated method stub
 				if (event.getAction() == MotionEvent.ACTION_DOWN) {
 					Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
 					vibe.vibrate(30);

 					
 					if(StandOrSit == false)
 					{
 						Toast.makeText(getApplicationContext(), "plz make NAO Stand before walk!", Toast.LENGTH_LONG).show();
 						
 					}
 					else {
 						Disable_exceptMe(storeBtn, "right_move");
 						OnlyMyself(storeBtn, "right_move");
 	
						OperatingNow = true;


 						try {
 	
 							Log.i("hyem", "moving start");
 	
 							
 							naostatus.setImageResource(R.drawable.busy);
 							if (-0.03 < simul_angle_yaw && simul_angle_yaw < 0.03) {
 								socket_serv
 										.sendMsg("ALMotion^moveToward^0.0^-0.4^0.0");
 	
 							} else {
 	
 								socket_serv
 										.sendMsg("ALMotion^setAngles^HeadYaw^0.0^0.5");
 	
 								socket_serv.sendMsg("ALMotion^moveTo^0.0^0.0^"
 										+ simul_angle_yaw);
 	
 								socket_serv
 										.sendMsg("ALMotion^waitUntilMoveIsFinished");
 	
 								socket_serv
 										.sendMsg("ALMotion^moveToward^0.0^-0.5^0.0");
 	
 								simul_angle_yaw = 0;
 	
 							}
 	
 						} catch (RemoteException e) {
 							// TODO Auto-generated catch block
 							e.printStackTrace();
 						}
 					}

 				} else if (event.getAction() == MotionEvent.ACTION_UP) {

 					if(StandOrSit == false)
 					{
 						
 					} else {
 						try {
 	
 							last_order_end = System.currentTimeMillis();
 	
 							socket_serv.sendMsg("ALMotion^stopMove");
 	
 							while (System.currentTimeMillis() < last_order_end + 3000) {
 	
 							}
 							naostatus.setImageResource(R.drawable.ready);
 							Enable_exceptMe(storeBtn, "right_move");
 							ShowOthers(storeBtn, "right_move");
							OperatingNow = false;

 	
 						} catch (RemoteException e) {
 							// TODO Auto-generated catch block
 							e.printStackTrace();
 						} // �ϳ��� ������ ���������� ������ �ʵ��� �ؾ��� �� �ϴ�.
 					}
 				}

 				return false;
 			}

 		});

 		leftkey.setOnTouchListener(new OnTouchListener() {

 			@Override
 			public boolean onTouch(View v, MotionEvent event) {
 				// TODO Auto-generated method stub
 				if (event.getAction() == MotionEvent.ACTION_DOWN) {
 					Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
 					vibe.vibrate(30);

 					
 					if(StandOrSit == false)
 					{
 						Toast.makeText(getApplicationContext(), "plz make NAO Stand before walk!", Toast.LENGTH_LONG).show();
 						
 					}
 					else {
 						Disable_exceptMe(storeBtn, "left_move");
 						OnlyMyself(storeBtn, "left_move");

						OperatingNow = true;

 						try {
 							Log.i("hyem", "clicked");
 	
 							
 							Log.i("hyem", "moving start");
 	
 							naostatus.setImageResource(R.drawable.busy);
 	
 							if (-0.03 < simul_angle_yaw && simul_angle_yaw < 0.03) {
 								socket_serv
 										.sendMsg("ALMotion^moveToward^0.0^0.4^0.0");
 	
 							} else {
 	
 								socket_serv
 										.sendMsg("ALMotion^setAngles^HeadYaw^0.0^0.5");
 	
 								socket_serv.sendMsg("ALMotion^moveTo^0.0^0.0^"
 										+ simul_angle_yaw);
 	
 								socket_serv
 										.sendMsg("ALMotion^waitUntilMoveIsFinished");
 	
 								socket_serv
 										.sendMsg("ALMotion^moveToward^0.0^0.5^0.0");
 	
 								simul_angle_yaw = 0;
 	
 							}
 	
 						} catch (RemoteException e) {
 							// TODO Auto-generated catch block
 							e.printStackTrace();
 						}
 					}

 				} else if (event.getAction() == MotionEvent.ACTION_UP) {

 					if(StandOrSit == false)
 					{
 						
 					} else {
 						try {
 	
 							last_order_end = System.currentTimeMillis();
 	
 							socket_serv.sendMsg("ALMotion^stopMove");
 	
 							while (System.currentTimeMillis() < last_order_end + 3000) {
 	
 							}
 							naostatus.setImageResource(R.drawable.ready);
 							Enable_exceptMe(storeBtn, "left_move");
 							ShowOthers(storeBtn, "left_move");

							OperatingNow = false;

 	
 						} catch (RemoteException e) {
 							// TODO Auto-generated catch block
 							e.printStackTrace();
 						} // �ϳ��� ������ ���������� ������ �ʵ��� �ؾ��� �� �ϴ�.

 					}
 				}
 					

 				return false;
 			}

 		});

 		backkey.setOnTouchListener(new OnTouchListener() {

 			@Override
 			public boolean onTouch(View arg0, MotionEvent arg1) {
 				// TODO Auto-generated method stub
 				
 				if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
 					Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
 					vibe.vibrate(30);
 					
 					if(StandOrSit == false)
 					{
 						Toast.makeText(getApplicationContext(), "plz make NAO Stand before walk!", Toast.LENGTH_LONG).show();
 						
 					}
 					else {
 						Disable_exceptMe(storeBtn, "back_move");
 						OnlyMyself(storeBtn, "back_move");

						OperatingNow = true;

 	
 						try {
 							Log.i("hyem", "clicked");
 	
 							
 	
 							Log.i("hyem", "moving start");
 	
 							naostatus.setImageResource(R.drawable.busy);
 	
 							if (-0.03 < simul_angle_yaw && simul_angle_yaw < 0.03) {
 								socket_serv
 										.sendMsg("ALMotion^moveToward^-0.3^0.0^0.0");
 	
 							} else {
 	
 								socket_serv
 										.sendMsg("ALMotion^setAngles^HeadYaw^0.0^0.5");
 	
 								socket_serv.sendMsg("ALMotion^moveTo^0.0^0.0^"
 										+ (simul_angle_yaw));
 	
 								socket_serv
 										.sendMsg("ALMotion^waitUntilMoveIsFinished");
 	
 								socket_serv
 										.sendMsg("ALMotion^moveToward^-0.3^0.0^0.0");
 	
 								simul_angle_yaw = 0;
 	
 							}
 	
 						} catch (RemoteException e) {
 							// TODO Auto-generated catch block
 							e.printStackTrace();
 						}
 					}

 				} else if (arg1.getAction() == MotionEvent.ACTION_UP) {

 					if(StandOrSit == false)
 					{
 						
 					} else {
 						try {
 	
 							last_order_end = System.currentTimeMillis();
 	
 							socket_serv.sendMsg("ALMotion^stopMove");
 	
 							while (System.currentTimeMillis() < last_order_end + 3000) {
 	
 							}
 	
 							naostatus.setImageResource(R.drawable.ready);
 							Enable_exceptMe(storeBtn, "back_move");
 							ShowOthers(storeBtn, "back_move");

							OperatingNow = false;

 	
 						} catch (RemoteException e) {
 							// TODO Auto-generated catch block
 							e.printStackTrace();
 						} // �ϳ��� ������ ���������� ������ �ʵ��� �ؾ��� �� �ϴ�.
 					}

 				}

 				return false;
 			}

 		});

 		forwardkey.setOnTouchListener(new OnTouchListener() {

 			// �ٵ� ���?�̰� �������� ó���� ���� ���� �ʳ�?
 			// �׳� �������� �˾Ƽ� ���� ������ headyaw�� ������ �������� �о �װ� ó���϶��?�ϸ� ���� �ʳ�
 			// ���� ������ ����ġ -> ������ ��ŭ �� ȸ�� -> �� ������ �� ���� gogo
 			@Override
 			public boolean onTouch(View v, MotionEvent event) {
 				// TODO Auto-generated method stub

 				if (event.getAction() == MotionEvent.ACTION_DOWN) {
 					Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
 					vibe.vibrate(30);

 					if(StandOrSit == false)
 					{
 						Toast.makeText(getApplicationContext(), "plz make NAO Stand before walk!", Toast.LENGTH_LONG).show();
 						
 					} else {
 						Disable_exceptMe(storeBtn, "forward_move");
 						OnlyMyself(storeBtn, "forward_move");

						OperatingNow = true;


 						try {
 	
 							
 							Log.i("hyem", "moving start");
 	
 							naostatus.setImageResource(R.drawable.busy);
 							if (-0.03 < simul_angle_yaw && simul_angle_yaw < 0.03) {
 								socket_serv
 										.sendMsg("ALMotion^moveToward^0.3^0.0^0.0");
 							} else {
 	
 								socket_serv
 										.sendMsg("ALMotion^setAngles^HeadYaw^0.0^0.5");
 	
 								socket_serv.sendMsg("ALMotion^moveTo^0.0^0.0^"
 										+ simul_angle_yaw);
 	
 								socket_serv
 										.sendMsg("ALMotion^waitUntilMoveIsFinished");
 	
 								socket_serv
 										.sendMsg("ALMotion^moveToward^0.5^0.0^0.0");
 	
 								simul_angle_yaw = 0;
 	
 							}
 	
 						} catch (RemoteException e) {
 							// TODO Auto-generated catch block
 							e.printStackTrace();
 						}
 					}

 				} else if (event.getAction() == MotionEvent.ACTION_UP) {

 					if(StandOrSit == false)
 					{
 						Toast.makeText(getApplicationContext(), "plz make NAO Stand before walk!", Toast.LENGTH_LONG).show();
 						
 					} else {
 						try {
 	
 							last_order_end = System.currentTimeMillis();
 	
 							socket_serv.sendMsg("ALMotion^stopMove");
 	
 							while (System.currentTimeMillis() < last_order_end + 3000) {
 	
 							}
 							naostatus.setImageResource(R.drawable.ready);
 	
 							Enable_exceptMe(storeBtn, "forward_move");
 							ShowOthers(storeBtn, "forward_move");

							OperatingNow = false;

 	
 						} catch (RemoteException e) {
 							// TODO Auto-generated catch block
 							e.printStackTrace();
 						} // �ϳ��� ������ ���������� ������ �ʵ��� �ؾ��� �� �ϴ�.
 					}

 				}
 				return false;
 			}

 		});

		// //////////////////////////////////////////////////////////////////////////////////////////
		// ///////////////////////////////// move NAO's Head
		// //////////////////////////////////////////////////////////////////////////////////////////


		leftBtn.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
					vibe.vibrate(30);

					OperatingNow = true;


					Disable_exceptMe(storeBtn, "left_shake");
					OnlyMyself(storeBtn, "left_shake");

					button_start_left = System.currentTimeMillis();

					naostatus.setImageResource(R.drawable.busy);

				}

				else if (event.getAction() == MotionEvent.ACTION_UP) {
					button_end_left = System.currentTimeMillis();

					int duration = (int) button_end_left
							- (int) button_start_left;

					float duration_f = (float) duration / 1000;

					if ((simul_angle_yaw + duration_f > 1.2)) {
						try {
							simul_angle_yaw = (float) 1.2;

							socket_serv
									.sendMsg("ALMotion^setAngles^HeadYaw^1.2^0.1");

							last_order_start = System.currentTimeMillis();
							last_order_end = last_order_start + (long) duration;

						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

					else if ((simul_angle_yaw + duration_f <= 1.2)) {
						try {

							socket_serv
									.sendMsg("ALMotion^changeAngles^HeadYaw^"
											+ duration_f + "^0.1");

							last_order_start = System.currentTimeMillis();
							last_order_end = last_order_start + (long) duration;

							simul_angle_yaw = simul_angle_yaw + duration_f;

						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
					while (System.currentTimeMillis() < last_order_end + 2000) {

					}
					naostatus.setImageResource(R.drawable.ready);
					Enable_exceptMe(storeBtn, "left_shake");
					ShowOthers(storeBtn, "left_shake");

					OperatingNow = false;


				}

				return false;

			}
		});

///////////////////////////20140123 modified ///////////////////////////////////////////////

		rightBtn.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				Runnable mLongPressed = new Runnable()	{

					public void run(){
						
						try{
							socket_serv
							.sendMsg("ALMotion^changeAngles^HeadYaw^-0.15^0.05");
							Log.i("MS","turn right");
						} catch(RemoteException e) { e.printStackTrace();}							

					}
				};			    
			    
				v.setClickable(true);
			
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
					vibe.vibrate(30);

					OperatingNow = true;
					Log.i("MS","ACTioNDOWN");

					Disable_exceptMe(storeBtn, "right_shake");
					OnlyMyself(storeBtn, "right_shake");
					
					naostatus.setImageResource(R.drawable.busy);
			
					handler.post(mLongPressed);
					
				}else  if((event.getAction() == MotionEvent.ACTION_MOVE)||(event.getAction() == MotionEvent.ACTION_UP)){
					handler.removeCallbacks(mLongPressed);
					Log.i("MS","ActionUP");
					naostatus.setImageResource(R.drawable.ready);
					Enable_exceptMe(storeBtn, "right_shake");
					ShowOthers(storeBtn, "right_shake");

					OperatingNow = false;
				}
				return true;
			}

		});
		
		
		
	
	
		
///////////////////////////20140123 modified ///////////////////////////////////////////////

//		rightBtn.setOnTouchListener(new OnTouchListener() {
//
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				if (event.getAction() == MotionEvent.ACTION_DOWN) {
//					Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//					vibe.vibrate(30);
//
//					OperatingNow = true;
//
//
//					Disable_exceptMe(storeBtn, "right_shake");
//					OnlyMyself(storeBtn, "right_shake");
//
//					button_start_right = System.currentTimeMillis();
//
//					naostatus.setImageResource(R.drawable.busy);
//
//				} else if (event.getAction() == MotionEvent.ACTION_UP) {
//
//					button_end_right = System.currentTimeMillis();
//
//					int duration = (int) button_end_right
//							- (int) button_start_right;
//
//					float duration_f = (float) -duration / 1000;
//
//					if ((simul_angle_yaw + duration_f < -1.2)) {
//						try {
//							simul_angle_yaw = (float) -1.2;
//
//							socket_serv
//									.sendMsg("ALMotion^setAngles^HeadYaw^-1.2^0.1");
//
//							last_order_start = System.currentTimeMillis();
//							last_order_end = last_order_start + (long) duration;
//
//						} catch (RemoteException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//
//					}
//
//					else if ((simul_angle_yaw + duration_f >= -1.2)) {
//						try {
//
//							socket_serv
//									.sendMsg("ALMotion^changeAngles^HeadYaw^"
//											+ duration_f + "^0.1");
//
//							last_order_start = System.currentTimeMillis();
//							last_order_end = last_order_start + (long) duration;
//
//							simul_angle_yaw = simul_angle_yaw + duration_f;
//
//						} catch (RemoteException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//
//					}
//					while (System.currentTimeMillis() < last_order_end + 2000) {
//
//					}
//					naostatus.setImageResource(R.drawable.ready);
//					Enable_exceptMe(storeBtn, "right_shake");
//					ShowOthers(storeBtn, "right_shake");
//
//					OperatingNow = false;
//
//
//				}
//				return false;
//			}
//
//		});

		upBtn.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
					vibe.vibrate(30);

					OperatingNow = true;

					Disable_exceptMe(storeBtn, "up_shake");
					OnlyMyself(storeBtn, "up_shake");

					button_start_up = System.currentTimeMillis();

					naostatus.setImageResource(R.drawable.busy);

				} else if (event.getAction() == MotionEvent.ACTION_UP) {

					button_end_up = System.currentTimeMillis();

					int duration = (int) button_end_up - (int) button_start_up;

					last_order_end = System.currentTimeMillis()
							+ (long) duration;

					float duration_f = (float) -duration / 500;

					if ((simul_angle_pitch + duration_f < -0.25)) {
						try {
							simul_angle_pitch = (float) -0.25;
							socket_serv
									.sendMsg("ALMotion^setAngles^HeadPitch^-0.25^0.1");
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

					else if ((simul_angle_pitch + duration_f >= -0.25)) {
						try {
							socket_serv
									.sendMsg("ALMotion^changeAngles^HeadYaw^"
											+ duration_f + "^0.1");
							simul_angle_pitch = simul_angle_pitch + duration_f;
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
					while (System.currentTimeMillis() < last_order_end + 2000) {

					}
					naostatus.setImageResource(R.drawable.ready);
					Enable_exceptMe(storeBtn, "up_shake");
					ShowOthers(storeBtn, "up_shake");

					OperatingNow = false;


				}
				return false;

			}

		});

		downBtn.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
					vibe.vibrate(30);
					Disable_exceptMe(storeBtn, "down_shake");
					OnlyMyself(storeBtn, "down_shake");

					OperatingNow = true;


					button_start_down = System.currentTimeMillis();

					naostatus.setImageResource(R.drawable.busy);

				} else if (event.getAction() == MotionEvent.ACTION_UP) {

					button_end_down = System.currentTimeMillis();

					int duration = (int) button_end_down
							- (int) button_start_down;

					last_order_end = System.currentTimeMillis()
							+ (long) duration;

					float duration_f = (float) duration / 500;

					if ((simul_angle_pitch + duration_f > 0.25)) {
						try {
							simul_angle_pitch = (float) 0.25;
							socket_serv
									.sendMsg("ALMotion^setAngles^HeadPitch^0.25^0.1");
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

					else if ((simul_angle_pitch + duration_f <= 0.25)) {
						try {
							socket_serv
									.sendMsg("ALMotion^changeAngles^HeadPitch^"
											+ duration_f + "^0.1");
							simul_angle_pitch = simul_angle_pitch + duration_f;
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

					while (System.currentTimeMillis() < last_order_end + 2000) {

					}
					naostatus.setImageResource(R.drawable.ready);
					Enable_exceptMe(storeBtn, "down_shake");
					ShowOthers(storeBtn, "down_shake");

					OperatingNow = false;

				}
				return false;
			}

		});

		
		/*codes related to Gsteramer
		
		ConnThread con_Thread2 = new ConnThread();
		con_Thread2.IP = "192.168.0.9";
		con_Thread2.PORT = 5100;
		con_Thread2.m_socket = new Socket();

		con_Thread2.execute();

		try {
			Log.i("hyem", "waiting..");
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (con_Thread2.m_socket.isConnected())
		{
			OutputStream out;
			try {
				out = con_Thread2.m_socket.getOutputStream();
				PrintWriter pw = new PrintWriter(new OutputStreamWriter(out));
				// pw.println(makeInstr(msg));
				pw.write("192.168.0.33");
				pw.flush();
				Log.i("gst","sending ip...");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//SystemClock.sleep(500);
		Log.i("gst","start gst...");
		gstsetting();
		*/
	}

	/*
	private void gstsetting() {
		try
		{
        GStreamer.init(this);
		} catch (Exception e) 
		{
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        finish(); 
        return;
		}
		nativeInit();

		is_playing_desired = true;
		nativePlay();
	}
	*/

	public class moveBtn {
		ImageButton mainBtn;
		ImageButton lieBtn;
		String name;

		moveBtn(ImageButton main, ImageButton lie, String n) {
			this.mainBtn = main;
			this.lieBtn = lie;
			this.name = n;
		}

		void showMain() {

			lieBtn.setVisibility(View.GONE);
			mainBtn.setVisibility(View.VISIBLE);
		}

		void showLie() {
			mainBtn.setVisibility(View.GONE);
			lieBtn.setVisibility(View.VISIBLE);
		}

		void click_disable() {
			mainBtn.setEnabled(false);
		}

		void click_able() {
			mainBtn.setEnabled(true);

		}
	}

	public void Disable_exceptMe(moveBtn[] store, String nameBtn) {
		int length = store.length;
		for (int i = 0; i < length; i++) {
			if (!store[i].name.equals(nameBtn)) {
				store[i].click_disable();
			} else {
				continue;
			}
		}
	}

	public void Disable_All(moveBtn[] store) {
		for (int i = 0; i < store.length; i++) {
			store[i].click_disable();
		}
	}

	public void Enable_exceptMe(moveBtn[] store, String nameBtn) {
		int length = store.length;
		for (int i = 0; i < length; i++) {
			if (!store[i].name.equals(nameBtn)) {
				store[i].click_able();
			} else {
				continue;
			}
		}
	}

	public void Enable_All(moveBtn[] store) {
		for (int i = 0; i < store.length; i++) {
			store[i].click_able();
		}
	}

	public void OnlyMyself(moveBtn[] store, String nameBtn) {
		int length = store.length;
		for (int i = 0; i < length; i++) {
			if (!store[i].name.equals(nameBtn)) {
				store[i].showLie();
			} else {
				continue;
			}
		}
	}

	public void CoverAll(moveBtn[] store) {
		int length = store.length;
		for (int i = 0; i < length; i++) {
			store[i].showLie();
		}
	}

	public void ShowAll(moveBtn[] store) {
		int length = store.length;
		for (int i = 0; i < length; i++) {
			store[i].showMain();
		}
	}

	public void ShowOthers(moveBtn[] store, String nameBtn) {
		int length = store.length;
		for (int i = 0; i < length; i++) {
			if (!store[i].name.equals(nameBtn)) {
				store[i].showMain();
			} else {
				continue;
			}
		}
	}

	public void angleTest(View view) {

		try {
			head_angle_yaw = Float.parseFloat(socket_serv
					.Send_recvMsg("ALMotion^getAngles^HeadYaw^false"));

			Log.i("angle", "yaw simulated : " + simul_angle_yaw + ", Real : "
					+ head_angle_yaw);

			head_angle_pitch = Float.parseFloat(socket_serv
					.Send_recvMsg("ALMotion^getAngles^HeadPitch^false"));

			Log.i("angle", "pitch simulated : " + simul_angle_pitch
					+ ", Real : " + head_angle_pitch);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public InputFilter filterAlphaNum = new InputFilter() {

		@Override
		public CharSequence filter(CharSequence source, int start, int end,
				Spanned dest, int dstart, int dend) {
			// TODO Auto-generated method stub

			Pattern ps = Pattern.compile("^[a-zA-Z0-9]+$");
			if (!ps.matcher(source).matches()) {
				return "";
			}
			return null;
		}

	};

	public void RecordVoice(View view) {
		Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vibe.vibrate(30);

		Intent recording = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

		recording.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		recording.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
		recording.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak through NAO");

		startActivityForResult(recording, 1);

	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		EditText things_to_say = (EditText) findViewById(R.id.wordsforNAO);

		if (requestCode == 1 && resultCode == RESULT_OK) {
			ArrayList<String> results = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

			things_to_say.setText("");

			things_to_say.append(results.get(1));
		}

		super.onActivityResult(requestCode, resultCode, data);
	}


	public void NAOspeak(View view) {
		Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vibe.vibrate(30);

		EditText things_to_say = (EditText) findViewById(R.id.wordsforNAO);
		String msg_to_say = things_to_say.getText().toString();
		
		
		try {
			socket_serv.sendMsg("ALTextToSpeech^say^" + msg_to_say);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void callMenu(View view) {
		Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vibe.vibrate(30);

		Intent myIntent = new Intent(getApplicationContext(), MenuTab.class);

		startActivity(myIntent);

		onPause();


	}

	@Override
	public void onPause() {
		super.onPause();
		 
		thread_killer = true;
		
		if(thread_watch != null)
		{
			thread_watch = null;
		}
		
		OperatingNow = true;

		Log.i("hyem", "activity is paused");
	}

	@Override
	public void onStop() {
		super.onStop();
		
		Log.i("hyem", "activity is stopped");
	}

	@Override
	public void onResume() {
		super.onResume();

		thread_killer = false;
		
		thread_watch_birth = true;
		
		watching_order();
				
		OperatingNow = false;
		Log.i("hyem", "activity is resumed");
	}

	@Override
	public void onRestart() {
		super.onRestart();
		Log.i("hyem", "activity is restarted");
	}

	@Override
	public void finish() {

		/*codes related to Gsteramer
		 //is_playing_desired = false;
         //nativePause();
		nativeFinalize();
		*/
		try {

			thread_killer = true;
			
			if(thread_watch != null)
			{
				thread_watch = null;
			}
			
			OperatingNow = true;
			

			socket_serv.sendMsg("ALRobotPosture^goToPosture^Sit^1.0");
			socket_serv.sendMsg("ALMotion^setStiffnesses^Body^0.0");

			socket_serv.sendMsg("close");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getApplicationContext().unbindService(servConn);
		Log.i("hyem", "unbind by camera finish");

		getApplicationContext().stopService(
				new Intent(getApplicationContext(), SocketService.class));
		// �̰� ������ �࿩�� ���� ���� ������ �ȴ�.

		super.finish();

	}



	private void watching_order(){
		thread_watch = new Thread(null, startWatching, "watching");
		
		thread_watch.start();
		
		Log.i("watch","watch_start");
		
	}
	
	private Runnable startWatching = new Runnable()
	{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			do_Watch();
			
		}
		
	};
	
	private void do_Watch()
	{
	//	long start_time = System.currentTimeMillis();
		boolean working_now = OperatingNow;		
	
		instruction[0] ="ALMemory^getData^Device/SubDeviceList/HeadPitch/Temperature/Sensor/Value";		

		instruction[1] = "ALMemory^getData^Device/SubDeviceList/HeadYaw/Temperature/Sensor/Value";

		instruction[2] = "ALMemory^getData^Device/SubDeviceList/LAnklePitch/Temperature/Sensor/Value";
		instruction[3] = "ALMemory^getData^Device/SubDeviceList/LAnkleRoll/Temperature/Sensor/Value";
		instruction[4] = "ALMemory^getData^Device/SubDeviceList/LElbowRoll/Temperature/Sensor/Value";
		instruction[5] = "ALMemory^getData^Device/SubDeviceList/LElbowYaw/Temperature/Sensor/Value";
		instruction[6] = "ALMemory^getData^Device/SubDeviceList/LHand/Temperature/Sensor/Value";
		instruction[7] = "ALMemory^getData^Device/SubDeviceList/LHipPitch/Temperature/Sensor/Value";
		instruction[8] = "ALMemory^getData^Device/SubDeviceList/LHipRoll/Temperature/Sensor/Value";
		instruction[9] = "ALMemory^getData^Device/SubDeviceList/LHipYawPitch/Temperature/Sensor/Value";
		instruction[10] = "ALMemory^getData^Device/SubDeviceList/LKneePitch/Temperature/Sensor/Value";
		instruction[11] = "ALMemory^getData^Device/SubDeviceList/LShoulderPitch/Temperature/Sensor/Value";
		instruction[12] = "ALMemory^getData^Device/SubDeviceList/LShoulderRoll/Temperature/Sensor/Value";
		instruction[13] = "ALMemory^getData^Device/SubDeviceList/LWristYaw/Temperature/Sensor/Value";

		instruction[14] = "ALMemory^getData^Device/SubDeviceList/RAnklePitch/Temperature/Sensor/Value";
		instruction[15] = "ALMemory^getData^Device/SubDeviceList/RAnkleRoll/Temperature/Sensor/Value";
		instruction[16] = "ALMemory^getData^Device/SubDeviceList/RElbowRoll/Temperature/Sensor/Value";
		instruction[17] = "ALMemory^getData^Device/SubDeviceList/RElbowYaw/Temperature/Sensor/Value";
		instruction[18] = "ALMemory^getData^Device/SubDeviceList/RHand/Temperature/Sensor/Value";
		instruction[19] = "ALMemory^getData^Device/SubDeviceList/RHipPitch/Temperature/Sensor/Value";
		instruction[20] = "ALMemory^getData^Device/SubDeviceList/RHipRoll/Temperature/Sensor/Value";
		instruction[21] = "ALMemory^getData^Device/SubDeviceList/RKneePitch/Temperature/Sensor/Value";
		instruction[22] = "ALMemory^getData^Device/SubDeviceList/RShoulderPitch/Temperature/Sensor/Value";
		instruction[23] = "ALMemory^getData^Device/SubDeviceList/RShoulderRoll/Temperature/Sensor/Value";
		instruction[24] = "ALMemory^getData^Device/SubDeviceList/RWristYaw/Temperature/Sensor/Value";
		instruction[25] = "ALMemory^getData^Device/SubDeviceList/Battery/Charge/Sensor/Value";
		
		try {
			Thread cur_thread = Thread.currentThread();
			cur_thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		while( thread_killer == false )
		{
			//200�ʿ� �ѹ� üũ����.
			//long check_time = System.currentTimeMillis();
			boolean working_new = OperatingNow; 
			
			if( ((working_now == true) && (working_new == false)) || thread_watch_birth == true)
			{
				Log.i("watch","getting data...");
				
				thread_watch_birth = false;
				
				handler_wat.post(toast_rest);
				
				long start_time = System.currentTimeMillis();
				
				while(OperatingNow != true)
				{
					//��ٸ����ִ� �ð��� �־�� �� ��.... ���ʿ� �ѹ� �̶��
					long check_time = System.currentTimeMillis();
					
					if( ( check_time - start_time) % 10000 == 0 )
					{
						if(data_order == 25)
						{					
							
							 try {
								bat_charge = Float.parseFloat( socket_serv.Send_recvMsg(instruction[data_order]) );
								data_order = 0;
								handler_wat.post(toast_batt);
								
								
							} catch (NumberFormatException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							 
						} else {
							
							try {
								
								calculate_max( Integer.parseInt(socket_serv.Send_recvMsg(instruction[data_order])) );
								
								data_order = data_order + 1;
								
								handler_wat.post(toast_motor);
								
								//pause �Ǿ��� ���� �� �����带 ������Ŵ
								//finish �Ǿ��� ���� �ƿ� ���߾� ������.
								
								
							} catch (NumberFormatException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						
						
					}
					else 
						continue;
					
					
					//data_order �� �´� ��ɾ� �����ͼ� �ӹ�����
					//���� data_order�� �´� ��ɾ ������ �� �ʿ�.
					
				}
				
				
				working_now = working_new;
				
			} else if ( (working_now == false) && (working_new == true) )
			{
				Log.i("watch","getting rest");
				
				handler_wat.post(toast_work);
				working_now = working_new;
			}
			else{
				continue;
			}
		}
		
		Log.i("watch","finished");
		
	}
	
	private Runnable toast_batt = new Runnable()
	{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			start_toasting_batt();
		}
		
	};
	
	private void start_toasting_batt()
	{
		//Toast.makeText(getApplicationContext(), "batt", Toast.LENGTH_SHORT).show();
		show_battery(bat_charge);
		
	}
	
	private Runnable toast_motor = new Runnable()
	{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			start_toasting_motor();
		}
		
	};
	
	private void start_toasting_motor()
	{
		//Toast.makeText(getApplicationContext(), "motor", Toast.LENGTH_SHORT).show();
		show_motor(motor_max_temp);
		//���� ���⿡�ٰ� ���͸�, ���Ͱ����� �ϳ��� �������� �ϴ� �Լ��� ������
				//�� �Լ� ���� ������ �ؿ��� ����!
		//�׸��� rest �� ������ �� ���ϱ� ����
		//�� �Լ������� ���� operating now�� true ������ Ȯ���ؾ���. ���� true 
	}
	
	
	private Runnable toast_work = new Runnable()
	{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			start_toasting_work();
		}
		
	};
	
	private void start_toasting_work()
	{
		Toast.makeText(getApplicationContext(), "work", Toast.LENGTH_SHORT).show();
		
	}
	
	private Runnable toast_rest = new Runnable()
	{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			start_toasting_rest();
		}
		
	};
	
	private void start_toasting_rest()
	{
		Toast.makeText(getApplicationContext(), "rest", Toast.LENGTH_SHORT).show();
		
	}
	
	///////////////////////
	///////////���͸� �ܷ� ǥ��////////////
	
	private void show_battery(float bat_charge)
	{
		final ImageButton battery = (ImageButton) findViewById(R.id.battery);
		Log.i("battery", "left= " + bat_charge * 100);

		if (0.9 <= bat_charge && bat_charge <= 1.0) {
			battery.setImageResource(R.drawable.battery_100);
			}
		else if (0.8 <= bat_charge && bat_charge <= 0.9) {
			battery.setImageResource(R.drawable.battery_90);
		}
		else if (0.7 <= bat_charge && bat_charge <= 0.8) {
			battery.setImageResource(R.drawable.battery_80);
		} else if (0.6 <= bat_charge && bat_charge <= 0.7) {
			battery.setImageResource(R.drawable.battery_70);
		} else if (0.5 <= bat_charge && bat_charge <= 0.6) {
			battery.setImageResource(R.drawable.battery_60);
		} else if (0.4 <= bat_charge && bat_charge <= 0.5) {
			battery.setImageResource(R.drawable.battery_50);
		} else if (0.3 <= bat_charge && bat_charge <= 0.4) {
			battery.setImageResource(R.drawable.battery_40);
		} else if (0.2 <= bat_charge && bat_charge <= 0.3) {
			battery.setImageResource(R.drawable.battery_30);
		} else if (0.1 <= bat_charge && bat_charge <= 0.2) {
			battery.setImageResource(R.drawable.battery_20);
		} else if (0.0 <= bat_charge && bat_charge <= 0.1) {
			battery.setImageResource(R.drawable.battery_10);
		}
		
	}
	
	/////���� �µ� ǥ��//////////
	private void show_motor(int max_temp)
	{
		final ImageButton nao_motor = (ImageButton) findViewById(R.id.naohead);
		
		if (max_temp <= 50) {
			nao_motor.setImageResource(R.drawable.motor_1);

		} else if (50 < max_temp && max_temp <= 55) {
			nao_motor.setImageResource(R.drawable.motor_2);
		} else if (55 < max_temp && max_temp <= 60) {
			nao_motor.setImageResource(R.drawable.motor_3);
		} else if (60 < max_temp && max_temp <= 65) {
			nao_motor.setImageResource(R.drawable.motor_4);
		} else if (65 < max_temp) {
			nao_motor.setImageResource(R.drawable.motor_5);
		}
		
	}
	//////////////////////���� �ִ� ���ϴ� �Լ�/////
	private void calculate_max(int new_temp)
	{
		if(motor_max_temp < new_temp)
		{
			motor_max_temp = new_temp;
		}
		else
		{
			
		}	
		
	}
	//////////////
	

	private Runnable doUpdateGUI = new Runnable(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
			updateGUI();
		}
		
	};
	
	private void updateGUI()
	{
		show_battery(bat_charge);
		
		final ImageView naostatus = (ImageView) findViewById(R.id.naostatus);

		naostatus.setImageResource(R.drawable.ready);

		ShowAll(storeBtn);

		Enable_All(storeBtn);
		
		 if (loadingDialog != null) {
			   loadingDialog.dismiss();
		       loadingDialog = null;
		    }
		
				 
		OperatingNow = false;
		
		//
		
		
		
		
	}
	private void backgroundThreadProcessing()
	{
		try {
			Thread.sleep(500);
			
			
			//
						
//			socket_serv.sendMsg("ALRobotPosture^goToPosture^Stand^1.0");			
			socket_serv.sendMsg("ALMotion^setAngles^HeadYaw^0.0^0.1");
			socket_serv.sendMsg("ALMotion^setAngles^HeadPitch^0.0^0.1");
			
		
			String leftBattery = socket_serv
					.Send_recvMsg("ALMemory^getData^Device/SubDeviceList/Battery/Charge/Sensor/Value");

			bat_charge = Float.parseFloat(leftBattery);

			
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		
		handler.post(doUpdateGUI);
		
	}
	private Runnable doBackgroundThreadProcessing = new Runnable(){

		@Override
		public void run() {
			backgroundThreadProcessing();
			// TODO Auto-generated method stub
			
		}
		
	};





	private ServiceConnection servConn = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			Log.i("hyem", "start Binding");

			socket_serv = socketAidl.Stub.asInterface(service);


			thread_bind = new Thread(null, doBackgroundThreadProcessing, "Background");
			thread_bind.start();
			

			

		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			Log.i("hyem", "start unBinding");

			
			if( (thread_bind != null) && (thread_bind.isAlive()) )
			{
				Log.i("thread","bind alive, interrupt");
				thread_bind.interrupt();
			}
			socket_serv = null;
		}

	};
	
	
	
	/*codes related to Gsteramer
	/////////////////////////////////////////
	
	 protected void onSaveInstanceState (Bundle outState) {
	        Log.d ("GStreamer", "Saving state, playing:" + is_playing_desired);
	        outState.putBoolean("playing", is_playing_desired);
	    }
	 
	 private void setMessage(final String message) {
	    }

	 private void onGStreamerInitialized () {
	        Log.i ("GStreamer", "Gst initialized. Restoring state, playing:" + is_playing_desired);
	        // Restore previous playing state
	        if (is_playing_desired) {
	            nativePlay();
	        } else {
	            nativePause();
	        }

	       
	    }
	 static {
	        System.loadLibrary("gstreamer_android");
	        System.loadLibrary("tutorial-3");
	        nativeClassInit();
	    }
	 
	 public void surfaceChanged(SurfaceHolder holder, int format, int width,
	            int height) {
	        Log.d("GStreamer", "Surface changed to format " + format + " width "
	                + width + " height " + height);
	        nativeSurfaceInit (holder.getSurface());
	    }

	    public void surfaceCreated(SurfaceHolder holder) {
	        Log.d("GStreamer", "Surface created: " + holder.getSurface());
	    }

	    public void surfaceDestroyed(SurfaceHolder holder) {
	        Log.d("GStreamer", "Surface destroyed");
	        nativeSurfaceFinalize ();
	    }


	 */
}
