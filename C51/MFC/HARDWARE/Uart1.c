/******************************************************************
本程序只供学习使用，未经作者许可，不得用于其它任何用途
程序结构参考 安徽师范大学  Lyzhangxiang的EasyHW OS结构设计
uart1.C  file
作者：bg8wj
建立日期: 2011.12.23
版本：V1.0
Copyright(C) bg8wj
/*******************************************************************/
#include "Uart1.h"
#include "STC15W.h"
#include "string.h"
#include "STDLIB.h"
#include "adc.h"
#include "pid.h"
#include "pwm.h"
 
 

#define _U_SetVotage	 '0'
#define _U_SetPTerm      '1'
#define _U_SetITerm      '2'
#define _U_SetDTerm	     '3'
#define _U_SetDura       '4'
#define _U_SetPWMVal     '5'
#define _U_GetVotage     '6'
#define _U_SetTClose     '7'
#define _U_SetTOpen      '8'
#define _U_SetTPID       '9'
#define _U_SetVotageTimes 'a'
 
unsigned   char   buf[10];
char rec;
char i;
char flag;//buf:@XX 1234
/************************************************************
				   uart1初始化
*************************************************************/
void UartInit1(void)		//9600bps@24.000MHz
{
	SCON = 0x50;		//8位数据,可变波特率
	AUXR |= 0x40;		//定时器1时钟为Fosc,即1T
	AUXR &= 0xFE;		//串口1选择定时器1为波特率发生器
	TMOD &= 0x0F;		//设定定时器1为16位自动重装方式
	TL1 = 0x8F;		//设定定时初值
	TH1 = 0xFD;		//设定定时初值
	ET1 = 0;		//禁止定时器1中断
	TR1 = 1;		//启动定时器1
}
 
/************************************************************
				   向串口uart1发送一个字符
*************************************************************/
void send_char_com(unsigned char ch)  
{
    SBUF=ch;
    while(TI==0);
    TI=0;
}
/************************************************************
	向串口uart1发送一个字符串，strlen为该字符串长度
*************************************************************/
void send_string_com(unsigned char *str)
{
    unsigned int k=0;
	
	do
    {
        send_char_com(*(str + k));
        k++;
    } while(k < strlen(str));
}
void send_int_com(uint v)
{

	uchar rec[5]; 
	rec[0] = '@';
	rec[1] = 'P';
	rec[2] = v/256;
	rec[3] = v%256;
	rec[4] = checksumCalc(rec);

    send_char_com(rec[0]);
	send_char_com(rec[1]);
	send_char_com(rec[2]);
	send_char_com(rec[3]);
	send_char_com(rec[4]);
 
     
}
unsigned char checksumCalc(uchar rec[])
{ 
	return ((unsigned char)rec[0])^((unsigned char)rec[1])^((unsigned char)rec[2])^((unsigned char)rec[3]);
}								  	 
/*------------------------------------------------*/
void serial () interrupt 4	 using 1
{ 		   
 
	if(RI) {		
		rec=SBUF;		
		if(rec == '@'){//begin
			flag = 1;	
		}	
		if(flag ==1){
			buf[i] = rec;			
			i++;	 
			if(i==5){	//@x0001
			flag = 0;
			i=0;
			if(buf[4] == checksumCalc(buf))UartFunction(buf);			
			}

		}
	 	RI=0;	
	}
	if(TI){
		TI = 0;
	}

}
void UartFunction(unsigned char *rec){//rec 1-7  x @ x cmd xx data  '\0'
 
	 unsigned int v_data;	 
	 v_data =  rec[2]*256+rec[3] ;
 
     switch(rec[1]){
	  	  case _U_SetVotage:SetSetPoint(v_data);break;//0 

		  case _U_SetPTerm:SetPIDparam_P_inc(v_data);break;//2

		  case _U_SetITerm:SetPIDparam_I_inc(v_data);break;//4
 
		  case _U_SetDTerm:SetPIDparam_D_inc(v_data);break;//6
 
		  case _U_SetDura:SetPIDparam_Dura_inc(v_data);break;//8
		   
		  case _U_SetPWMVal:PWM1_set(v_data);//SetPWMValue(v_data);break;//8
		  
		  case _U_GetVotage:GetPosition();break;//8

		  case _U_SetTClose:SetTClose();break;//8

		  case _U_SetTOpen:SetTOpen();break;//8

		  case _U_SetTPID:SetTPID();break;//8

		  case _U_SetVotageTimes:SetVotageTimes(v_data);break;//8
		  
 								
		  default:break;
	 }
}

