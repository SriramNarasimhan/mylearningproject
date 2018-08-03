package com.suntrust.dotcom.components;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;

//import org.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.suntrust.dotcom.beans.LocationAddressItemsBean;
import com.suntrust.dotcom.beans.LocationAddressMultiBean;

public class LocationAddressMultiComponent extends WCMUsePojo {
	/** Default log. */
	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	private LocationAddressMultiBean mBean = null;
	private LocationAddressItemsBean iBean = null;
	private List<LocationAddressItemsBean> lBean = null;
	private List<LocationAddressMultiBean> multiList = null;
	private String hiddenMapData = null;

	@Override
	public void activate() throws Exception {
		multiList = new ArrayList<LocationAddressMultiBean>();
		//Node currentNode = getResource().adaptTo(Node.class);
		Node currentNode = getCurrentPage().getContentResource().adaptTo(Node.class);
		String currentItem = "adv_addressItems";
		if (currentNode.hasProperty(currentItem)) {
			setItems(currentNode, currentItem);
			multiList.add(mBean);
		}
		if(currentNode.hasProperty("adv_hiddenData")) {
			hiddenMapData = currentNode.getProperty("adv_hiddenData").getString();
		}
	}

	private void setItems(Node currentNode, String advisorAddress)
			throws PathNotFoundException, RepositoryException,
			ValueFormatException, org.json.JSONException {
		try {
			Value[] value;
			JSONObject jObj;
			Property currentProperty;
			mBean = new LocationAddressMultiBean();
			lBean = new ArrayList<LocationAddressItemsBean>();
			currentProperty = currentNode.getProperty(advisorAddress);
			if (currentProperty.isMultiple()) {
				value = currentProperty.getValues();
			} else {
				value = new Value[1];
				value[0] = currentProperty.getValue();
			}
			for (int i = 0; i < value.length; i++) {
				jObj = new JSONObject(value[i].getString());
				iBean = new LocationAddressItemsBean();
				if(i%2 == 0){
					iBean.setRowOpen("yes");
					if(i==value.length-1){
						iBean.setRowClose("yes");
					}
				}else{
					iBean.setRowClose("yes");
				}

				iBean.setAddress(jObj.getString("adv_address"));
				iBean.setCity(jObj.getString("adv_city"));
				iBean.setState(jObj.getString("adv_state"));
				iBean.setZipCode(jObj.getString("adv_zipcode"));
				iBean.setLocationName(jObj.getString("adv_location_name"));
				iBean.setLatitude(jObj.getString("adv_latitude"));
				iBean.setLongitude(jObj.getString("adv_longitude"));
				lBean.add(iBean);
			}
			mBean.setItems(lBean);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<LocationAddressMultiBean> getMBean() {
		return this.multiList;
	}

	public String getHiddenMapData()
	{
		return this.hiddenMapData;
	}


}