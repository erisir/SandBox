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

public class XMTStage {

	private SerialPort serialPort;
	private OutputStream outputStream;
	private int baudRate = 9600;
	private String comId = "COM1";
	private InputStream inputStream;
	private String lastError = "No error";
	private boolean isDeviceReady;
	private int timeout  = 100;
	private boolean debugLog = false;
	private static XMTStage instance;
	public static XMTStage getInstance()
	{
		if(instance == null)
			instance  = new XMTStage();
		return instance;
	}
	public XMTStage() {
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
	public boolean setPosition(double step) throws IOException//um
	{
		byte[] rawData = new byte[4];
		FloatToRaw((float)step,rawData);
		byte[] cmd = new byte[3];
		cmd[0] = 'T';
		cmd[1] = (char)0;
		cmd[2] = 'S';
		byte []buf = new byte[10];
		PackageCommand(cmd,rawData,buf);
		if(debugLog){
			LogMessage("\r\nSet Position to[");
			LogMessage(step);
			LogMessage("]");
		}
		double delta = 0;
		boolean timoutocr = false;
		long a = System.nanoTime()/1000;
		long time = 2000;
		sendCommand(buf);
//		do{
//		Sleep();
//		delta = Math.abs(getPosition()-step);
//		timoutocr = (System.nanoTime()/1000 - a ) >time;
//		}while(delta>0.2 && !timoutocr);
		return true;
		

	}
	public boolean setRelativeStagePosition(double step) {

		return false;
	}

	public double getPosition()
	{
		try {
			byte[] buf = new byte[9];
			if(debugLog){
				LogMessage("\r\nTry to get position");
			}
			PackageCommand(new byte[]{'R','B','1'},null,buf);
			sendCommand(buf);
			Sleep();
			String ret = readAnswer();
			byte[] bret = ret.getBytes();
			if(ret.length()<=0)
				return  -1;
			if(checkSumCalc(bret,0, 6) != bret[6])
				return -1;
			return (double) RawToFloat(bret,2);
		} catch (IOException e) {
			lastError = e.toString();
			return -1;
		}
	}


	private void LogMessage(Object msg)
	{
		System.out.print(msg);
	}
	private void LogMessage(byte[] m)
	{
		for (int i = 0; i < m.length; i++) {
			System.out.print("("+(byte)m[i]+"|"+(char)m[i]+")¡ª¡ª");
		}
	}

	private boolean sendCommand(byte[] command) throws IOException
	{
		if(debugLog ){
			LogMessage("\r\nSend buffer:***");
			LogMessage(command);
			LogMessage("***\r\n");
		}
		LogMessage("\r\n");
		for (int i = 6; i < 9; i++) {
			System.out.print((byte)command[i]+"-");
		}
		outputStream.write(command,0,9);
		return true;
	}
	private boolean sendCommand(String command) throws IOException
	{
		if(debugLog){
			LogMessage("\r\nSend buffer:***");
			LogMessage(command.getBytes());
			LogMessage("***\r\n");
		}
		outputStream.write(command.getBytes(),0,9);
		return true;
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
	private void PackageCommand(byte[] cmd,byte[] data,byte[] buf)
	{
		//CMD
		buf[0] = '@';
		buf[1] = (byte)cmd[0];
		buf[2] = (byte)cmd[1];
		buf[3] = (byte)cmd[2];
		//data
		if (data  == null){
//			buf[4] = 'X';
//			buf[5] = 'X';
//			buf[6] = 'X';
//			buf[7] = 'X';
		}
		else{
			buf[4] = data[0];
			buf[5] = data[1];
			buf[6] = data[2];
			buf[7] = data[3];
		}
		//checkSum
		buf[8] = checkSumCalc(buf, 0, 8);
	}
	private float RawToFloat(byte[] rawData,int offset)
	{
		if (rawData[offset + 0] >= 0x80)
		{
			// Negative number
			return -(float)((rawData[offset + 0] - 0x80) * 256 + rawData[offset + 1]
					+ (rawData[offset + 2] * 256 + rawData[offset + 3]) * 0.001);
		}
		else
		{
			return (float)(rawData[offset + 0] * 256 + rawData[offset + 1]
					+ (rawData[offset + 2] * 256 + rawData[offset + 3]) * 0.001);
		}
	}
	//
	//Convert target position to XMT format
	private void FloatToRaw(float val,byte[] rawData)
	{
		if (val < 0)
		{
			val *= -1;
			int a = (int)val;
			rawData[0] = (byte)(a / 256 + 0x80);
			rawData[1] = (byte)(a % 256);
			a = (int)((val - a) * 1000);
			rawData[2] = (byte)(a / 256);
			rawData[3] = (byte)(a % 256);
		}
		else
		{
			int a = (int)val;
			int temp = 0;
			temp = (byte)(a / 256);
			rawData[0] = (byte) temp;
			temp = (byte)(a % 256);
			rawData[1] =  (byte) temp;

			a = (int)((val - a) * 1000);
			temp = (byte)(a / 256);
			rawData[2] = (byte) temp;
			temp = (byte)(a % 256);
			rawData[3] = (byte) temp;
		}
	}
	private boolean initStage()

	{
		try {
			byte[] buf = new byte[9];
			PackageCommand(new byte[]{'A','B','C'},null,buf);
			sendCommand(buf);
			Sleep(500);
			PackageCommand(new byte[]{'R','A','X'},null,buf);
			sendCommand(buf);
			LogMessage("\r\nInit Stage OK");
			return true;

		} catch (IOException e) {
			lastError = e.toString();
			return false;
		}
	}

	private  void Sleep() {
		try {
			TimeUnit.MILLISECONDS.sleep(timeout);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	private  void Sleep(int l) {
		try {
			TimeUnit.MILLISECONDS.sleep(l);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	private String readAnswer()
	{
		byte[] readBuffer = new byte[15];

		try {
			TimeUnit.MILLISECONDS.sleep(50);
			while (inputStream.available() > 0) {
				int numBytes = inputStream.read(readBuffer);
			}
			String answer = new String(readBuffer);
			if(debugLog){
				LogMessage("\r\nRead buffer:***");
				LogMessage(answer.getBytes());
			}
			return answer;
		}catch (IOException e) {
			lastError = e.toString();
			return null;
		} catch (InterruptedException e) {
			lastError = e.toString();
			return null;
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
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		XMTStage sk = new XMTStage();
		try {
			for(int i=1;i<10;i++){
				double target = 5+i*0.1;//11.258;
				sk.setPosition(target);
				sk.Sleep(50);
		//	double ret = sk.getPosition();
   //	double delta = ret-target;
//			sk.LogMessage("\r\n Target:["+target+"]\tReturn:\t["+ret+"]\tDelta:\t["+delta+"]");
	//		sk.LogMessage("\r\n"+i+"," +delta);
			}


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
