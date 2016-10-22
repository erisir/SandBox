package org.ndaguan.micromanager.stagecontrol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogManager;

import javax.comm.CommPortIdentifier;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.UnsupportedCommOperationException;

public class StepMotor {

	private SerialPort serialPort;
	private OutputStream outputStream;
	private int baudRate = 2400;
	private String comId = "COM8";
	private InputStream inputStream;
	private String lastError = "No error";
	private boolean isDeviceReady;
	private int timeout  = 100;
	private boolean debugLog = false;
	private double um2Step =  100;
	private static StepMotor instance;
	private final char DEVICE_OK=0x00;  
	private final char DEVICE_BUSY='J';
	private final char OUT_OF_LOW_LIMIT='K';
	private final char OUT_OF_HIGH_LIMIT='L';
	private final char CHECK_SUM_ERROR='M';
	private final char BAD_COMMAND='N';
	public static StepMotor getInstance()
	{
		if(instance == null)
			instance  = new StepMotor();
		return instance;
	}
	private byte checkSumCalc(byte[] data,int offset,int count)
	{
		byte checksum = data[offset];
		for (int i = offset + 1; i < offset + count; ++i)
		{
			checksum = (byte)(checksum ^ data[i]);
		}
		return checksum;

	}
	public StepMotor() {
		isDeviceReady = true;
		if(!initCom())
		{
			LogMessage(lastError);
			isDeviceReady = false;
		}
		else if(!initStage()){
			LogMessage(lastError);
			isDeviceReady = false;

		}
	}

	public boolean isDeviceReady()
	{
		return isDeviceReady;
	}
	private void PackageCommand(byte cmd,byte[] data,byte[] buf)
	{
		//CMD
		buf[0] = '@';
		buf[1] = (byte)cmd;

		//data
		if (data  == null){
			buf[2] = 'X';
			buf[3] = 'X';
			buf[4] = 'X';
			buf[5] = 'X';
		}
		else{
			buf[2] = data[0];
			buf[3] = data[1];
			buf[4] = data[2];
			buf[5] = data[3];
		}
		//checkSum
		buf[6] = checkSumCalc(buf, 0, 6);
	}
	private long RawToLong(byte[] rawData,int offset)
	{

		return (long)(    (0xff&rawData[offset + 0]) * 256*256*256 +     (0xff&rawData[offset + 1]) * 256*256
				+ (0xff&rawData[offset + 2]) * 256 +  (0xff&rawData[offset + 3]) );

	}
	//
	//Convert target position to XMT format
	private void LongToRaw(long value,byte[] rawData)
	{
		rawData[3] = (byte) (value % 256);
		value /= 256;
		rawData[2] = (byte) (value % 256);
		value /= 256;
		rawData[1] = (byte) (value % 256);
		value /= 256;
		rawData[0] = (byte) (value % 256);
	}
	public boolean setPosition(double step) throws IOException//um
	{
		double curr = getPosition();
		double delta = curr - step;
		double step2Um = 0.49827043;
		double sleept =Math.abs( 0.2*delta/step2Um);
		byte[] rawData = new byte[4];
		LongToRaw((long)step,rawData);
		byte []buf = new byte[10];
		PackageCommand((byte) 'T',rawData,buf);
		sendCommand(buf);
		Sleep(50);
		Sleep((int) sleept);
		
		boolean err = true;
		while(err){
			err = checkError();
			Sleep(3);
		}
		return true;
	}
	public boolean setRelativeStagePosition(double step) {
		return false;
	}

	public double getPosition() throws IOException
	{
		byte []buf = new byte[10];
		PackageCommand((byte) 'Q',null,buf);
		sendCommand(buf);
		Sleep(10);
		byte[] bret = readAnswer();
		if(bret.length<=0){
			LogMessage("getPosition--read nothing");
			return  -1;
		}
		if(bret.length<=6){
			LogMessage("getPosition--Not adq data");
			for (int i = 0; i < bret.length; i++) {
				System.out.print('[');
				System.out.print((int)(0xff&bret[i]));
				System.out.print("]\t");
			}
			return  -1;
		}
		if(checkSumCalc(bret,0, 6) != bret[6]){
			LogMessage("\r\ngetPosition--checkSumCalc error");
			for (int i = 0; i < bret.length; i++) {
				System.out.print('[');
				System.out.print((int)(0xff&bret[i]));
				System.out.print("]\t");
			}
			return -1;
		}
		long pos = RawToLong(bret,2);
		return (double)pos ;
	}

