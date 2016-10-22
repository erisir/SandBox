/*---------------------------------------------------------------------*/
/* --- STC MCU Limited ------------------------------------------------*/
/* --- STC15F4K60S4 ϵ�� ��ʱ��1��������1�Ĳ����ʷ���������------------*/
/* --- Mobile: (86)13922805190 ----------------------------------------*/
/* --- Fax: 86-0513-55012956,55012947,55012969 ------------------------*/
/* --- Tel: 86-0513-55012928,55012929,55012966-------------------------*/
/* --- Web: www.STCMCU.com --------------------------------------------*/
/* --- Web: www.GXWMCU.com --------------------------------------------*/
/* ���Ҫ�ڳ�����ʹ�ô˴���,���ڳ�����ע��ʹ����STC�����ϼ�����        */
/* ���Ҫ��������Ӧ�ô˴���,����������ע��ʹ����STC�����ϼ�����        */
/*---------------------------------------------------------------------*/

//��ʾ����Keil������������ѡ��Intel��8058оƬ�ͺŽ��б���
//�����ر�˵��,����Ƶ��һ��Ϊ11.0592MHz

#include <STC15F2K60S2.H>

#include "intrins.h"
#include "../user/public.h"
#include "Uart1.h"
#include "string.h"
 

#define FOSC 24576000L          //ϵͳƵ��
#define BAUD 19200             //���ڲ�����


 

#define S1_S0 0x40              //P_SW1.6
#define S1_S1 0x80              //P_SW1.7
 
bit busy; 
bit uart_flag;
bit uart_start_flag;  
unsigned   char receive[16]={0};// ���Կ����ڶ���ʱ����idata,�Ͼ���������32
  			  	   //����64���ȵ�������unsigned char idata receive[16]={0};  
unsigned   char rec;				       
u8 boardaddress; 
u8 cmd_len=7; 
static u8 uart_cnt=0;//���ڽ��ռ����ı��� 
void Uart1Init()
{
    

    ACC = P_SW1;
    ACC &= ~(S1_S0 | S1_S1);    //S1_S0=0 S1_S1=0
    P_SW1 = ACC;                //(P3.0/RxD, P3.1/TxD)

    SCON = 0x50;                //8λ�ɱ䲨����
    AUXR = 0x40;                //��ʱ��1Ϊ1Tģʽ
    TMOD = 0x00;                //��ʱ��1Ϊģʽ0(16λ�Զ�����)
    TL1 = (65536 - (FOSC/4/BAUD));   //���ò�������װֵ
    TH1 = (65536 - (FOSC/4/BAUD))>>8;
    TR1 = 1;                    //��ʱ��1��ʼ����
    ES = 1;                     //ʹ�ܴ����ж�
        
}

/*----------------------------
UART �жϷ������
-----------------------------*/
void Uart() interrupt 4 using 1
{
    if (RI)
    {
	
	 	
		receive[uart_cnt] = SBUF;	
		if(receive[uart_cnt] == '@'){//begin
			uart_start_flag = 1;	
		}	
		if(uart_start_flag ==1){
						
			uart_cnt++;	 
			if(uart_cnt==cmd_len){	//@x0001
				uart_start_flag = 0;
				uart_cnt=0;
				if(checksumCalc(receive,cmd_len)){
					ES = 0;
					uart_flag = 1;
				}			
			}

		}
		RI = 0;                 //���RIλ 
    }
    if (TI)
    {
        TI = 0;                 //���TIλ
        busy = 0;               //��æ��־
    }
}
 
void parseCMD(){
	uart_flag = 0;
	ES = 1;
	SendString(receive);
}
bit cmd_ready()
{
	return uart_flag; 
}
/*----------------------------
���ʹ�������
----------------------------*/
void SendData(unsigned   char dat)
{
    while (busy);               //�ȴ�ǰ������ݷ������
    busy = 1;
    SBUF = dat;                 //д���ݵ�UART���ݼĴ���
}

/*----------------------------
�����ַ���
----------------------------*/
void SendString(unsigned   char *s)
{
    while (*s)                  //����ַ���������־
    {
        SendData(*s++);         //���͵�ǰ�ַ�
    }
}
void SendInt(unsigned  long  int v){

   	unsigned char rec[10]; 
	rec[0] = '0';
	rec[1] = '0';

	rec[6] = v%10+'0'; 
	v/=10;	
	rec[5] = v%10+'0'; 
	v/=10;
	rec[4] = v%10+'0'; 
	v/=10;
	rec[3] = v%10+'0'; 
	v/=10;
	rec[2] = +'0'; 

 		rec[7] = '\0';
		rec[8] = '\r';
		rec[9] = '\n';
   SendString(rec);
}