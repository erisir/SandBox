
#include <STC15F2K60S2.H>
#include "public.h"
#include "../hardware/Uart1.h"
#include "../hardware/ADC.h"
#include "../hardware/PWM.h"
#include "../hardware/PID.h"

void sysInit();

void main()
{
 	EA = 1;
	sysInit(); 
    Uart1Init();
	SendString("Uart Init OK !\r\n"); 
	InitADC(); 
	SendString("ADC Init OK !\r\n");  
    PWMn_init(); //初始化pwm
	SendString("PWM Init OK !\r\n"); 
	PIDInit(); //初始化pwm
	SendString("PID Init OK !\r\n");  
    while(1){
		if(cmd_ready()){
			parseCMD();
		}
		if(isPIDEnable()){
		 PIDStart();
		 } 
 
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