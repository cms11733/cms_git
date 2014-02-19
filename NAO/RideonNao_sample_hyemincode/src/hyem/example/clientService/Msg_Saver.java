package hyem.example.clientService;

public class Msg_Saver {

	public String message;
	
	public int order_num;

	
	Msg_Saver(String msg, int order)
	{
		this.message = msg;
		this.order_num = order;
		
	}
}
