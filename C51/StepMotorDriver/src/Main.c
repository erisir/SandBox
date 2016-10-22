#include "SerialDriver.h"
#include "1602LCDDriver.h" 
#include "StepMotorDriver.h" 

	unsigned char ch;
	unsigned char rec[8];
	unsigned char i=0;
	bool databegin = 0;

main()
{
    EA = 1;//�����ж�
	LCD_Initial();
    _lowLimitPort = 1;
    _highLimitPort =1; 
	InitSerial(); //Serial
	InitDevice(); //StepMotor
    LCD_Printf1("Device Init ok!");
	refLCD(  );
	SendStr("Device Init ok\r\n");

	while(1){
			if(_manualUpPort==0) //�м������𣿣�k1=0 ?��
			{ 
				delay_ms(10); //��ʱ����
				if(_manualUpPort==0)     //ȷʵ���м����£���
				{
					_directionPort = 0;
					LCD_Printf1("MOVE UP [um]");
					if(_manualAcceleratePort ==  1){

						while(_manualUpPort == 0 &&  _highLimitPort== 1)
						{
							ManualMove(0,1);

						}
						refLCD();
					}else{

						while(_manualUpPort == 0 &&  _highLimitPort== 1)
						{
							ManualMove(0,0);

						}
						refLCD();
					}
				}
			} //�ȴ������ſ�



			if(_manualDownPort==0) //�м������𣿣�k1=0 ?��
			{ 
				delay_ms(10); //��ʱ����
				if(_manualDownPort==0)     //ȷʵ���м����£���
				{
					_directionPort = 1;
					LCD_Printf1("MOVE DOWN [um]");
					if(_manualAcceleratePort ==  1){

						while(_manualDownPort  == 0 && _lowLimitPort == 1 )
						{
							ManualMove(1,1);
						}
						refLCD();
					}else{

						while(_manualDownPort  == 0 && _lowLimitPort == 1)
						{
							ManualMove(1,0);

						}
						refLCD();

					}
				} //�ȴ������ſ�
			} 
			if(_manualRotaUpPort==0) //�м������𣿣�k1=0 ?��
			{ 
				delay_ms(10); //��ʱ����
				if(_manualRotaUpPort==0)     //ȷʵ���м����£���
				{
					_rotationDirectionPort = 0;
					LCD_Printf1("Rota UP [um]");
					if(_manualRotaAcceleratePort ==  1){

						while(_manualRotaUpPort == 0 &&  _highLimitPort== 1)
						{
							ManualMove1(0,1);

						}
						refLCD();
					}else{

						while(_manualRotaUpPort == 0 &&  _highLimitPort== 1)
						{
							ManualMove1(0,0);

						}
						refLCD();
					}
				}
			} //�ȴ������ſ�



			if(_manualRotaDownPort==0) //�м������𣿣�k1=0 ?��
			{ 
				delay_ms(10); //��ʱ����
				if(_manualRotaDownPort==0)     //ȷʵ���м����£���
				{
					_rotationDirectionPort = 1;
					LCD_Printf1("Rota DOWN [um]");
					if(_manualRotaAcceleratePort ==  1){

						while(_manualRotaDownPort  == 0 && _lowLimitPort == 1 )
						{
							ManualMove1(1,1);
						}
						refLCD();
					}else{

						while(_manualRotaDownPort  == 0 && _lowLimitPort == 1)
						{
							ManualMove1(1,0);

						}
						refLCD();

					}
				} //�ȴ������ſ�
			} 
	}
}

/*------------------------------------------------
                     �����жϳ���
------------------------------------------------*/
void serial () interrupt 4
{

 
	if(RI) {	
	
		ch=SBUF;
		
		
		if(ch == '@'){//begin
			databegin = 1;	
		}
	
		if(databegin ==1){
			rec[i] = ch;
			i++;	
			
			if(i==7){ 
			databegin = 0;
			i=0;
			rec[7] = '\0';
			parseCMD(rec);				
			}

		}
	 	RI=0;
	}

}

/*------------------------------------------------
                     �ⲿ�жϳ���
------------------------------------------------
void key_scan() interrupt 2 //ʹ�����ⲿ�ж�0�ļ���ɨ���Ӻ���
{
	
}
*/