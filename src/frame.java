import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Set;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;



class frame extends JFrame implements ItemListener {

	static JFrame mainFrame;
	static JComboBox<String> c1;
	static JSlider firstSlider;
	static JSlider secondSlider;
	static JCheckBox averageTrans;
	static JCheckBox lowTrans;
	static JCheckBox highTrans;
	static JPanel firstPanel;
	static JPanel secondPanel;
	static JPanel fourthPanel;
	static JPanel linePanel;
	static ChartPanel piePanel;
	static JFreeChart barChart;
	static JFreeChart pieChart;
	static JFreeChart lineChart;
	
	public static void barChart() throws FileNotFoundException, IOException {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		Blocks.timeDiff(Blocks.getBlockByNumber(15049308), Blocks.getBlockByNumber(15049407));
		
		barChart = ChartFactory.createBarChart(
				"Time Difference Between Blocks", // chart title
				"Blocks", // domain axis label
				"Time Units (hours, minutes, secs", // range axis label
				dataset, // data
				PlotOrientation.VERTICAL, //orientation
				false, // don't include legend
				false,
				false
		);		
	}
	
	public static void pieChart(int from, int to) throws FileNotFoundException, IOException {
		DefaultPieDataset dataset = new DefaultPieDataset();
		LinkedHashMap<String, Integer> data = Blocks.calUniqMiners(from,to);
		Set<String> keys = data.keySet();
		
		for (String key : keys) {
			dataset.setValue(key,data.get(key));
		}
		
		pieChart = ChartFactory.createPieChart(      
		         "Each Unique Miner and its Frequency",   // chart title 
		         dataset,          // data    
		         true,             // include legend   
		         true, 
		         false);
		
		PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator(
	            "{0}: {1}");
		
		PiePlot plot = (PiePlot) pieChart.getPlot();
		plot.setLabelGenerator(gen);
	}
	
