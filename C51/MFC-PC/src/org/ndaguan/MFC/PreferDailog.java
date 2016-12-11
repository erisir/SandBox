package org.ndaguan.MFC;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class PreferDailog extends JFrame {
	private static final long serialVersionUID = 1L;
	private int ITEMWIDTH = 140;
	private int ITEMHEIGHT = 25;
	final private int ITEMROW = 24;
	private JFrame instance_;

	private  JTextField[] jTextField;

	private JLabel[] jLabel;

	private ActionListener DialogListener;
	private int preferencesLen;
	private int columnNum = 4;
	String userDataDir_ = "";
	private List<RoiItem> roiList_;
	private CommTool comm_;
	private JButton CloseButton;
	private JButton PIDButton;
	private JButton OpenButton;
	private Kernel kernel;
	private JButton PauseButton;
	public static void main(String[] arg){

		PreferDailog pre = new PreferDailog(null,null, null);
		pre.setVisible(true);

	}
	public void enableEdit(boolean flag){
		 
		for (int i = 0; i < MMT.unEditAfterCalbration.length; i++) {
			this.jTextField[MMT.unEditAfterCalbration[i]].setEnabled(flag);
		}
	}
	public PreferDailog(Kernel kr, List<RoiItem> rt, CommTool comm) {
		roiList_ = rt;
		comm_ = comm;
		kernel = kr;
		preferencesLen = MMT.VariablesNUPD.values().length;
		jTextField = new JTextField[preferencesLen];
		jLabel = new JLabel[preferencesLen];
		MMT.Coefficients = new double[3][2];
		instance_ = this;
		userDataDir_ = System.getProperty("user.home");
		initialize();
		onDataChange(getUserData());
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				UpdateData(false);//GUI  update
			}
		});

	}
	public void onDataChange(double[] data) {
		if(data != null){
			for (int i = 0; i < data.length; i++) {
				
				if(MMT.VariablesNUPD.values()[i].value() != data[i])
					{
						MMT.VariablesNUPD.values()[i].value(data[i]);
						 
						switch(MMT.VariablesNUPD.values()[i].name()){
						case "chartWidth":
							roiList_.get(0).setChartWidth(MMT.VariablesNUPD.chartWidth.value());
							break;
						case "chartStatisWindow":
							roiList_.get(0).setChartRangeWidowSize(MMT.VariablesNUPD.chartStatisWindow.value());
							break;
						case "Kp":							 
							comm_.setPTerm(MMT.VariablesNUPD.Kp.value());														 							
							break;
						case "Ki":
							comm_.setITerm(MMT.VariablesNUPD.Ki.value());
							break;
						case "Kd":
							comm_.setDTerm(MMT.VariablesNUPD.Kd.value());
							break;
						case "PWMValue":
							comm_.SetPWM((int) MMT.VariablesNUPD.PWMValue.value());
							kernel.rout = (int) MMT.VariablesNUPD.PWMValue.value();
							break;
						case "votageSmoothWindow":
							comm_.SetVotageTimes(MMT.VariablesNUPD.votageSmoothWindow.value());
							break;
						case "SetPoint":
							comm_.setVotage((int) MMT.VariablesNUPD.SetPoint.value());
						case "PIDMode":
							comm_.setPIDMode((int) MMT.VariablesNUPD.PIDMode.value());
							
							break;
						case "PIDPeriod":
							comm_.setPIDPeriod((int) MMT.VariablesNUPD.PIDPeriod.value());
							break;
							

						}
					}
			}
			//PID
		}
		saveUserData();
	}

	public double[] getUserData() {
		try {
			File loginDataFile = new File(System.getProperty("user.home")+"/MMTracker/"+MMT.currentUser+"_userPreferences.txt");
			if(!loginDataFile.exists())
				return null;
			BufferedReader in;
			in = new BufferedReader(new FileReader(loginDataFile));
			String line;
			if((line = in.readLine()) == null)
			{
				in.close();
				return null;
			}
			String[] temp = line.split(","); 
			
			if((line = in.readLine()) != null)
				userDataDir_ = line;
			
			double[] userDataSet = new double[MMT.VariablesNUPD.values().length];
			if((temp.length-1) < MMT.VariablesNUPD.values().length){
				
				for (int i = 0; i < temp.length-1; i++) {//old var
					userDataSet[i] = Double.parseDouble(temp[i]);				
				}
				
				for (int i = temp.length-1; i < MMT.VariablesNUPD.values().length; i++) {//new var,user default
					 userDataSet[i]  = MMT.VariablesNUPD.values()[i].value();
				}
				onDataChange(userDataSet);
				saveUserData();
			}
			else{
			for (int i = 0; i < userDataSet.length; i++) {
				userDataSet[i] = Double.parseDouble(temp[i]);				
			}
			}
			
			in.close();
			return userDataSet;
		} catch (IOException e) {
			MMT.logError("read user data false");
			return null;
		} 
	}


	public void saveUserData(){
		try {
			File dir = new File(System.getProperty("user.home"),"MMTracker");
			if(!dir.isFile())
				dir.mkdirs();

			File loginDataFile = new File(System.getProperty("user.home")+"/MMTracker/"+MMT.currentUser+"_userPreferences.txt");
			FileWriter out = new FileWriter((loginDataFile)); 
			String sData = "";
			for (int i = 0; i < MMT.VariablesNUPD.values().length; i++) {
				sData +=  Double.toString(MMT.VariablesNUPD.values()[i].value())+ " , ";
			}
			sData += "\r\n"+ userDataDir_;
			out.write(sData);
			out.close(); 
		} catch (IOException e) {
			MMT.logError("save user data err");
		}
	}
	/**
	 * @param flag true save to file,false: update gui
	 */
	public void UpdateData(boolean flag) {
		
		if(flag){//flush
			double[] preferences = new double[preferencesLen];
			for (int i = 0; i <preferencesLen; i++) {
				preferences[i] = Double.parseDouble(jTextField[i].getText());
			}			
			onDataChange(preferences);
		}
		else{//refresh GUI
			for (int i = 0; i <preferencesLen; i++) {
				double presicion = MMT.VariablesNUPD.values()[i].getPresicion();
				String str;
				if((presicion < 1/1e10)){
					str = String.format("%s.%df","%" , 0);
				}
				else{
				str = String.format("%s.%df","%" ,(int)(Math.log10(1/presicion)));
				}
				jTextField[i].setText(String.format(str,getUserData()[i]));
			}	
		}

	}
	public  void updatePID(){
		double A=0,B=0,C=0;
		double Ti=0,Td=0;
		double Tc=1;
		double T=0.01;
		double kp=0;
		
		T = MMT.VariablesNUPD.PIDPeriod.value();
		Tc = MMT.VariablesNUPD.Tu.value();
		kp = MMT.VariablesNUPD.Kp.value();
		
		Ti = 0.5*Tc;
		Td = 0.125*Tc;

		A = kp*(1+T/Ti+Td/T);
		B = kp*(1+2*Td/T);
		C = kp*Td/T;
		MMT.VariablesNUPD.Ki.value(B);
		MMT.VariablesNUPD.Kd.value(C);
		System.out.print(String.format("\r\nP:%.4f,I:%.4f,D:%.4f", A,B,C));
	}
	private void initialize(){
		DialogListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{PhraseActionEvent(e);}};

			Toolkit kit = Toolkit.getDefaultToolkit();
			Dimension screen = kit.getScreenSize();
			getContentPane().setLayout(null);
			int frameWidth = (int)(ITEMWIDTH*columnNum);
			int frameHeight = 4*ITEMHEIGHT*preferencesLen/columnNum + ITEMHEIGHT*ITEMROW/3 ;
			setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			final JTabbedPane tabbedPane = new JTabbedPane();
	 
			int classifyLen = MMT.VariablesClassify.values().length;
			JPanel tab[] = new JPanel[classifyLen];
			for(int i = 0;i<classifyLen;i++){
				tab[i] = new JPanel();
				tab[i].setLayout(null);
				tabbedPane.addTab(MMT.VariablesClassify.values()[i].name(),null,tab[i],null);
			}
			int tabY[] = new int[classifyLen];
			int tabX[] = new int[classifyLen];
			for (int i = 0; i < preferencesLen; i++) {//edit & label
				int tabIndex = MMT.VariablesNUPD.values()[i].getTabIndex();
				if(tabIndex == -1)continue;
				jLabel[i] = new JLabel();
				jLabel[i].setText(String.format("%s%s", MMT.VariablesNUPD.values()[i].name(),MMT.VariablesNUPD.values()[i].getUnit()));
				jLabel[i].setBounds(tabX[tabIndex],tabY[tabIndex],ITEMWIDTH,ITEMHEIGHT);
				
				tabY[tabIndex] += ITEMHEIGHT;
				jTextField[i] = new JTextField();
				jTextField[i].setBounds(tabX[tabIndex], tabY[tabIndex], ITEMWIDTH,ITEMHEIGHT);
				jTextField[i].setToolTipText(MMT.VariablesNUPD.values()[i].getToolTip());
				if(MMT.VariablesNUPD.values()[i].getImp()==1)
				jTextField[i].setForeground(new Color(255,0,0));
				tab[tabIndex].add(jLabel[i]);
				tab[tabIndex].add(jTextField[i]);
				tabX[tabIndex] += ITEMWIDTH;
				tabY[tabIndex] -= ITEMHEIGHT;
				if(tabX[tabIndex]/ITEMWIDTH  == columnNum ){
					tabY[tabIndex] += 2*ITEMHEIGHT;
					tabX[tabIndex] = 0;
				}
			}			
			int x = 0;
			int y = 0;
			for(int i=0;i<classifyLen;i++){//谁最高取谁
				y = y<tabY[i]?tabY[i]:y;
			}
			y += ITEMHEIGHT;//tip,edit
			y += 2*ITEMHEIGHT;//tip,edit
			 
			frameHeight = y+4*ITEMHEIGHT;//整体高度
			setBounds((int)(screen.width -frameWidth)/2,(int)(screen.height-frameHeight)/2,frameWidth ,frameHeight);
			
			tabbedPane.setBounds(0,0,(int)(ITEMWIDTH*(columnNum+0.2)), y);
			getContentPane().add(tabbedPane);
			final JPanel buttonBox = new JPanel();
			buttonBox.setLayout(null);
			buttonBox.setBounds(0, 10,  frameWidth,frameHeight);
			getContentPane().add(buttonBox);
			
			final JSeparator separator2 = new JSeparator();
			separator2.setBounds(0,y, ITEMWIDTH, 50);
			buttonBox.add(separator2);
			
			ITEMWIDTH = ITEMWIDTH*3/5;
			
			final JButton OK = new JButton("OK");
			OK.setBounds(0,  y,ITEMWIDTH,(int)(ITEMHEIGHT*1.5));
			buttonBox.add(OK);
			x += ITEMWIDTH;
			
			final JButton Apply = new JButton("Apply");
			Apply.setBounds(x,y,ITEMWIDTH,(int)(ITEMHEIGHT*1.5));
			buttonBox.add(Apply);
			x += ITEMWIDTH/5;
			x += ITEMWIDTH;

			CloseButton = new JButton("Close");
			CloseButton.setBounds(x,y, ITEMWIDTH,(int)(ITEMHEIGHT*1.5));
			buttonBox.add(CloseButton);
			x += ITEMWIDTH;

			PIDButton = new JButton("PID");
			PIDButton.setBounds(x,y, ITEMWIDTH,(int)(ITEMHEIGHT*1.5));
			buttonBox.add(PIDButton);
			x += ITEMWIDTH;

			OpenButton = new JButton("Open");
			OpenButton.setBounds(x,y, ITEMWIDTH,(int)(ITEMHEIGHT*1.5));
			buttonBox.add(OpenButton);
			x += ITEMWIDTH/5;
			x += ITEMWIDTH;
			PauseButton = new JButton("Pause");
			PauseButton.setBounds(x,y, ITEMWIDTH,(int)(ITEMHEIGHT*1.5));
			buttonBox.add(PauseButton);

			OK.addActionListener(DialogListener);
			Apply.addActionListener(DialogListener);
			CloseButton.addActionListener(DialogListener);
			PIDButton.addActionListener(DialogListener);
			OpenButton.addActionListener(DialogListener);
			PauseButton.addActionListener(DialogListener);
			
			
			
	}
	public void activateButton(int num)
	{
		CloseButton.setBackground(new Color(100,100,100));
		PIDButton.setBackground(new Color(100,100,100));
		OpenButton.setBackground(new Color(100,100,100));
		switch(num){
		case 1:CloseButton.setBackground(new Color(250,100,250));comm_.CloseTunel();break;
		case 2:PIDButton.setBackground(new Color(250,100,250));comm_.PIDTunel();break;
		case 3:OpenButton.setBackground(new Color(250,100,250));comm_.OpenTunel();break;
		}
	}
	
	private void PhraseActionEvent(ActionEvent e){
		if (e.getActionCommand().equals("OK")) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					UpdateData(true);//flush
					comm_.SetPWM((int) (MMT.VariablesNUPD.PWMValue.value()));
					
				}
			});

		}
		if (e.getActionCommand().equals("Apply")) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					UpdateData(true);//flush

				}
			});
			
		}
		if (e.getActionCommand().equals("Close")) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					activateButton(1);
				}
			});

		}

		if (e.getActionCommand().equals("PID")) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					activateButton(2);
				}
			});

		}

		if (e.getActionCommand().equals("Open")) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					activateButton(3);
				}
			});

		}
		if (e.getActionCommand().equals("Pause")) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					PauseButton.setText("Resume");
					MMT.VariablesNUPD.Pause.value(0);
				}
			});

		}
		if (e.getActionCommand().equals("Resume")) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					PauseButton.setText("Pause");
					MMT.VariablesNUPD.Pause.value(1);
				}
			});

		}


	}

}
