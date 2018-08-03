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
public class RowContainerColumnControlHelper extends WCMUsePojo {
	Logger logger = LoggerFactory.getLogger(RowContainerColumnControlHelper.class);
	Session session = null;
	
	Node currentNode = null; 
	@Override
	public void activate(){		
	}
	public String moveNodes() {
		try{		
			session=this.getResourceResolver().adaptTo(Session.class);
			currentNode = (Node)getResource().adaptTo(Node.class);
			NodeIterator componentRootNode = currentNode.getNodes();
			long componentRootNodeSize = componentRootNode.getSize();
			String currentNodePath= currentNode.toString();			
			String layout =null; //layout value
			int parNode =1; //default columns
			Node par1node = null;	
			Node par2node = null;	
			Node par3node = null;			
			NodeIterator par2nodeItr =null;	
			NodeIterator par3nodeItr =null;			
			//move the components only if it exists otherwise consider as featured container component on load  
			if(componentRootNodeSize !=0){
				if(currentNode != null && currentNode.hasProperty("layout")){
					layout = currentNode.getProperty("layout").getString();
					parNode=(layout.equalsIgnoreCase("triple"))?3:(layout.equalsIgnoreCase("rightrail") || layout.equalsIgnoreCase("leftrail") ||layout.equalsIgnoreCase("pair") || 
							layout.equalsIgnoreCase("fullwidthleftrail") || layout.equalsIgnoreCase("fullwidthrightrail"))?2:1;
				}
				//create empty par 1 node, this is needed to handle failures, if the component is only added in col 2 
				if(!currentNodePath.contains("col1")){
					par1node=currentNode.addNode("col1");
					session.save();			
				}
				par1node = currentNode.getNode("col1");	
					
				//create empty par 2 node, this is needed to handle failures, if the component is only added in col 3
				if(!currentNodePath.contains("col2")){								
					par2node=currentNode.addNode("col2");				
					session.save();		
				}
				par2node = currentNode.getNode("col2");	
				par2nodeItr = par2node.getNodes();	
				//create empty par 3 node, this is needed to handle failures, if the component is only added in col 2 
				if(!currentNodePath.contains("col3")){
					par3node=currentNode.addNode("col3");
					session.save();	
				}
				par3node = currentNode.getNode("col3");				
				par3nodeItr = par3node.getNodes();	
				//on change from 3 columns to 2 columns 
				if(par3nodeItr.getSize()>0 && parNode== 2){
				while(par3nodeItr.hasNext()){
					Node par3Node = (Node)par3nodeItr.next();
					String nodeName = par3Node.getName();					
					if(!nodeName.contains("_")){
						nodeName = nodeName+"_"+generateNodeUniqueId();
					}
					par3Node.getSession().move(par3Node.getPath(), par2node.getPath()+"/"+nodeName);
					session.save();
				}	
				}
				//on change from 2 columns to 1 column 
				if(par2nodeItr.getSize()>0 && parNode== 1){
				while(par2nodeItr.hasNext()){
						Node par2Node = (Node)par2nodeItr.next();
						String nodeName = par2Node.getName();					
						if(!nodeName.contains("_")){
							nodeName = nodeName+"_"+generateNodeUniqueId();
						}
						par2Node.getSession().move(par2Node.getPath(), par1node.getPath()+"/"+nodeName);
						session.save();
				}	
				}
				//on change from 3 columns to 1 column 
				if((par2nodeItr.getSize()>0 || par3nodeItr.getSize()>0) && parNode== 1){
				while(par2nodeItr.hasNext()){
						Node par2Node = (Node)par2nodeItr.next();
						String nodeName = par2Node.getName();					
						if(!nodeName.contains("_")){
							nodeName = nodeName+"_"+generateNodeUniqueId();
						}
						par2Node.getSession().move(par2Node.getPath(), par1node.getPath()+"/"+nodeName);
						session.save();
				}
				while(par3nodeItr.hasNext()){
					Node par3Node = (Node)par3nodeItr.next();
					String nodeName = par3Node.getName();					
					if(!nodeName.contains("_")){
						nodeName = nodeName+"_"+generateNodeUniqueId();
					}
					par3Node.getSession().move(par3Node.getPath(), par1node.getPath()+"/"+nodeName);
					session.save();
				}	
				}
			}
		}
		catch(Exception e){
			logger.error("error in moving nodes "+e.getMessage());					
			return "";
		}
		return "";
	}
	public String getWrapperModeValue(){
		session=this.getResourceResolver().adaptTo(Session.class);
		currentNode = (Node)getResource().adaptTo(Node.class);
		String wrapperModeClass="suntrust-wrapperContainer container-fluid adjContainerPad";
		try {
			String theme=currentNode.getProperty("theme").getString();
			String layout=currentNode.getProperty("layout").getString();
			if(theme.equalsIgnoreCase("whitegrey")&&layout.equalsIgnoreCase("rightrail")){
				wrapperModeClass="suntrust-wrapperContainer container-fluid adjContainerPad column-gray-background";
			}else if(theme.equalsIgnoreCase("grey")){
				wrapperModeClass="suntrust-wrapperContainer container-fluid adjContainerPad rowContBgGrey rowContFullWidthBGborderTop";
			}	
			
		} catch (ValueFormatException e) {
			logger.error("Error message "+e.getMessage());	
		} catch (PathNotFoundException e) {
			logger.error("Error message "+e.getMessage());	
		} catch (RepositoryException e) {
			logger.error("Error message "+e.getMessage());	
		}
		return wrapperModeClass;		
	}
	public int generateNodeUniqueId(){
		long timeSeed = System.nanoTime(); // to get the current date time value
		double randSeed = Math.random() * 1000; // random number generation
		long midSeed = (long) (timeSeed * randSeed); // mixing up the time and
		String strMidSeed = midSeed + "";
		String strUniqueId = strMidSeed.substring(0, 9);
		int id = Integer.parseInt(strUniqueId);    // integer value
		return id;
	}

}