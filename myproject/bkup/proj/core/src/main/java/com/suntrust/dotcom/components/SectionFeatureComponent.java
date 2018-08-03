package com.suntrust.dotcom.components;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.suntrust.dotcom.beans.SectionFeatureItemsBean;
import com.suntrust.dotcom.utils.Utils;

/**
* The SectionFeature class extends the WCMPojo object that 
* sets the dialog box tabs.
*
* @author  Jagan Mohan Rao Y
* @version 1.0
* @since   2017-09-27 
*/
public class SectionFeatureComponent extends WCMUsePojo {

	/** Default log. */
	private static final Logger LOGGER = LoggerFactory.getLogger(SectionFeatureComponent.class); 	
	/** Selected Style. */
	private String selectedStyle = "Image Top Horizontal";
	/** SectionFeature Items Bean. */
	private SectionFeatureItemsBean iBean;
	/** SectionFeatureItems List Bean. */
	private List<SectionFeatureItemsBean> beans = null;

	/**
     * activate method gets initiated when the page is called.
     *     
     * @throws java.lang.Exception
     */
	@Override
	public void activate() throws Exception {

		LOGGER.debug("##### INVOKED ACTIVATE from SectionFeatureComponent"); 
		Node currentNode = getResource().adaptTo(Node.class);
		setItems(currentNode);
		if(currentNode.hasProperty("styles")) {
			selectedStyle = currentNode.getProperty("styles").getValue().getString();
			setSelectedStyle(selectedStyle);
		}
 	}
	
	/**
     * setItems method sets the items to the bean that are selected in the dialog box.
     * 
     *  @param  currentNode the currentNode in the Component
     *  @return Nothing. 
     *  @throws org.json.JSONException.
     *  @throws ValueFormatException. 
     */	
	private void setItems(Node currentNode)
			throws PathNotFoundException, RepositoryException, ValueFormatException,
			org.json.JSONException {
		try {
			beans = new ArrayList<SectionFeatureItemsBean>();
			NodeIterator currentNodeIterator = currentNode.getNodes();			
			Node sectionNode = currentNodeIterator.nextNode();
			Node succesiveNode;

			if(sectionNode.hasNodes())
			{
				NodeIterator nodeIterator= sectionNode.getNodes();
				while(nodeIterator.hasNext())
				{
					iBean = new SectionFeatureItemsBean();
					succesiveNode=nodeIterator.nextNode();					
					iBean.setHeading(succesiveNode.hasProperty("heading")?succesiveNode.getProperty("heading").getString():"");										
					iBean.setAlt(succesiveNode.hasProperty("alt")?succesiveNode.getProperty("alt").getString():"");					
					iBean.setAnchorTag(succesiveNode.hasProperty("anchorTag")?succesiveNode.getProperty("anchorTag").getString():"");										
					iBean.setImage(succesiveNode.hasProperty("image")?succesiveNode.getProperty("image").getString():"");										
					iBean.setTarget(succesiveNode.hasProperty("target")?succesiveNode.getProperty("target").getString():"");										
					iBean.setText(succesiveNode.hasProperty("text")?succesiveNode.getProperty("text").getString():"");
					iBean.setUrlParams(succesiveNode.hasProperty("urlParams")?succesiveNode.getProperty("urlParams").getString():"");										
					iBean.setLinkURL(succesiveNode.hasProperty("linkURL")?succesiveNode.getProperty("linkURL").getString():"");										
					String stringURL = Utils.getModifyURL(iBean.getLinkURL().trim(),iBean.getUrlParams().trim(),iBean.getAnchorTag().trim());
					iBean.setLinkURL(stringURL.trim());
					beans.add(iBean);
				}
			}
		} catch (Exception e) {
			LOGGER.error(
					"Exception in lightstream api service. Message: {}, Trace: {}",
					e.getMessage(), e);
		}
	}

	/**
     * getBeans method is called from the Sightly code.
     *  
     *     return the beans that are set from the  the setItems.
     */
	public List<SectionFeatureItemsBean> getBeans() {
		return beans;
	}
	/**
     * getSelectedStyle method is called from the Sightly code.
     *  
     *     return the selectedStyle from the dialog box.
     */	
	public String getSelectedStyle() {
		return selectedStyle;
	}
	/**
     * setSelectedStyle method sets the selctedStyle.
     *  
     *     sets the selectedStyle from the dialog box.
     */	
	public void setSelectedStyle(String selectedStyle) {
		this.selectedStyle = selectedStyle;
	}
}