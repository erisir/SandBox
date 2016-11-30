/*---------------------------------------------------------------------*/
/* --- STC MCU Limited ------------------------------------------------*/
/* --- STC15F4K60S4 系列 AD转换查询方式举例----------------------------*/
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
#include "ADC.h"
#include "Uart1.h"
 
 
#define ADC_POWER   0x80            //ADC电源控制位
#define ADC_FLAG    0x10            //ADC完成标志
#define ADC_START   0x08            //ADC起始控制位
#define ADC_SPEEDLL 0x00            //540个时钟
#define ADC_SPEEDL  0x20            //360个时钟
#define ADC_SPEEDH  0x40            //180个时钟
#define ADC_SPEEDHH 0x60            //90个时钟

unsigned int get_votage_smooth_window = 300;
unsigned int ch = 2;
unsigned int vBandgap = 0;
unsigned int vBandgapReal = 1270;
float Dig2Ang = 0.0;
/*----------------------------
初始化ADC
----------------------------*/
void InitADC()
{
	P1ASF = 0x00;                   //不设置P1口为模拟口   读取bangap电压
	ADC_RES = 0;                    //清除结果寄存器
    ADC_CONTR = ADC_POWER | ADC_SPEEDLL;
	Delay100ms();                       //ADC上电并延时
	vBandgap = GetADCResult(0);
	Dig2Ang = (float)(vBandgapReal)/vBandgap;
    P1ASF = 0xff;                   //设置P1口为AD口
    ADC_RES = 0;                    //清除结果寄存器
    ADC_CONTR = ADC_POWER | ADC_SPEEDLL;
    Delay100ms();                       //ADC上电并延时
}
 
void SetVotageChanel(unsigned int v){
	ch = v;
}
void SetVotageTimes(unsigned int v_data)
{
  get_votage_smooth_window =   v_data;
}

void GetPosition(){
    unsigned  int temp[3];
	ch = 2;
	temp[0]=getCurrentVoatage();
	ch = 4;
	temp[1]=getCurrentVoatage();
	ch = 6;
	temp[2]=getCurrentVoatage();
	SendInt(temp);
} 
unsigned int getCurrentVoatage(){
    
	//return (unsigned int)(GetADCResult(ch)*Dig2Ang);
	return GetADCResult(ch);
}

/*----------------------------
读取ADC结果
----------------------------*/
unsigned  int GetADCResult(unsigned char ch)
{   
	unsigned int temp;
	ADC_CONTR = ADC_POWER | ADC_SPEEDLL | ch | ADC_START;
    _nop_();                        //等待4个NOP
    _nop_();
    _nop_();
    _nop_();
    while (!(ADC_CONTR & ADC_FLAG));//等待ADC转换完成
    ADC_CONTR &= ~ADC_FLAG;         //Close ADC

    temp = 	 ADC_RES& 0xFF;
	temp = (temp << 2) | (ADC_RESL & 0x03); 

    return temp;                //返回数据（10位AD值，ADC_RES全8位+ADC_RESL低2位）	
 	
}

 

