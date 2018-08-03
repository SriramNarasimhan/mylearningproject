package com.suntrust.dotcom.components;

import com.adobe.cq.sightly.WCMUsePojo;
import com.suntrust.dotcom.beans.AccordionItemsBean;
import com.suntrust.dotcom.beans.AccordionMultiBean;

import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import javax.jcr.Value;
import javax.jcr.Property;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This program manages sections
 * and parsys in the component
 * @author Cognizant
 * @version 1.0
 *
 */
public class AccordionComponent extends WCMUsePojo {
	/** Default log. */
	private static final Logger LOGGER = LoggerFactory.getLogger(AccordionComponent.class);
	/** Multifield bean. */
	private AccordionMultiBean mBean = null;
	/** AccordionMultiBean list object. */
	private List<AccordionMultiBean> multiList = null;
	/** The ID for each parsys in the sections. */
	private int ctr = 0;
	
	/**
	 * Method which initiates storing in JSON objects
	 * 
	 */
	@Override
	public void activate() {
		//This is a test comment;
		LOGGER.info("##### AccordionComponent ACTIVATE method INVOKED");				
		multiList = new ArrayList<AccordionMultiBean>();		
		Node currentNode = getResource().adaptTo(Node.class);

		String[] tabs = {"i"};
		for (int i = 0; i < tabs.length; i++) {
			String currentItem = tabs[i] + "Items";
			try {
				if (currentNode.hasProperty(currentItem)) {
					LOGGER.info("##### ITEMS ARE BEING SET" + currentItem);
					LOGGER.info("##### Accordion");
					setItems(currentNode, currentItem);
					multiList.add(mBean);
				}
			}
			catch (RepositoryException e) {
				LOGGER.error("RepositoryException " + e.getMessage());
			}
		}
	}
	
	/**
	 * Method to set one list of items from multifield
	 */
	private void setItems(Node currentNode, String tab) {
		/** Multifield constant item. */
		final String I_ITEMS = "iItems";
		/** AccordionItemsBean class object. */
		AccordionItemsBean iBean = null;
		/** AccordionItemsBean list object. */
		List<AccordionItemsBean> lBean = null;
		/** Variable to hold the path of current node. */
		String currentNodePath = null;
		
		try {
			Value[] value;
			JSONObject jObj;
			Property currentProperty;				
			currentNodePath = currentNode.getPath();
			LOGGER.info("##### AccordionComponent currentNodePath::" + currentNodePath);
			ResourceResolver resolver = getResourceResolver();
			//Resource res= resolver.getResource(currentNodePath);
			//LOGGER.info("##### resolver::" + resolver+"#########res"+res);
			/**/
			mBean = new AccordionMultiBean();
			lBean = new ArrayList<AccordionItemsBean>();
			currentProperty = currentNode.getProperty(tab);
			if (currentProperty.isMultiple()) {
				value = currentProperty.getValues();
			}
			else {
				value = new Value[1];
				value[0] = currentProperty.getValue();
			}
			for (int i = 0; i < value.length; i++) {
				jObj = new JSONObject(value[i].getString());
				iBean = new AccordionItemsBean();				
				//String stringURL;
				if (tab.equalsIgnoreCase(I_ITEMS)) {					
					iBean.setAccosecheading(jObj.getString("accosecheading"));
					iBean.setAccosecanchor(jObj.getString("accosecanchor"));
					iBean.setAccosecexpand(jObj.getString("accosecexpand"));
					iBean.setAccosecid(jObj.getString("accosecid"));					
				}
				
				else {					
					iBean.setAccosecheading(jObj.getString("accosecheading"));
					iBean.setAccosecanchor(jObj.getString("accosecanchor"));
					iBean.setAccosecexpand(jObj.getString("accosecexpand"));
					iBean.setAccosecid(jObj.getString("accosecid"));
				}
				lBean.add(iBean);
				ctr++;
				iBean.setCount(Integer.toString(ctr));
			}
			mBean.setItems(lBean);
			
			
			NodeIterator iterator = currentNode.getNodes();						
			Node parNode=null;
			while(iterator.hasNext()) {
				Boolean trigger = false;
				parNode = iterator.nextNode();
				LOGGER.info("AccordionComponent : setItems : START :"+"~~~~~~~~~~~~~~~~~");
				for(int i=0;i<lBean.size();i++) {
					if(parNode.getName().equals(lBean.get(i).getAccosecid())){
						trigger = true;
					}				
				}
				if(!trigger){
					LOGGER.info("AccordionComponent : setItems : Item to be removed :"+parNode.getName());
					parNode.remove();
					resolver.commit();
									
				}
				LOGGER.info("AccordionComponent : setItems : END :"+"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			}
			
			
			
		}
		catch (RepositoryException | IllegalStateException | JSONException | PersistenceException e) {
			LOGGER.error("RepositoryException " + e.getMessage());
		}
	}
	
	/**
	 * Method to return the count of sections added in Accordion
	 * @return
	 */
	public int multifieldCounter() {
		LOGGER.info("#### INSIDE FUNCTION MULTIFIELD COUNTER = "+ctr+" ####");
		return ctr;
	}	
	
	
	/**
	 * Method to return the set list of items
	 * @return
	 */
	public List<AccordionMultiBean> getMBean() {
		return this.multiList;
	}
}