#include <reg52.h>
#include "lcd1602.h"
#include "pwm.h"
#include "key.h"
#include "adc.h"
#include "pid.h"
#include "Uart1.h"

/****************************Ӳ���ӿڶ���***********************************
  PWM:ģ��0��ӦP1.3,ģ��1��ӦP1.4  [0Ϊ�ֶ����ƿڣ�1λPID�������ƿڡ�
  lcd:sbit RS = P2^3; sbit RW = P2^4; sbit E  = P2^5;  DBPort  P0 //���ݶ˿�
  adc:P1^0,P1^1��·�ɼ�
  key:KeyPort P3���Ժ���չʹ�á� ��sbit KEY3	= P2^6;   KEY4	= P2^7;
****************************************************************************/


void main(void)
{
	EA=1;                   //���жϿ�		
	UartInit1();		   //����
	send_string_com("UartInit OK!");
	//PIDInit();			   //PID
	//PWM_Initial();		   //PWM 
	DelayMs(250);	
	//ADC_Init();			   //ADC
	/*
	lcd1602_init();
	hz_lcdDis(0,4,"Welcome!");
	lcd_Write_com(0x01);	//����
	hz_lcdDis(0,0,"S          Q");
	hz_lcdDis(1,0,"G          d");
	
	*/
	ADC_Init();
	ADC_start();


 }

 