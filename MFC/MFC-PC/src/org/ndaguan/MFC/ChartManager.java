package org.ndaguan.MFC;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class ChartManager extends JFrame  {
	/**
	 *  
	 */
	private static final long serialVersionUID = 1L;
	private static ChartManager instance;
	private String[] dataSet;
	public JFreeChart chart;
	private HashMap<String, XYSeries> dataSeries_;
	private HashMap<String, JFreeChart> chartSeries_;
	private XYSeriesCollection dataset_;
	private int ChartMaxItemCount = 1000;
	private final int DEFAULT_WIDTH = 680;
	private int tapSize = 680;
	private final int DEFAULT_HEIGHT =(int)( DEFAULT_WIDTH*0.71);
	private JTabbedPane tabbedPane;
	public JSlider slider;
    public static ChartManager getInstance(){
    	return instance;
    }
	public HashMap<String, XYSeries> getDataSeries(){
		return dataSeries_;
	}

	public HashMap<String, JFreeChart> getChartSeries(){
		return chartSeries_;
	}

	public void setMaxCount(String chartName,int acount)
	{
		dataSeries_.get(chartName).setMaximumItemCount(acount);
	}

	public  static void main(String[] arg){
		String[] sdataSet = new String[]{"Chart-Z","Chart-X","Chart-Y","Chart-FX","Chart-FY","Chart-Debug"};
		String titleName = "bean1";
		ChartManager chartManager = new ChartManager(sdataSet,2000,titleName );
		chartManager.setVisible(true);
	}

	ChartManager(String[] dataSet_, int maxCount, String titleName){
		ChartMaxItemCount = maxCount;
		dataSet =dataSet_;
		dataSeries_ = new HashMap<String,XYSeries>();
		chartSeries_ = new HashMap<String,JFreeChart>();
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		this.setTitle(titleName);
		initialize();
		this.addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentResized(ComponentEvent e)
			{
				updateGUI();
			}
		});
		instance = this;
	}
	protected void updateGUI() {
		// TODO Auto-generated method stub
		int backw = getWidth();
		tapSize = (int) (backw*0.95);
		for(int i=0;i<this.tabbedPane.getComponents().length ;i++){
			this.tabbedPane.getComponents()[i].getComponentAt(10, 10).setBounds(10, 10, tapSize, (int) (tapSize*0.55));
		}
	}

	void initialize(){
		//tabbedPane
		tabbedPane = new JTabbedPane();
		tabbedPane.setBounds(0,0, tapSize, (int)(tapSize*0.618));
		getContentPane().add(tabbedPane);
		for (int i = 0; i <  dataSet.length; i++) {
			tabbedPane.addTab( dataSet[i], null, createChartPanel(dataSet[i]), null);
		}
	}
	
	public void addControlPanel(JPanel mainpanel,int height){
		mainpanel.setBounds(0, DEFAULT_HEIGHT, mainpanel.WIDTH, mainpanel.HEIGHT);
		getContentPane().add(mainpanel);
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT+200);
	}
	public int getSelectedTap(){
		return tabbedPane.getSelectedIndex();
	}
	private JPanel createChartPanel(String tableName) {
		if(dataSeries_.containsKey(tableName))
			return null;
		String []temp = tableName.split(",");
		final XYSeries temp_ =  new XYSeries(temp[0]);
		final XYSeries temp1_ =  new XYSeries(temp[1]);
		final XYSeries temp2_ =  new XYSeries(temp[2]);

		temp_.setMaximumItemCount(ChartMaxItemCount );
		temp1_.setMaximumItemCount(ChartMaxItemCount );
		temp2_.setMaximumItemCount(ChartMaxItemCount );
		dataset_ = new XYSeriesCollection();
		dataset_.addSeries(temp_);
		dataset_.addSeries(temp1_);
		dataset_.addSeries(temp2_);
		chart = ChartFactory.createXYLineChart(temp[0], "-Time",
				"-value", dataset_, PlotOrientation.VERTICAL, true, true,
				false);

		chartSeries_.put(temp[0], chart);
		dataSeries_.put(temp[0],temp_);	
		dataSeries_.put(temp[1],temp1_);	
		dataSeries_.put(temp[2],temp2_);	

		ChartPanel cPanel = new ChartPanel(chart, true);
		cPanel.setBounds(10, 10, tapSize, (int)(tapSize*0.6));

		slider = new JSlider(JSlider.VERTICAL);
		slider.setMinimum(1);
		slider.setValue(500);
		slider.setMaximum(1000);
		slider.setBounds(0, 40, 10, 260);
 
		slider.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent e) {
			    JSlider source = (JSlider)e.getSource();
			    if (!source.getValueIsAdjusting()) {
			        int fps = (int)source.getValue();
			        chart.setTitle(String.format("SetFlow:%.1f", (double)fps/10));
			        double voltage = 120.2+30.05*fps/10;
					//CommTool.getInstance().setVotage(voltage);
			        if (fps == 0) {
			            
			        } else {
			             
			        }
			    }
			}
			
		});
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.add(slider);
		panel.add( cPanel);
 	

		return panel;
	}

	public void setSelectTap(int i) {
		tabbedPane.setSelectedIndex(i);
	}

	public void setChartWidth(int size) {
		for(int i =0;i<MMT.CHARTLIST.length;i++){	
			String []temp = MMT.CHARTLIST[i].split(",");
			dataSeries_.get(temp[0]).setMaximumItemCount(size);
			dataSeries_.get(temp[1]).setMaximumItemCount(size);
			dataSeries_.get(temp[2]).setMaximumItemCount(size);
		}
	}

}
