/**
 ******************************************************************************
 * @file    main.c
 * @author  fire
 * @version V1.0
 * @date    2013-xx-xx
 * @brief   MFC
	资源：串口：PA9,PA10
	      ADC PB0----sensor
				ADC PB1----refence
		    PWM PA6
				LED PB9
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

#include "stm32f37x.h"
#include <stdio.h>
#include "../hardware/usart/mfc_usart1.h"
#include "../hardware/adc/mfc_adc.h"
#include "../hardware/pid/mfc_pid.h"
#include "../hardware/pwm/mfc_pwm.h"
#include "main.h"
#include "../hardware/led/mfc_led.h"
#include "../hardware/TimBase/mfc_TiMbase.h"

volatile u32 time = 0; // ms 计时变量 
__IO uint32_t TimingDelay = 0;
char led = 0;
/**
 * @brief  主函数
 * @param  无
 * @retval 无
 */
int main(void)
{	
	uint32_t ret = 0;
	__IO float InputVoltageMv = 0;
	RCC_ClocksTypeDef RCC_Clocks;
	/* SysTick end of count event each 1ms */
  RCC_GetClocksFreq(&RCC_Clocks);
  SysTick_Config(RCC_Clocks.HCLK_Frequency / 1000);
	USART1_Config();	
	printf("welcome!!\n");
	printf("USART1_Config OK\n");
	TIM2_PWM_Init();
	printf("TIM2_PWM_Init OK\n");
	/* led 端口配置 */ 
	LED_GPIO_Config();
	printf("LED_GPIO_Config OK\n");
	/* PID采样周期使用time4中断 */ 
	TIM4_PID_Init();
	printf("TIM4_PID_Init OK\n");
	PIDInit() ;
	printf("PIDInit OK\n");
	SetVotageTimes(200);
	ret = SDADC1_Config();
  if( ret != 0){
		printf("SDADC1_Config false!!error code[%d]\n",ret);
	}else{
  printf("SDADC1_Config OK\n");
	}

	while (1)
	{ 
		if(cmd_ready()){
			parseCMD();
		}	
		if (isPIDEnable() && time >= getPeriod() ) /* 1000 * 1 ms = 1s 时间到 */
    {
      time = 0;
			PIDStart();
		  LED1_TOGGLE
    } 

	}
}

/**
  * @brief  Inserts a delay time.
  * @param  nTime: specifies the delay time length, in 1 ms.
  * @retval None
  */
void Delay(__IO uint32_t nTime)
{
  TimingDelay = nTime;

  while(TimingDelay != 0);
}

/**
  * @brief  Decrements the TimingDelay variable.
  * @param  None
  * @retval None
  */
void TimingDelay_Decrement(void)
{
  if (TimingDelay != 0x00)
  { 
    TimingDelay--;
  }
}
/* Private functions ---------------------------------------------------------*/


/*********************************************END OF FILE**********************/
