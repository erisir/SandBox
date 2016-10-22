/*---------------------------------------------------------------------*/
/* --- STC MCU Limited ------------------------------------------------*/
/* --- STC15F4K60S4 系列 定时器1用作串口1的波特率发生器举例------------*/
/* --- Mobile: (86)13922805190 ----------------------------------------*/
/* --- Fax: 86-0513-55012956,55012947,55012969 ------------------------*/
/* --- Tel: 86-0513-55012928,55012929,55012966-------------------------*/
/* --- Web: www.STCMCU.com --------------------------------------------*/
/* --- Web: www.GXWMCU.com --------------------------------------------*/
/* 如果要在程序中使用此代码,请在程序中注明使用了STC的资料及程序        */
/* 如果要在文章中应用此代码,请在文章中注明使用了STC的资料及程序        */
/*---------------------------------------------------------------------*/

//本示例在Keil开发环境下请选择Intel的8058芯片型号进行编译
//若无特别说明,工作频率一般为11.0592MHz

#include <STC15F2K60S2.H>

#include "intrins.h"
#include "../user/public.h"
#include "Uart1.h"
#include "string.h"
 

#define FOSC 24576000L          //系统频率
#define BAUD 19200             //串口波特率


 

#define S1_S0 0x40              //P_SW1.6
#define S1_S1 0x80              //P_SW1.7
 
bit busy; 
bit uart_flag;
bit uart_start_flag;  
unsigned   char receive[16]={0};// 可以考虑在定义时加上idata,毕竟还可能是32
  			  	   //或者64长度的数组呢unsigned char idata receive[16]={0};  
unsigned   char rec;				       
u8 boardaddress; 
u8 cmd_len=7; 
static u8 uart_cnt=0;//串口接收计数的变量 
void Uart1Init()
{
    

    ACC = P_SW1;
    ACC &= ~(S1_S0 | S1_S1);    //S1_S0=0 S1_S1=0
    P_SW1 = ACC;                //(P3.0/RxD, P3.1/TxD)

    SCON = 0x50;                //8位可变波特率
    AUXR = 0x40;                //定时器1为1T模式
    TMOD = 0x00;                //定时器1为模式0(16位自动重载)
    TL1 = (65536 - (FOSC/4/BAUD));   //设置波特率重装值
    TH1 = (65536 - (FOSC/4/BAUD))>>8;
    TR1 = 1;                    //定时器1开始启动
    ES = 1;                     //使能串口中断
        
}

/*----------------------------
UART 中断服务程序
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
		RI = 0;                 //清除RI位 
    }
    if (TI)
    {
        TI = 0;                 //清除TI位
        busy = 0;               //清忙标志
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
发送串口数据
----------------------------*/
void SendData(unsigned   char dat)
{
    while (busy);               //等待前面的数据发送完成
    busy = 1;
    SBUF = dat;                 //写数据到UART数据寄存器
}

/*----------------------------
发送字符串
----------------------------*/
void SendString(unsigned   char *s)
{
    while (*s)                  //检测字符串结束标志
    {
        SendData(*s++);         //发送当前字符
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