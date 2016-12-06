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
#include <stdio.h>
#define ADC1_DR_Address    ((u32)0x40012400+0x4c)
// ADC1转换的电压值通过MDA方式传到SRAM
uint16_t ADC_ConvertedValue;
uint16_t ADC_ConvertedSumWindow;
// 局部变量，用于保存转换计算后的电压值 	 
float ADC_ConvertedValueLocal;  

void SetVotageTimes(unsigned int val){
	ADC_ConvertedSumWindow = val;
}
/**
 * @brief  使能ADC1和DMA1的时钟，初始化PC.01
 * @param  无
 * @retval 无
 */
static void ADC1_GPIO_Config(void)
{
	GPIO_InitTypeDef GPIO_InitStructure;

	/* Enable DMA clock */
	RCC_AHBPeriphClockCmd(RCC_AHBPeriph_DMA1, ENABLE);

	/* Enable ADC1 and GPIOC clock */
	RCC_APB2PeriphClockCmd(RCC_APB2Periph_ADC1 | RCC_APB2Periph_GPIOB, ENABLE);

	/* Configure PB.01  as analog input */
	GPIO_InitStructure.GPIO_Pin = GPIO_Pin_1;
	GPIO_InitStructure.GPIO_Mode = GPIO_Mode_AIN;
	GPIO_Init(GPIOB, &GPIO_InitStructure);				// PC1,输入时不用设置速率
}

/**
 * @brief  配置ADC1的工作模式为MDA模式
 * @param  无
 * @retval 无
 */
static void ADC1_Mode_Config(void)
{
	DMA_InitTypeDef DMA_InitStructure;
	ADC_InitTypeDef ADC_InitStructure;

	/* DMA channel1 configuration */
	DMA_DeInit(DMA1_Channel1);

	DMA_InitStructure.DMA_PeripheralBaseAddr = ADC1_DR_Address;	 			//ADC地址
	DMA_InitStructure.DMA_MemoryBaseAddr = (u32)&ADC_ConvertedValue;	//内存地址
	DMA_InitStructure.DMA_DIR = DMA_DIR_PeripheralSRC;
	DMA_InitStructure.DMA_BufferSize = 1;
	DMA_InitStructure.DMA_PeripheralInc = DMA_PeripheralInc_Disable;	//外设地址固定
	DMA_InitStructure.DMA_MemoryInc = DMA_MemoryInc_Disable;  				//内存地址固定
	DMA_InitStructure.DMA_PeripheralDataSize = DMA_PeripheralDataSize_HalfWord;	//半字
	DMA_InitStructure.DMA_MemoryDataSize = DMA_MemoryDataSize_HalfWord;
	DMA_InitStructure.DMA_Mode = DMA_Mode_Circular;										//循环传输
	DMA_InitStructure.DMA_Priority = DMA_Priority_High;
	DMA_InitStructure.DMA_M2M = DMA_M2M_Disable;
	DMA_Init(DMA1_Channel1, &DMA_InitStructure);

	/* Enable DMA channel1 */
	DMA_Cmd(DMA1_Channel1, ENABLE);

	/* ADC1 configuration */	
	ADC_InitStructure.ADC_Mode = ADC_Mode_Independent;			//独立ADC模式
	ADC_InitStructure.ADC_ScanConvMode = DISABLE ; 	 				//禁止扫描模式，扫描模式用于多通道采集
	ADC_InitStructure.ADC_ContinuousConvMode = ENABLE;			//开启连续转换模式，即不停地进行ADC转换
	ADC_InitStructure.ADC_ExternalTrigConv = ADC_ExternalTrigConv_None;	//不使用外部触发转换
	ADC_InitStructure.ADC_DataAlign = ADC_DataAlign_Right; 	//采集数据右对齐
	ADC_InitStructure.ADC_NbrOfChannel = 1;	 								//要转换的通道数目1
	ADC_Init(ADC1, &ADC_InitStructure);

	/*配置ADC时钟，为PCLK2的8分频，即9MHz*/
	RCC_ADCCLKConfig(RCC_PCLK2_Div8); 
	/*配置ADC1的通道11为55.	5个采样周期，序列为1 */ 
	ADC_RegularChannelConfig(ADC1, ADC_Channel_9, 1, ADC_SampleTime_55Cycles5);

	/* Enable ADC1 DMA */
	ADC_DMACmd(ADC1, ENABLE);

	/* Enable ADC1 */
	ADC_Cmd(ADC1, ENABLE);

	/*复位校准寄存器 */   
	ADC_ResetCalibration(ADC1);
	/*等待校准寄存器复位完成 */
	while(ADC_GetResetCalibrationStatus(ADC1));

	/* ADC校准 */
	ADC_StartCalibration(ADC1);
	/* 等待校准完成*/
	while(ADC_GetCalibrationStatus(ADC1));

	/* 由于没有采用外部触发，所以使用软件触发ADC转换 */ 
	ADC_SoftwareStartConvCmd(ADC1, ENABLE);
}

/**
 * @brief  ADC1初始化
 * @param  无
 * @retval 无
 */
void ADC1_Init(void)
{
	ADC_ConvertedSumWindow = 1000;
	ADC1_GPIO_Config();
	ADC1_Mode_Config();
}

void GetPosition(void){//串口调用
	printf("@P%d",GetADCVoltage()); 
} 
unsigned int  GetADCVoltage(void){//PID调用
	float votage = 0.0; 
  votage =(float) ADC_Mean()/2048*3300; 
	return votage; // 读取转换的AD值	 
}
unsigned int ADC_Mean(void) {//去掉最大最小值
	int i = 0;
	uint32_t sum=0;
	uint16_t min=ADC_ConvertedValue;
	uint16_t max=ADC_ConvertedValue;
	uint16_t temp;
	
	for(i=0;i<ADC_ConvertedSumWindow;i++){
		temp = ADC_ConvertedValue>>1;
		sum+=temp;
		if(temp>max)
			max = temp;
		if(temp<min)
			temp = min;
	}
	sum -=(min+max);
	return (unsigned int)sum/(ADC_ConvertedSumWindow-2); 
}
/*****************************************
---------------ADC中值滤波----------------
采样N点，排序，去掉最大最小值，取平均
*****************************************/
#define N 40 
unsigned int ADC_Filter(void) 
{ 
	unsigned int count,i,j; 
	unsigned int value_buf[N],temp; 
	unsigned int trimEnd = 5;
	uint32_t  sum=0; 
	for  (count=0;count<N;count++) 
	{ 
		value_buf[count] = ADC_ConvertedValue>>1;	   //去掉最低位
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
	return (unsigned int)(sum/(N-trimEnd*2));  
} 
/*********************************************END OF FILE**********************/
