package hyem.example.rideonnao;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class Motion extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nao_motion);
	}
	
	public static class MotionFragment extends Fragment {
		public View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
			View root = inflater.inflate(R.layout.nao_motion, container, false);
			
			ImageButton nao_standup = (ImageButton)root.findViewById(R.id.nao_standup);
			
			nao_standup.setOnClickListener(new ImageButton.OnClickListener(){
				public void onClick(View v){
//					socket_serv.sendMsg("ALRobotPosture^goToPosture^Stand^1.0");
				}
			});
			
			
			return root;
			
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.motion, menu);
		return true;
	}

}
