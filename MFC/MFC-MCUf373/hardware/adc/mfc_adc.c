/**
 ******************************************************************************
 * @file    bsp_xxx.c
 * @author  fire
 * @version V1.0
 * @date    2013-xx-xx
 * @brief   adc1 应用bsp / DMA 模式
 ******************************************************************************
 * @attention
 *
 * 实验平台:野火 iSO STM32 开发板 
 * 论坛    :http://www.chuxue123.com
 * 淘宝    :http://firestm32.taobao.com
 *
 ******************************************************************************
 */ 

#include "mfc_adc.h"
#include "../../user/main.h"
#include <stdio.h>

// ADC1转换的电压值通过MDA方式传到SRAM
int16_t InjectedConvData[2];
uint16_t ADC_ConvertedSumWindow;
// 局部变量，用于保存转换计算后的电压值

void SetVotageTimes(unsigned int val){
	ADC_ConvertedSumWindow = val;
}
/**
 * @brief  使能ADC1和DMA1的时钟，初始化PB.0/1
 * @param  无
 * @retval 无
 */


/**
 * @brief  ADC1初始化
 * @param  无
 * @retval 无
 */
void GetPosition(void){//串口调用
	printf("@P%.3f,%.3f\n",GetADCVoltage(0),GetADCVoltage(1));
} 
float  GetADCVoltage(unsigned char ch){//PID调用
	float votage = 0.0; 
	votage = (((ADC_Mean(ch) + 32768) * SDADC_VREF) / (SDADC_GAIN * SDADC_RESOL));
	return votage; // 读取转换的AD值	 
}
float ADC_Mean(unsigned char ch) {//去掉最大最小值
	int i = 0;
	uint32_t sum=0;
	int16_t min=InjectedConvData[ch];
	int16_t max=InjectedConvData[ch];
	int16_t temp;

	
	for(i=0;i<ADC_ConvertedSumWindow;i++){
		temp = InjectedConvData[ch];
		sum+=temp;
		if(temp>max)
			max = temp;
		if(temp<min)
			min = temp;
	}
	sum -=(min+max);
	
	return (float)sum/(ADC_ConvertedSumWindow-2); 
}
/*****************************************
---------------ADC中值滤波----------------
采样N点，排序，去掉最大最小值，取平均
*****************************************/
#define N 40 
float ADC_Filter(unsigned char ch) 
{ 
	unsigned int count,i,j; 
	int16_t value_buf[N],temp; 
	unsigned int trimEnd = 5;
	uint32_t  sum=0; 
	for  (count=0;count<N;count++) 
	{ 
		value_buf[count] = InjectedConvData[ch];	   //去掉最低位
	} 
	for (j=0;j<N-1;j++) //冒泡排序
	{ 
		for (i=0;i<N-j;i++) 
		{ 
			if ( value_buf[i]>value_buf[i+1] ) 
			{ 
				temp = value_buf[i]; 
				value_buf[i] = value_buf[i+1];  
				value_buf[i+1] = temp; 
			} 
		} 
	} 
	for(count=trimEnd;count<N-trimEnd;count++) 
		sum += value_buf[count]; 
	return (float)(sum/(N-trimEnd*2));  
}
 
/* Private function prototypes -----------------------------------------------*/

