

#define     PWM_DUTY        20000            //����PWM�����ڣ���ֵΪʱ��������������ʹ��24.576MHZ����Ƶ����PWMƵ��Ϊ6000HZ��

#define     PWM_HIGH_MIN    32              //����PWM�������Сռ�ձȡ��û������޸ġ�
#define     PWM_HIGH_MAX    (PWM_DUTY-PWM_HIGH_MIN) //����PWM��������ռ�ձȡ��û������޸ġ�

extern unsigned int PWM_high;               //PWM�ձȼĴ�������PWM����ߵ�ƽ��PCAʱ�����������ռ�ձ�д���������
void LoadPWM(unsigned int high);
void PWMn_init();