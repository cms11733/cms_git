package hyem.example.clientService;

import hyem.example.clientService.socketAidl;
import hyem.example.rideonnao.Camera;
import hyem.example.rideonnao.EnterIP;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

public class SocketService extends Service {

	public static Socket m_socket;

	protected ConnThread con_Thread;
	protected MsgThread msg_Thread;
	protected RecvThread recv_Thread;

	protected String IP = EnterIP.IP;
	protected int PORT = EnterIP.PORT;
	protected int order_number_RECV = 0;
	protected int order_number_SEND = 0;

	protected SocketService soc_ser = this;
	public static Queue<String> msg_queue = new LinkedList<String>();
	Queue<MsgThread> msg_Thread_Q = new LinkedList<MsgThread>();

	public static Queue<Msg_Saver> RECV_msg_saver_Q = new LinkedList<Msg_Saver>();
	public static Queue<Msg_Saver> SEND_msg_saver_Q = new LinkedList<Msg_Saver>();

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Log.i("Hyem", "onBind()");

		return mBinder;
	}

	socketAidl.Stub mBinder = new socketAidl.Stub() {

		@Override
		public void sendMsg(String message) throws RemoteException {
			// TODO Auto-generated method stub

			order_number_SEND = order_number_SEND + 1;

			Msg_Saver new_msg = new Msg_Saver(message, order_number_SEND);

			SEND_msg_saver_Q.offer(new_msg);

			while (true) {
				if (SEND_msg_saver_Q.peek().order_num == new_msg.order_num) {
					Log.i("SEND", "front number = "
							+ SEND_msg_saver_Q.peek().order_num
							+ " msg number = " + new_msg.order_num);

					msg_Thread = new MsgThread();
					msg_Thread.msg = message;
					msg_Thread.m_socket = m_socket;

					try {
						msg_Thread.execute().get(10000, TimeUnit.MILLISECONDS);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (TimeoutException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					SEND_msg_saver_Q.remove();

					break;

				} else {
					continue;
				}
			}

			/*
			 * msg_Thread = new MsgThread(); msg_Thread.msg = message;
			 * msg_Thread.m_socket = m_socket;
			 * 
			 * msg_Thread_Q.offer(msg_Thread);
			 * 
			 * 
			 * /* MsgThread sending_Thread = msg_Thread_Q.poll();
			 * sending_Thread.execute(); try { Thread.sleep(300);
			 * Log.i("hyem","waiting.."); } catch (InterruptedException e) { //
			 * TODO Auto-generated catch block e.printStackTrace(); }
			 */
			if (msg_Thread.connected == 0)
				Toast.makeText(getApplicationContext(),
						"Can't Order to NAO, plz log in again",
						Toast.LENGTH_SHORT).show();

		}

		@Override
		public String recvMsg() throws RemoteException {
			// TODO Auto-generated method stub

			recv_Thread = new RecvThread();
			recv_Thread.m_socket = m_socket;

			try {
				recv_Thread.execute().get(15000, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (recv_Thread.connected == 0) {
				Toast.makeText(getApplicationContext(),
						"Can't Recv Msg From NAO, plz log in again",
						Toast.LENGTH_SHORT).show();
				return null;
			}

			else {
				return recv_Thread.recv_msg;
			}

		}

		@Override
		public String Send_recvMsg(String message) throws RemoteException {
			// TODO Auto-generated method stub
			order_number_RECV = order_number_RECV + 1;
			String result_msg = "";
			Msg_Saver new_msg = new Msg_Saver(message, order_number_RECV);

			RECV_msg_saver_Q.offer(new_msg);

			while (true) {
				if (RECV_msg_saver_Q.peek().order_num == new_msg.order_num) {
					Log.i("RECV", "front number = "
							+ RECV_msg_saver_Q.peek().order_num
							+ " msg number = " + new_msg.order_num);

					/*

					MsgThread newThread = new MsgThread();
					newThread.msg = message;
					newThread.m_socket = m_socket;

					try {
						newThread.execute().get(10000, TimeUnit.MILLISECONDS);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (TimeoutException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					*/

					sendMsg(message);

					result_msg = recvMsg();

					RECV_msg_saver_Q.remove();

					break;

				} else {
					continue;
				}
			}

			return result_msg;
		}
	};

	@Override
	public boolean onUnbind(Intent arg0) {
		Log.i("Hyem", "onUnbind()");
		return true;
	}

	@Override
	public void onRebind(Intent arg0) {
		Log.i("Hyem", "onRebind()");
	}

	@Override
	public void onCreate() {
		super.onCreate();

		m_socket = new Socket();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		con_Thread = new ConnThread();
		con_Thread.IP = IP;
		con_Thread.PORT = PORT;
		con_Thread.m_socket = m_socket;

		con_Thread.execute();

		try {
			Log.i("hyem", "waiting..");
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (m_socket.isConnected())

		{
			

			msg_Thread = new MsgThread();
			msg_Thread.m_socket = m_socket;
			msg_Thread.msg = "hello";
			msg_Thread.execute();

			EnterIP.EnterAct.finish();

			Context context = getApplicationContext();
			Intent startCon = new Intent(context, Camera.class);
			startCon.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(startCon);

		} else {
			Toast.makeText(this, "Connect Error. Check & Try Again",
					Toast.LENGTH_SHORT).show();

			stopService(new Intent(getApplicationContext(), SocketService.class));
		}

		return Service.START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (m_socket != null) {
			try {
				m_socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else
			Toast.makeText(this, "Null Socket?! Fatal Error!",
					Toast.LENGTH_SHORT).show();
	}

	private boolean isMyServiceRunning() {
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if ("hyem.example.servicesocket.SocService".equals(service.service
					.getClassName())) {
				return true;
			}
		}
		return false;
	}
}
