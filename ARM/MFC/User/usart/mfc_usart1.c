/**
  ******************************************************************************
  * @file    bsp_usart1.c
  * @author  fire
  * @version V1.0
  * @date    2013-xx-xx
  * @brief   usart应用bsp
  ******************************************************************************
  * @attention
  *
  * 实验平台:野火 iSO STM32 开发板 
  * 论坛    :http://www.chuxue123.com
  * 淘宝    :http://firestm32.taobao.com
  *
  ******************************************************************************
  */
  
#include "mfc_usart1.h"

#include "../pid/mfc_pid.h"
#include "../adc/mfc_adc.h"
#include "../pwm/mfc_pwm.h"

extern __IO unsigned char busy; 
extern __IO  char uart_flag;
extern __IO unsigned char uart_start_flag;  
extern __IO unsigned char receive[16];
 /**
  * @brief  USART1 GPIO 配置,工作模式配置。9600 8-N-1
  * @param  无
  * @retval 无
  */
void USART1_Config(void)
{
	GPIO_InitTypeDef GPIO_InitStructure;
	USART_InitTypeDef USART_InitStructure;
	
	/* config USART1 clock */
	RCC_APB2PeriphClockCmd(RCC_APB2Periph_USART1 | RCC_APB2Periph_GPIOA, ENABLE);
	
	/* USART1 GPIO config */
	/* Configure USART1 Tx (PA.09) as alternate function push-pull */
	GPIO_InitStructure.GPIO_Pin = GPIO_Pin_9;
	GPIO_InitStructure.GPIO_Mode = GPIO_Mode_AF_PP;
	GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;
	GPIO_Init(GPIOA, &GPIO_InitStructure);    
	/* Configure USART1 Rx (PA.10) as input floating */
	GPIO_InitStructure.GPIO_Pin = GPIO_Pin_10;
	GPIO_InitStructure.GPIO_Mode = GPIO_Mode_IN_FLOATING;
	GPIO_Init(GPIOA, &GPIO_InitStructure);
	
	/* USART1 mode config */
	USART_InitStructure.USART_BaudRate = 115200;
	USART_InitStructure.USART_WordLength = USART_WordLength_8b;
	USART_InitStructure.USART_StopBits = USART_StopBits_1;
	USART_InitStructure.USART_Parity = USART_Parity_No ;
	USART_InitStructure.USART_HardwareFlowControl = USART_HardwareFlowControl_None;
	USART_InitStructure.USART_Mode = USART_Mode_Rx | USART_Mode_Tx;
	USART_Init(USART1, &USART_InitStructure);
	
	/* 使能串口1接收中断 */
	USART_ITConfig(USART1, USART_IT_RXNE, ENABLE);
	
	USART_Cmd(USART1, ENABLE);
}

/// 配置USART1接收中断
void NVIC_Configuration(void)
{
	NVIC_InitTypeDef NVIC_InitStructure; 
	/* Configure the NVIC Preemption Priority Bits */  
	NVIC_PriorityGroupConfig(NVIC_PriorityGroup_0);
	
	/* Enable the USARTy Interrupt */
	NVIC_InitStructure.NVIC_IRQChannel = USART1_IRQn;	 
	NVIC_InitStructure.NVIC_IRQChannelPreemptionPriority = 0;
	NVIC_InitStructure.NVIC_IRQChannelSubPriority = 1;
	NVIC_InitStructure.NVIC_IRQChannelCmd = ENABLE;
	NVIC_Init(&NVIC_InitStructure);
}


/// 重定向c库函数printf到USART1
int fputc(int ch, FILE *f)
{
		/* 发送一个字节数据到USART1 */
		USART_SendData(USART1, (uint8_t) ch);
		
		/* 等待发送完毕 */
		while (USART_GetFlagStatus(USART1, USART_FLAG_TXE) == RESET);		
	
		return (ch);
}

/// 重定向c库函数scanf到USART1
int fgetc(FILE *f)
{
		/* 等待串口1输入数据 */
		while (USART_GetFlagStatus(USART1, USART_FLAG_RXNE) == RESET);

		return (int)USART_ReceiveData(USART1);
}

unsigned char checksumCalc(unsigned char rec[])
{ 

	return (( unsigned char)rec[0])^(( unsigned char)rec[1])^(( unsigned char)rec[2])^(( unsigned char)rec[3]);
} 
void parseCMD(){
	unsigned int v_data;
	uart_flag = 0;
		 	
	v_data =  receive[2]*256+receive[3] ;
	 	 
 
     switch(receive[1]){
		   
		  case _U_SetVotage:SetSetPoint(v_data);break;//0 

		  case _U_SetPTerm:SetPIDparam_P_inc(v_data);break;//2

		  case _U_SetITerm:SetPIDparam_I_inc(v_data);break;//4
 
		  case _U_SetDTerm:SetPIDparam_D_inc(v_data);break;//6
		   
		  case _U_SetPWMVal:SetPWMValue(v_data);break;//8
		  
		  case _U_GetVotage:GetPosition();break;//8
		  
		  case _U_SetDura:GetPIDStatu();break;//8

		  case _U_SetTClose:SetTClose();break;//8

		  case _U_SetTOpen:SetTOpen();break;//8

		  case _U_SetTPID:SetTPID();break;//8

		  //case _U_SetVotageTimes:SetVotageTimes(v_data);break;//8

		  //case _U_SetVotageChanel:SetVotageChanel(v_data);break;//8
		  
 			
		  default:break;
	 }	    
}
unsigned char cmd_ready()
{
	return uart_flag; 
}

/*********************************************END OF FILE**********************/
