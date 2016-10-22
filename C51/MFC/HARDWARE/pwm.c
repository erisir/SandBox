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

void DelayMs(U8 ms) //��11.0592M�����£�stc10fϵ�У�������ָ���ms����ʱ
{      
	U16 i;
	while(ms--)
	{
	   for(i = 0; i < 850; i++);
	}
}

sfr CCON     = 0xD8; 	//PCA���ƼĴ���
sfr CMOD     = 0xD9; 	//PCAģʽ�Ĵ���
sfr CCAPM0   = 0xDA; 	//PCAģ��0ģʽ�Ĵ��� //ģ��0��ӦP1.3/CEX0/PCA0/PWM0(STC12C5A60S2ϵ��)   
sfr CCAPM1   = 0xDB; 	//PCAģ��1ģʽ�Ĵ��� //ģ��1��ӦP1.4/CEX1/PCA1/PWM1(STC12C5A60S2ϵ��)
sfr CL       = 0xE9;    //PCA ��ʱ�Ĵ��� ��λ
sfr CH       = 0xF9; 	//PCA ��ʱ�Ĵ��� ��λ
sfr CCAP0L   = 0xEA; 	//PCAģ��0�� ����Ĵ��� ��λ
sfr CCAP0H   = 0xFA;    //PCAģ��0�� ����Ĵ��� ��λ
sfr CCAP1L   = 0xEB; 	//PCAģ��1�� ����Ĵ��� ��λ
sfr CCAP1H   = 0xFB; 	//PCAģ��1�� ����Ĵ��� ��λ
sfr PCA_PWM0 = 0xF2; 	//PCA PWM ģʽ�����Ĵ���0
sfr PCA_PWM1 = 0xF3; 	//PCA PWM ģʽ�����Ĵ���1
sbit CF   = 0xDF;       //PCA���������־λ
sbit CR   = 0xDE;       //PCA������ ���п���λ
sbit CCF1 = 0xD9;       //PCAģ��1�жϱ�־
sbit CCF0 = 0xD8;       //PCAģ��0�жϱ�־

bit oFlag = 0;		  //�жϴ���=ż��?0:1
bit pwmValueMode = 0;// pwm <256?0:1


void PWM_clock(U8 clock)
{ 
	CMOD |= (clock<<1);
	CL = 0x00;
	CH = 0x00;
}

void PWM_Initial(void)	   // ģ��0,ģ��1����ΪPWM���,���ж�,��ʼռ������Ϊ50%
{
	U16 a = 0;
	TMOD|=0x02; 		   // timer 0 mode 2: 8-Bit reload  
	TH0=0xff;
	TR0=1;
	PWM_clock(2);      	   // PCA/PWMʱ��ԴΪ ��ʱ��0�����
	CCAP0L = 0X80;
	CCAP0H = 0X80;         //ģ��0��ʼ��� ռ������Ϊ50%
	CCAP1L = 0X80;
	CCAP1H = 0X80;         //ģ��0��ʼ��� ռ������Ϊ50%

	PWM_start(2,2);

}  


void PWM_start(U8 module,U8 mode) 
{
	if(module==0)
	{
		switch(mode)
		{ 
			case 0: CCAPM0 = 0X42;break; 	//ģ��0����Ϊ8λPWM��������ж�
			case 1: CCAPM0 = 0X53;break; 	//ģ��0����Ϊ8λPWM������½��ز����ж�
			case 2: CCAPM0 = 0X63;break; 	//ģ��0����Ϊ8λPWM����������ز����ж�
			case 3: CCAPM0 = 0X73;break; 	//ģ��0����Ϊ8λPWM����������ز����ж�
			default: break;
		}
	}
	else if(module==1)
		{
			switch(mode)
			{ 
				case 0: CCAPM1 = 0X42;break; 	//ģ��1����Ϊ8λPWM��������ж�
				case 1: CCAPM1 = 0X53;break; 	//ģ��1����Ϊ8λPWM������½��ز����ж�
				case 2: CCAPM1 = 0X63;break; 	//ģ��1����Ϊ8λPWM����������ز����ж�
				case 3: CCAPM1 = 0X73;break; 	//ģ��1����Ϊ8λPWM����������ز����ж�
				default: break;
			}
		} 
	else if(module==2)
		{
			switch(mode)
			{ 
				case 0: CCAPM0 = CCAPM1 = 0X42;break; //ģ��0��1����Ϊ8λPWM��������ж�
				case 1: CCAPM0 = CCAPM1 = 0X53;break; //ģ��0��1����Ϊ8λPWM������½��ز����ж�
				case 2: CCAPM0 = CCAPM1 = 0X63;break; //ģ��0��1����Ϊ8λPWM����������ز����ж�
				case 3: CCAPM0 = CCAPM1 = 0X73;break; //ģ��0��1����Ϊ8λPWM����������ز����ж�
				default: break;
			}
		} 
	CR=1; //PCA��������ʼ���� 
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
		CCF1=0;   //������� 
	}
	if(CF)   
		CF=0;     //������� 	 	
}
/*********************************************************************************************
��������PWM0ռ�ձ����ú���
��  �ã�PWM0_set();
��  ����0x00~0xFF�������0~255��
����ֵ����
��  ��������PWMģʽռ�ձȣ�Ϊ0ʱȫ���ߵ�ƽ��Ϊ1ʱȫ���͵�ƽ
��  ע�������ҪPWM1�����ú�����ֻҪ��CCAP0L��CCAP0H�е�0��Ϊ1����
/**********************************************************************************************/
void PWM0_set (unsigned char a)
{
	CCAP0L= 256-a; //����ֱֵ��д��CCAP0L
 	CCAP0H= 256-a; //����ֱֵ��д��CCAP0H
	
}

void PWM1_set (unsigned char a)
{
	CCAP1L= 256-a; //����ֱֵ��д��CCAP0L
 	CCAP1H= 256-a; //����ֱֵ��д��CCAP0H
	
}
/*********************************************************************************************
��������9bitPWM1ռ�ձ����ú���
��  �ã�PWM1_set9();��Ҫ���õ�PWM0 �������ش����ж�
��  ����0~512
����ֵ����
��  ��������PWMģʽռ�ձȣ�Ϊ0ʱȫ���ߵ�ƽ��Ϊ1ʱȫ���͵�ƽ
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