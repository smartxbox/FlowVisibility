/** Copyright 2015 SmartX Collaboration (GIST NetCS). All rights reserved.
 *
 * This file is part of [SmartX Visibility:FlowVisibility] Software.
 *
 * [SmartX Visibility:FlowVisibility] is free software: you can redistribute 
 * it and/or modify it under the terms of the GNU General Public License as 
 * published by the Free Software Foundation, either version 3 of 
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package flow.visibility.pcap;

import java.io.PrintStream;
import java.sql.Timestamp;

import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jnetpcap.JCaptureHeader;
import org.jnetpcap.Pcap;
import org.jnetpcap.packet.JFlowMap;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.JPacketHandler;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.lan.Ethernet;
import org.jnetpcap.protocol.network.Icmp;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;

/**
*
* The Class File for FlowProcess.
* 
* This class for Processing of Captured packets. 
* Developing by using JNETPCAP Library.
* The code refer to some sample of JNETPCAP documentation
* from Sly Technologies, Inc.
*
* @author Aris C. Risdianto
* @author GIST NetCS
*/


public class FlowProcess {

   private static JFreeChart createChart(XYSeriesCollection dataset) {
	   
	   /** The function to create the chart */ 
	   
	  JFreeChart Chart = ChartFactory.createHistogram("Number Packets of Flows", "Flow Number", "Number of Packets", dataset,PlotOrientation.VERTICAL, false, false, false);
	  Chart.getXYPlot().setForegroundAlpha(0.75f);
			
	  return Chart;
			   
   }
	
   /** function to create internal frame contain flow summary chart */ 
   
   public static JInternalFrame FlowStatistic()
   {
       
       final StringBuilder errbuf = new StringBuilder(); // For any error msgs  
       final String file = "tmp-capture-file.pcap";
	   
	   //System.out.printf("Opening file for reading: %s%n", file);  
       
       /*************************************************************************** 
        * Second we open up the selected file using openOffline call 
        **************************************************************************/  
       Pcap pcap = Pcap.openOffline(file, errbuf);  
 
       if (pcap == null) {  
           System.err.printf("Error while opening device for capture: "  
               + errbuf.toString());
       }  
       
       	Pcap pcap1 = Pcap.openOffline(file, errbuf);
        FlowMap map = new FlowMap();
        pcap1.loop(Pcap.LOOP_INFINITE, map, null);
        
        //System.out.printf(map.toString());
        //System.out.printf(map.toString2());
        
        
        /** Splitting the packets statistics strings from FlowMap function */
        
        String packet = map.toString2();
        String[] NumberPacket = packet.split(",");
           
        final XYSeries Flow = new XYSeries("Flow");
           
        for (int i = 0; i<NumberPacket.length-1; i=i+1) {
               
          	//System.out.printf(NumberPacket[i+1] + "\n");
           	double NoPacket = Double.valueOf(NumberPacket[i+1]);
           	Flow.add(i, NoPacket);

        }
        
        /** Create dataset for chart */
 
		final XYSeriesCollection dataset = new XYSeriesCollection();

   	    dataset.addSeries(Flow);
   	    
   	    /** Create the internal frame contain flow summary chart */
   	    
   	    JInternalFrame FlowStatistic = new JInternalFrame("Flow Statistic", true, true, true, true);
   	    FlowStatistic.setBounds(0, 0, 600, 330);

   	    ChartPanel chartPanel = new ChartPanel(createChart(dataset));
        chartPanel.setMouseZoomable(true, false);

  	    FlowStatistic.add(chartPanel);
   	    FlowStatistic.setVisible(true);
        FlowStatistic.revalidate();
        pcap1.close();
        
        return FlowStatistic;
        
       }
   
   
   /** function to create internal frame contain flow summary in text */
   
   public static JInternalFrame FlowSummary()
   {
       
       final StringBuilder errbuf = new StringBuilder(); // For any error msgs  
       final String file = "tmp-capture-file.pcap";
	   
	   //System.out.printf("Opening file for reading: %s%n", file);  
       
       /*************************************************************************** 
        * Second we open up the selected file using openOffline call 
        **************************************************************************/  
       Pcap pcap = Pcap.openOffline(file, errbuf);  
 
       if (pcap == null) {  
           System.err.printf("Error while opening device for capture: "  
               + errbuf.toString());
       }  
       
       /** create blank internal frame */
       
       JInternalFrame FlowSummary = new JInternalFrame("Flow Summary", true, true, true, true);
       FlowSummary.setBounds(0, 331, 600, 329);
       JTextArea textArea = new JTextArea(50, 10);
       PrintStream printStream = new PrintStream(new CustomOutputStream(textArea));
	        System.setOut(printStream);
	        System.setErr(printStream);
	        JScrollPane scrollPane = new JScrollPane(textArea);
	        FlowSummary.add(scrollPane);
	   
	   /** Process the FlowMap */
	        
       Pcap pcap2 = Pcap.openOffline(file, errbuf);
       JFlowMap superFlowMap = new JFlowMap(); 
       pcap2.loop(Pcap.LOOP_INFINITE, superFlowMap, null);
	   
       /** Redirect the FlowMap Output into the Frame Text Area */
       
       FlowSummary.setVisible(true);
       System.out.printf("%s%n", superFlowMap);
   	
       FlowSummary.revalidate();
       pcap2.close();
       
       return FlowSummary;
       
   }   

