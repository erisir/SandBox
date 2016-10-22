#include <reg52.h>
#include "lcd1602.h"
#include "pwm.h"
#include "key.h"
#include "adc.h"
#include "pid.h"
#include "Uart1.h"

/****************************硬件接口定义***********************************
  PWM:模块0对应P1.3,模块1对应P1.4  [0为手动控制口，1位PID反馈控制口】
  lcd:sbit RS = P2^3; sbit RW = P2^4; sbit E  = P2^5;  DBPort  P0 //数据端口
  adc:P1^0,P1^1两路采集
  key:KeyPort P3【以后扩展使用】 ，sbit KEY3	= P2^6;   KEY4	= P2^7;
****************************************************************************/


void main(void)
{
	EA=1;                   //总中断开		
	UartInit1();		   //串口
	send_string_com("UartInit OK!");
	//PIDInit();			   //PID
	//PWM_Initial();		   //PWM 
	DelayMs(250);	
	//ADC_Init();			   //ADC
	/*
	lcd1602_init();
	hz_lcdDis(0,4,"Welcome!");
	lcd_Write_com(0x01);	//清屏
	hz_lcdDis(0,0,"S          Q");
	hz_lcdDis(1,0,"G          d");
	
	*/
	ADC_Init();
	ADC_start();


 }

 