package hyem.example.clientService;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

import android.os.AsyncTask;
import android.util.Log;

public class MsgThread extends AsyncTask<Void, Void, Void> {

	protected String IP = "192.168.0.0";
	protected int PORT = 0;

	public Socket m_socket;
	public int connected = 1;
	public String msg;
	public String sending_msg;

	// protected Queue<String> msg_queue = new LinkedList<String>();

	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub

		if (m_socket.isConnected() == false)
			connected = 0;

		else {

			// connected = 1;
			try {
				/*
				 * Queue<String> msg_queue = SocketService.msg_queue; String
				 * queue_msg = this.msg;
				 * 
				 * msg_queue.offer(queue_msg);
				 * Log.i("hyem","put the "+queue_msg+" in the queue");
				 * 
				 * sending_msg = msg_queue.poll();
				 * Log.i("hyem","get "+sending_msg+" from the queue");
				 */

				OutputStream out = m_socket.getOutputStream();
				PrintWriter pw = new PrintWriter(new OutputStreamWriter(out));
				// pw.println(makeInstr(msg));
				pw.write(makeInstr(msg));
				pw.flush();
				Log.i("hyem", "Sending : " + makeInstr(msg));

				// 여기서 메세지를 가공해 주는게 더 좋지 않을까?

			} catch (IOException e) {
				e.printStackTrace();
			}

			// connected = 1;
		}

		return null;
	}

	public String makeInstr(String instr) {
		int lengthOfIns = instr.length();

		if (lengthOfIns < 10) {
			// String.format("%010d", Integer.parseInt(mystring));
			instr = String.format("%02d", lengthOfIns) + instr;
			// instr = "0" + Integer.toString(lengthOfIns) + instr;

		} else if (lengthOfIns >= 10) {

			instr = Integer.toString(lengthOfIns) + instr;

		}

		return instr;
	}
}
