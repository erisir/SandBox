
#include "stm32f10x.h"
#define     PWM_DUTY        65535            //����PWM�����ڣ���ֵΪʱ��������������ʹ��24.576MHZ����Ƶ����PWMƵ��Ϊ6000HZ��

#define     PWM_HIGH_MIN    0              //����PWM�������Сռ�ձȡ��û������޸ġ�
#define     PWM_HIGH_MAX    (PWM_DUTY-PWM_HIGH_MIN) //����PWM��������ռ�ձȡ��û������޸ġ�

extern unsigned int PWM_high;               //PWM�ձȼĴ�������PWM����ߵ�ƽ��PCAʱ�����������ռ�ձ�д���������
void TIM3_Breathing_Init(void);
void LoadPWM(unsigned int pwm);
