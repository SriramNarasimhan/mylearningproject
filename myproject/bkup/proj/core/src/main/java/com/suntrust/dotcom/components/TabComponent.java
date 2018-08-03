package com.suntrust.dotcom.components;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;

import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.suntrust.dotcom.beans.TabContainerItemsBean;
import com.suntrust.dotcom.beans.TabContainerMultiBean;

public class TabComponent extends WCMUsePojo{

	/** Default log. */
	private static final Logger LOGGER = LoggerFactory.getLogger(TabComponent.class);
	private static final String TAB_ID = "tabid";
	private static final String TAB_NAME = "tabname";
	private static final String TAB_ANCHORNAME = "anchorname";
	private TabContainerMultiBean mBean=null;
	
	
	private List<TabContainerMultiBean> multiList=null;
	private int counter = 0;
	private String currentNodePath = null;
	ResourceResolver resourceResolver = null;

	@Override
	public void activate() throws PathNotFoundException, ValueFormatException, RepositoryException, JSONException, PersistenceException{

		LOGGER.info("Tab Container Component Activated");
		multiList=new ArrayList<TabContainerMultiBean>();
		Node currentNode = getResource().adaptTo(Node.class);
		
		String[] tabs = {"i"};
		for (int i = 0; i < tabs.length; i++) {
			String currentItem = tabs[i] + "Items";
			if (currentNode.hasProperty(currentItem)) {
				LOGGER.info("##### ITEMS ARE BEING SET" + currentItem);
				LOGGER.info("##### Tab Component");
				setItems(currentNode, currentItem);
				multiList.add(mBean);
			}
		}

	}

	private void setItems(Node currentNode, String tab) throws PathNotFoundException, RepositoryException, ValueFormatException, org.json.JSONException, PersistenceException {
			Value[] value;
			JSONObject jObj;
			Property currentProperty;				
			currentNodePath = currentNode.getPath();
			List<TabContainerItemsBean> lBean=null;
			TabContainerItemsBean iBean=null;
			LOGGER.info("##### TabComponent currentNodePath::" + currentNodePath);
			resourceResolver = getResourceResolver();
			//Resource res= resolver.getResource(currentNodePath);
			//LOGGER.info("##### resolver::" + resolver+"#########res"+res);
			/**/
			mBean = new TabContainerMultiBean();
			lBean = new ArrayList<TabContainerItemsBean>();
			currentProperty = currentNode.getProperty(tab); //get property of iItems
			if (currentProperty.isMultiple()) {
				value = currentProperty.getValues();
			}
			else {
				value = new Value[1];
				value[0] = currentProperty.getValue();
			}
			for (int i = 0; i < value.length; i++) {
				jObj = new JSONObject(value[i].getString());
				iBean = new TabContainerItemsBean();	
				
					iBean.setAnchorname(jObj.getString(TAB_ANCHORNAME));
					iBean.setTabname(jObj.getString(TAB_NAME));
					iBean.setTabid(jObj.getString(TAB_ID));					
				
				lBean.add(iBean);
				counter++;
				iBean.setCount(Integer.toString(counter));
			}
			mBean.setItems(lBean);
			
			
			NodeIterator iterator = currentNode.getNodes();						
			Node parNode=null;
			while(iterator.hasNext()) {
				Boolean trigger = false;
				parNode = iterator.nextNode();
				LOGGER.info("TabComponent : setItems : START :"+"~~~~~~~~~~~~~~~~~");
				for(int i=0;i<lBean.size();i++){
					if(parNode.getName().equals(lBean.get(i).getTabid())){
						trigger = true;
					}
				}
				if(!trigger){
					LOGGER.info("TabComponent : setItems : Item to be removed :"+parNode.getName());
					parNode.remove();
					resourceResolver.commit();
									
				}
				LOGGER.info("AccordionComponent : setItems : END :"+"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			}
	}
	
	public List<TabContainerMultiBean> getMBean() {
		return this.multiList;
	}


	public int getCounter() {
		LOGGER.info("Tab Counter value is {}", counter);
		return counter;
	}


	
}