	public static void lineChart() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		lineChart = ChartFactory.createLineChart(
				"Transaction Cost of Blocks", // chart title
				"Block Number", // domain axis label
				"Transaction Count (ETH)", // range axis label
				dataset, // data
				PlotOrientation.VERTICAL, // orientation
				false, // include legend
				true, // tooltips
				false); // urls
	}
	
	public static JFrame frameInitial() {
		mainFrame = new JFrame("Project 5 - Christopher Kuzmickas");
		mainFrame.setLayout(null);
		return mainFrame;
	}
	
	public static void comboBoxInitial(frame o) {
		// array of string containing cities
		String s1[] = {"--select chart--","Unique Miners","Transaction Cost","Time Difference" };
		
		// create checkbox
		c1 = new JComboBox<String>(s1);
		
		// add ItemListener
		c1.addItemListener(o);
	}
	
	
	
	public static JSlider sliderInitialization(int min, int max) {
		//creat slider
		JSlider slider = new JSlider(min, max);
		
		//paint ticks and tracks
		slider.setPaintTrack(true);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		
		//set spacing
		slider.setMajorTickSpacing(50);
		slider.setMinorTickSpacing(5);
		return slider;
	}

	// main class
	public static void main(String[] args) throws FileNotFoundException, IOException
	{
		
		Blocks.readFile("ethereumP1data.csv");
		Blocks.sortBlocksByNumber();
		
		System.out.println(Blocks.avgTransactionCost());
		
		frame s = new frame();
		
		mainFrame = frameInitial(); //initializ mainframe
		comboBoxInitial(s); //initialize combobox
		
		//initialize firstslider and secondslider with change events
		firstSlider = sliderInitialization(Blocks.getBlocks().get(0).getNumber(), Blocks.getBlocks().get(99).getNumber());
		secondSlider = sliderInitialization(firstSlider.getValue()+1, Blocks.getBlocks().get(99).getNumber());
		firstSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				secondSlider.setMinimum(source.getValue()+1);
				secondSlider.setMaximum(source.getMaximum());
				int min = firstSlider.getValue();
				int max = secondSlider.getValue();
				min = min - 15049308;
				max = max - 15049308 + 1;
				try {
					mainFrame.remove(piePanel);
					pieChart(min,max);
					piePanel = new ChartPanel(pieChart);
					piePanel.setBounds(300, 100, 1000,600);
					mainFrame.add(piePanel);
					mainFrame.repaint();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				piePanel.repaint();
				
		
			}
		});
		secondSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				int min = firstSlider.getValue();
				int max = secondSlider.getValue();
				min = min - 15049308;
				max = max - 15049308 + 1;
				try {
					mainFrame.remove(piePanel);
					pieChart(min,max);
					piePanel = new ChartPanel(pieChart);
					piePanel.setBounds(300, 100, 1000,600);
					mainFrame.add(piePanel);
					mainFrame.repaint();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				piePanel.repaint();
			}
		});
		
		averageTrans = new JCheckBox("Average Transaction Cost");
		lowTrans = new JCheckBox("Lowest Transaction Cost");
		highTrans = new JCheckBox("Highest Transaction Cost");

		// create a first panel
		firstPanel = new JPanel();
		firstPanel.setBounds(650,0,200,100);
		firstPanel.add(c1);
		
		//create second panel with sliders for piechart
		secondPanel = new JPanel();
		secondPanel.setBounds(50,200,200,200);
		secondPanel.add(firstSlider);
		secondPanel.add(secondSlider);
		secondPanel.setVisible(false);
		
		//create third panel w/ pie chart
		pieChart(0,100);
		piePanel = new ChartPanel(pieChart);
		piePanel.setBounds(300, 100, 1000,600);
		piePanel.setVisible(false);
		
		//create panel w/line chart
		lineChart();
		linePanel = new ChartPanel(lineChart);
		linePanel.setBounds(200,150,1000,600);
		linePanel.setVisible(false);
		
		//create fourth panel w/check boxes
		fourthPanel = new JPanel();
		fourthPanel.setLayout(new GridBagLayout());
		fourthPanel.add(averageTrans);
		fourthPanel.add(lowTrans);
		fourthPanel.add(highTrans);
		fourthPanel.setBounds(375,100,750,50);
		fourthPanel.setVisible(false);
		
		//adding panels to main frame
		mainFrame.add(firstPanel);
		mainFrame.add(secondPanel);
		mainFrame.add(piePanel);
		mainFrame.add(fourthPanel);
		mainFrame.add(linePanel);
		
		
		// set the size of frame
		mainFrame.setSize(1500, 800);
		mainFrame.setVisible(true);
		
		System.out.print(Blocks.calUniqMiners(0,100));

	}
	
	
	public void itemStateChanged(ItemEvent e)
	{
		// if the state combobox is changed
		if (e.getSource() == c1) {
			JComboBox cb = (JComboBox)e.getSource();
			String msg = (String)cb.getSelectedItem();
			switch (msg) {
				case "--select chart--": 
					piePanel.setVisible(false);
					linePanel.setVisible(false);
					secondPanel.setVisible(false);
					fourthPanel.setVisible(false);
					mainFrame.repaint();
					mainFrame.revalidate();
					break;
				case "Unique Miners": 
					piePanel.setVisible(true);
					linePanel.setVisible(false);
					secondPanel.setVisible(true);
					fourthPanel.setVisible(false);
					mainFrame.repaint();
					mainFrame.revalidate();
					break;
				case "Transaction Cost": 
					linePanel.setVisible(true);
					piePanel.setVisible(false);
					secondPanel.setVisible(false);
					fourthPanel.setVisible(true);
					mainFrame.repaint();
					mainFrame.revalidate();
					break;
				case "Time Difference":
					piePanel.setVisible(false);
					linePanel.setVisible(false);
					secondPanel.setVisible(false);
					fourthPanel.setVisible(false);
					mainFrame.repaint();
					mainFrame.revalidate();
					break;
			}

		}
	}
	
	
}

