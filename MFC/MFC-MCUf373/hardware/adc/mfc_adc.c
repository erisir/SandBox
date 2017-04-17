#include "mfc_adc.h"
#include "../../user/main.h"
#include "../pid/mfc_pid.h"
#include <stdio.h>

// SDADCת���ĵ�ѹֵͨ��inject��ʽ����SRAM
int16_t InjectedConvData[2]={0};
int16_t ADC_ConvertedValue = 0;
uint16_t ADC_ConvertedSumWindow=10000;
#define ADC1_DR_Address    ((uint32_t)0x4001244C)

void SetVotageTimes(float val){
	ADC_ConvertedSumWindow = val;
}

void GetPosition(void){//���ڵ���
	printf("@P%.3f,%.3f,%d\n",GetADCVoltage(0),GetADCVoltage(1),GetPIDOutput());
} 
float  GetADCVoltage(unsigned char ch){//PID����
	float votage = 0.0; 
	votage =2* (((ADC_Mean(ch) + 32768) * SDADC_VREF) / (SDADC_GAIN * SDADC_RESOL));	
	return votage; // ��ȡת����ADֵ	 
}

float ADC_Mean(unsigned char ch) {
	int i = 0;
	int32_t sum=0;
	for(i=0;i<ADC_ConvertedSumWindow;i++){
		sum+=InjectedConvData[ch];	
	}
	return ((float)sum/(ADC_ConvertedSumWindow)); 
}
/*****************************************
---------------ADC��ֵ�˲�----------------
����N�㣬����ȥ�������Сֵ��ȡƽ��
*****************************************/
#define N 180 
int16_t ADC_Filter(unsigned char ch) 
{ 
	unsigned int count,i,j; 
	int16_t value_buf[N],temp; 
	unsigned int trimEnd = ADC_ConvertedSumWindow;
	int32_t  sum=0; 
	for  (count=0;count<N;count++) 
	{ 
		value_buf[count] = InjectedConvData[ch];	   //ȥ�����λ
	} 
	for (j=0;j<N-1;j++) //ð������
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
	return (int16_t)(sum/(N-trimEnd*2));  
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

static void ADC1_GPIO_Config(void)
{
	GPIO_InitTypeDef GPIO_InitStructure;
	
	/* Enable DMA clock */
	RCC_AHBPeriphClockCmd(RCC_AHBPeriph_DMA1, ENABLE);
	
	/* Enable ADC1 and GPIOC clock */
	RCC_APB2PeriphClockCmd(RCC_APB2Periph_ADC1 | RCC_AHBPeriph_GPIOB, ENABLE);
	
	/* Configure PB.01  as analog input */
	GPIO_InitStructure.GPIO_Pin = GPIO_Pin_1;
	GPIO_InitStructure.GPIO_Mode = GPIO_Mode_AN;
	GPIO_Init(GPIOB, &GPIO_InitStructure);				// PC1,����?������������
}

/**
  * @brief  ����ADC1�L���g??MDAg?
  * @param  ��
  * @retval ��
  */
static void ADC1_Mode_Config(void)
{
	DMA_InitTypeDef DMA_InitStructure;
	ADC_InitTypeDef ADC_InitStructure;
	
	/* DMA channel1 configuration */
	DMA_DeInit(DMA1_Channel1);
	
	DMA_InitStructure.DMA_PeripheralBaseAddr = ADC1_DR_Address;	 			//ADC��?
	DMA_InitStructure.DMA_MemoryBaseAddr = (u32)&ADC_ConvertedValue;	//�?��?
	DMA_InitStructure.DMA_DIR = DMA_DIR_PeripheralSRC;
	DMA_InitStructure.DMA_BufferSize = 1;
	DMA_InitStructure.DMA_PeripheralInc = DMA_PeripheralInc_Disable;	//�����?�?�
	DMA_InitStructure.DMA_MemoryInc = DMA_MemoryInc_Disable;  				//�?��?�?�
	DMA_InitStructure.DMA_PeripheralDataSize = DMA_PeripheralDataSize_HalfWord;	//����
	DMA_InitStructure.DMA_MemoryDataSize = DMA_MemoryDataSize_HalfWord;
	DMA_InitStructure.DMA_Mode = DMA_Mode_Circular;										//?������
	DMA_InitStructure.DMA_Priority = DMA_Priority_High;
	DMA_InitStructure.DMA_M2M = DMA_M2M_Disable;
	DMA_Init(DMA1_Channel1, &DMA_InitStructure);
	
	/* Enable DMA channel1 */
	DMA_Cmd(DMA1_Channel1, ENABLE);
	
	/* ADC1 configuration */	
	ADC_InitStructure.ADC_ScanConvMode = DISABLE ; 	 				//��??��g?��?��g?���?�?�2?�
	ADC_InitStructure.ADC_ContinuousConvMode = ENABLE;			//����l��?��g?������?�?���ADC?��
	ADC_InitStructure.ADC_ExternalTrigConv = ADC_ExternalTrigConv_None;	//��'���?����?��
	ADC_InitStructure.ADC_DataAlign = ADC_DataAlign_Right; 	//�?������?���
	ADC_InitStructure.ADC_NbrOfChannel = 1;	 								//??����?����?1
	ADC_Init(ADC1, &ADC_InitStructure);
	
	/*����ADC?�?�?PCLK2��8��?����9MHz*/
	RCC_ADCCLKConfig(RCC_PCLK2_Div4); 
	/*����ADC1��?��11?55.	5���������?�����?1 */ 
	ADC_RegularChannelConfig(ADC1, ADC_Channel_9, 1, ADC_SampleTime_55Cycles5);
	
	/* Enable ADC1 DMA */
	ADC_DMACmd(ADC1, ENABLE);
	
	/* Enable ADC1 */
	ADC_Cmd(ADC1, ENABLE);
	
	/*��???�J��� */   
	ADC_ResetCalibration(ADC1);
	/*�?�??�J�����?��� */
	while(ADC_GetResetCalibrationStatus(ADC1));
	
	/* ADC?? */
	ADC_StartCalibration(ADC1);
	/* �?�??���*/
	while(ADC_GetCalibrationStatus(ADC1));
	
	/* ������?����?����������'���������ADC?�� */ 
	ADC_Cmd(ADC1, ENABLE);
}

/**
  * @brief  ADC1��'��
  * @param  ��
  * @retval ��
  */
void ADC_Config(void)
{
	ADC1_GPIO_Config();
	ADC1_Mode_Config();
}
/*********************************************END OF FILE**********************/
