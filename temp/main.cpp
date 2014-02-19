/*
 * Copyright (c) 2012 Aldebaran Robotics. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the COPYING file.
 */
#define WIN32_LEAN_AND_MEAN
#include <iostream>
#include <Windows.h>
#include <WinSock2.h>
#include <ws2tcpip.h>
#include <pthread.h>
#include <alcommon/alproxy.h>
#include <alproxies/almotionproxy.h>
#include <alproxies/alvideodeviceproxy.h>
#include <alproxies/almemoryproxy.h>
#include <alerror/alerror.h>
#include <alvision/alimage.h>
#include <vector>
#include <string>
#include <ctype.h>

#include <MMSystem.h>

#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv/cv.h>

#pragma comment ( lib, "ws2_32.lib" )
#pragma comment (lib, "winmm.lib")
#pragma pack(1)

typedef struct _REV_STRUCT{
 int size;
 char p[256];
} REV_STRUCT;

using namespace AL;

DWORD WINAPI commandNao(void *p);
void* commandN(void *p);
void showImages(const std::string& robotIp, SOCKET soc);
std::vector<std::string> ClassfyCommand(char* command, char separator);
void executeCommand(std::vector<std::string> commandlist, SOCKET soc);
//ALProxy memory = ALProxy("ALMemory","192.168.0.9",9559);
std::string robot_ip;

int main()
{
	pthread_t tid[10];
	WSADATA wsadata;
	if( WSAStartup( MAKEWORD( 2, 2 ), &wsadata ) != 0 ) {
		std::cout << "winsock 초기화 실패 : " << WSAGetLastError() << std::endl;
		return 0;
	} // if
	SOCKET soc = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
	if( soc == SOCKET_ERROR ) {
		std::cout << "Socket Error!" << std::endl;
		return 0;
	}

	SOCKADDR_IN addr;
	addr.sin_family = AF_INET;
	addr.sin_port = htons(4500);
	addr.sin_addr.S_un.S_addr = INADDR_ANY;
	//ALProxy tts = ALProxy("ALTextToSpeech","127.0.0.1",9559);
	//tts.callVoid("say", std::string("Hello"));

	std::cout <<"Enter Robot IP"<< std::endl;
	std::cin >> robot_ip;

	if( bind( soc, (SOCKADDR*)&addr, sizeof(addr)) == SOCKET_ERROR) {
		std::cout << "Failed binding" << std::endl;
		return 0;
	}

	if( listen( soc, 5 ) == -1 ) {
		std::cout << "Failed listening" << std::endl;
		return 0;
	}

	std::cout << "Waiting for connencting from Client..." << std::endl;

	while( 1 ) {
		SOCKET c_s = NULL;
		int size = sizeof( addr );
		listen(soc, 10);
		if(c_s == NULL) c_s = accept( soc, (SOCKADDR*)&addr, &size );
		std::cout << "접속된 Client : " << inet_ntoa( addr.sin_addr ) << std::endl;
		//tts.callVoid("say", std::string("Connect"));

		CloseHandle( CreateThread( 0, 0, commandNao, (void*) c_s,0, 0 ) );//pthread_create(tid, NULL, commandN, (void*) c_s);
	}
	closesocket(soc);
	WSACleanup();
	return 0;
}

