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
#include "PWM.h" 
#include "ADC.h"
#include "PID.h"

#define FOSC 24576000L          //ϵͳƵ��
#define BAUD 19200             //���ڲ�����


 

#define S1_S0 0x40              //P_SW1.6
#define S1_S1 0x80              //P_SW1.7
 
bit busy; 
bit uart_flag;
bit uart_start_flag;  
unsigned char receive[16]={0};// ���Կ����ڶ���ʱ����idata,�Ͼ���������32
  			  	   //����64���ȵ�������unsigned char idata receive[16]={0};  
unsigned char rec;				       
u8 boardaddress; 
u8 cmd_len=5; 
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
			if(uart_cnt==cmd_len-1){	//@x0001X
				uart_start_flag = 0;
				uart_cnt=0;
				//if(checksumCalc(receive)==receive[cmd_len-1]){
					uart_flag = 1;
			//	} 			
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
unsigned char checksumCalc(unsigned char rec[])
{ 

	return (( unsigned char)rec[0])^(( unsigned char)rec[1])^(( unsigned char)rec[2])^(( unsigned char)rec[3]);
} 
void parseCMD(){
	unsigned int v_data;
	uart_flag = 0;
		 	
	v_data =  receive[2]*256+receive[3] ;
	ES = 1;
		 
 
     switch(receive[1]){
		   
		  case _U_SetVotage:SetSetPoint(v_data);break;//0 

		  case _U_SetPTerm:SetPIDparam_P_inc(v_data);break;//2

		  case _U_SetITerm:SetPIDparam_I_inc(v_data);break;//4
 
		  case _U_SetDTerm:SetPIDparam_D_inc(v_data);break;//6
		   
		  case _U_SetPWMVal:SetPWMValue(v_data);break;//8
		  
		  case _U_GetVotage:GetPosition();break;//8
		  
		  case _U_SetDura:GetPIDStatu();break;//8

		  case _U_SetTClose:SetTClose();break;//8

		  case _U_SetTOpen:SetTOpen();break;//8

		  case _U_SetTPID:SetTPID();break;//8

		  case _U_SetVotageTimes:SetVotageTimes(v_data);break;//8

		   case _U_SetVotageChanel:SetVotageChanel(v_data);break;//8
		  
 			
		  default:break;
	 }	    
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

   	u8 rec[5]; 
	rec[0] = '@';
	rec[1] = 'P';
	rec[2] = v/256;
	rec[3] = v%256;
	rec[4] = checksumCalc(rec);

    SendData(rec[0]);
	SendData(rec[1]);
	SendData(rec[2]);
	SendData(rec[3]);
	SendData(rec[4]);
 
}
void SendInt1(unsigned int setV,unsigned int pwm){
   	u8 rec[7]; 
	rec[0] = '@';
	rec[1] = 'P';
	rec[2] = setV/256;
	rec[3] = setV%256;
	rec[4] = pwm/256;
	rec[5] = pwm%256;
	rec[6] = checksumCalc(rec);

    SendData(rec[0]);
	SendData(rec[1]);
	SendData(rec[2]);
	SendData(rec[3]);
	SendData(rec[4]);
	SendData(rec[5]);
	SendData(rec[6]);
}