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

 
 
#define ADC_POWER   0x80            //ADC电源控制位
#define ADC_FLAG    0x10            //ADC完成标志
#define ADC_START   0x08            //ADC起始控制位
#define ADC_SPEEDLL 0x00            //540个时钟
#define ADC_SPEEDL  0x20            //360个时钟
#define ADC_SPEEDH  0x40            //180个时钟
#define ADC_SPEEDHH 0x60            //90个时钟

void Delay(unsigned int n);
 
 
/*----------------------------
读取ADC结果
----------------------------*/
unsigned long int GetADCResult(unsigned char ch,unsigned int ADC_smooth_window)
{
    

	unsigned int cnt = 0;
	float v = 0.0;
	unsigned int temp;
    ADC_CONTR = ADC_POWER | ADC_SPEEDLL | ADC_START | ch ;//开始转换
    _nop_();_nop_();_nop_();_nop_();_nop_();_nop_();                         
    
	for(cnt = 0;cnt<ADC_smooth_window;cnt++){
	    while (!(ADC_CONTR & ADC_FLAG));             //等待转换结束(ADC_FLAG=0时一直等待，直到变为1跳出)
	    	ADC_CONTR &= !ADC_FLAG;                 //关闭AD转换
			temp = 	 ADC_RES;
			temp = (temp << 2) | (ADC_RESL & 3);
			v += temp/ADC_smooth_window;
		  	ADC_RES = 0;
			ADC_RESL = 0;
		 	ADC_CONTR = ADC_POWER | ADC_SPEEDHH | ADC_START | ch ;//开始转换
		 	_nop_();_nop_();_nop_();_nop_();_nop_();_nop_();    
		}
		
    return (unsigned long int) (v*4900.0/(1024));    //返回数据（10位AD值，ADC_RES高8位+ADC_RESL低2位）	
}

 
/*----------------------------
初始化ADC
----------------------------*/
void InitADC()
{
    P1ASF = 0xff;                   //设置P1口为AD口
    ADC_RES = 0;                    //清除结果寄存器
    ADC_CONTR = ADC_POWER | ADC_SPEEDLL;
    Delay(2);                       //ADC上电并延时
}

 
/*----------------------------
软件延时
----------------------------*/
void Delay(unsigned int n)
{
    unsigned int x;

    while (n--)
    {
        x = 5000;
        while (x--);
    }
}

