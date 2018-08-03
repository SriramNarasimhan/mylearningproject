package com.suntrust.dotcom.utils;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.adobe.cq.sightly.WCMUsePojo;

@SuppressWarnings("deprecation")
public class ColumnContainerColumnControlHelper extends WCMUsePojo {
	Logger logger = LoggerFactory.getLogger(RowContainerColumnControlHelper.class);
	Session session = null;
	
	Node currentNode = null; 
	@Override
	public void activate() {
	}
	public String moveNodes() {
		try {		
			session=this.getResourceResolver().adaptTo(Session.class);
			currentNode = (Node)getResource().adaptTo(Node.class);
			NodeIterator componentRootNode = currentNode.getNodes();
			long componentRootNodeSize = componentRootNode.getSize();
			String currentNodePath = currentNode.toString();
			String layout = null;		//layout value
			int parNode = 1;			//default columns
			//Node par1node = null;
			Node par2node = null;
			Node par3node = null;
			Node par4node = null;
			NodeIterator par2nodeItr = null;
			NodeIterator par3nodeItr = null;
			NodeIterator par4nodeItr = null;
			//move the components only if it exists otherwise consider as featured container component on load
			if(componentRootNodeSize != 0) {
				if(currentNode != null && currentNode.hasProperty("layout")) {
					layout = currentNode.getProperty("layout").getString();
					parNode = (layout.equalsIgnoreCase("col4"))?4:(layout.equalsIgnoreCase("col3"))?3:2;
				}
				//create empty par 1 node, this is needed to handle failures, if the component is only added in col 2
				/*if(!currentNodePath.contains("colcol1")) {
					par1node = currentNode.addNode("colcol1");
					session.save();
				}
				par1node = currentNode.getNode("colcol1");*/
					
				//create empty par 2 node, this is needed to handle failures, if the component is only added in col 3
				if(!currentNodePath.contains("colcol2")) {
					par2node = currentNode.addNode("colcol2");
					session.save();
				}
				par2node = currentNode.getNode("colcol2");
				par2nodeItr = par2node.getNodes();
				
				//create empty par 3 node, this is needed to handle failures, if the component is only added in col 4
				if(!currentNodePath.contains("colcol3")) {
					par3node = currentNode.addNode("colcol3");
					session.save();
				}
				par3node = currentNode.getNode("colcol3");
				par3nodeItr = par3node.getNodes();

				//create empty par 3 node, this is needed to handle failures, if the component is only added in col 4
				if(!currentNodePath.contains("colcol4")) {
					par4node = currentNode.addNode("colcol4");
					session.save();
				}
				par4node = currentNode.getNode("colcol4");
				par4nodeItr = par4node.getNodes();


				//create empty par 3 and par 4 node, this is needed to handle failures, if the component is only added in col 2
				/*if(!currentNodePath.contains("colcol3") && !currentNodePath.contains("colcol4")) {
					par3node = currentNode.addNode("colcol3");
					par3node = currentNode.addNode("colcol4");
					session.save();
				}
				par3node = currentNode.getNode("colcol3");
				par3nodeItr = par3node.getNodes();
				par4node = currentNode.getNode("colcol4");
				par4nodeItr = par4node.getNodes();
				
				//create empty par 4 node, this is needed to handle failures, if the component is only added in col 3
				if(!currentNodePath.contains("colcol4")) {
					par3node = currentNode.addNode("colcol4");
					session.save();
				}
				par4node = currentNode.getNode("colcol4");
				par4nodeItr = par4node.getNodes();*/
				
				//on change from 4 columns to 3 columns
				if(par4nodeItr.getSize() > 0 && parNode == 3) {
					while(par4nodeItr.hasNext()) {
						Node par4Node = (Node)par4nodeItr.next();
						String nodeName = par4Node.getName();
						if(!nodeName.contains("_")) {
							nodeName = nodeName+"_"+generateNodeUniqueId();
						}
						par4Node.getSession().move(par4Node.getPath(), par3node.getPath()+"/"+nodeName);
						session.save();
					}
				}
				
				//on change from 3 columns to 2 column
				if(par3nodeItr.getSize()>0 && parNode == 2) {
					while(par3nodeItr.hasNext()) {
						Node par3Node = (Node)par3nodeItr.next();
						String nodeName = par3Node.getName();
						if(!nodeName.contains("_")) {
							nodeName = nodeName+"_"+generateNodeUniqueId();
						}
						par3Node.getSession().move(par3Node.getPath(), par2node.getPath()+"/"+nodeName);
						session.save();
					}
				}
				
				//on change from 4 columns to 2 column 
				if((par3nodeItr.getSize()>0 || par4nodeItr.getSize()>0) && parNode == 2) {
					while(par3nodeItr.hasNext()) {
						Node par3Node = (Node)par3nodeItr.next();
						String nodeName = par3Node.getName();
						if(!nodeName.contains("_")) {
							nodeName = nodeName+"_"+generateNodeUniqueId();
						}
						par3Node.getSession().move(par3Node.getPath(), par2node.getPath()+"/"+nodeName);
						session.save();
					}
					while(par4nodeItr.hasNext()) {
						Node par4Node = (Node)par4nodeItr.next();
						String nodeName = par4Node.getName();
						if(!nodeName.contains("_")) {
							nodeName = nodeName+"_"+generateNodeUniqueId();
						}
						par4Node.getSession().move(par4Node.getPath(), par2node.getPath()+"/"+nodeName);
						session.save();
					}
				}
			}
		}
		catch(Exception e) {
			logger.error("error in moving nodes "+e.getMessage());
			return "";
		}
		return "";
	}

	public int generateNodeUniqueId() {
		long timeSeed = System.nanoTime();				// to get the current date time value
		double randSeed = Math.random() * 1000;			// random number generation
		long midSeed = (long) (timeSeed * randSeed);	// mixing up the time and
		String strMidSeed = midSeed + "";
		String strUniqueId = strMidSeed.substring(0, 9);
		int id = Integer.parseInt(strUniqueId);			// integer value
		return id;
	}
}