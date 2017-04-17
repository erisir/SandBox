/**
 ******************************************************************************
 * @file    main.c
 * @author  DeadNight
 * @version V1.0
 * @date    2016-xx-xx
 * @brief   MFC
	资源：串口：PA9,PA10
	      SDADC PB0----sensor/ADC ch8
				SDADC PB1----refence/ADC ch9
		    PWM TIM,PA6
 ******************************************************************************
 */

#include "stm32f37x.h"
#include <stdio.h>
#include "../hardware/usart/mfc_usart1.h"
#include "../hardware/adc/mfc_adc.h"
#include "../hardware/pid/mfc_pid.h"
#include "../hardware/pwm/mfc_pwm.h"
#include "main.h"
#include "../hardware/led/mfc_led.h"
#include "../hardware/TimBase/mfc_TiMbase.h"

__IO uint32_t TimingDelay = 0;
/**
 * @brief  主函数
 * @param  无
 * @retval 无
 */
int main(void)
{	

	RCC_ClocksTypeDef RCC_Clocks;
  RCC_GetClocksFreq(&RCC_Clocks);
  SysTick_Config(RCC_Clocks.HCLK_Frequency / 1000L);
	
	USART1_Config();	
	printf("Welcome!!\n");
	printf("System Clock[%d]\n",RCC_Clocks.HCLK_Frequency);
	printf("USART1_Config OK\n");
	TIM2_PWM_Init();
	printf("TIM2_PWM_Init OK\n");
	PIDInit() ;
	printf("PIDInit OK\n");
	SDADC1_Config();
	//ADC_Config();
  printf("SDADC1_Config OK\n");


	while (1)
	{ 
		if(cmd_ready()){
			parseCMD();
		}	
		if (isPIDEnable() /*&& TimingDelay == 0*/ ) /* 1000 * 1 ms = 1s 时间到 */
    {
      //TimingDelay = getPeriod();
			PIDStart();
    } 

	}
}

/**
  * @brief  Inserts a delay time.
  * @param  nTime: specifies the delay time length, in *10 us.
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
