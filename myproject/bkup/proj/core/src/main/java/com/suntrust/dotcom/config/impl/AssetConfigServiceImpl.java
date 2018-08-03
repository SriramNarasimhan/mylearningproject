package com.suntrust.dotcom.config.impl; 

import java.util.Map;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.PropertyUnbounded;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.suntrust.dotcom.config.AssetConfigService;

/**
 * @author ugjy26
 *
 */
@Service(value={AssetConfigService.class})
@Component(immediate=true,metatype=true,label="Conguration for Suntrust ASSET Archival Conversion", description="Conguration for Suntrust ASSET Archival Conversion")
public class AssetConfigServiceImpl implements AssetConfigService{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AssetConfigServiceImpl.class);
		
	@Property(label="ASSET OUTPUT PATH",description="Enter path inside Sling Home. Sling home path is till crx-quickstart directory. Example:/snapshots will refer to crx-quickstart/snapshots. "
			+ "Leave it blank for sling homepath only(default).")
	private static final String	ASSET_OUTPUT_PATH="asset.output.path";	
	
	@Property(unbounded=PropertyUnbounded.ARRAY, cardinality=Integer.MAX_VALUE, label="Email recipients",description="Enter email address to send mail in failover cases")
	private static final String EMAIL_RECIPIENTS="email.to.users";
	
	@Property(unbounded=PropertyUnbounded.ARRAY, cardinality=Integer.MAX_VALUE, label="Email CC USERS",description="Enter email address to carbon copy mail in failover cases")
	private static final String EMAIL_CC="email.cc.users";	
	
	@Property(unbounded=PropertyUnbounded.ARRAY, cardinality=Integer.MAX_VALUE, label="ASSET MIME TYPE",description="Enter asset mime types that needs to be archived")
	private static final String	MIME_TYPE="doc.mimeType";
	
	private String[] mimeType;
	private String 	 assetOutputPath; 
	private String[] emailTo;
	private String[] emailCC;	
	
	public String getAssetOutputPath() {
		return assetOutputPath;
	}
	public String[] getEmailTo(){
		return emailTo;
	}
	public String[] getEmailCC(){
		return emailCC;
	}
	
	public String[] getMimeType(){
		return mimeType;
	}
	
	@Activate
	  protected void activate(Map<String, Object> properties)
	  {
			LOGGER.debug("[*** AssetConfigServiceImpl ConfigurationService]: activating configuration service");
			readProperties(properties);
	  }
	
	 protected void readProperties(Map<String, Object> properties)
	  {		 		    	   
		    this.assetOutputPath = PropertiesUtil.toString(properties.get(ASSET_OUTPUT_PATH), "");
		    this.emailTo=PropertiesUtil.toStringArray(properties.get(EMAIL_RECIPIENTS));
		    this.emailCC=PropertiesUtil.toStringArray(properties.get(EMAIL_CC));	
		    this.mimeType=PropertiesUtil.toStringArray(properties.get(MIME_TYPE));    	
	  }
	

}
