/**
 ******************************************************************************
 * @file    main.c
 * @author  fire
 * @version V1.0
 * @date    2013-xx-xx
 * @brief   MFC
	资源：串口：PA9,PA10
	      ADC PB1
				ADC outer PB0
		    PWM PA6
				采样定时器:TIM4 B8/B9
 ******************************************************************************
 * @attention
 *
 * 实验平台:野火 iSO STM32 开发板 
 * 论坛    :http://www.chuxue123.com
 * 淘宝    :http://firestm32.taobao.com
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

volatile u32 time = 0; // ms 计时变量 


/**
 * @brief  主函数
 * @param  无
 * @retval 无
 */
int main(void)
{	
	USART1_Config();
	NVIC_Configuration();
	TIM2_PWM_Init();
	/* led 端口配置 */ 
	LED_GPIO_Config();
	/* PID采样周期使用time4中断 */ 
	TIM4_PID_Init();
	PIDInit() ;
	ADC1_Init();

	
	while (1)
	{ 
		if(cmd_ready()){
			parseCMD();
		}
				
		if (isPIDEnable() && time >= getPeriod() ) /* 1000 * 1 ms = 1s 时间到 */
    {
      time = 0;
			/* LED1 取反 */      
			LED1_TOGGLE; 
			PIDStart();
    }  
	}
}
/*********************************************END OF FILE**********************/
