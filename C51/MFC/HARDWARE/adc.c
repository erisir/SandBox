/*
在中断中读取votage_input值，100ms一次，在adc_start中实现循环，pid控制
*/
#include "STC15W.h"
#include "intrins.h"
#include "adc.h"
#include "PID.h"
#include "key.h"
#include "pwm.h"
#include "lcd1602.h"
#include "Uart1.h"

 
 


#define ADC_POWER   0x80            //ADC power control bit 电源控制位
#define ADC_FLAG    0x10            //ADC complete flag   标志位
#define ADC_START   0x08            //ADC start control bit 启动控制位
#define ADC_SPEEDLL 0x00            //420 clocks
#define ADC_SPEEDL  0x20            //280 clocks
#define ADC_SPEEDH  0x40            //140 clocks
#define ADC_SPEEDHH 0x60            //70 clocks

 
sbit STS= P2^0;			   
sbit CS = P2^1;
sbit A0 = P2^2;
sbit RC = P2^3;
sbit CE = P2^4;

uint  votage_input =0;
uint   votage_avr = 0;
char  getVotageTimes = 30;
uint  getVotageCounter = 0;
uint  chSel = 0;
uint  PIDEnable = 0; 
float res = 0.0;
void ADC_start()
{
    char a = 0;
	unsigned char buf[8];
	while(1)
	{   	     
		votage_input  =   Get_ADC_ResultReq(0,getVotageTimes);
	 	//votage_input = 3000;
		//res = AD1674_Read(); 
		//votage_input =  (u32) ((res/256)*625);
		votage_avr 	=votage_input;

	  
	 	buf[0] = 'V';
		buf[1] = '=';	 
		buf[6] = votage_avr%10 + '0';
		votage_avr = votage_avr/10;
		buf[5] = votage_avr%10 + '0';
		votage_avr = votage_avr/10;		
		buf[4] = votage_avr%10 + '0';
		votage_avr = votage_avr/10;
		buf[3] = '.';
		buf[2] = votage_avr%10 + '0';
		votage_avr = votage_avr/10;		
		buf[7] =  ';';	
	
	    send_string_com(buf);  

	    if(PIDEnable == 1){
		 compare_temper(votage_input);
		 } 
		
		}	 		
 		
}
void  SetTClose()
{
	 PIDEnable = 0;
	 PWM1_set(5) ;
	 num_lcdDis(0,0x0D,5,3);
	 hz_lcdDis(1,0x0C," ---");
}
void  SetTOpen()
{
	 PIDEnable = 0;
	 PWM1_set(250) ;
	 num_lcdDis(0,0x0D,510,3);
	 hz_lcdDis(1,0x0C," ---");
}
void  SetTPID()
{
	 PIDEnable = 1;

}
void SetVotageTimes(v_data)
{
  getVotageTimes =   v_data;
}
void  GetPosition()
{
	send_int_com(votage_input);
}
void  keyFunction(uint key_value)
{
   	switch(key_value)
	 {
		  case 0x00:SetPointDown();break;//0 按下相应的键显示相对应的码值
		  case 0x01:SetPointUp();break;//1
		  default:break;
	 }
 
} 


void ADC_Init()
{	
	P1ASF = 0xFF;
    ADC_RESL = 0;                                          //清除上次采集的结果	
	ADC_RES = 0;                                           //清除上次采集的结果	
	EADC = 1;											   //开启ADC
    ADC_CONTR = ADC_POWER | ADC_SPEEDLL  ; //上电    */
	Delay(20);
	P0M0 = 0;  
	P0M1 = 0;  //设置P0口为准双向口，用于测量外部状态    
	P2M0 = 0;  
	P2M1 = 0;  //设置P0口为准双向口，用于测量外部状态                                                  
}

void Delay(uint n)
{
	uint x;
    while (n--)
		{
			  x=5000;
			  while (x--);
		}
}

uint  Get_ADC_ResultReq(char ch,uint getADCCounter)  //执行周期约 0.07ms
{
    uint ADCCounter = 0;
	float v = 0.0;
    ADC_CONTR = ADC_POWER | ADC_SPEEDLL | ADC_START | ch ;//开始转换
    _nop_();_nop_();_nop_();_nop_();_nop_();_nop_();                         
    
	for(ADCCounter = 0;ADCCounter<getADCCounter;ADCCounter++){
	    while (!(ADC_CONTR & ADC_FLAG));             //等待转换结束(ADC_FLAG=0时一直等待，直到变为1跳出)
	    	ADC_CONTR &= !ADC_FLAG;                 //关闭AD转换
			v += (ADC_RES*4+ADC_RESL)/getADCCounter;
		  	ADC_RES = 0;
			ADC_RESL = 0;
		 	ADC_CONTR = ADC_POWER | ADC_SPEEDHH | ADC_START | ch ;//开始转换
		 	_nop_();_nop_();_nop_();_nop_();_nop_();_nop_();    
		}
		
    return (uint) (v*4101.0/(1024));    //返回数据（10位AD值，ADC_RES高8位+ADC_RESL低2位）	

}
  
void Delay1000ms()		//@11.0592MHz
{
	unsigned char i, j, k;

	_nop_();
	i = 36;
	j = 5;
	k = 211;
	do
	{
		do
		{
			while (--k);
		} while (--j);
	} while (--i);
}


  