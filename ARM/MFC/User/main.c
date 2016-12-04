/**
  ******************************************************************************
  * @file    main.c
  * @author  fire
  * @version V1.0
  * @date    2013-xx-xx
  * @brief   MFC
	资源：串口：PA9,PA10
	      ADC PB0
				PWM PB1
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

// ADC1转换的电压值通过MDA方式传到SRAM
extern __IO uint16_t ADC_ConvertedValue;

// 局部变量，用于保存转换计算后的电压值 	 
float ADC_ConvertedValueLocal;        

// 软件延时
void Delay(__IO uint32_t nCount)
{
  for(; nCount != 0; nCount--);
} 

/**
  * @brief  主函数
  * @param  无
  * @retval 无
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
		//ADC_ConvertedValueLocal =(float) ADC_ConvertedValue/4096*3.3; // 读取转换的AD值	
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
