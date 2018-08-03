package com.suntrust.dotcom.components;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.suntrust.dotcom.beans.FAQItemsBean;
import com.suntrust.dotcom.beans.FAQMultiBean;
import com.suntrust.dotcom.services.FAQService;

/**
* This FAQComponent is used to fetch the faqs details
* @author Cognizant
* @version 1.0
* @since 10 July 2017
* 
 */
public class FAQComponent extends WCMUsePojo {

	/** Default log. */
	private static final Logger LOGGER = LoggerFactory.getLogger(FAQComponent.class);
	/**	Multifield faq path items **/
	private static final String FAQPATHS_ITEMS = "faqpaths";	

	/**	 faqunt bean items * */
	private FAQMultiBean mBean = null;
	/**	 list of faq bean items * */	
	private List<FAQMultiBean> multiList = null;
	/**	 list of faq pages * */
	private List<String> faqPageList = new ArrayList<String>();
	/**	 Faqservice * */
	private FAQService faqService = null;

	/* (non-Javadoc)
	 * @see com.adobe.cq.sightly.WCMUsePojo#activate()
	 */
	@Override
	/**
	 * @method overrides com.adobe.cq.sightly.WCMUsePojo#activate()
	 */
	public void activate(){

		LOGGER.info("##### INVOKED ACTIVATE");
		
			multiList = new ArrayList<FAQMultiBean>();		
	
			Node currentNode = getResource().adaptTo(Node.class);
	
			String[] tabs = { FAQPATHS_ITEMS};
			
			for (int i = 0; i < tabs.length; i++) {			
				String currentItem = tabs[i];	
				
					try {
						if (currentNode.hasProperty(currentItem)) {	
							LOGGER.info("##### ITEMS ARE BEING SET" + currentItem);
							setItems(currentNode, currentItem);
							multiList.add(mBean);
						}
						else
						{	
							multiList.add(new FAQMultiBean());
						}
					} catch (RepositoryException repositoryException) {
						LOGGER.error("FAQComponent : activate() :Exception, {}",repositoryException.getMessage(),repositoryException);
					} catch (IllegalStateException e) {
						LOGGER.error("FAQComponent : activate() :Exception, {}",e.getMessage(),e);
					} catch (JSONException e) {			
						LOGGER.error("FAQComponent : activate() :Exception, {}",e.getMessage(),e);
					}
				
			}
		
		
	}

	/**
	 * @param currentNode
	 * @param item
	 * @throws RepositoryException 
	 * @throws PathNotFoundException 
	 * @throws JSONException 
	 * @throws IllegalStateException 
	 */
	private void setItems(Node currentNode, String item) throws PathNotFoundException, RepositoryException, IllegalStateException, JSONException{

			Value[] value;

			JSONObject jObj;

			Property currentProperty;
			
			mBean = new FAQMultiBean();
			FAQItemsBean itemBean = null;	
			List<FAQItemsBean> faqBean=null;
			faqBean = new ArrayList<FAQItemsBean>();

			currentProperty = currentNode.getProperty(item);
			
			if (currentProperty.isMultiple()) {
				value = currentProperty.getValues();

			} else {
				value = new Value[1];
				value[0] = currentProperty.getValue();

			}			
			

			faqService = getSlingScriptHelper().getService(FAQService.class);
			faqPageList.clear();
			for (int i = 0; i < value.length; i++) {
				
					jObj = new JSONObject(value[i].getString());														
				if (item.equalsIgnoreCase(FAQPATHS_ITEMS)) {
					LOGGER.info("item =" + item);
					String faqPath=jObj.getString("faqpath").trim();
					LOGGER.info("faqPath =" + faqPath);										
					faqPageList.add(faqPath);
					List<String> faqChildPageList = new ArrayList<String>();
					//faqChildPageList=faqService.getChildPages(faqPath);
					faqChildPageList=getChildPagesList(faqPath);
					if(faqChildPageList!=null){					
						faqPageList.addAll(faqChildPageList);					
					}
					
				}				
			}
			faqPageList = new ArrayList<String>(new LinkedHashSet<String>(faqPageList));
			for (String faqPage : faqPageList) {
				itemBean = new FAQItemsBean();	
				LOGGER.info("faqPage =" + faqPage);
				itemBean.setFaqquestion(getFaqProperty(faqPage,"question").trim());
				itemBean.setFaqanswer(getFaqProperty(faqPage,"answer").trim());
				itemBean.setFaqanchorname(getFaqProperty(faqPage,"anchorname").trim());
				faqBean.add(itemBean);
			}			
			mBean.setItems(faqBean);
				

	}

	
	/**
	 * @return faqmultilist
	 */
	public List<FAQMultiBean> getMBean(){
		return this.multiList;
	}		
	
	/**
	 * @param pageUrls
	 * @param propertyName
	 * @return pageproperty
	 */
	public String getFaqProperty(String pageUrls,String propertyName){			
		String faqPropertry = "";
		faqService = getSlingScriptHelper().getService(FAQService.class);
		try {
			if(faqService.getPageProperty(pageUrls,propertyName)!=null){
				faqPropertry=faqService.getPageProperty(pageUrls,propertyName);
			}
		} catch (RepositoryException repositoryException) {
			LOGGER.error("FAQComponent : getFaqProperty() :Exception, {}",repositoryException.getMessage(),repositoryException);
		}
		return faqPropertry;
	}
	
	/**
	 * @param faqPath
	 * @return list of faq child pages
	 */
	public List<String> getChildPagesList(String faqPath) {		
		Page faqPage = null;
		Page childPage = null;
		List<String> resultPages = new ArrayList<String>();		
		LOGGER.info("faqPath =" + faqPath);
		faqPage = getPageManager().getPage(faqPath);			
		if (faqPage != null) {
			Iterator<Page> pageItr = faqPage.listChildren(new PageFilter(),true);
			while (pageItr.hasNext()) {
				childPage = pageItr.next();					
				resultPages.add(childPage.getPath());
			}
		}			

		return resultPages;
	}
	

}