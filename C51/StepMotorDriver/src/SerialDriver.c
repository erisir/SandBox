#include "SerialDriver.h"
/************************************************************

 ************************************************************/
void  InitSerial()
{

	SCON  = 0x50;//������ʽ1�������н���
	PCON =0x80;

	TMOD |= 0x20;//��ʱ��һ ��ʽ�� �Զ����س�ʼֵ
 
	TH1   = 0xFD; //19200
 
	ET1 = 0;//��ֹ�����������ж�
	TR1   = 1; //��ʱ��1 ��ʼ����
	RI = 0;
	TI = 0;
	ES    = 1;	 //�������ж�
	PS	  = 1 ;	 //�����жϸ����ȼ�
}                            
/************************************************************

 ************************************************************/
void SendByte(unsigned char dat)
{
	SBUF = dat;
	while(!TI);
	TI = 0;
}
/************************************************************

 ************************************************************/
void SendStr(unsigned char *s)
{
	SendByte('@');
	SendByte(s[0]);// 0OK 1NG
	SendByte(s[1]);
	SendByte(s[2]);
	SendByte(s[3]);
	SendByte(s[4]);
	SendByte( ((uchar)'@')^((uchar)s[0])^((uchar)s[1])^((uchar)s[2])^((uchar)s[3])^((uchar)s[4]));
}
void SendErr(unsigned char s)
{
	SendByte('@');
	SendByte(s);// 0OK 1NG
	SendByte('X');
	SendByte('X');
	SendByte('X');
	SendByte('X');
	SendByte( ((uchar)'@')^((uchar)s)^((uchar)'X')^((uchar)'X')^((uchar)'X')^((uchar)'X'));
}
