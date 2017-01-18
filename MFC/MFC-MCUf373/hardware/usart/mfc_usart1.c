/**
 ******************************************************************************
 * @file    bsp_usart1.c
 * @author  fire
 * @version V1.0
 * @date    2013-xx-xx
 * @brief   usartӦ��bsp
 ******************************************************************************
 * @attention
 *
 * ʵ��ƽ̨:Ұ�� iSO STM32 ������ 
 * ��̳    :http://www.chuxue123.com
 * �Ա�    :http://firestm32.taobao.com
 *
 ******************************************************************************
 */

#include "mfc_usart1.h"

#include "../pid/mfc_pid.h"
#include "../adc/mfc_adc.h"
#include "../pwm/mfc_pwm.h"
#include "../TimBase/mfc_TiMbase.h"

extern __IO unsigned char busy; 
extern __IO  char uart_flag;
extern __IO unsigned char uart_start_flag;  
extern __IO unsigned char receive[16];
/**
 * @brief  USART1 GPIO ����,����ģʽ���á�9600 8-N-1
 * @param  ��
 * @retval ��
 */
void USART1_Config(void)
{
	GPIO_InitTypeDef GPIO_InitStructure;
	USART_InitTypeDef USART_InitStructure;

	/* config USART1 clock */
	
	RCC_AHBPeriphClockCmd( RCC_AHBPeriph_GPIOA, ENABLE);
	RCC_APB2PeriphClockCmd(RCC_APB2Periph_USART1, ENABLE );

	/* USART1 GPIO config */
	GPIO_PinAFConfig(GPIOA,GPIO_PinSource9,GPIO_AF_7);
	GPIO_PinAFConfig(GPIOA,GPIO_PinSource10,GPIO_AF_7);        
	/*
	*  USART1_TX -> PA9 , USART1_RX ->        PA10
	*/    
	GPIO_InitStructure.GPIO_Pin = GPIO_Pin_9|GPIO_Pin_10;                 
	GPIO_InitStructure.GPIO_Mode = GPIO_Mode_AF; 
	GPIO_InitStructure.GPIO_OType = GPIO_OType_PP;
	GPIO_InitStructure.GPIO_PuPd = GPIO_PuPd_UP;
	GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz; 
	GPIO_Init(GPIOA, &GPIO_InitStructure);

	/* USART1 mode config */
	USART_InitStructure.USART_BaudRate = 115200;
	USART_InitStructure.USART_WordLength = USART_WordLength_8b;
	USART_InitStructure.USART_StopBits = USART_StopBits_1;
	USART_InitStructure.USART_Parity = USART_Parity_No ;
	USART_InitStructure.USART_HardwareFlowControl = USART_HardwareFlowControl_None;
	USART_InitStructure.USART_Mode = USART_Mode_Rx | USART_Mode_Tx;
	USART_Init(USART1, &USART_InitStructure);

	/* ʹ�ܴ���1�����ж� */
	USART_ITConfig(USART1, USART_IT_RXNE, ENABLE);

	USART_Cmd(USART1, ENABLE);
	
	NVIC_Configuration();
}

/// ����USART1�����ж�
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


/// �ض���c�⺯��printf��USART1
int fputc(int ch, FILE *f)
{
	/* ����һ���ֽ����ݵ�USART1 */
	USART_SendData(USART1, (uint8_t) ch);

	/* �ȴ�������� */
	while (USART_GetFlagStatus(USART1, USART_FLAG_TXE) == RESET);		

	return (ch);
}

/// �ض���c�⺯��scanf��USART1
int fgetc(FILE *f)
{
	/* �ȴ�����1�������� */
	while (USART_GetFlagStatus(USART1, USART_FLAG_RXNE) == RESET);

	return (int)USART_ReceiveData(USART1);
}

