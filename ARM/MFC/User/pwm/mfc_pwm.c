#include "mfc_pwm.h"

/**
 * @brief  配置TIM3复用输出PWM时用到的I/O
 * @param  无
 * @retval 无
 */
void TIM3_GPIO_Config(void) 
{
	GPIO_InitTypeDef GPIO_InitStructure;

	/* GPIOB clock enable */
	RCC_APB2PeriphClockCmd(RCC_APB2Periph_GPIOB, ENABLE); 

	/* 配置PWM用到的PB0引脚 */
	GPIO_InitStructure.GPIO_Pin =  GPIO_Pin_0 ;
	GPIO_InitStructure.GPIO_Mode = GPIO_Mode_AF_PP;		    // 复用推挽输出
	GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;

	GPIO_Init(GPIOB, &GPIO_InitStructure);
}


/**
 * @brief  配置嵌套向量中断控制器NVIC
 * @param  无
 * @retval 无
 */
void NVIC_Config_PWM(void)
{
	NVIC_InitTypeDef NVIC_InitStructure;

	/* Configure one bit for preemption priority */
	NVIC_PriorityGroupConfig(NVIC_PriorityGroup_1);

	/* 配置TIM3_IRQ中断为中断源 */
	NVIC_InitStructure.NVIC_IRQChannel = TIM3_IRQn;
	NVIC_InitStructure.NVIC_IRQChannelPreemptionPriority = 0;
	NVIC_InitStructure.NVIC_IRQChannelSubPriority = 2;
	NVIC_InitStructure.NVIC_IRQChannelCmd = ENABLE;
	NVIC_Init(&NVIC_InitStructure);
}

/**
 * @brief  配置TIM3输出的PWM信号的模式，如周期、极性
 * @param  无
 * @retval 无
 */

/*
 * TIMxCLK/CK_PSC --> TIMxCNT --> TIMx_ARR --> 中断 & TIMxCNT 重新计数
 *                    TIMx_CCR(电平发生变化)
 *
 * 信号周期=(TIMx_ARR +1 ) * 时钟周期
 * 
 */

/*    _______    ______     _____      ____       ___        __         _
 * |_|       |__|      |___|     |____|    |_____|   |______|  |_______| |________|
 */
void TIM3_Mode_Config(unsigned int Prescaler)
{
	TIM_TimeBaseInitTypeDef  TIM_TimeBaseStructure;
	TIM_OCInitTypeDef  TIM_OCInitStructure;																				

	/* 设置TIM3CLK 时钟为72MHZ */
	RCC_APB1PeriphClockCmd(RCC_APB1Periph_TIM3, ENABLE); 					//使能TIM3时钟

	/* 基本定时器配置 */		 
	TIM_TimeBaseStructure.TIM_Period = PWM_HIGH_MAX;   				  //当定时器从0计数到255，即为266次，为一个定时周期
	TIM_TimeBaseStructure.TIM_Prescaler = Prescaler;	    							//设置预分频：
	TIM_TimeBaseStructure.TIM_ClockDivision = TIM_CKD_DIV1 ;			//设置时钟分频系数：不分频(这里用不到)
	TIM_TimeBaseStructure.TIM_CounterMode = TIM_CounterMode_Up;  	//向上计数模式
	TIM_TimeBaseInit(TIM3, &TIM_TimeBaseStructure);

	/* PWM模式配置 */
	TIM_OCInitStructure.TIM_OCMode = TIM_OCMode_PWM1;	    				//配置为PWM模式1
	TIM_OCInitStructure.TIM_OutputState = TIM_OutputState_Enable;	//使能输出
	TIM_OCInitStructure.TIM_Pulse = 0;										  			//设置初始PWM脉冲宽度为0	
	TIM_OCInitStructure.TIM_OCPolarity = TIM_OCPolarity_High;  	  //当定时器计数值小于CCR1_Val时为低电平

	TIM_OC3Init(TIM3, &TIM_OCInitStructure);	 									//使能通道3


	TIM_OC3PreloadConfig(TIM3, TIM_OCPreload_Enable);						//使能预装载	

	TIM_ARRPreloadConfig(TIM3, ENABLE);			 										//使能TIM3重载寄存器ARR

	/* TIM3 enable counter */
	TIM_Cmd(TIM3, ENABLE);                   										//使能定时器3	

	TIM_ITConfig(TIM3,TIM_IT_Update, ENABLE);										//使能update中断

																			

}

/**
 * @brief  TIM3 呼吸灯初始化
 *         配置PWM模式和GPIO
 * @param  无
 * @retval 无
 */
void TIM3_PWM_Init(void)
{
	TIM3_GPIO_Config();//引脚
	TIM3_Mode_Config(2);	//TIMER 相关
	NVIC_Config_PWM();//中断优先级					
}
void TIM3_Set_Prescaler(unsigned int Prescaler){
	TIM3_Mode_Config(Prescaler);	//TIMER 相关
}
/**************** 计算PWM重装值函数 *******************/
//注意：TIM_SetCompare3的3为ch3的寄存器
void    LoadPWM(unsigned int pwmval)
{
	TIM_SetCompare3(TIM3,pwmval);	
}
/*********************************************END OF FILE**********************/
