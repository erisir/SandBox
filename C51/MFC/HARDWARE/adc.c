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
#include "Uart1.h"
 
 
#define ADC_POWER   0x80            //ADC��Դ����λ
#define ADC_FLAG    0x10            //ADC��ɱ�־
#define ADC_START   0x08            //ADC��ʼ����λ
#define ADC_SPEEDLL 0x00            //540��ʱ��
#define ADC_SPEEDL  0x20            //360��ʱ��
#define ADC_SPEEDH  0x40            //180��ʱ��
#define ADC_SPEEDHH 0x60            //90��ʱ��

unsigned int get_votage_smooth_window = 300;
unsigned int ch = 2;
unsigned int vBandgap = 0;
unsigned int vBandgapReal = 1270;
float Dig2Ang = 0.0;
/*----------------------------
��ʼ��ADC
----------------------------*/
void InitADC()
{
	P1ASF = 0x00;                   //������P1��Ϊģ���   ��ȡbangap��ѹ
	ADC_RES = 0;                    //�������Ĵ���
    ADC_CONTR = ADC_POWER | ADC_SPEEDLL;
	Delay100ms();                       //ADC�ϵ粢��ʱ
	vBandgap = GetADCResult(0);
	Dig2Ang = (float)(vBandgapReal)/vBandgap;
    P1ASF = 0xff;                   //����P1��ΪAD��
    ADC_RES = 0;                    //�������Ĵ���
    ADC_CONTR = ADC_POWER | ADC_SPEEDLL;
    Delay100ms();                       //ADC�ϵ粢��ʱ
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
��ȡADC���
----------------------------*/
unsigned  int GetADCResult(unsigned char ch)
{   
	unsigned int temp;
	ADC_CONTR = ADC_POWER | ADC_SPEEDLL | ch | ADC_START;
    _nop_();                        //�ȴ�4��NOP
    _nop_();
    _nop_();
    _nop_();
    while (!(ADC_CONTR & ADC_FLAG));//�ȴ�ADCת�����
    ADC_CONTR &= ~ADC_FLAG;         //Close ADC

    temp = 	 ADC_RES& 0xFF;
	temp = (temp << 2) | (ADC_RESL & 0x03); 

    return temp;                //�������ݣ�10λADֵ��ADC_RESȫ8λ+ADC_RESL��2λ��	
 	
}

 

