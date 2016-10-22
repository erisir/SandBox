#include<reg52.h>
#include "pwm.h"
#include "key.h"
#include "adc.h"
#include <intrins.h>
#define U8 unsigned char
#define U16 unsigned int


void PWM_clock(U8 clock);
void PWM_start(U8 Module,U8 mode);
void PMW_mode(void);
U16 count = 0;
uint currentPWM = 0;

void DelayMs(U8 ms) //在11.0592M晶振下，stc10f系列（单周期指令）的ms级延时
{      
	U16 i;
	while(ms--)
	{
	   for(i = 0; i < 850; i++);
	}
}

sfr CCON     = 0xD8; 	//PCA控制寄存器
sfr CMOD     = 0xD9; 	//PCA模式寄存器
sfr CCAPM0   = 0xDA; 	//PCA模块0模式寄存器 //模块0对应P1.3/CEX0/PCA0/PWM0(STC12C5A60S2系列)   
sfr CCAPM1   = 0xDB; 	//PCA模块1模式寄存器 //模块1对应P1.4/CEX1/PCA1/PWM1(STC12C5A60S2系列)
sfr CL       = 0xE9;    //PCA 定时寄存器 低位
sfr CH       = 0xF9; 	//PCA 定时寄存器 高位
sfr CCAP0L   = 0xEA; 	//PCA模块0的 捕获寄存器 低位
sfr CCAP0H   = 0xFA;    //PCA模块0的 捕获寄存器 高位
sfr CCAP1L   = 0xEB; 	//PCA模块1的 捕获寄存器 低位
sfr CCAP1H   = 0xFB; 	//PCA模块1的 捕获寄存器 高位
sfr PCA_PWM0 = 0xF2; 	//PCA PWM 模式辅助寄存器0
sfr PCA_PWM1 = 0xF3; 	//PCA PWM 模式辅助寄存器1
sbit CF   = 0xDF;       //PCA计数溢出标志位
sbit CR   = 0xDE;       //PCA计数器 运行控制位
sbit CCF1 = 0xD9;       //PCA模块1中断标志
sbit CCF0 = 0xD8;       //PCA模块0中断标志

bit oFlag = 0;		  //中断次数=偶数?0:1
bit pwmValueMode = 0;// pwm <256?0:1


void PWM_clock(U8 clock)
{ 
	CMOD |= (clock<<1);
	CL = 0x00;
	CH = 0x00;
}

void PWM_Initial(void)	   // 模块0,模块1设置为PWM输出,无中断,初始占空因素为50%
{
	U16 a = 0;
	TMOD|=0x02; 		   // timer 0 mode 2: 8-Bit reload  
	TH0=0xff;
	TR0=1;
	PWM_clock(2);      	   // PCA/PWM时钟源为 定时器0的溢出
	CCAP0L = 0X80;
	CCAP0H = 0X80;         //模块0初始输出 占空因数为50%
	CCAP1L = 0X80;
	CCAP1H = 0X80;         //模块0初始输出 占空因数为50%

	PWM_start(2,2);

}  


void PWM_start(U8 module,U8 mode) 
{
	if(module==0)
	{
		switch(mode)
		{ 
			case 0: CCAPM0 = 0X42;break; 	//模块0设置为8位PWM输出，无中断
			case 1: CCAPM0 = 0X53;break; 	//模块0设置为8位PWM输出，下降沿产生中断
			case 2: CCAPM0 = 0X63;break; 	//模块0设置为8位PWM输出，上升沿产生中断
			case 3: CCAPM0 = 0X73;break; 	//模块0设置为8位PWM输出，跳变沿产生中断
			default: break;
		}
	}
	else if(module==1)
		{
			switch(mode)
			{ 
				case 0: CCAPM1 = 0X42;break; 	//模块1设置为8位PWM输出，无中断
				case 1: CCAPM1 = 0X53;break; 	//模块1设置为8位PWM输出，下降沿产生中断
				case 2: CCAPM1 = 0X63;break; 	//模块1设置为8位PWM输出，上升沿产生中断
				case 3: CCAPM1 = 0X73;break; 	//模块1设置为8位PWM输出，跳变沿产生中断
				default: break;
			}
		} 
	else if(module==2)
		{
			switch(mode)
			{ 
				case 0: CCAPM0 = CCAPM1 = 0X42;break; //模块0和1设置为8位PWM输出，无中断
				case 1: CCAPM0 = CCAPM1 = 0X53;break; //模块0和1设置为8位PWM输出，下降沿产生中断
				case 2: CCAPM0 = CCAPM1 = 0X63;break; //模块0和1设置为8位PWM输出，上升沿产生中断
				case 3: CCAPM0 = CCAPM1 = 0X73;break; //模块0和1设置为8位PWM输出，跳变沿产生中断
				default: break;
			}
		} 
	CR=1; //PCA计数器开始计数 
}



void PCA_Intrrpt(void) interrupt 7
{    
	if(CCF0) {
		CCF0=0;
	/*	oFlag = ~oFlag;
		if(oFlag){
			if(pwmValueMode == 0)PCA_PWM1 = 0x02;  //EPC0L = 0
			if(pwmValueMode == 1)CCAP1L   = currentPWM;  
		} */
	}
	
	if(CCF1) {
		CCF1=0;   //软件清零 
	}
	if(CF)   
		CF=0;     //软件清零 	 	
}
/*********************************************************************************************
函数名：PWM0占空比设置函数
调  用：PWM0_set();
参  数：0x00~0xFF（亦可用0~255）
返回值：无
结  果：设置PWM模式占空比，为0时全部高电平，为1时全部低电平
备  注：如果需要PWM1的设置函数，只要把CCAP0L和CCAP0H中的0改为1即可
/**********************************************************************************************/
void PWM0_set (unsigned char a)
{
	CCAP0L= 256-a; //设置值直接写入CCAP0L
 	CCAP0H= 256-a; //设置值直接写入CCAP0H
	
}

void PWM1_set (unsigned char a)
{
	CCAP1L= 256-a; //设置值直接写入CCAP0L
 	CCAP1H= 256-a; //设置值直接写入CCAP0H
	
}
/*********************************************************************************************
函数名：9bitPWM1占空比设置函数
调  用：PWM1_set9();需要利用到PWM0 的上升沿触发中断
参  数：0~512
返回值：无
结  果：设置PWM模式占空比，为0时全部高电平，为1时全部低电平
/**********************************************************************************************/
void PWM1_set9 (unsigned int a)
{
    if(a<256){
	 CCAP1L= 256-a;  
 	 CCAP1H= 256-a;  
	 pwmValueMode = 0;
	 PCA_PWM1 = 0x03;//EPC0H=EPC0L=1
	}else{	 
	 CCAP1L= 0;  
 	 CCAP1H= 0; 
	 currentPWM = 512-a; 
	 pwmValueMode = 1;
	 PCA_PWM1 = 0x00;//EPC0H=EPC0L=0
	}
	
}	  