#ifndef __adc_H_
#define __adc_H_


#define uint unsigned int
#define uchar unsigned char
#define	u32 unsigned long int

void  ADC_Init();
void  ADC_start();
void  keyFunction(uint key_value);
void  Delay(uint z); 	// —” ±
void  GetPosition();
uint  Get_ADC_ResultReq(char ch,uint getADCCounter);
void  SetTClose();
void  SetTOpen();
void  SetTPID();
void  SetVotageTimes(v_data);
u32   AD1674_Read();
void Delay1000ms();
#endif