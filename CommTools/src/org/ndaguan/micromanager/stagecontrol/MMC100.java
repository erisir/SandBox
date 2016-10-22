package org.ndaguan.micromanager.stagecontrol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;

import javax.comm.CommPortIdentifier;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.UnsupportedCommOperationException;

public class MMC100 {

	private SerialPort serialPort;
	private OutputStream outputStream;
	private int baudRate = 38400;
	private String comId = "COM3";
	private InputStream inputStream;
	private String lastError = "No error";
	private boolean isDeviceReady;
	private static MMC100 instance;
	public static MMC100 getInstance()
	{
		if(instance == null)
			instance  = new MMC100();
		return instance;
	}
	public MMC100() {
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
	public boolean setPosition(double step)//um
	{
		double stepmm = (step/1000);
		try {
			if(sendCommand("1MVA"+stepmm+"\n\r"))
				return true;
			else 
				return false;
		} catch (IOException e) {
			lastError = e.toString();
			return false;
		}
	}
	public boolean setRelativeStagePosition(double step) {
		double stepmm = (step/1000);
		try {

			if(sendCommand("1MVR"+stepmm+"\n\r"))
				return true;
			else 
				return false;
		} catch (IOException e) {
			lastError = e.toString();
			return false;
		}

	}

	public double getPosition()
	{
		try {
			sendCommand("1POS?\n\r");
			String buf = readAnswer();
			String[] temp = buf.split(",");
			return (double) Double.parseDouble(temp[1])*1000;
		} catch (IOException e) {
			lastError = e.toString();
			return -1;
		}
	}


	private void LogMessage(Object msg)
	{
		System.out.print(msg+"\r\n");
	}

	private boolean sendCommand(String command) throws IOException
	{
		outputStream.write(command.getBytes());
		return true;
	}
	private boolean checkStage()
	{
		if(readAnswer().equals("OK"))
			return true;
		if(readAnswer().equals("NG"))
			return false;
		if(readAnswer().isEmpty())
			return false;
		return true;

	}
	private boolean initStage()

	{
		try {
			sendCommand("1VER?\n\r");
			String buf = readAnswer();
			LogMessage("sendCommand('1VER?')<=  "+buf);
			if(buf.isEmpty())
			{
				lastError = "Nothing read from "+comId;
				return false;
			}
			if(buf.substring(1,8).equals("MMC-100")){
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
	private String readAnswer()
	{
		byte[] readBuffer = new byte[20];

		try {
			TimeUnit.MILLISECONDS.sleep(100);
			while (inputStream.available() > 0) {
				int numBytes = inputStream.read(readBuffer);
			}
			String answer = new String(readBuffer);	
			answer=answer.replace('\n', ' ');
			answer=answer.replace('\r', ' ');
			answer=answer.replace('#', ' ');
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
								portId.open("MMC100", 2000);
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
		MMC100 sk = new MMC100();
		if(sk.isDeviceReady()){
			sk.setPosition(3.06);
			try {
				TimeUnit.MILLISECONDS.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sk.LogMessage(sk.getPosition());
		}else{
			sk.LogMessage("Device is not ready");
		}
	}

}