unsigned char checksumCalc(unsigned char rec[])
{ 

	return (( unsigned char)rec[0])^(( unsigned char)rec[1])^(( unsigned char)rec[2])^(( unsigned char)rec[3]);
} 
//4 byte to float
float Byte2Float(unsigned char* byteArry)
{
 return *((float*)byteArry);
}
void parseCMD(){
	float v_data;
	unsigned char  byteArry[4];
	uart_flag = 0;
	
	byteArry[0] = receive[2];
	byteArry[1] = receive[3];
	byteArry[2] = receive[4];
	byteArry[3] = receive[5];
	
	v_data =  Byte2Float(byteArry);//receive[2]*256+receive[3] ;
	//printf("v_data=%.4f\n",v_data);
	switch(receive[1]){

	case _U_SetVotage:
		SetSetPoint(v_data);
		printf("SetSetPoint:%.3f\n",v_data);
		break;//0 
	case _U_SetPTerm:
		SetPIDparam_P_inc(v_data);
		printf("SetPIDparam_P_inc:%.3f\n",v_data);
		break;//2
	case _U_SetITerm:
		SetPIDparam_I_inc(v_data);
		printf("SetPIDparam_I_inc:%.3f\n",v_data);
		break;//4
	case _U_SetDTerm:
		SetPIDparam_D_inc(v_data);
		printf("SetPIDparam_D_inc:%.3f\n",v_data);
		break;//6
	case _U_SetPWMVal:
		SetPWMValue(v_data);
		printf("SetPWMValue:%.3f\n",v_data);
		break;//8
	case _U_GetVotage:
		GetPosition();
		break;//8
	case _U_SetDura:
		GetPIDStatu();
		break;//8
	case _U_SetTClose:
		SetTClose();
		printf("SetTClose OK\n");
		break;//8
	case _U_SetTOpen:
		SetTOpen();
		printf("SetTOpen OK\n");
		break;//8
	case _U_SetTPID:
		SetTPID();
		printf("SetTPID OK\n");
		break;//8
	case _U_SetVotageTimes:
		SetVotageTimes(v_data);
		printf("SetVotageTimes:%.3f\n",v_data);
		break;//8
	case _U_SetPIDMode:
		SetPIDMode(v_data);
		printf("SetPIDMode:%.3f\n",v_data);
		break;//8
	case _U_SetPIDPeriod: 
		SetPIDPeriod(v_data);
		printf("SetPIDPeriod:%.3f\n",v_data);
		break;//8
	case _U_SetTIM4Prescaler: 
		TIM2_Set_Prescaler(v_data);
		printf("TIM2_Set_Prescaler:%.3f\n",v_data);
		break;
	case _U_SetPIDVotageChanel: 
		SetPIDVotageChanel(v_data);
		printf("SetPIDVotageChanel:%.3f\n",v_data);
		break;
	case _U_SetPIDThredHold: 
		SetPIDThredHold(v_data);
		printf("SetPIDThredHold:%.3f\n",v_data);
		break;
	case _U_SetForwardA: 
		SetForwardA(v_data);
		printf("SetForwardA:%.3f\n",v_data);
		break;
	case _U_SetForwardB:  
		SetForwardB(v_data);
		printf("SetForwardB:%.3f\n",v_data);
		break;
	case _U_SetForwardC:  
		SetForwardC(v_data);
		printf("SetForwardC:%.3f\n",v_data);
		break;
	case _U_SetBackwardA:  
		SetBackwardA(v_data);
		printf("SetBackwardA:%.3f\n",v_data);
		break;
	case _U_SetBackwardB:  
		SetBackwardB(v_data);
		printf("SetBackwardB:%.3f\n",v_data);
		break;
	case _U_SetBackwardC:  
		SetBackwardC(v_data);
		printf("SetBackwardC:%.3f\n",v_data);
		break;
	default:
		printf("unknow cmd\n");
		break;
	}	    
}
unsigned char cmd_ready()
{
	return uart_flag; 
}

/*********************************************END OF FILE**********************/
