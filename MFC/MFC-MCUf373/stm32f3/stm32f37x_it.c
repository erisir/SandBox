/**
  ******************************************************************************
  * @file    ADC/ADC_AnalogWatchdog/stm32f37x_it.c 
  * @author  MCD Application Team
  * @version V1.0.0
  * @date    20-September-2012
  * @brief   Main Interrupt Service Routines.
  *          This file provides template for all exceptions handler and 
  *          peripherals interrupt service routine.
  ******************************************************************************
  * @attention
  *
  * <h2><center>&copy; COPYRIGHT 2012 STMicroelectronics</center></h2>
  *
  * Licensed under MCD-ST Liberty SW License Agreement V2, (the "License");
  * You may not use this file except in compliance with the License.
  * You may obtain a copy of the License at:
  *
  *        http://www.st.com/software_license_agreement_liberty_v2
  *
  * Unless required by applicable law or agreed to in writing, software 
  * distributed under the License is distributed on an "AS IS" BASIS, 
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  *
  ******************************************************************************
  */

/* Includes ------------------------------------------------------------------*/
#include "stm32f37x_it.h"
#include "main.h"
extern int16_t InjectedConvData[2];
extern volatile u32 time;

__IO unsigned char busy; 
__IO char uart_flag;
__IO unsigned char uart_start_flag;  
__IO unsigned char receive[16];// ??????????idata,??????32
  			  	   //??64??????unsigned char idata receive[16]={0};  
unsigned char rec;				       
u8 boardaddress; 
u8 cmd_len=8; //@aXXXXa
static u8 uart_cnt=0;//????????? 
/** @addtogroup STM32F37x_StdPeriph_Examples
  * @{
  */
// volatile uint32_t time=0; //计时变量
 volatile unsigned char touch_flag=0;
/** @addtogroup ADC_AnalogWatchdog
  * @{
  */

/* Private typedef -----------------------------------------------------------*/
/* Private define ------------------------------------------------------------*/
/* Private macro -------------------------------------------------------------*/
/* Private variables ---------------------------------------------------------*/
/* Private function prototypes -----------------------------------------------*/
/* Private functions ---------------------------------------------------------*/

/******************************************************************************/
/*            Cortex-M4 Processor Exceptions Handlers                         */
/******************************************************************************/

/**
  * @brief  This function handles NMI exception.
  * @param  None
  * @retval None
  */
void NMI_Handler(void)
{
}

/**
  * @brief  This function handles Hard Fault exception.
  * @param  None
  * @retval None
  */
void HardFault_Handler(void)
{
  /* Go to infinite loop when Hard Fault exception occurs */
  while (1)
  {
  }
}

/**
  * @brief  This function handles Memory Manage exception.
  * @param  None
  * @retval None
  */
void MemManage_Handler(void)
{
  /* Go to infinite loop when Memory Manage exception occurs */
  while (1)
  {
  }
}

/**
  * @brief  This function handles Bus Fault exception.
  * @param  None
  * @retval None
  */
void BusFault_Handler(void)
{
  /* Go to infinite loop when Bus Fault exception occurs */
  while (1)
  {
  }
}

/**
  * @brief  This function handles Usage Fault exception.
  * @param  None
  * @retval None
  */
void UsageFault_Handler(void)
{
  /* Go to infinite loop when Usage Fault exception occurs */
  while (1)
  {
  }
}

/**
  * @brief  This function handles SVCall exception.
  * @param  None
  * @retval None
  */
void SVC_Handler(void)
{
}

/**
  * @brief  This function handles Debug Monitor exception.
  * @param  None
  * @retval None
  */
void DebugMon_Handler(void)
{
}

/**
  * @brief  This function handles PendSVC exception.
  * @param  None
  * @retval None
  */
void PendSV_Handler(void)
{
}

/**
  * @brief  This function handles SysTick Handler.
  * @param  None
  * @retval None
  */
void SysTick_Handler(void)
{
	TimingDelay_Decrement();
}
 
/******************************************************************************/
/*                 STM32F37x Peripherals Interrupt Handlers                   */
/******************************************************************************/
/**
  * @brief  This function handles ADC1 global interrupts requests.
  * @param  None
  * @retval None
  */
void ADC1_IRQHandler(void)
{
}


/******************************************************************************/
/*                 STM32F37x Peripherals Interrupt Handlers                   */
/*  Add here the Interrupt Handler for the used peripheral(s) (PPP), for the  */
/*  available peripheral interrupt handler's name please refer to the startup */
/*  file (startup_stm32f37x.s).                                               */
/******************************************************************************/
void USART1_IRQHandler(void)
{
	uint8_t ch;
	
	if(USART_GetITStatus(USART1, USART_IT_RXNE) != RESET)
	{ 	

		ch = USART_ReceiveData(USART1);	 	
		receive[uart_cnt] = ch;	
		if(receive[uart_cnt] == '@'){//begin
			uart_start_flag = 1;	
		}	
		if(uart_start_flag ==1){
						
			uart_cnt++;	 
			if(uart_cnt==cmd_len-1){	//@x0001X
				uart_start_flag = 0;
				uart_cnt=0;
				//if(checksumCalc(receive)==receive[cmd_len-1]){
					uart_flag = 1;
			//	} 			
			}

		}
		
		
	} 
	 
}


/* PWM?????? */
void TIM2_IRQHandler(void)
{	
	if (TIM_GetITStatus(TIM2, TIM_IT_Update) != RESET)	//TIM_IT_Update
 	{					
		TIM_ClearITPendingBit (TIM2, TIM_IT_Update);	//??????????
	}
}
/* PWM?????? */
void TIM3_IRQHandler(void)
{	
	if (TIM_GetITStatus(TIM3, TIM_IT_Update) != RESET)	//TIM_IT_Update
 	{					
		TIM_ClearITPendingBit (TIM3, TIM_IT_Update);	//??????????
	}
}
/**
  * @brief  This function handles TIM2 interrupt request.
  * @param  None
  * @retval None
  */
void TIM4_IRQHandler(void)
{
	if ( TIM_GetITStatus(TIM4 , TIM_IT_Update) != RESET ) 
	{	
		time++;
		TIM_ClearITPendingBit(TIM4 , TIM_FLAG_Update);  		 
	}		 	
}
/**
  * @brief  This function handles SDADC1 interrupt request.
  * @param  None
  * @retval : None
  */
void SDADC1_IRQHandler(void)
{
  uint32_t ChannelIndex;
  int16_t value = 0;
  if(SDADC_GetFlagStatus(SDADC1, SDADC_FLAG_JEOC) != RESET)
  {
    /* Get the converted value */
    value = SDADC_GetInjectedConversionValue(SDADC1, &ChannelIndex);
		if(ChannelIndex == 0x00050020)//5 PB1
			{
			InjectedConvData[1] = value;
		}
		if(ChannelIndex == 0x00060040)//6 PB0
			{
			InjectedConvData[0] = value;
		}
		 
  }
}

/**
  * @}
  */

/**
  * @}
  */

/************************ (C) COPYRIGHT STMicroelectronics *****END OF FILE****/
