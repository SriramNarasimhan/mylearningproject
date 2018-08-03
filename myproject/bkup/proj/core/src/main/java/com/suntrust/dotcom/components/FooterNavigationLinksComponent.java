package com.suntrust.dotcom.components;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.suntrust.dotcom.beans.FooterNavigationLinksItemBeans;
public class FooterNavigationLinksComponent extends BaseUsePojo {

	List<FooterNavigationLinksItemBeans> footerlinks;
	private final Logger log = LoggerFactory.getLogger(FooterNavigationLinksComponent.class);
			
	@Override
	public void activate() throws Exception {
		footerlinks= populateBeanList("label", FooterNavigationLinksItemBeans.class);
	}

	public List<FooterNavigationLinksItemBeans> getFooterlinks() {
		return footerlinks;
	}
}
