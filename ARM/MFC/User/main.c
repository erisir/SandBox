/**
  ******************************************************************************
  * @file    main.c
  * @author  fire
  * @version V1.0
  * @date    2013-xx-xx
  * @brief   MFC
	��Դ�����ڣ�PA9,PA10
	      ADC PB0
				PWM PB1
  ******************************************************************************
  * @attention
  *
  * ʵ��ƽ̨:Ұ�� iSO STM32 ������ 
  * ��̳    :http://www.chuxue123.com
  * �Ա�    :http://firestm32.taobao.com
  *
  ******************************************************************************
  */

//time : pc1-1 0      1us
//       pc1-2 0 0.5us    1.5us
//			 pc1-3 0 0.5us 0.8us

#include "stm32f10x.h"
#include "mfc_usart1.h"
#include "adc/mfc_adc.h"
#include "pid/mfc_pid.h"
#include "pwm/mfc_pwm.h"

// ADC1ת���ĵ�ѹֵͨ��MDA��ʽ����SRAM
extern __IO uint16_t ADC_ConvertedValue;

// �ֲ����������ڱ���ת�������ĵ�ѹֵ 	 
float ADC_ConvertedValueLocal;        

// �����ʱ
void Delay(__IO uint32_t nCount)
{
  for(; nCount != 0; nCount--);
} 

/**
  * @brief  ������
  * @param  ��
  * @retval ��
  */
int main(void)
{	
	/* USART1 config */
	uint8_t pwm = 0;
	USART1_Config();
	NVIC_Configuration();
	TIM3_Breathing_Init();
	PIDInit() ;
	/* enable adc1 and config adc1 to dma mode */
	ADC1_Init();
	 
	while (1)
	{
		//ADC_ConvertedValueLocal =(float) ADC_ConvertedValue/4096*3.3; // ��ȡת����ADֵ	
		//printf("\r\n The current AD value = %f V \r\n",ADC_ConvertedValueLocal); 
		//Delay(0x2fffee);  
		if(cmd_ready()){
			parseCMD();
		}
		if(isPIDEnable()){
		 PIDStart();
		 } 
		/* LoadPWM(pwm);
		 pwm ++;
		 if(pwm >200)
			 pwm = 0;*/
	}
}
/*********************************************END OF FILE**********************/
