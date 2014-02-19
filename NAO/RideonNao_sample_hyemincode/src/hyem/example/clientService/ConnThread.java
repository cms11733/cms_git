package hyem.example.clientService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import android.os.AsyncTask;
import android.util.Log;

public class ConnThread extends AsyncTask<Void, Void, Void> {

	public String IP = "192.168.0.0";
	public int PORT = 0;
	
	public Socket m_socket;
	
	public int DoneOrError = 0;
	
	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub
		
		SocketAddress sock_addr = new InetSocketAddress(IP, PORT);
		
		try {
			if(m_socket.isConnected() != true)
			{
				Log.i("hyem","start connecting..");
				m_socket.connect(sock_addr, 10000);
				
			} else {
				DoneOrError = 1;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			DoneOrError = 3;
		}
		
		Log.i("hyem","connection done");
		
		return null;
	}

}