   /** function to create internal frame contain flow details */
   
   public static JInternalFrame FlowInspection()
   {
       
       final StringBuilder errbuf = new StringBuilder(); // For any error msgs  
       final String file = "tmp-capture-file.pcap";
	   
	   //System.out.printf("Opening file for reading: %s%n", file);  
       
       /*************************************************************************** 
        * Second we open up the selected file using openOffline call 
        **************************************************************************/  
       Pcap pcap = Pcap.openOffline(file, errbuf);  
 
       if (pcap == null) {  
           System.err.printf("Error while opening device for capture: "  
               + errbuf.toString());
       }  

       /** create blank internal frame */
       
       JInternalFrame FlowInspection = new JInternalFrame("Flow Inspection", true, true, true, true);
       FlowInspection.setBounds(601, 0, 600, 660);
       JTextArea textArea2 = new JTextArea(50, 10);
       PrintStream printStream2 = new PrintStream(new CustomOutputStream(textArea2));
	        System.setOut(printStream2);
	        System.setErr(printStream2);
	        JScrollPane scrollPane2 = new JScrollPane(textArea2);
	        FlowInspection.add(scrollPane2);

       
       
		JPacketHandler<String> jpacketHandler = new JPacketHandler<String>() {

			public void nextPacket(JPacket packet, String user) {
				final JCaptureHeader header = packet.getCaptureHeader();
				System.out.printf("========================= Next Packet ===============================\n");
				System.out.printf("Packet caplen=%d wirelen=%d\n", header.caplen(),header.wirelen());
				System.out.println(packet.toString());

			}
		};
       
       Pcap pcap3 = Pcap.openOffline(file, errbuf);
       
       /** Redirect Output into the Frame Text Area */
       
       FlowInspection.setVisible(true);
       pcap3.loop(Pcap.LOOP_INFINITE, jpacketHandler, null);
       FlowInspection.revalidate();
       pcap3.close();
       
       return FlowInspection;
       
   }
       

   /** function to create internal frame contain flow sequence */
   
   public static JInternalFrame FlowSequence()
   {
       
       final StringBuilder errbuf = new StringBuilder(); // For any error msgs  
       final String file = "tmp-capture-file.pcap";
	   
	   //System.out.printf("Opening file for reading: %s%n", file);  
       
       /*************************************************************************** 
        * Second we open up the selected file using openOffline call 
        **************************************************************************/  
       Pcap pcap = Pcap.openOffline(file, errbuf);  
 
       if (pcap == null) {  
           System.err.printf("Error while opening device for capture: "  
               + errbuf.toString());
       }  

       /** create blank internal frame */
       
       JInternalFrame FlowSequence = new JInternalFrame("Flow Sequence", true, true, true, true);
       FlowSequence.setBounds(601, 0, 600, 660);
       JTextArea textArea3 = new JTextArea(50, 10);
       PrintStream printStream3 = new PrintStream(new CustomOutputStream(textArea3));
	        System.setOut(printStream3);
	        System.setErr(printStream3);
	        JScrollPane scrollPane3 = new JScrollPane(textArea3);
	        FlowSequence.add(scrollPane3);

	    /** Process to print the packet one by one */
       
		JPacketHandler<String> jpacketHandler = new JPacketHandler<String>() {

			public void nextPacket(JPacket packet, String user) {
				final JCaptureHeader header = packet.getCaptureHeader();
				Timestamp timestamp = new Timestamp(header.timestampInMillis());
				Tcp tcp = new Tcp();
				Udp udp = new Udp();
				Icmp icmp = new Icmp();
				Ip4 ip4 = new Ip4();
				Ethernet ethernet = new Ethernet();
				
				/** For IP Packet */
				
				if (packet.hasHeader(ip4)) {

					if (packet.hasHeader(tcp)) {
						System.out.println(timestamp.toString() + " :  [TCP]  :  " + FormatUtils.ip(ip4.source()) + ":" + tcp.source() + "->" + FormatUtils.ip(ip4.destination())+ ":" + tcp.destination());
					}	
					if (packet.hasHeader(udp)) {
						System.out.println(timestamp.toString() + " :  [UDP]  :  " + FormatUtils.ip(ip4.source()) + ":" + udp.source() + "->" + FormatUtils.ip(ip4.destination())+ ":" + udp.destination());
					}
					if (packet.hasHeader(icmp)) {
						System.out.println(timestamp.toString() + " : [ICMP]  :  " + FormatUtils.ip(ip4.source()) + "->" + FormatUtils.ip(ip4.destination())+ ":" + icmp.type());
					}

				}
				
				/** For Ethernet Packet */
				
				else if (packet.hasHeader(ethernet)) {
					System.out.println(timestamp.toString() + " :  [ETH]  :  " + FormatUtils.mac(ethernet.source()) + "->" + FormatUtils.mac(ethernet.destination())+ ":" + ethernet.type());

				}
			}
		};
       
       Pcap pcap4 = Pcap.openOffline(file, errbuf);
       
       /** Redirect the Output into Frame Text Area */
       
       FlowSequence.setVisible(true);
       pcap4.loop(Pcap.LOOP_INFINITE, jpacketHandler, null);
       FlowSequence.revalidate();
       pcap4.close();
       
       return FlowSequence;
       
   }

   
   
}