DWORD WINAPI commandNao(void *p){
	SOCKET s = (SOCKET)p;

	char recvbuf[3];
	char *buff_1;
	std::vector<std::string> commandlist;
 
	int current = 0;
	int length;
	int k;
	while(1){
		Sleep(500);
		int n  = recv( s, recvbuf, 3, 0 );
		if(n > 0){
			if(recvbuf[2]>=48 && recvbuf[2]<=57){
				length =  (recvbuf[0] - '0') * 100 + (recvbuf[1] - '0') * 10 + (recvbuf[2] - '0');
				buff_1 = new char[length+1];
				k  = recv( s, buff_1, length, 0 );
			}else{
				length = (recvbuf[0] - '0') * 10 + recvbuf[1] - '0';
				buff_1 = new char[length+1];
				buff_1[0] = recvbuf[2];
				k  = recv( s, (buff_1+1), length-1, 0 );
				k++;
			}
			std::cout << length << " and " << n << std::endl;
			buff_1[k] = '\0';
			std::cout << k << std::endl;
			std::cout << buff_1 << std::endl;
			if(length != k){
				send(s, "Length of Command doesn't match.",32,0);
				delete buff_1;
				continue;
			}
			if(strcmp(buff_1,"close")==0){
				//showImages("169.254.183.94",s);
				delete buff_1;
				send(s,"ByeBye",6,0);
				break;
			}

	/*char recvbuf[2];
	char *buff_1;
	std::vector<std::string> commandlist;
 
	int current = 0;
	while(1){
		Sleep(500);
		int n  = recv( s, recvbuf, 2, 0 );
		if(n > 0)
			////////////////////////////////////
			
			////////////////////////////////////
			int length = (recvbuf[0] - '0') * 10 + recvbuf[1] - '0';
			buff_1 = new char[length+1];
			int k  = recv( s, buff_1, length, 0 );
			std::cout << length << " and " << n << std::endl;
			buff_1[k] = '\0';
			std::cout << k << std::endl;
			std::cout << buff_1 << std::endl;
			if(length != k){
				send(s, "Length of Command doesn't match.",32,0);
				delete buff_1;
				continue;
			}
			if(strcmp(buff_1,"close")==0){
				//showImages("169.254.183.94",s);
				delete buff_1;
				send(s,"ByeBye",6,0);
				break;
			}*/


			/*ALProxy posture = ALProxy("ALRobotPosture","192.168.1.40",9559);
			ALProxy motion = ALProxy("ALMotion","192.168.1.40",9559);
			float speed = 1.0f;
			std::string command = "goToPosture"; //buff_2;
			std::string parameter = buff_1;
			posture.callVoid(command, parameter, speed);
			std::vector<float> angles = motion.call<std::vector<float>, std::string, bool>("getAngles",std::string("Head"),false);
			for(int i = 0; i<angles.size(); i++){
				char send_buff[10];
				sprintf(send_buff,"%f",angles[i]);
				send(s,send_buff,10,0);
			}*/
			//memory = ALProxy("ALMemory","192.168.0.9", 9559);
			commandlist = ClassfyCommand(buff_1, '^');
			if(commandlist[0] != "hello")
				executeCommand(commandlist, s);
			
			delete buff_1;
		}
		else {
			std::cout<<"command length : " << n <<std::endl;
			break;
		}
	}
	closesocket(s);
	return 0;
}

void * commandN(void *p){
	SOCKET s = (SOCKET)p;

	char recvbuf[2];
 
	int current = 0;

	int n  = recv( s, recvbuf, 3, 0 );
	char *buff_1 = new char[recvbuf[0]];
	//char *buff_2 = new char[recvbuf[1]];
	int k  = recv( s, buff_1, recvbuf[0], 0 );
	//int l  = recv( s, buff_2, recvbuf[1], 0 );
	std::cout << recvbuf[0] << std::endl;
	buff_1[k] = '\0';
	//buff_2[l] = '\0';
	std::cout << k << std::endl;
	std::cout << buff_1 << std::endl;
	/*ALProxy posture = ALProxy("ALRobotPosture","192.168.1.40",9559);
	ALProxy motion = ALProxy("ALMotion","192.168.1.40",9559);
	float speed = 1.0f;
	std::string command = "goToPosture"; //buff_2;
	std::string parameter = buff_1;
	posture.callVoid(command, parameter, speed);
	std::vector<float> angles = motion.call<std::vector<float>, std::string, bool>("getAngles",std::string("Head"),false);
	for(int i = 0; i<angles.size(); i++){
		char send_buff[10];
		sprintf(send_buff,"%f",angles[i]);
		send(s,send_buff,10,0);
	}*/
	delete buff_1;
	//delete buff_2;
	return 0;
}

