package com.suntrust.dotcom.components;


import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.suntrust.dotcom.beans.MortgageRatesItemsBean;
import com.suntrust.dotcom.beans.MortgageRatesMultiBean;

/**
* This MortgageRatesComponent is used to fetch the rates details
* @author Cognizant
* @version 1.0
* @since 10 July 2017
* 
 */
public class MortgageRatesComponent extends WCMUsePojo {

	/** Default log. */
	private static final Logger LOGGER = LoggerFactory.getLogger(MortgageRatesComponent.class);
	
	/**	Multifield rates items **/
	private static final String RATES_ITEMS = "ratesexamples";
	
	/**	 Mortgage rates bean items * */
	private MortgageRatesMultiBean mBean = null;
	/**	List of Mortgage Rates items * */
	private List<MortgageRatesMultiBean> multiList = null;
	

	/* (non-Javadoc)
	 * @see com.adobe.cq.sightly.WCMUsePojo#activate()
	 */
	@Override
	/**
	 * @method overrides com.adobe.cq.sightly.WCMUsePojo#activate()
	 */
	public void activate()  {

		LOGGER.info("##### INVOKED ACTIVATE");

		multiList = new ArrayList<MortgageRatesMultiBean>();		

		Node currentNode = getResource().adaptTo(Node.class);

		String[] tabs = {RATES_ITEMS};
				
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
					multiList.add(new MortgageRatesMultiBean());
				}
			}  catch (PathNotFoundException pathNotFoundException) {
				LOGGER.error("MortgageRatesComponent : activate() :Exception, {}",pathNotFoundException.getMessage(),pathNotFoundException);
			}  catch (ValueFormatException valueFormatException) {
				LOGGER.error("MortgageRatesComponent : activate() :Exception, {}",valueFormatException.getMessage(),valueFormatException);
			}catch (RepositoryException repositoryException) {
				LOGGER.error("MortgageRatesComponent : activate() :Exception, {}",repositoryException.getMessage(),repositoryException);
			}
		}
		
			
	}

	/**
	 * @param currentNode
	 * @param item
	 * @throws PathNotFoundException,RepositoryException	
	 * @throws ValueFormatException	 
	 * @throws JSONException
	 */
	private void setItems(Node currentNode, String item) {
		try{
			Value[] value;

			JSONObject jObj;

			Property currentProperty;
			MortgageRatesItemsBean itemBean = null;	
			mBean = new MortgageRatesMultiBean();
			List<MortgageRatesItemsBean> ratesBean=null;
			ratesBean = new ArrayList<MortgageRatesItemsBean>();

			currentProperty = currentNode.getProperty(item);

			if (currentProperty.isMultiple()) {

				value = currentProperty.getValues();

			} else {

				value = new Value[1];

				value[0] = currentProperty.getValue();

			}			
			
			for (int i = 0; i < value.length; i++) {

				jObj = new JSONObject(value[i].getString());
				itemBean = new MortgageRatesItemsBean();			
				
					LOGGER.info("item =" + item);
					itemBean.setAmount(jObj.getString("amount"));
					itemBean.setRates(jObj.getString("rates"));
					itemBean.setMonths(jObj.getString("months"));
															
				ratesBean.add(itemBean);
			}
			mBean.setItems(ratesBean);	
		} catch (PathNotFoundException pathNotFoundException) {
			LOGGER.error("MortgageRatesComponent : setItems() :pathNotFoundException, {}",pathNotFoundException.getMessage(),pathNotFoundException);
		}  catch (ValueFormatException valueFormatException) {
			LOGGER.error("MortgageRatesComponent : setItems() :valueFormatException, {}",valueFormatException.getMessage(),valueFormatException);
		}catch (IllegalStateException illegalStateException) {
			LOGGER.error("MortgageRatesComponent : setItems() :illegalStateException, {}",illegalStateException.getMessage(),illegalStateException);
		}catch (RepositoryException repositoryException) {
			LOGGER.error("MortgageRatesComponent : setItems() :repositoryException, {}",repositoryException.getMessage(),repositoryException);
		} catch (JSONException jSONException) {
			LOGGER.error("MortgageRatesComponent : setItems() :JsonException, {}",jSONException.getMessage(),jSONException);
		} 

	}
	
	
	/**
	 * @return List<MortgageRatesMultiBean>
	 */
	public List<MortgageRatesMultiBean> getMBean() {

		return this.multiList;

	}
	
	

}