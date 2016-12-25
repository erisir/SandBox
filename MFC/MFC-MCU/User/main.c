/**
 ******************************************************************************
 * @file    main.c
 * @author  fire
 * @version V1.0
 * @date    2013-xx-xx
 * @brief   MFC
	��Դ�����ڣ�PA9,PA10
	      ADC PB1
				ADC outer PB0
		    PWM PA6
				������ʱ��:TIM4 B8/B9
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

#include "led/mfc_led.h"
#include "TimBase/mfc_TiMbase.h"

volatile u32 time = 0; // ms ��ʱ���� 


/**
 * @brief  ������
 * @param  ��
 * @retval ��
 */
int main(void)
{	
	USART1_Config();
	NVIC_Configuration();
	TIM2_PWM_Init();
	/* led �˿����� */ 
	LED_GPIO_Config();
	/* PID��������ʹ��time4�ж� */ 
	TIM4_PID_Init();
	PIDInit() ;
	ADC1_Init();

	
	while (1)
	{ 
		if(cmd_ready()){
			parseCMD();
		}
				
		if (isPIDEnable() && time >= getPeriod() ) /* 1000 * 1 ms = 1s ʱ�䵽 */
    {
      time = 0;
			/* LED1 ȡ�� */      
			LED1_TOGGLE; 
			PIDStart();
    }  
	}
}
/*********************************************END OF FILE**********************/
