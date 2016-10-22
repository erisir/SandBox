/*---------------------------------------------------------------------*/
/* --- STC MCU Limited ------------------------------------------------*/
/* --- STC15F4K60S4 ϵ�� ADת����ѯ��ʽ����----------------------------*/
/* --- Mobile: (86)13922805190 ----------------------------------------*/
/* --- Fax: 86-0513-55012956,55012947,55012969 ------------------------*/
/* --- Tel: 86-0513-55012928,55012929,55012966-------------------------*/
/* --- Web: www.STCMCU.com --------------------------------------------*/
/* --- Web: www.GXWMCU.com --------------------------------------------*/
/* ���Ҫ�ڳ�����ʹ�ô˴���,���ڳ�����ע��ʹ����STC�����ϼ�����        */
/* ���Ҫ��������Ӧ�ô˴���,����������ע��ʹ����STC�����ϼ�����        */
/*---------------------------------------------------------------------*/

//��ʾ����Keil������������ѡ��Intel��8058оƬ�ͺŽ��б���
//�����ر�˵��,����Ƶ��һ��Ϊ11.0592MHz

#include <STC15F2K60S2.H>
#include "intrins.h"
#include "ADC.h"

 
 
#define ADC_POWER   0x80            //ADC��Դ����λ
#define ADC_FLAG    0x10            //ADC��ɱ�־
#define ADC_START   0x08            //ADC��ʼ����λ
#define ADC_SPEEDLL 0x00            //540��ʱ��
#define ADC_SPEEDL  0x20            //360��ʱ��
#define ADC_SPEEDH  0x40            //180��ʱ��
#define ADC_SPEEDHH 0x60            //90��ʱ��

void Delay(unsigned int n);
 
 
/*----------------------------
��ȡADC���
----------------------------*/
unsigned long int GetADCResult(unsigned char ch,unsigned int ADC_smooth_window)
{
    

	unsigned int cnt = 0;
	float v = 0.0;
	unsigned int temp;
    ADC_CONTR = ADC_POWER | ADC_SPEEDLL | ADC_START | ch ;//��ʼת��
    _nop_();_nop_();_nop_();_nop_();_nop_();_nop_();                         
    
	for(cnt = 0;cnt<ADC_smooth_window;cnt++){
	    while (!(ADC_CONTR & ADC_FLAG));             //�ȴ�ת������(ADC_FLAG=0ʱһֱ�ȴ���ֱ����Ϊ1����)
	    	ADC_CONTR &= !ADC_FLAG;                 //�ر�ADת��
			temp = 	 ADC_RES;
			temp = (temp << 2) | (ADC_RESL & 3);
			v += temp/ADC_smooth_window;
		  	ADC_RES = 0;
			ADC_RESL = 0;
		 	ADC_CONTR = ADC_POWER | ADC_SPEEDHH | ADC_START | ch ;//��ʼת��
		 	_nop_();_nop_();_nop_();_nop_();_nop_();_nop_();    
		}
		
    return (unsigned long int) (v*4900.0/(1024));    //�������ݣ�10λADֵ��ADC_RES��8λ+ADC_RESL��2λ��	
}

 
/*----------------------------
��ʼ��ADC
----------------------------*/
void InitADC()
{
    P1ASF = 0xff;                   //����P1��ΪAD��
    ADC_RES = 0;                    //�������Ĵ���
    ADC_CONTR = ADC_POWER | ADC_SPEEDLL;
    Delay(2);                       //ADC�ϵ粢��ʱ
}

 
/*----------------------------
�����ʱ
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

