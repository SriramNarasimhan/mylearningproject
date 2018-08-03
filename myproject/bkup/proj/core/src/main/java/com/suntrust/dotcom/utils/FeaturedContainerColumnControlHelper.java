package com.suntrust.dotcom.utils;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;

@SuppressWarnings("deprecation")
public class FeaturedContainerColumnControlHelper extends WCMUsePojo {
	Logger logger = LoggerFactory.getLogger(FeaturedContainerColumnControlHelper.class);
	@Override
	public void activate(){		
	}
	public String moveNodes() {
		try{
		
			Session session = this.getResourceResolver().adaptTo(Session.class);
			Node currentNode = (Node)getResource().adaptTo(Node.class);
			NodeIterator componentRootNode = currentNode.getNodes();
			long componentRootNodeSize = componentRootNode.getSize();
			String currentNodePath= currentNode.toString();			
			long layout = 0; //layout value
			int parNode =3; //default columns
			Node par2node = null;	
			Node par3node = null;
			NodeIterator par3nodeItr =null;
			
			//move the components only if it exists otherwise consider as featured container component on load  
			if(componentRootNodeSize !=0){
				if(currentNode != null && currentNode.hasProperty("layout")){
					layout = currentNode.getProperty("layout").getLong();
					parNode=(layout == 4)? 3:2;
				}
				//create empty par 3 node, this is needed to handle failures, if the component is only added in col 2 
				if(!currentNodePath.contains("col3")){
					par3node=currentNode.addNode("col3");
					session.save();			
				}
				par3node = currentNode.getNode("col3");				
				par3nodeItr = par3node.getNodes();			
				//create empty par 2 node, this is needed to handle failures, if the component is only added in col 3
				if(!currentNodePath.contains("col2")){								
					par2node=currentNode.addNode("col2");				
					session.save();		
				}
				par2node = currentNode.getNode("col2");	
				if(par3nodeItr.getSize()>0 && parNode==2){
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
			}
		}
		catch(Exception e){
			logger.error("error in moving nodes"+e.getMessage());			
			return "";
		}		
		return "";
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