/*
void showImages(const std::string& robotIp, SOCKET soc)
{
	ALVideoDeviceProxy camProxy(robotIp, 9559);

	std::string clientName = camProxy.subscribeCamera("test1",0, kQVGA, kBGRColorSpace, 30);
	std::string clientName2 = camProxy.subscribeCamera("test2",1, kQVGA, kBGRColorSpace, 30);

	//kQVGA, kBGRColorSpace 는 #include <alvision/alimage.h>를 통해 사용할 수 있게 됩니다

	cv::Mat imgHeader1 = cv::Mat(cv::Size(320,240), CV_8UC3);
	cv::Mat imgHeader2 = cv::Mat(cv::Size(320,240), CV_8UC3);
	cv::Mat showim;
	//특정 사이즈, TYPE의 이미지 파일을 생성하였다.

	cv::namedWindow("image_head");
	cv::namedWindow("image_chin");
	//이미지를 보여줄 창의 이름을 설정하였다.
	//근데 이거 필요 없는거 같은뎅???
	unsigned int prev_time = 0;

	while((char)cv::waitKey(30)!=27)
	{
		prev_time = timeGetTime();
		ALValue img1 = camProxy.getImageRemote(clientName);
		ALValue img2 = camProxy.getImageRemote(clientName2);

		// 선언한 Proxy를 subscribe 하는 아이의 이름을 이미지를 가져오는 함수의 argument에 넣어준다.

		imgHeader1.data = (uchar*)img1[6].GetBinary();
		imgHeader2.data = (uchar*)img2[6].GetBinary();

		//std::cout << imgHeader1.data << " and " << img2[6].getSize() << std::endl;

		//img 는 여러가지 성질을 갖는 ALValue 타입의,이미지 정보 저장 자료인데,
		//그중 6번, img[6]은 이미지 버퍼에 대한 정보를 가지고 있음. 
		//그 정보를 앞에서 지정한 이미지 파일에다가 넣어준다.

		//즉, 로봇에서 받아온 이미지가 가지는 성질을 clientName이라는 이름을 빌려 가져와서, (subscribe)

		//그것의 정보를 ALValue 타입인 img를 통해 가져온 뒤, 
		//그것의 data를 우리가 보여주려는 화면에 해당하는 imgHeader 에 가져오게 되는 것이다.

		
		camProxy.releaseImage(clientName);
		camProxy.releaseImage(clientName2);

		 //이건 getimagelocal 인경우에는 필수, getImageRemote인 경우에는 선택사항이라고 하는데
		 //기능의 설명은
		 ///** Tells to ALVideoDevice that it can give back the image buffer to the
		/* driver. Optional after a getImageRemote but MANDATORY after a getImageLocal.
		  //라고 한다.
		 //local인 경우 줬던걸 다시 가져와야 하나보다.
		//std::vector<int> p;
		//p.push_back(CV_IMWRITE_JPEG_QUALITY);
		//p.push_back(95);
		//std::vector<unsigned char> buf;
		//cv::imencode(".jpg",imgHeader1,buf,p);
		//cv::imwrite("nao.jpg",imgHeader1);
		//std::cout << camProxy.getFrameRate(clientName) << std::endl;
		
		//cv::cvtColor(imgHeader1, showim, CV_YUV2BGR);

		cv::imshow("image_head",imgHeader1);
		cv::imshow("image_chin",imgHeader2);
		std::cout << timeGetTime() - prev_time << std::endl;

	}

	camProxy.unsubscribe(clientName);
	camProxy.unsubscribe(clientName2);
	//해지해 줍니당.

}
*/

std::vector<std::string> ClassfyCommand(char* command, char separator){
	std::vector<std::string> commands;
	std::string temp_command = command;
	int pos = 0;
	while(pos!=temp_command.npos){
		pos = temp_command.find(separator);
		commands.push_back(temp_command.substr(0,pos));
		temp_command.erase(0,pos+1);
	}
	for(std::vector<std::string>::iterator it=commands.begin(); it!=commands.end();it++){
		std::cout << it->data() <<std::endl;
	}

	return commands;
}

