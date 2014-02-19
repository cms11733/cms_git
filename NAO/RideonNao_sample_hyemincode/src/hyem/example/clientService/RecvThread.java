package hyem.example.clientService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import android.os.AsyncTask;
import android.util.Log;

////tnwjdwnd
public class RecvThread extends AsyncTask<Void, Void, Void>{

	protected String IP = "192.168.0.0";
	protected int PORT = 0;
	
	public Socket m_socket;
	
	//
	public int connected = 1;
	//
	//public String recv_msg = null;
	public String recv_msg = null;
	
	@Override
	protected void onPostExecute(Void result)
	{
		Log.i("hyem","postexecute/");	
		
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub
		
		if(m_socket.isConnected() == false)
		{
			connected = 0;
		
		}
		else
		{
			try {
				
				InputStream in = m_socket.getInputStream();
				
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
			
				Log.i("hyem","get into recvThread");
				
			
				while( true )//( (recv_msg) = br.readLine() ) != null )
				{
					Log.i("hyem","getting message...");
					
				
					recv_msg = br.readLine();
					
					Log.i("hyem","just got message..."+recv_msg);
					
					if(recv_msg != null)
					{
						Log.i("hyem","not getting null");
					
						
						break;
					}
					else if (recv_msg == null)
					{
						Log.i("hyem","getting null");
					
						recv_msg = "null";
						
						break;
					}
				}
			
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
		Log.i("hyem","out of while");
		
		return null;
	}
	

}