	private  void Sleep() {
		return ;
	}
	private  void Sleep(int l) {
		try {
			TimeUnit.MILLISECONDS.sleep(l);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	} 
	private boolean initCom()

	{
		Enumeration portList = CommPortIdentifier.getPortIdentifiers();

		while (portList.hasMoreElements()) {
			CommPortIdentifier portId = (CommPortIdentifier) portList.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				if (portId.getName().equals(comId)) {
					try {
						serialPort = (SerialPort)
								portId.open("XMTStage", 2000);
						//						LogMessage("\r\nInit COM ["+comId+"] OK");
					} catch (PortInUseException e) {
						lastError = e.toString();
						return false;
					}

					try {
						outputStream = serialPort.getOutputStream();
						inputStream = serialPort.getInputStream();
					} catch (IOException e) {
						lastError = e.toString();
						return false;
					}
					try {
						serialPort.setSerialPortParams(baudRate ,
								SerialPort.DATABITS_8,
								SerialPort.STOPBITS_1,
								SerialPort.PARITY_NONE);
					} catch (UnsupportedCommOperationException e) {
						lastError = e.toString();
						return false;
					}
					return true;
				}
			}
		}
		lastError = "Can not find " + comId;
		return false;

	}
	private void LogMessage(Object msg)
	{
		System.out.print(msg+"\r\n");
	}
	private boolean initStage()

	{
		try {
			sendCommand("@EXXXXX");
			Sleep(500);
			byte[] buf = readAnswer();
			if(buf.length ==0)
			{
				lastError = "Nothing read from "+comId;
				return false;
			}
			if(strcp(buf,"@DEADN")){
				return true;
			}else{
				lastError = "UnKnown error";
				return false;
			}

		} catch (IOException e) {
			lastError = e.toString();
			return false;
		}
	}
	private boolean strcp(byte[] buf, String string) {
		for (int i = 0; i <string.length(); i++) {
			if(buf[i] != string.getBytes()[i])
				return false;
		}
		return true;
	}
	private boolean sendCommand(String command) throws IOException
	{
		byte cmd[] = command.getBytes();
		byte checksum = cmd[0];
		for(int i=1;i<6;i++){
			checksum = (byte) (checksum ^ cmd[i]);
		}
		cmd[6] = checksum;
		outputStream.write(cmd);
		return true;
	}
	private boolean sendCommand(byte[] cmd) throws IOException
	{
		outputStream.write(cmd);
		return true;
	}
	private boolean checkStage()
	{
		if(readAnswer().equals("OK"))
			return true;
		if(readAnswer().equals("NG"))
			return false;
		if(readAnswer().length ==0)
			return false;
		return true;

	}
	private boolean checkError(){
		byte[] bret = readAnswer();
		if(bret.length<=0){
			LogMessage("checkError---read nothing");
			return  true;
		}
		if(bret.length<=6){
			LogMessage("checkError---Not adq data");
			for (int i = 0; i < bret.length; i++) {
				System.out.print('[');
				System.out.print((int)bret[i]);
				System.out.print("'\t");
			}
			//LogMessage(bret);
			return  true;
		}
		if(checkSumCalc(bret,0, 6) != bret[6]){
			LogMessage("checkError---checkSumCalc error");
			return true;
		}
		switch((char)bret[1]){

		case DEVICE_OK:
			//			LogMessage("DEVICE_OK");
			return false;
		case DEVICE_BUSY:
			LogMessage("DEVICE_BUSY");
			return true;
		case OUT_OF_LOW_LIMIT:
			LogMessage("OUT_OF_LOW_LIMIT");
			return true;
		case OUT_OF_HIGH_LIMIT:
			LogMessage("OUT_OF_HIGH_LIMIT");
			return true;
		case CHECK_SUM_ERROR:
			LogMessage("CHECK_SUM_ERROR");
			return true;
		case BAD_COMMAND:
			LogMessage("BAD_COMMAND");
			return true;
		default:
			LogMessage("UNKNOW ERROR");
			return true;
		}

	}
	private byte[] readAnswer()
	{
		byte[] readBuffer = new byte[20];
		Sleep(100);
		try {
			while (inputStream.available() > 0) {
				int numBytes = inputStream.read(readBuffer);
			}
			
			for (int i = 0; i < readBuffer.length; i++) {
				readBuffer[i]  = (byte)(0xff&readBuffer[i]);
			}
			String answer = new String(readBuffer);
			//String[] temp = answer.split("\r");
			//int line = temp.length;
			return readBuffer;
		}catch (IOException e) {
			lastError = e.toString();
			return null;
		} 
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		StepMotor sk = new StepMotor();
		if(!sk.isDeviceReady()){
			sk.LogMessage("Device not Ready");	
			return;
		}else{
			sk.LogMessage("Device Ready");	
		}
		try {
			int from = (int) sk.getPosition();
			int to = 0;
			for(int i = from;i>to;i-=10000){
				sk.Sleep(2000);
				if(i<=0)i=1;
				sk.setPosition(i);
				double get = sk.getPosition();
				if(get != i)
					sk.LogMessage(String.format("\r\n[%d]/[%d]\t set:[%d]\tget:[%.0f]\tdelta:[%.0f]",i,to,i,get, get- 500)); 
				else{
					sk.LogMessage(String.format("\r\n[%d]/[%d]-[%.0f]\t",i,to,get));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