uint32_t SDADC1_Config(void)
{
	
  SDADC_AINStructTypeDef SDADC_AINStructure;
  GPIO_InitTypeDef GPIO_InitStructure;
  NVIC_InitTypeDef NVIC_InitStructure;
  uint32_t SDADCTimeout = 0;
  /* POT_SDADC APB2 interface clock enable */
  RCC_APB2PeriphClockCmd(POT_SDADC_CLK, ENABLE);
  
  /* PWR APB1 interface clock enable */
  RCC_APB1PeriphClockCmd(RCC_APB1Periph_PWR, ENABLE);
  /* Enable POT_SDADC analog interface */
  PWR_SDADCAnalogCmd(POT_SDADC_PWR, ENABLE);

  /* Set the SDADC divider: The SDADC should run @6MHz */
  /* If Sysclk is 72MHz, SDADC divider should be 12 */
  RCC_SDADCCLKConfig(RCC_SDADCCLK_SYSCLK_Div12);

  /* POT_GPIO_CLK Peripheral clock enable */
  RCC_AHBPeriphClockCmd(POT_GPIO_CLK, ENABLE);

  /* POT_SDADC channel 5P (PB1) */
  GPIO_InitStructure.GPIO_Mode  = GPIO_Mode_AN;
  GPIO_InitStructure.GPIO_PuPd = GPIO_PuPd_NOPULL;
  GPIO_InitStructure.GPIO_Pin = POT_GPIO_PIN;
  GPIO_Init(POT_GPIO_PORT, &GPIO_InitStructure);

  /* Select External reference: The reference voltage selection is available
     only in SDADC1 and therefore to select the VREF for SDADC2/SDADC3, SDADC1
     clock must be already enabled */
  SDADC_VREFSelect(POT_SDADC_VREF);

  /* Insert delay equal to ~5 ms */
  Delay(5);
  /* Enable POT_SDADC */
  SDADC_Cmd(POT_SDADC, ENABLE);

  /* Enter initialization mode */
  SDADC_InitModeCmd(POT_SDADC, ENABLE);
  SDADCTimeout = SDADC_INIT_TIMEOUT;
  /* wait for INITRDY flag to be set */
  while((SDADC_GetFlagStatus(POT_SDADC, SDADC_FLAG_INITRDY) == RESET) && (--SDADCTimeout != 0));

  if(SDADCTimeout == 0)
  {
    /* INITRDY flag can not set */
    return 1;
  }

  /* Analog Input configuration conf0: use single ended zero reference */
  SDADC_AINStructure.SDADC_InputMode = SDADC_InputMode_SEZeroReference;
  SDADC_AINStructure.SDADC_Gain = POT_SDADC_GAIN;
  SDADC_AINStructure.SDADC_CommonMode = SDADC_CommonMode_VSSA;
  SDADC_AINStructure.SDADC_Offset = 0;
  SDADC_AINInit(POT_SDADC, SDADC_Conf_0, &SDADC_AINStructure);
 
  /* select POT_SDADC channel 5 to use conf0 only one channel each time*/
	SDADC_ChannelConfig(POT_SDADC, SDADC_Channel_6, SDADC_Conf_0);
  SDADC_ChannelConfig(POT_SDADC, SDADC_Channel_5, SDADC_Conf_0);
	


  /* select channel(*) 5 */
	SDADC_InjectedChannelSelect(POT_SDADC, SDADC_Channel_5|SDADC_Channel_6);


  /* Enable continuous mode */
  SDADC_InjectedContinuousModeCmd(POT_SDADC, ENABLE);

  /* Exit initialization mode */
  SDADC_InitModeCmd(POT_SDADC, DISABLE);

  /* NVIC Configuration */
  NVIC_InitStructure.NVIC_IRQChannel = SDADC1_IRQn;
  NVIC_InitStructure.NVIC_IRQChannelPreemptionPriority = 0;
  NVIC_InitStructure.NVIC_IRQChannelSubPriority = 0;
  NVIC_InitStructure.NVIC_IRQChannelCmd = ENABLE;
  NVIC_Init(&NVIC_InitStructure);

  /* configure calibration to be performed on conf0 */
  SDADC_CalibrationSequenceConfig(POT_SDADC, SDADC_CalibrationSequence_1);
  /* start POT_SDADC Calibration */
  SDADC_StartCalibration(POT_SDADC);
  /* Set calibration timeout: 5.12 ms at 6 MHz in a single calibration sequence */
  SDADCTimeout = SDADC_CAL_TIMEOUT;
  /* wait for POT_SDADC Calibration process to end */
  while((SDADC_GetFlagStatus(POT_SDADC, SDADC_FLAG_EOCAL) == RESET) && (--SDADCTimeout != 0));
  
  if(SDADCTimeout == 0)
  {
    /* EOCAL flag can not set */
    return 2;
  }

  /* Enable end of injected conversion interrupt */
  SDADC_ITConfig(POT_SDADC, SDADC_IT_JEOC, ENABLE);
  /* Start a software start conversion */
  SDADC_SoftwareStartInjectedConv(POT_SDADC);
  
  return 0;
}
/*********************************************END OF FILE**********************/
