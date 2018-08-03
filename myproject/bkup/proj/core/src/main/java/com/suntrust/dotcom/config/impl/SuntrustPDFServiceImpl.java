package com.suntrust.dotcom.config.impl;

import java.util.Map;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.PropertyOption;
import org.apache.felix.scr.annotations.PropertyUnbounded;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.suntrust.dotcom.config.SuntrustPDFService;

/**
 * @author uiam82
 *
 */
@Service(value={SuntrustPDFService.class})
@Component(immediate=true,metatype=true,label="Conguration for Suntrust PDF Conversion", description="Conguration for Suntrust PDF Conversion")
public class SuntrustPDFServiceImpl implements SuntrustPDFService{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SuntrustPDFServiceImpl.class);
	
	@Property(label="Archival Enabled", description="Whether Content Archival of the published page is enabled.", boolValue={true})
	private static final String ARCHIVAL_ENABLED="archival.enabled";
	@Property(label="JS HELPER PATH",description="Enter path inside Sling Home. Sling home path is till crx-quickstart directory. Example:/opt/helpers will refer to crx-quickstart/opt/helpers. "
			+ "Leave it blank for sling homepath only(default).")
	private static final String	HELPER_PATH="js.helper.path";
	@Property(label="PHANTOM JS PATH",description="Enter absolute fs path for phantomjs")
	private static final String	PHANTOM_PATH="phantom.path";
	@Property(label="JS HELPER NAME",description="Enter helper JS name.")
	private static final String	HELPER_JS_NAME="js.helper.name";
	@Property(label="PDF OUTPUT PATH",description="Enter path inside Sling Home. Sling home path is till crx-quickstart directory. Example:/snapshots will refer to crx-quickstart/snapshots. "
			+ "Leave it blank for sling homepath only(default).")
	private static final String	PDF_OUTPUT_PATH="pdf.output.path";
	@Property(label="Environment", description="Select environment to extract resources.",
			  options={@PropertyOption(value="author",name="author"),
					   @PropertyOption(value="publish",name="publish")})
	private static final String PAGE_ENV="environment";
	@Property(label="AUTH USERNAME", description="Enter username with admin access for author environment.")
	private static final String	ENV_USERNAME="auth.admin.username";
	@Property(label="AUTH PASSWORD", description="Enter auth password for author environment.", passwordValue="admin")
	private static final String	ENV_PASSWORD="auth.admin.pwd";
	@Property(label="DAM Upload Enabled", description="Whether PDF/PNG will be uploaded to DAM", boolValue={true})
	private static final String DAM_ENABLED="dam.upload.enabled";
	@Property(label="DAM ARCHIVAL BASE PATH",value="/content/dam/archive",description="Example: /content/dam/archive")
	private static final String ARCHIVAL_PATH="dam.archival.path";
	@Property(label="PDF Paper Size",description="examples: 5in*7.5in, 10cm*20cm, A4, Letter")
	private static final String PDF_PAPER_SIZE="pdf.paper.size";
	@Property(label="JS Evaluation Option", description="Mark this as true only when the HTML contains no JS errors.",
			  options={@PropertyOption(value="true",name="true"),
					   @PropertyOption(value="false",name="false")})
	private static final String JS_EVAL_OPTION="js.eval.option";
	@Property(label="PNG Enabled", description="Whether PNG will be created for the page. PNG location will be same as PDF.", boolValue={true})
	private static final String PNG_ENABLED="png.enabled";
	@Property(label="PDF MetaData Enabled", description="This will create a new page at the end of the pdf and write metadata into it.", boolValue={true})
	private static final String PDF_METADATA_ENABLED="pdf.metadata.enabled";
	@Property(label="PNG Image Size",description="Examples: 800px*600px (for window clipped to 800*600), 1920px(for entire page of window width 1920)")
	private static final String IMAGE_SIZE="png.image.size";
	@Property(unbounded=PropertyUnbounded.ARRAY, cardinality=Integer.MAX_VALUE, label="Email recipients",description="Enter email address to send mail in failover cases")
	private static final String EMAIL_RECIPIENTS="email.to.users";
	@Property(unbounded=PropertyUnbounded.ARRAY, cardinality=Integer.MAX_VALUE, label="Email CC USERS",description="Enter email address to carbon copy mail in failover cases")
	private static final String EMAIL_CC="email.cc.users";
	
	private String helperPath;
	private boolean damUploadEnabled;
	private String archivalPath;
	private String paperSize;
	private String enviroment;
	private String username;
	private String password;
	private String jsEval;
	private String helperJSName;
	private String pdfOutputPath;
	private String phantomJsPath;
	private boolean archivalEnabled;
	private boolean pngEnabled;
	private boolean pdfMetadataEnabled;
	private String imageDimension;
	private String[] emailTo;
	private String[] emailCC;
	
	
	public boolean isArchivalEnabled(){
		return archivalEnabled;
	}
	public String getHelperPath() {
		return helperPath;
	}
	public boolean isDAMUploadEnabled(){
		return damUploadEnabled;
	}
	public String getArchivalPath() {
		return archivalPath;
	}
	public String getPaperSize() {
		return paperSize;
	}
	public String getJsEval() {
		return jsEval;
	}
	public String getHelperJSName() {
		return helperJSName;
	}
	public String getPdfOutputPath() {
		return pdfOutputPath;
	}
	public String getPhantomJsPath() {
		return phantomJsPath;
	}
	public boolean isPngEnabled(){
		return pngEnabled;
	}
	public boolean ispdfMetadataEnabled(){
		return pdfMetadataEnabled;
	}
	public String getImageDimension(){
		return imageDimension;
	}
	public String[] getEmailTo(){
		return emailTo;
	}
	public String[] getEmailCC(){
		return emailCC;
	}
	
	public String getEnviroment() {
		return enviroment;
	}
	public String getUsername() {
		return username;
	}
	public String getPassword() {
		return password;
	}
	@Activate
	  protected void activate(Map<String, Object> properties)
	  {
	    LOGGER.info("[*** SuntrustPDFServiceImpl ConfigurationService]: activating configuration service");
	    readProperties(properties);
	  }
	
	 protected void readProperties(Map<String, Object> properties)
	  {
	    LOGGER.info("OSGi properties set for PDF Service==>"+properties.toString());
	    
	    this.helperPath = PropertiesUtil.toString(properties.get(HELPER_PATH), "");
	    this.helperJSName = PropertiesUtil.toString(properties.get(HELPER_JS_NAME), "");
	    this.phantomJsPath = PropertiesUtil.toString(properties.get(PHANTOM_PATH), "");
	    this.pdfOutputPath = PropertiesUtil.toString(properties.get(PDF_OUTPUT_PATH), "");
	    this.damUploadEnabled=PropertiesUtil.toBoolean(properties.get(DAM_ENABLED), false);
	    this.archivalPath = PropertiesUtil.toString(properties.get(ARCHIVAL_PATH), "/content/dam/archive");
	    this.paperSize = PropertiesUtil.toString(properties.get(PDF_PAPER_SIZE), "");
	    this.jsEval = PropertiesUtil.toString(properties.get(JS_EVAL_OPTION), "false");
	    this.pngEnabled=PropertiesUtil.toBoolean(properties.get(PNG_ENABLED), false);
	    this.pdfMetadataEnabled=PropertiesUtil.toBoolean(properties.get(PDF_METADATA_ENABLED), false);
	    this.imageDimension=PropertiesUtil.toString(properties.get(IMAGE_SIZE),"");
	    this.emailTo=PropertiesUtil.toStringArray(properties.get(EMAIL_RECIPIENTS));
	    this.emailCC=PropertiesUtil.toStringArray(properties.get(EMAIL_CC));
	    this.username=PropertiesUtil.toString(properties.get(ENV_USERNAME), "");
	    this.password=PropertiesUtil.toString(properties.get(ENV_PASSWORD), "");
	    this.enviroment=PropertiesUtil.toString(properties.get(PAGE_ENV), "");
	    this.archivalEnabled=PropertiesUtil.toBoolean(properties.get(ARCHIVAL_ENABLED), false);
	  }
	

}
