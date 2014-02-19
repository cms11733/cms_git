package hyem.example.clientService;

interface socketAidl {
	void sendMsg(String message);
	String recvMsg();
	String Send_recvMsg(String message);
	}