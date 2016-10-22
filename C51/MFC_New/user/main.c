
#include <STC15F2K60S2.H>
#include "public.h"
#include "../hardware/Uart1.h"
#include "../hardware/ADC.h"
#include "../hardware/PWM.h"

void sysInit();
unsigned char buf[16]={0};
unsigned int PWM_MAX = 6000;
unsigned int pwmTest = 32;    //pwm初值

void main()
{
 	EA = 1;
	sysInit(); 
    Uart1Init();
	SendString("Uart1 Init OK !\r\n"); 
	InitADC(); 
	SendString("ADC Init OK !\r\n");  
    PWMn_init(); //初始化pwm
	SendString("PWM Init OK !\r\n");  
    while(1){
		if(cmd_ready()){
			parseCMD();
		}
		SendString("\r\n");
		SendInt(pwmTest);
		SendString(" ");
		SendInt(GetADCResult(0,300));
		

		//itoa(GetADCResult(0,100),buf);
		//SendString(buf);
		
		
		pwmTest += 10;
		if(pwmTest >= PWM_MAX) pwmTest = 32;
		LoadPWM(pwmTest);               //更新PWM的占空
		 
	}
}	

void sysInit()
{
    P0M0 = 0x00;
    P0M1 = 0x00;
    P1M0 = 0x00;
    P1M1 = 0x00;
    P2M0 = 0x00;
    P2M1 = 0x00;
    P3M0 = 0x00;
    P3M1 = 0x00;
    P4M0 = 0x00;
    P4M1 = 0x00;
    P5M0 = 0x00;
    P5M1 = 0x00;
    P6M0 = 0x00;
    P6M1 = 0x00;
    P7M0 = 0x00;
    P7M1 = 0x00;
}