void executeCommand(std::vector<std::string> commandlist, SOCKET soc){
	ALProxy prox = ALProxy(commandlist[0],robot_ip,9559);
	std::vector<ALValue> param;
	for(std::vector<std::string>::iterator it=commandlist.begin(); it!=commandlist.end();it++){
		if(it->find(".")!=it->npos && it->find("_")==it->npos)
			param.push_back(std::stof(*it));
		else if((*it)=="true")
			param.push_back(true);
		else if((*it)=="false")
			param.push_back(false);
		else
			param.push_back((*it));
	}
	if(commandlist[1].find("get")==commandlist[1].npos){	//get이 없으면 callVoid를 사용할 수 
		///////////////////////////////////////////////////
		if(commandlist[1] == "setAngles" && commandlist[2] == "Body"){
			std::string s = commandlist[3];
			std::cout<<"commandlist[3] = "<<s<<std::endl;
			std::vector<float> angles;
			size_t pos = 0;
			std::string delimiter = "_";
			int i=0;
			while ((pos = s.find(delimiter)) != std::string::npos) {
				std::cout<<"Noew to parsing into substring"<< std::endl;
				angles.push_back(atof(s.substr(0, pos).c_str()));
				std::cout<<"angls["<<i<<"] = "<<angles[i]<< std::endl;
				s.erase(0, pos + delimiter.length());
				i++;
			} angles.push_back(atof(s.substr(0, std::string::npos).c_str()));
			std::cout<<angles<<std::endl;
			AL::ALMotionProxy motion(robot_ip);
			motion.setAngles(commandlist[2],angles,param[4]);
			std::cout<<"Set angles is complete"<<std::endl;
		}
		///////////////////////////////////////////////////
		else{
			switch(commandlist.size()){
			case 2 : prox.callVoid(param[1]);
					break;
			case 3 : prox.callVoid(param[1], param[2]);
					break;
			case 4 : prox.callVoid(param[1], param[2], param[3]);
					break;
			case 5 : prox.callVoid(param[1], param[2], param[3], param[4]);
					break;
			case 6 : prox.callVoid(param[1], param[2], param[3], param[4], param[5]);
					break;
			case 7 : prox.callVoid(param[1], param[2], param[3], param[4], param[5], param[6]);
					break;
			default : std::cout<<"Error"<<std::endl;
			}
		}
	}
	else if(commandlist[0]=="ALMemory"){
		
		if(robot_ip!="127.0.0.1"){
		ALValue value_nao;
		switch(commandlist.size()){
		case 2 : value_nao = prox.call<ALValue>(param[1]);
				break;
		case 3 : value_nao = prox.call<ALValue, ALValue>(param[1], param[2]);
				break;
		case 4 : value_nao = prox.call<ALValue, ALValue, ALValue>(param[1], param[2], param[3]);
				break;
		case 5 : value_nao = prox.call<ALValue, ALValue, ALValue, ALValue>(param[1], param[2], param[3], param[4]);
				break;
		case 6 : value_nao = prox.call<ALValue, ALValue, ALValue, ALValue, ALValue>(param[1], param[2], param[3], param[4], param[5]);
				break;
		case 7 : value_nao = prox.call<ALValue, ALValue, ALValue, ALValue, ALValue, ALValue>(param[1], param[2], param[3], param[4], param[5], param[6]);
				break;
		default : std::cout<<"Error"<<std::endl;
		}
		std::string buffer = value_nao.toString();
		buffer += "\n";
		send(soc, buffer.c_str(),buffer.size(), 0);
		std::cout<<buffer<<std::endl;
		}
		else{		
		std::string buffer = "0";
		buffer += "\n";
		send(soc, buffer.c_str(),buffer.size(), 0);
		std::cout<<buffer<<std::endl;
		}
	}
	else if(commandlist[1]=="getAngles"){
		std::vector<float> value_nao;
		std::string send_buff_str;
		char send_buff[10];
		switch(commandlist.size()){
		case 2 : value_nao = prox.call<std::vector<float>>(param[1]);
				break;
		case 3 : value_nao = prox.call<std::vector<float>, ALValue>(param[1], param[2]);
				break;
		case 4 : value_nao = prox.call<std::vector<float>, ALValue, ALValue>(param[1], param[2], param[3]);
				break;
		case 5 : value_nao = prox.call<std::vector<float>, ALValue, ALValue, ALValue>(param[1], param[2], param[3], param[4]);
				break;
		case 6 : value_nao = prox.call<std::vector<float>, ALValue, ALValue, ALValue, ALValue>(param[1], param[2], param[3], param[4], param[5]);
				break;
		case 7 : value_nao = prox.call<std::vector<float>, ALValue, ALValue, ALValue, ALValue, ALValue>(param[1], param[2], param[3], param[4], param[5], param[6]);
				break;
		default : std::cout<<"Error"<<std::endl;
		}
		for(int i = 0; i<value_nao.size(); i++){
				sprintf(send_buff,"%f",value_nao[i]);
				if(i!=0){
					send_buff_str.append("_");
				}
				send_buff_str.append(send_buff);
				//send(soc,send_buff,10,0);
				//std::cout<<"send_buff "<<i<<" ="<<send_buff<<std::endl;
		}
		send_buff_str.append("\n");
		std::cout<<"send_buff_str = "<<send_buff_str<<std::endl;
		std::cout<< commandlist[1] <<std::endl;
		send(soc,send_buff_str.c_str(),send_buff_str.size(),0);
	}
	else if(commandlist[1].find("Posture")==commandlist[1].npos){
		std::vector<float> value_nao;
		switch(commandlist.size()){
		case 2 : value_nao = prox.call<std::vector<float>>(param[1]);
				break;
		case 3 : value_nao = prox.call<std::vector<float>, ALValue>(param[1], param[2]);
				break;
		case 4 : value_nao = prox.call<std::vector<float>, ALValue, ALValue>(param[1], param[2], param[3]);
				break;
		case 5 : value_nao = prox.call<std::vector<float>, ALValue, ALValue, ALValue>(param[1], param[2], param[3], param[4]);
				break;
		case 6 : value_nao = prox.call<std::vector<float>, ALValue, ALValue, ALValue, ALValue>(param[1], param[2], param[3], param[4], param[5]);
				break;
		case 7 : value_nao = prox.call<std::vector<float>, ALValue, ALValue, ALValue, ALValue, ALValue>(param[1], param[2], param[3], param[4], param[5], param[6]);
				break;
		default : std::cout<<"Error"<<std::endl;
		}
		for(int i = 0; i<value_nao.size(); i++){
				char send_buff[10];
				sprintf(send_buff,"%f",value_nao[i]);
				send_buff[8] = '\n';
				send(soc,send_buff,10,0);
				std::cout<<send_buff<<std::endl;
		}
	}
	else {
		std::vector<std::string> value_nao;
		switch(commandlist.size()){
		case 2 : value_nao = prox.call<std::vector<std::string>>(param[1]);
				break;
		case 3 : value_nao = prox.call<std::vector<std::string>, ALValue>(param[1], param[2]);
				break;
		case 4 : value_nao = prox.call<std::vector<std::string>, ALValue, ALValue>(param[1], param[2], param[3]);
				break;
		case 5 : value_nao = prox.call<std::vector<std::string>, ALValue, ALValue, ALValue>(param[1], param[2], param[3], param[4]);
				break;
		case 6 : value_nao = prox.call<std::vector<std::string>, ALValue, ALValue, ALValue, ALValue>(param[1], param[2], param[3], param[4], param[5]);
				break;
		case 7 : value_nao = prox.call<std::vector<std::string>, ALValue, ALValue, ALValue, ALValue, ALValue>(param[1], param[2], param[3], param[4], param[5], param[6]);
				break;
		default : std::cout<<"Error"<<std::endl;
		}
		for(int i = 0; i<value_nao.size(); i++){
			send(soc,value_nao[i].c_str(),10,0);
				std::cout<<value_nao[i]<<std::endl;
		}
		std::cout<< commandlist[1] <<std::endl;
	}
}