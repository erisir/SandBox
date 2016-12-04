package org.ndaguan.MFC;

import java.io.IOException;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Time;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.comm.CommPortIdentifier;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.UnsupportedCommOperationException;



public class CommTool {

	final byte _U_SetVotage     = '0';
	final byte _U_SetPTerm      = '1';
	final byte _U_SetITerm      = '2';
	final byte _U_SetDTerm	    = '3';
	final byte _U_SetDura       = '4';
	final byte _U_SetPWMVal     = '5';
	final byte _U_GetVotage     = '6';
	final byte _U_SetTClose     = '7';
	final byte _U_SetTOpen      = '8';
	final byte _U_SetTPID       = '9';
	final byte _U_SetVotageTimes= 'a';
	final byte _U_SetVotageChanel= 'b';
	private SerialPort serialPort;
	private OutputStream outputStream;
	private int baudRate = 115200;
	private String comId = "COM6";
	private InputStream inputStream;
	private String lastError = "No error";
	private boolean isDeviceReady;
	private static CommTool instance;

	private List<RoiItem> roiList_;
	private Kernel kernel;
	public static CommTool getInstance(List<RoiItem> rt)
	{
		if(instance == null)
			instance  = new CommTool( null, rt);
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
	public CommTool(Kernel kr, List<RoiItem> rt) {
		roiList_ = rt;
		kernel = kr;
		isDeviceReady = true;
		if(!initCom())
		{
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
		}
		else{
			buf[2] = data[0];
			buf[3] = data[1];
		}
		//checkSum
		buf[4] = checkSumCalc(buf, 0, 5);
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
		double  curr = getPosition();
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
	public boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
 }	
	public double getPosition() throws IOException
	{
		byte []buf = new byte[5];
		PackageCommand(_U_GetVotage,null,buf);
		sendCommand(buf);
		Sleep(10);
		byte[] bret = readAnswer();
		char [] ret = new char[20];
		LogMessage("\r\nreadAnswer");
		for (int i = 0; i < bret.length; i++) {
			System.out.print((char)bret[i]);
			ret[i]= (char) bret[i];
		}
		
		if(bret.length<=0){
			LogMessage("getPosition--read nothing");
			return  0;
		}
		if(ret[0]=='@' && ret[1]=='P' && isNumeric(String.copyValueOf(ret).substring(2,3))){
			double Vsensor =  Double.valueOf(String.copyValueOf(ret).substring(2,2+6)).doubleValue();//RawToLong(bret,2);		 
			return Vsensor;
			}else{
				return 0;
			}
	}
	
	public double[] getPIDStatue() throws IOException
	{
		byte []buf = new byte[5];
		PackageCommand(_U_SetDura,null,buf);
		sendCommand(buf);
		Sleep(10);
		byte[] bret = readAnswer();
		/*LogMessage("readAnswer");
		for (int i = 0; i < 5; i++) {
			System.out.print((int)bret[i]);
			System.out.print((char)' ');
		}
		*/
		if(bret.length<=0){
			LogMessage("getPIDStatue--read nothing");
 
		}
		 
		long setv = (int)((bret[2]&0x0FF)*256+(bret[3]&0x0FF));//RawToLong(bret,2);
		long pwm = (int)((bret[4]&0x0FF)*256+(bret[5]&0x0FF));//RawToLong(bret,2);
 
		return new double[]{setv,pwm};////pos ;
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
								portId.open("CommTool", 2000);
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
	
	@SuppressWarnings("unused")
	private boolean strcp(byte[] buf, String string) {
		for (int i = 0; i <string.length(); i++) {
			if(buf[i] != string.getBytes()[i])
				return false;
		}
		return true;
	}
	/*@SuppressWarnings("unused")
	private boolean sendCommand(String command) throws IOException
	{
		byte cmd[] = command.getBytes();
		byte checksum = cmd[0];
		for(int i=1;i<4;i++){
			checksum = (byte) (checksum ^ cmd[i]);
		}
		cmd[4] = checksum;
		outputStream.write(cmd);
		return true;
	}*/
	private boolean sendCommand(byte[] cmd) throws IOException
	{
		byte checksum = cmd[0];
		for(int i=1;i<4;i++){
			checksum = (byte) (checksum ^ cmd[i]);
		}
		cmd[4] = checksum;
		outputStream.write(cmd);
		/*LogMessage("sendCommand");
		for (int i = 0; i < 5; i++) {
			System.out.print((int)cmd[i]);
			System.out.print((char)' ');
		}*/
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
			return  true;
		}
		if(checkSumCalc(bret,0, 6) != bret[6]){
			LogMessage("checkError---checkSumCalc error");
			return true;
		}
		 
        return false;
	}
	private byte[] readAnswer()
	{
		byte[] readBuffer = new byte[20];
		Sleep(100);
		try {
			while (inputStream.available() > 0) {
				inputStream.read(readBuffer);
			}
			for (int i = 0; i < readBuffer.length; i++) {
				readBuffer[i]  = (byte)(0xff&readBuffer[i]);
			}

			return readBuffer;
		}catch (IOException e) {
			//lastError = e.toString();
			return null;
		} 
	}
	/**
	 * @param args
	 */
	public void setPTerm(double value) {
		// TODO Auto-generated method stub
		byte buf[] = new byte[5];
		buf[0] = '@';
		buf[1] = _U_SetPTerm;
		buf[3] = (byte) (value%256);
		buf[2] = (byte) ((value/256)%256);
		try {
			sendCommand(buf);
			TimeUnit.MILLISECONDS.sleep(200);
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void setITerm(double value) {
		// TODO Auto-generated method stub
		byte buf[] = new byte[5];
		buf[0] = '@';
		buf[1] = _U_SetITerm;
		buf[3] = (byte) (value%256);
		buf[2] = (byte) ((value/256)%256);
		try {
			sendCommand(buf);
			TimeUnit.MILLISECONDS.sleep(200);
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void setDTerm(double value) {
		// TODO Auto-generated method stub
		byte buf[] = new byte[5];
		buf[0] = '@';
		buf[1] = _U_SetDTerm;
		buf[3] = (byte) (value%256);
		buf[2] = (byte) ((value/256)%256);
		try {
			sendCommand(buf);
			TimeUnit.MILLISECONDS.sleep(200);
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void setVotage(double value) {
		// TODO Auto-generated method stub
		byte buf[] = new byte[5];
		buf[0] = '@';
		buf[1] = _U_SetVotage;
		buf[3] = (byte) (value%256);
		buf[2] = (byte) ((value/256)%256);
		try {
			sendCommand(buf);
			TimeUnit.MILLISECONDS.sleep(200);
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void setVotageChanel(double value) {
		// TODO Auto-generated method stub
		byte buf[] = new byte[5];
		buf[0] = '@';
		buf[1] = _U_SetVotageChanel;
		buf[3] = (byte) (value%256);
		buf[2] = (byte) ((value/256)%256);
		try {
			sendCommand(buf);
			TimeUnit.MILLISECONDS.sleep(200);
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void PIDTunel() {
		// TODO Auto-generated method stub
        kernel.rout = 0;
		MMT.VariablesNUPD.PIDbyPC.value(0);
		byte buf[] = new byte[5];
		buf[0] = '@';
		buf[1] = _U_SetTPID;
		buf[2] = 'X';
		buf[3] = 'X';
		try {
			sendCommand(buf);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void SetVotageTimes(double value) {
		// TODO Auto-generated method stub
		byte buf[] = new byte[5];
		buf[0] = '@';
		buf[1] = _U_SetVotageTimes;
		buf[3] = (byte) (value%256);
		buf[2] = (byte) ((value/256)%256);
		try {
			sendCommand(buf);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void CloseTunel() {
		// TODO Auto-generated method stub
		kernel.rout = 0;
		byte buf[] = new byte[5];
		buf[0] = '@';
		buf[1] = _U_SetTClose;
		buf[2] = 'X';
		buf[3] = 'X';
		try {
			sendCommand(buf);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MMT.VariablesNUPD.PIDbyPC.value(0);
	}
	public void OpenTunel() {
		// TODO Auto-generated method stub
		kernel.rout = 0;
		
		byte buf[] = new byte[5];
		buf[0] = '@';
		buf[1] = _U_SetTOpen;
		buf[2] = 'X';
		buf[3] = 'X';
		try {
			sendCommand(buf);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MMT.VariablesNUPD.PIDbyPC.value(0);
	}
	public void SetPWM(int value) {
		// TODO Auto-generated method stub
	    byte buf[] = new byte[5];
		buf[0] = '@';
		buf[1] = _U_SetPWMVal;
		buf[3] = (byte) (value%256);
		buf[2] = (byte) ((value/256)%256);
		try {
			sendCommand(buf);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
