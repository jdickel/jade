package com.sos.jade.cli;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import sos.util.SOSString;

public class JadeXml2IniConverter {
	
	private static Logger LOGGER	= Logger.getLogger(JadeXml2IniConverter.class);
	private static final String CHARSET = "UTF-8";
	private static final String CLASSNAME = JadeXml2IniConverter.class.getSimpleName();
	private static final int EXIT_CODE_ON_SUCCESS = 0;
	private static final int EXIT_CODE_ON_ERROR = 99;
	
	private static final String SCHEMA_ATTRIBUTE_NAME = "name";
	private static final String SCHEMA_ATTRIBUTE_VALUE = "value";
	private static final String SCHEMA_ATTRIBUTE_SUPPRESS_PREFIX = "suppress_prefix";
	private static final String SCHEMA_ATTRIBUTE_OPPOSITE_VALUE = "opposite_value";
	private static final String SCHEMA_ATTRIBUTE_MAIL_NAME = "mail_name";
	private static final String SCHEMA_ATTRIBUTE_ALTERNATIVE_NAME = "alternative_name";
		
	private int _countNotificationMailFragments = 0;
	private int _countAlternativeFragments = 0;
	private int _countNotificationBackgroundServiceFragments = 0;
	private int _countMailServerFragments = 0;
	private int _countCredentialStoreFragments = 0;
	private int _countProtocolFragments = 0;
	private int _countProfiles = 0;
	private int _countWarnings = 0;
	
	private boolean _hasGlobalSection = false;
	private BufferedWriter _writer = null;
	private HashMap<String,String> _jumpIncludes;
	private HashMap<String,String> _credentialStoreIncludes;
	private HashMap<String,LinkedHashMap<String,String>> _mailFragments;
	private HashMap<String,LinkedHashMap<String,String>> _mailServerFragments;
	
	private XPath _xpathSchema;
	private XPath _xpathXml;
	private Node _rootSchema;
	private Node _rootXml;
	
	private String _profileJumpInclude = null;
	
	private enum Fragment {
		protocolFragment, 
		alternativeFragment, 
		notificationFragment, 
		credentialStoreFragment,
		mailServerFragment,
		backgroundServiceFragment
	}
	
	public static void main (String[] args){
		
		if(args.length < 3){
			System.out.println("Arguments:");
			System.out.println("     1 - required - XSD Schema location");
			System.out.println("     2 - required - XML file path");
			System.out.println("     3 - required - INI file path");
			System.out.println("     4 - optional - log4j.properties file path");
			System.out.println("e.g.:");
			System.out.println(String.format("%s \"http://www.sos-berlin.com/schema/jade/JADE_configuration_v1.0.xsd\" \"C:/Temp/jade_settings.xml\" \"C:/Temp/jade_settings.ini\"",CLASSNAME));
			System.out.println(String.format("%s \"http://www.sos-berlin.com/schema/jade/JADE_configuration_v1.0.xsd\" \"C:/Temp/jade_settings.xml\" \"C:/Temp/jade_settings.ini\" \"C:/Temp/log4j.properties\"",CLASSNAME));
			
			System.exit(EXIT_CODE_ON_ERROR);
		}
		
		String log4j = null;
		int exitCode = EXIT_CODE_ON_SUCCESS;
		
		String schemaFile = args[0];
		String xmlFile = args[1];
		String iniFile = args[2];
		
		if(args.length > 3){
			log4j = args[3];
		}
		
		try {
			setLogger(log4j);
			LOGGER.info("Arguments:");
			for(int i=0;i<args.length;i++){
				LOGGER.info(String.format("  %s",args[i]));
			}
			
			JadeXml2IniConverter converter = new JadeXml2IniConverter();
			converter.proccess(schemaFile,xmlFile,iniFile);
			
			LOGGER.info("");
			LOGGER.info("Summary:");
			LOGGER.info(String.format("    %s General",converter.hasGlobalSection() ? "1" : "0"));
			LOGGER.info(String.format("    %s Protocol Fragments",converter.getCountProtocolFragments()));
			LOGGER.info(String.format("    %s Alternative Fragments",converter.getCountAlternativeFragments()));
			LOGGER.info(String.format("    %s Notification MailFragments",converter.getCountNotificationMailFragments()));
			LOGGER.info(String.format("    %s Notification BackgroundServiceFragments",converter.getCountNotificationBackgroundServiceFragments()));
			LOGGER.info(String.format("    %s CredentialStore Fragments",converter.getCountCredentialStoreFragments()));
			LOGGER.info(String.format("    %s MailServer Fragments",converter.getCountMailServerFragments()));
			LOGGER.info(String.format("    %s Profiles",converter.getCountProfiles()));
			if(converter.getCountWarnings() > 0){
				LOGGER.info("");
				LOGGER.info(String.format("    !!! Converted with %s warnings",converter.getCountWarnings()));
			}
			
			File f = new File(iniFile);
			if(f.length() == 0){
				f.deleteOnExit();
				throw new Exception(String.format("Converted file %s is empty and will be deleted",iniFile));
			}
			
		
		} catch (Exception e) {
			exitCode = EXIT_CODE_ON_ERROR;
			LOGGER.error(e);
			e.printStackTrace();
		}
		System.exit(exitCode);
	} 
	
	public void proccess(String schemaFilePath,String xmlFilePath, String iniFilePath) throws Exception{
		InputSource schemaSource = new InputSource(schemaFilePath);
		InputSource xmlSource = new InputSource(xmlFilePath);
		
		_xpathSchema =  XPathFactory.newInstance().newXPath();
		_xpathSchema.setNamespaceContext(getSchemaNamespaceContext());
		_xpathXml =  XPathFactory.newInstance().newXPath();
		
		XPathExpression schemaExpression = _xpathSchema.compile("/xs:schema");
		XPathExpression xmlExpression = _xpathXml.compile("/Configurations");
		
		_rootSchema = (Node) schemaExpression.evaluate(schemaSource,XPathConstants.NODE);
		
		Document xmlDoc = getXmlFileDocument(xmlSource, xmlFilePath);
		_rootXml = (Node) xmlExpression.evaluate(xmlDoc, XPathConstants.NODE);
		
		if(_rootSchema == null){
			throw new Exception(String.format("\"xs:schema\" element not found in the schema file %s",schemaFilePath));
		}
		if(_rootXml == null){
			throw new Exception(String.format("\"Configurations\" element not found in the xml file %s",xmlFilePath));
		}
		
		try{
			_writer = new BufferedWriter(new OutputStreamWriter(
			          new FileOutputStream(iniFilePath),CHARSET));
			
			_jumpIncludes = new HashMap<String, String>();
			_credentialStoreIncludes = new HashMap<String, String>();
			
			_mailFragments = new HashMap<String, LinkedHashMap<String,String>>();
			_mailServerFragments = new HashMap<String, LinkedHashMap<String,String>>();
			
			handleMailServerFragments();
			
			handleGeneral();
			handleProtocolFragments();
			handleNotificationMailFragments();
			handleNotificationBackgroundServiceFragments();
			handleCredentialStoreFragments();
			handleProfiles();
		}
		catch(Exception ex){
			throw ex;
		}
		finally{
			if(_writer != null){
				_writer.close();
				_writer = null;
			}
		}
	}
	
	private Document getXmlFileDocument(InputSource xmlSource,String xmlFile) throws Exception{
		String normalized = xmlFile.toLowerCase();
		if(!normalized.startsWith("http://") && !normalized.startsWith("https://")){
			System.setProperty("user.dir",new File(xmlFile).getParent());
		}
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
        factory.setXIncludeAware(true); 
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(xmlSource);
	}
	
	public int getCountNotificationMailFragments(){
		return _countNotificationMailFragments;
	}
	
	public int getCountNotificationBackgroundServiceFragments(){
		return _countNotificationBackgroundServiceFragments;
	}
	
	public int getCountProtocolFragments(){
		return _countProtocolFragments;
	}
	
	public int getCountProfiles(){
		return _countProfiles;
	}
	
	public boolean hasGlobalSection(){
		return _hasGlobalSection;
	}
	
	public int getCountWarnings(){
		return _countWarnings;
	}
	
	public int getCountMailServerFragments(){
		return _countMailServerFragments;
	}
	
	public int getCountCredentialStoreFragments(){
		return _countCredentialStoreFragments;
	}
	
	public int getCountAlternativeFragments(){
		return _countAlternativeFragments;
	}
	
	private void handleGeneral() throws Exception{
		XPathExpression expression = _xpathXml.compile("./General");
		Node general = (Node) expression.evaluate(_rootXml, XPathConstants.NODE);
		if (general == null || !general.hasChildNodes()) {
			return;
		}
		_hasGlobalSection = true;
		
		NodeList childs = general.getChildNodes();
		String section = "[globals]";
		writeLine(section);
		LOGGER.info(String.format("write %s",section));
		
		for (int i = 0; i< childs.getLength(); i++) {
			Node child = childs.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				if(child.getNodeName().toLowerCase().equals("notifications") && child.hasChildNodes()){
					for(int j=0;j<child.getChildNodes().getLength();j++){
						Node nChild = child.getChildNodes().item(j);
						if (nChild.getNodeType() == Node.ELEMENT_NODE) {
							if(nChild.getAttributes().getNamedItem("ref") != null){
							String refName = nChild.getAttributes().getNamedItem("ref").getNodeValue();
							if(nChild.getNodeName().equals("MailServerFragmentRef")){
								if(_mailServerFragments.containsKey(refName)){
									writeParams(_mailServerFragments.get(refName));
									writeNewLine();
								}
							}
							else if(nChild.getNodeName().equals("BackgroundServiceFragmentRef")){
								String include = getFragmentInclude(nChild,"");
								if(include != null){
									writeLine(include);
								}
							}
							continue;
							}
						}
					}
				}
				if(child.getNodeName().equals("SystemPropertyFiles")){
					String val = getMergedChildsEntry(child,"system_property_files");
					if(!SOSString.isEmpty(val)){
						writeLine(val);
						writeNewLine();
					}
				}
				else{
					handleChildNodes(child.getNodeName(), child, 0);
					writeNewLine();
				}
			}
		}
	}
	
	private String getMergedChildsEntry(Node parent, String defaultParamName){
		StringBuffer sb = new StringBuffer();
		NodeList childs = parent.getChildNodes();
		for (int i = 0; i< childs.getLength(); i++) {
			Node child = childs.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				String val = child.getFirstChild() == null ? child.getNodeValue() : child.getFirstChild().getNodeValue();
				if(!SOSString.isEmpty(val)){
					if(sb.length() > 0){
						sb.append(";");
					}
					sb.append(val.trim());
				}
			}
		}
		
		String paramName = defaultParamName;
		if(sb.length() > 0){
			try{
				String xpath = String.format("./xs:element[@name='%s']/xs:annotation/xs:appinfo/FlatParameter[1]",parent.getNodeName());
		
				XPathExpression ex = _xpathSchema.compile(xpath);
				Node fp = (Node)ex.evaluate(_rootSchema,XPathConstants.NODE);
				if(fp != null){
					if(fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME) != null){
						paramName = fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME).getNodeValue();
					}
				}
			}
			catch(Exception ex){
				LOGGER.warn(String.format("error on parse flat parameter for \"%s\": %s",parent.getNodeName(),ex.toString()));
			}
		}
		
		return sb.length() == 0 ? null : formatParameter(paramName,sb.toString());
	}
	
	private void handleProtocolFragments() throws Exception{
		
		XPathExpression expression = _xpathXml.compile("./Fragments/ProtocolFragments");
		Node fragments = (Node) expression.evaluate(_rootXml, XPathConstants.NODE);
		if (fragments == null) {
			throw new Exception(String.format("\"%s/Fragments/ProtocolFragments\" element not found",
					_rootXml.getNodeName()));
		}
			
		NodeList childs = fragments.getChildNodes();
		int childCount = 0;
		for (int i = 0; i< childs.getLength(); i++) {
			Node child = childs.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				if(!child.hasAttributes()){
					throw new Exception(String.format("attributes not found. node = %s",child.getNodeName()));
				}
				Node attrName = child.getAttributes().getNamedItem("name");
				if(attrName == null){
					throw new Exception(String.format("attribute \"name\" not found. node = %s",child.getNodeName()));
				}
				
				if(childCount == 0){
					if(!_hasGlobalSection){
						writeNewLine();
					}
				}
				else{
					writeNewLine();
				}
				String sectionName = getFragmentName(Fragment.protocolFragment,child);
				String section = "["+sectionName+"]";
				writeLine(section);
				LOGGER.info(String.format("write %s",section));
				_countProtocolFragments++;
				
				XPathExpression ex = _xpathSchema.compile(String.format("./xs:element[@name='%s']/xs:annotation/xs:appinfo/FlatParameter",
							child.getNodeName()));
				NodeList flatParameters = (NodeList)ex.evaluate(_rootSchema,XPathConstants.NODESET);
				if(flatParameters != null){
					for (int j = 0; j < flatParameters.getLength(); j++) {
						Node fp = flatParameters.item(j);
						if(fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME) != null && fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_VALUE) != null){
							String param = formatParameter(fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME).getNodeValue(),fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_VALUE).getNodeValue());
							writeLine(param);
							if(j == 0){
								writeNewLine();
							}
						}
					}
				}
				
				String prefix = "";
				if(child.getNodeName().toLowerCase().startsWith("jump")){
					prefix = "jump_";
				}
				
				handleProtocolFragmentsChildNodes(child,sectionName,0,0,prefix,"");
				childCount++;
			}
		}
	}
		
	private void handleCredentialStoreFragments() throws Exception{
		
		XPathExpression expression = _xpathXml.compile("./Fragments/CredentialStoreFragments");
		Node fragments = (Node) expression.evaluate(_rootXml, XPathConstants.NODE);
		if (fragments == null) {
			return;
		}
			
		NodeList childs = fragments.getChildNodes();
		for (int i = 0; i< childs.getLength(); i++) {
			Node child = childs.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				if(!child.hasAttributes()){
					throw new Exception(String.format("attributes not found. node = %s",child.getNodeName()));
				}
				Node attrName = child.getAttributes().getNamedItem("name");
				if(attrName == null){
					throw new Exception(String.format("attribute \"name\" not found. node = %s",child.getNodeName()));
				}
				
				writeNewLine();
				String sectionName = getFragmentName(Fragment.credentialStoreFragment,child);
				String section = "["+sectionName+"]";
				writeLine(section);
				LOGGER.info(String.format("write %s",section));
				_countCredentialStoreFragments++;
				
				XPathExpression ex = _xpathSchema.compile(String.format("./xs:element[@name='%s']/xs:annotation/xs:appinfo/FlatParameter",
							child.getNodeName()));
				NodeList flatParameters = (NodeList)ex.evaluate(_rootSchema,XPathConstants.NODESET);
				if(flatParameters != null){
					for (int j = 0; j < flatParameters.getLength(); j++) {
						Node fp = flatParameters.item(j);
						if(fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME) != null && fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_VALUE) != null){
							String param = formatParameter(fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME).getNodeValue(),fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_VALUE).getNodeValue());
							writeLine(param);
							if(j == 0){
								writeNewLine();
							}
						}
					}
				}
								
				handleChildNodes(child.getNodeName(), child,0);
			}
		}
	}
		
	private void handleNotificationBackgroundServiceFragments() throws Exception{
		
		XPathExpression expression = _xpathXml.compile("./Fragments/NotificationFragments/BackgroundServiceFragment");
		NodeList fragments = (NodeList) expression.evaluate(_rootXml, XPathConstants.NODESET);
		if (fragments == null) {
			return;
		}
			
		for (int i = 0; i< fragments.getLength(); i++) {
			Node child = fragments.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				if(!child.hasAttributes()){
					throw new Exception(String.format("attributes not found. node = %s",child.getNodeName()));
				}
				Node attrName = child.getAttributes().getNamedItem("name");
				if(attrName == null){
					throw new Exception(String.format("attribute \"name\" not found. node = %s",child.getNodeName()));
				}
				
				writeNewLine();
				String sectionName = getFragmentName(Fragment.backgroundServiceFragment,child);
				String section = "["+sectionName+"]";
				writeLine(section);
				LOGGER.info(String.format("write %s",section));
				_countNotificationBackgroundServiceFragments ++;
				
				writeMainFlatParameters(child);
								
				handleChildNodes(child.getNodeName(), child,0);
			}
		}
	}
	
	private void writeMainFlatParameters(Node child) throws Exception{
		XPathExpression ex = _xpathSchema.compile(String.format("./xs:element[@name='%s']/xs:annotation/xs:appinfo/FlatParameter",
				child.getNodeName()));
		
		NodeList params = (NodeList)ex.evaluate(_rootSchema,XPathConstants.NODESET);
		if(params != null){
			for (int i = 0; i < params.getLength(); i++) {
				Node fp = params.item(i);
				if(fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME) != null && fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_VALUE) != null){
					String param = formatParameter(fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME).getNodeValue(),fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_VALUE).getNodeValue());
					writeLine(param);
					if(i == 0){
						writeNewLine();
					}
				}
			}
		}
	}
	
	private void handleNotificationMailFragments() throws Exception{
		
		XPathExpression expression = _xpathXml.compile("./Fragments/NotificationFragments/MailFragment");
		NodeList fragments = (NodeList) expression.evaluate(_rootXml, XPathConstants.NODESET);
		if (fragments == null) {
			return;
		}
			
		NodeList childs = fragments;
		for (int i = 0; i< childs.getLength(); i++) {
			Node child = childs.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				if(!child.hasAttributes()){
					throw new Exception(String.format("attributes not found. node = %s",child.getNodeName()));
				}
				Node attrName = child.getAttributes().getNamedItem("name");
				if(attrName == null){
					throw new Exception(String.format("attribute \"name\" not found. node = %s",child.getNodeName()));
				}
				
				String mailFragment = child.getAttributes().getNamedItem("name").getNodeValue();
				LOGGER.info(String.format("found Notification MailFragment \"%s\"",mailFragment));
				_countNotificationMailFragments++;
				LinkedHashMap<String,String> mailFragmentParams = new LinkedHashMap<String, String>();
				
				XPathExpression ex = _xpathSchema.compile(String.format("./xs:element[@name='%s']/xs:annotation/xs:appinfo/FlatParameter",
							child.getNodeName()));
				NodeList flatParameters = (NodeList)ex.evaluate(_rootSchema,XPathConstants.NODESET);
				if(flatParameters != null){
					for (int j = 0; j < flatParameters.getLength(); j++) {
						Node fp = flatParameters.item(j);
						
						if(fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME) != null && fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_VALUE) != null){
							mailFragmentParams.put(fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME).getNodeValue(),fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_VALUE).getNodeValue());
						}
					}
				}
				
				mailFragmentParams = handleNotificationMailFragmentsChildNodes(child,mailFragmentParams,0);
				
				_mailFragments.put(mailFragment, mailFragmentParams);
			}
		}
	}

	private void handleMailServerFragments() throws Exception{
		XPathExpression expression = _xpathXml.compile("./Fragments/MailServerFragments");
		Node fragments = (Node) expression.evaluate(_rootXml, XPathConstants.NODE);
		if (fragments == null) {
			return;
		}
			
		NodeList childs = fragments.getChildNodes();
		for (int i = 0; i< childs.getLength(); i++) {
			Node child = childs.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				if(!child.hasAttributes()){
					throw new Exception(String.format("attributes not found. node = %s",child.getNodeName()));
				}
				Node attrName = child.getAttributes().getNamedItem("name");
				if(attrName == null){
					throw new Exception(String.format("attribute \"name\" not found. node = %s",child.getNodeName()));
				}
				
				String mailFragment = child.getAttributes().getNamedItem("name").getNodeValue();
				LOGGER.info(String.format("found MailServer Fragment \"%s\"",mailFragment));
				_countMailServerFragments++;
				LinkedHashMap<String,String> mailFragmentParams = new LinkedHashMap<String, String>();
				
				XPathExpression ex = _xpathSchema.compile(String.format("./xs:element[@name='%s']/xs:annotation/xs:appinfo/FlatParameter",
							child.getNodeName()));
				NodeList flatParameters = (NodeList)ex.evaluate(_rootSchema,XPathConstants.NODESET);
				if(flatParameters != null){
					for (int j = 0; j < flatParameters.getLength(); j++) {
						Node fp = flatParameters.item(j);
						
						if(fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME) != null && fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_VALUE) != null){
							mailFragmentParams.put(fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME).getNodeValue(),fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_VALUE).getNodeValue());
						}
					}
				}
				
				mailFragmentParams = handleNotificationMailFragmentsChildNodes(child,mailFragmentParams,0);
				_mailServerFragments.put(mailFragment, mailFragmentParams);
			}
		}
	}

	private void handleProfiles() throws Exception{
		
		XPathExpression expression = _xpathXml.compile("./Profiles/Profile");
		NodeList profiles = (NodeList) expression.evaluate(_rootXml, XPathConstants.NODESET);
		if (profiles == null) {
			throw new Exception(String.format("\"%s/Profiles/Profile\" elements not found",
					_rootXml.getNodeName()));
		}
		
		for (int i = 0; i< profiles.getLength(); i++) {
			Node profile = profiles.item(i);
			
			if(!profile.hasAttributes()){
				throw new Exception(String.format("attributes not found. node = %s",profile.getNodeName()));
			}
			Node attrProfileId = profile.getAttributes().getNamedItem("profile_id");
			if(attrProfileId == null){
				throw new Exception(String.format("attribute \"profile_id\" not found. node = %s",profile.getNodeName()));
			}
			
			writeNewLine();
			String sectionName = getProfileName(profile,attrProfileId);
			String section = "["+sectionName+"]"; 
			writeLine(section);
			LOGGER.info(String.format("write %s",section));
			_countProfiles++;
			_profileJumpInclude = null;
			
			NodeList childs = profile.getChildNodes();
			for (int j = 0; j< childs.getLength(); j++) {
				Node child = childs.item(j);
				if (child.getNodeType() == Node.ELEMENT_NODE) {
					if(child.getNodeName().toLowerCase().equals("operation")){
						handleProfileOperation(child,sectionName,"",0,0,"","");
						handleProfileAlternativeOperation(child,sectionName);
					}
					else if(child.getNodeName().toLowerCase().equals("client")){
						writeNewLine();
						handleChildNodes(child.getNodeName(), child,0);
					}
					else if(child.getNodeName().toLowerCase().equals("logging")){
						writeNewLine();
						handleChildNodes(child.getNodeName(), child,0);
					}
					else if(child.getNodeName().toLowerCase().equals("jobscheduler")){
						writeNewLine();
						handleChildNodes(child.getNodeName(), child,0);
					}
					else if(child.getNodeName().toLowerCase().equals("notifications")){
						writeNewLine();
						handleProfileNotification(child.getNodeName(), child,0);
					}
					else if(child.getNodeName().toLowerCase().equals("notificationtriggers")){
						writeNewLine();
						handleProfileNotification(child.getNodeName(), child,0);
					}
				}
			}
		}
	}
	
	private void handleProfileAlternativeOperation(Node operationNode, String sectionName) throws Exception{
		
		String xpath = String.format(".//*[contains(local-name(),'Alternative')]");
		XPathExpression ex = _xpathXml.compile(xpath);
		NodeList nodes = (NodeList)ex.evaluate(operationNode,XPathConstants.NODESET);
		if(nodes != null){
			for(int i=0;i< nodes.getLength();i++){
				writeNewLine();
				
				Node alternative = nodes.item(i);
				String prefix = alternative.getNodeName().contains("Target") ? "target_" : "source_";
				
				xpath = String.format(".//*[1]");
				ex = _xpathXml.compile(xpath);
				Node firstChild = (Node)ex.evaluate(alternative,XPathConstants.NODE);
				if(firstChild == null){
					LOGGER.warn(String.format("\"%s\": first child not found on \"%s\" element",sectionName,alternative.getNodeName()));
					_countWarnings++;
					continue;
				}
				
				if(firstChild.getAttributes() != null && firstChild.getAttributes().getNamedItem("ref") != null){
					//JumpFragmentRef -> JumpFragment
					String elementName = firstChild.getNodeName().substring(0,firstChild.getNodeName().length()-3);
					String refName = firstChild.getAttributes().getNamedItem("ref").getNodeValue();
					xpath = String.format("./Fragments/ProtocolFragments/%s[@name='%s']",elementName,refName);
					ex = _xpathXml.compile(xpath);
					Node protocolFragment = (Node)ex.evaluate(_rootXml,XPathConstants.NODE);
					if(protocolFragment == null){
						LOGGER.warn(String.format("\"%s\": not found Protocol Fragment \"%s\" for %s",sectionName,xpath,alternative.getNodeName()));
						_countWarnings++;
						writeLine(formatParameter("alternative_"+prefix+"include",refName));
					}
					else{
						//handleAlternativeProtocolFragments(protocolFragment, 0,0,"alternative_"+prefix,"");
						writeLine(formatParameter("alternative_"+prefix+"include", getFragmentName(Fragment.protocolFragment,protocolFragment)));
					}
					_countAlternativeFragments++;
				}
				handleAlternativeProtocolFragments(alternative, 0,0,"alternative_"+prefix,"");
			}
			
		}
	}
	
	private void handleChildNodes(String parentNodeName,Node xmlNode,int level) throws Exception{
		if(xmlNode.hasChildNodes()){
			level++;
			
			NodeList childs = xmlNode.getChildNodes();
			for (int i = 0; i< childs.getLength(); i++) {
				Node child = childs.item(i);
				if (child.getNodeType() == Node.ELEMENT_NODE) {
					if(child.getAttributes().getNamedItem("ref") != null){
						//
						continue;
					}
					String xpath = String.format("./xs:element[@name='%s']/xs:annotation/xs:appinfo/FlatParameter[1]",child.getNodeName());
					
					XPathExpression ex = _xpathSchema.compile(xpath);
					Node fp = (Node)ex.evaluate(_rootSchema,XPathConstants.NODE);
					if(fp != null){
						if(fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME) != null){
							String name = fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME).getNodeValue();
							String value = getParameterValue(fp,child);
							writeLine(formatParameter(name, value));
						}
					}
					handleChildNodes(parentNodeName, child,level);
				}
			}
		}
	}
	
	private void writeNotification2Profile(String parentNodeName,LinkedHashMap<String,String> values) throws Exception{
		String prefix = "";
		if(parentNodeName.equals("OnSuccess")){
			prefix = "mail_on_success_";
		}
		else if(parentNodeName.equals("OnError")){
			prefix = "mail_on_error_";
		}
		else if(parentNodeName.equals("OnEmptyFiles")){
			prefix = "mail_on_empty_files_";
		}
		
		for(Entry<String, String> entry : values.entrySet()){
			writeLine(formatParameter(prefix+entry.getKey(),entry.getValue()));
		}
		//writeNewLine();
	}
	
	private void writeParams(LinkedHashMap<String,String> values) throws Exception{
		for(Entry<String, String> entry : values.entrySet()){
			writeLine(formatParameter(entry.getKey(),entry.getValue()));
		}
	}
	
	private void handleProfileNotification(String parentNodeName,Node xmlNode,int level) throws Exception{
		if(xmlNode.hasChildNodes()){
			level++;
			
			NodeList childs = xmlNode.getChildNodes();
			for (int i = 0; i< childs.getLength(); i++) {
				Node child = childs.item(i);
				if (child.getNodeType() == Node.ELEMENT_NODE) {
					if(child.getAttributes().getNamedItem("ref") != null){
						String refName = child.getAttributes().getNamedItem("ref").getNodeValue();
						if(child.getNodeName().equals("MailFragmentRef")){
							if(_mailFragments.containsKey(refName)){
								writeNotification2Profile(child.getParentNode().getNodeName(),_mailFragments.get(refName));
							}
						}
						else if(child.getNodeName().equals("MailServerFragmentRef")){
							if(_mailServerFragments.containsKey(refName)){
								writeParams(_mailServerFragments.get(refName));
							}
						}
						else if(child.getNodeName().equals("BackgroundServiceFragmentRef")){
							String include = getFragmentInclude(child,"");
							if(include != null){
								writeLine(include);
							}
						}
						continue;
					}
					String xpath = String.format("./xs:element[@name='%s']/xs:annotation/xs:appinfo/FlatParameter[1]",child.getNodeName());
					
					XPathExpression ex = _xpathSchema.compile(xpath);
					Node fp = (Node)ex.evaluate(_rootSchema,XPathConstants.NODE);
					if(fp != null){
						if(fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_MAIL_NAME) != null){
							String name = fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_MAIL_NAME).getNodeValue();
							String value = getParameterValue(fp,child);
							writeLine(formatParameter(name, value));
						}
						else if(fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME) != null){
							String name = fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME).getNodeValue();
							String value = getParameterValue(fp,child);
							writeLine(formatParameter(name, value));
						}
					}
					handleProfileNotification(parentNodeName, child,level);
				}
			}
		}
	}
	
	private void handleAlternativeProtocolFragments(Node xmlNode,
			int level,
			int prefixLevel,
			String parentPrefix,
			String childPrefix) throws Exception{
		
		if(level == 0){
			String xpathP = String.format("./xs:element[@name='%s']/xs:annotation/xs:appinfo/FlatParameter",xmlNode.getNodeName());
			XPathExpression exP = _xpathSchema.compile(xpathP);
			NodeList fpList = (NodeList)exP.evaluate(_rootSchema,XPathConstants.NODESET);
			if(fpList != null){
				for(int i=0;i< fpList.getLength();i++){
					Node fpP = fpList.item(i);
					if(fpP.getAttributes() == null){
						continue;
					}
					
					if(i > 0){
						if(fpP.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_VALUE) == null){
							continue;
						}
					}
					
					String nameP = null;
					if(fpP.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_ALTERNATIVE_NAME) != null){
						nameP = fpP.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_ALTERNATIVE_NAME).getNodeValue();
					}
					else if(fpP.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME) != null){
						nameP = fpP.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME).getNodeValue();
					}
		
					if(nameP != null){
						if(level <= prefixLevel){
							prefixLevel = 0;
							childPrefix = "";
						}
						if(childPrefix.length() > 0 && !nameP.toLowerCase().startsWith(childPrefix)){
							nameP = childPrefix+nameP;
						}
			
						if(parentPrefix.length() > 0 && !parentPrefix.equals("jump_") && !nameP.toLowerCase().startsWith(parentPrefix)){
							nameP = parentPrefix+nameP;
						}
						String valueP = getParameterValue(fpP,xmlNode);
				
						writeLine(formatParameter(nameP, valueP));
					}
				}
			}
		}
		
		if(xmlNode.hasChildNodes()){
			level++;
			
			NodeList childs = xmlNode.getChildNodes();
			for (int i = 0; i< childs.getLength(); i++) {
				Node child = childs.item(i);
				if (child.getNodeType() == Node.ELEMENT_NODE) {
					boolean hasRef = false;
					if(child.getAttributes().getNamedItem("ref") != null){
						hasRef = true;
					}
					if(!hasRef){
						String xpath = String.format("./xs:element[@name='%s']/xs:annotation/xs:appinfo/FlatParameter",child.getNodeName());
						XPathExpression ex = _xpathSchema.compile(xpath);
						NodeList fpList = (NodeList)ex.evaluate(_rootSchema,XPathConstants.NODESET);
						if(fpList != null){
							for(int j=0;j< fpList.getLength();j++){
								Node fp = fpList.item(j);
								if(fp.getAttributes() == null){
									continue;
								}
								
								if(j > 0){
									if(fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_VALUE) == null){
										continue;
									}
								}
							
								String name = null;
								if(fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_ALTERNATIVE_NAME) != null){
									name = fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_ALTERNATIVE_NAME).getNodeValue();
								}
								else if(fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME) != null){
									name = fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME).getNodeValue();
								}
						
								if(name != null){
									if(level <= prefixLevel){
										prefixLevel = 0;
										childPrefix = "";
									}
									if(childPrefix.length() > 0 && !name.toLowerCase().startsWith(childPrefix)){
										name = childPrefix+name;
									}
							
									if(parentPrefix.length() > 0 && !parentPrefix.equals("jump_") && !name.toLowerCase().startsWith(parentPrefix)){
										name = parentPrefix+name;
									}
									String value = getParameterValue(fp,child);
								
									writeLine(formatParameter(name, value));
								}
							}
						}
					}
					if(child.getNodeName().toLowerCase().startsWith("proxy")){
						childPrefix = "proxy_";
						prefixLevel = level;
					}					
					handleAlternativeProtocolFragments(child,level,prefixLevel,parentPrefix,childPrefix);
				}
			}
		}
	}
	
	private void handleProtocolFragmentsChildNodes(Node xmlNode,
			String sectionName,
			int level,
			int prefixLevel,
			String parentPrefix,
			String childPrefix) throws Exception{
		
		if(xmlNode.hasChildNodes()){
			level++;
			
			NodeList childs = xmlNode.getChildNodes();
			for (int i = 0; i< childs.getLength(); i++) {
				Node child = childs.item(i);
				if (child.getNodeType() == Node.ELEMENT_NODE) {
					
					if(child.getNodeName().equals("SystemPropertyFiles")){
						String val = getMergedChildsEntry(child,"system_property_files");
						if(!SOSString.isEmpty(val)){
							writeLine(val);
						}
						continue;
					}
					else if(child.getNodeName().equals("ConfigurationFiles")){
						String val = getMergedChildsEntry(child,"configuration_files");
						if(!SOSString.isEmpty(val)){
							writeLine(val);
						}
						continue;
					}
					
					if(child.getAttributes().getNamedItem("ref") != null){
						//System.out.println(child.getNodeName()+" = "+child.getAttributes().getNamedItem("ref").getNodeValue());
						if(child.getNodeName().equals("JumpFragmentRef")){
							String include = getFragmentInclude(child,"jump_");
							_jumpIncludes.put(sectionName,include);
						}
						else if(child.getNodeName().equals("CredentialStoreFragmentRef")){
							String include = getFragmentInclude(child,"");
							_credentialStoreIncludes.put(sectionName,include);
						}
						else{
							String include = getFragmentInclude(child,"");
							if(include != null){
								writeNewLine();
								writeLine(include);
								writeNewLine();
							}
						}
						continue;
					}
					String xpath = String.format("./xs:element[@name='%s']/xs:annotation/xs:appinfo/FlatParameter[1]",child.getNodeName());
					
					XPathExpression ex = _xpathSchema.compile(xpath);
					Node fp = (Node)ex.evaluate(_rootSchema,XPathConstants.NODE);
					if(fp != null){
						
						if(fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME) != null){
							
							if(level <= prefixLevel){
								prefixLevel = 0;
								childPrefix = "";
							}
														
							String name = fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME).getNodeValue();
							if(childPrefix.length() > 0 && !name.toLowerCase().startsWith(childPrefix)){
								name = childPrefix+name;
							}
							
							if(parentPrefix.length() > 0 && !parentPrefix.equals("jump_") && !name.toLowerCase().startsWith(parentPrefix)){
								name = parentPrefix+name;
							}
							String value = getParameterValue(fp,child);
							
							writeLine(formatParameter(name, value));
						}
						
					}
					if(child.getNodeName().toLowerCase().startsWith("proxy")){
						childPrefix = "proxy_";
						prefixLevel = level;
					}					
					handleProtocolFragmentsChildNodes(child,sectionName,level,prefixLevel,parentPrefix,childPrefix);
				}
			}
		}
	}
	
	private void handleProfileOperation(Node xmlNode,
			String sectionName,
			String operation,
			int level,
			int prefixLevel,
			String parentPrefix,
			String childPrefix) throws Exception{
		
		if(xmlNode.hasChildNodes()){
			level++;
			
			NodeList childs = xmlNode.getChildNodes();
			for (int i = 0; i< childs.getLength(); i++) {
				Node child = childs.item(i);
				if (child.getNodeType() == Node.ELEMENT_NODE) {
					
					if(child.getNodeName().startsWith("Alternative")){
						continue;
					}
					
					
					if(level <= prefixLevel){
						prefixLevel = 0;
						childPrefix = "";
						if(parentPrefix.length() == 0){
							writeNewLine();
						}
					}
					if(child.getAttributes().getNamedItem("ref") != null){
						
						String include = getFragmentInclude(child,childPrefix);
						if(include != null){
							String[] arr = include.split("=");
							if(arr.length == 2){
								//source_include = protocol_fragment_ftp@ftp_fragment,credentialstore_fragment@credential_store_fragmenthv
								String includeName = arr[1].trim().split(",")[0].trim();
								if(_jumpIncludes.containsKey(includeName)){
									String jumpInclude = _jumpIncludes.get(includeName); 
									if(operation.equals("copyfrominternet") || operation.equals("copytointernet")){
										if(_profileJumpInclude == null){
											writeLine(jumpInclude);
											writeNewLine();
											_profileJumpInclude = jumpInclude;
										}
										else{
											LOGGER.warn(String.format("Profile [%s]: include of \"%s\" skipped(\"%s\" already included)",sectionName,jumpInclude.replaceAll(" ",""),_profileJumpInclude.replaceAll(" ","")));
											_countWarnings++;
										}
									}
									else{
										LOGGER.warn(String.format("Profile [%s]: include of \"%s\" skipped(jump host with operation \"%s\" is not implemented)",sectionName,jumpInclude.replaceAll(" ",""),operation));
										_countWarnings++;
									}
								}
								if(_credentialStoreIncludes.containsKey(includeName)){
									String csInclude = _credentialStoreIncludes.get(includeName); 
									arr = csInclude.split("=");
									if(arr.length == 2){
										include+=","+arr[1].trim();
									}
									else{
										LOGGER.warn(String.format("Profile [%s]: CredentialStore fragment cannot be included. Missing \"=\" character in \"%s\"",sectionName,csInclude));
										_countWarnings++;
									}
								}
							}
							writeLine(include);
						}
					}
					String xpath = String.format("./xs:element[@name='%s']/xs:annotation/xs:appinfo/FlatParameter[1]",child.getNodeName());
					
					XPathExpression ex = _xpathSchema.compile(xpath);
					Node fp = (Node)ex.evaluate(_rootSchema,XPathConstants.NODE);
					if(fp != null){
						if(fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME) != null){
							String name = fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME).getNodeValue();
							Node suppressPrefix = fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_SUPPRESS_PREFIX);
							if(suppressPrefix != null && suppressPrefix.getNodeValue().equalsIgnoreCase("true")){
								
							}
							else{
								if(childPrefix.length() > 0 && !name.toLowerCase().startsWith(childPrefix)){
									name = childPrefix+name;
								}
								if(parentPrefix.length() > 0 && !name.toLowerCase().startsWith(parentPrefix)){
									name = parentPrefix+name;
								}
							}
							String value = getParameterValue(fp, child);
							StringBuffer addition = null;
							if(fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_VALUE) != null){
								// the next flat parameters after first.
								// e.g.: LocalSource
								//  <FlatParameter name="protocol" value="local"/>
								//  <FlatParameter name="host" value="localhost"/>
								addition = new StringBuffer();
								Node sibling = fp.getNextSibling();
								while (sibling != null) {
									
									if(sibling.getNodeType() == Node.ELEMENT_NODE && sibling.getAttributes() != null 
											&& sibling.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME) != null 
											&& sibling.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_VALUE) != null){
										String addName = sibling.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME).getNodeValue();
										if(childPrefix.length() > 0 && !addName.toLowerCase().startsWith(childPrefix)){
											addName = childPrefix+addName;
										}
										if(addition.length() > 0){
											addition.append(System.getProperty("line.separator"));
										}
										addition.append(formatParameter(addName, sibling.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_VALUE).getNodeValue()));
									}
									sibling = sibling.getNextSibling();
							     }
							}
							
							if(level == 1 && name.equals("operation")){
								value = getProfileOperationValue(child,sectionName,value);
								operation = value;
							}
							
							writeLine(formatParameter(name, value));
							if(addition != null && addition.length() > 0){
								writeLine(addition.toString());
							}
						}
					}
					
					if(child.getNodeName().toLowerCase().endsWith("source")){
						childPrefix = "source_";
						prefixLevel = level;
					}
					else if(child.getNodeName().toLowerCase().endsWith("target")){
						childPrefix = "target_";
						prefixLevel = level;
					}
					
					if(level == 1){
						writeNewLine();
					}
					
					handleProfileOperation(child,sectionName,operation,level,prefixLevel,parentPrefix,childPrefix);
				}
			}
		}
	}

	private String getProfileOperationValue(Node node,String sectionName,String value) throws Exception{
		if((value.equals("copy") || value.equals("move")) && _jumpIncludes.size() > 0){
			String xpath = String.format(".//*[contains(local-name(),'Source')]/*[string-length(@ref)!=0]");
			XPathExpression ex = _xpathXml.compile(xpath);
			Node fp = (Node)ex.evaluate(node,XPathConstants.NODE);
			if(fp != null){
				if(fp.getNodeName().equals("ReadableAlternativeFragmentRef") || 
						fp.getNodeName().equals("WriteadableAlternativeFragmentRef")){
					
					String altName = fp.getNodeName().substring(0,fp.getNodeName().length()-3);
					xpath = String.format("./Fragments/AlternativeFragments/%s[@name='%s']/*[string-length(@ref)!=0][1]",altName,fp.getAttributes().getNamedItem("ref").getNodeValue());
					
					ex = _xpathXml.compile(xpath);
					Node altFp = (Node)ex.evaluate(_rootXml,XPathConstants.NODE);
					if(altFp != null){
						fp = altFp;
					}
				}
				
				for(Entry<String, String> entry : _jumpIncludes.entrySet()){
					//protocol_fragment_https@https_fragment
					String[] arr = entry.getKey().split("@");
					if(arr.length > 1){
						String fragmentXmlName = arr[1].trim();
						if(fragmentXmlName.equals(fp.getAttributes().getNamedItem("ref").getNodeValue())){
							return "copyfrominternet";
						}
					}
				}
			}
				
			xpath = String.format(".//*[contains(local-name(),'Target')]/*[string-length(@ref)!=0]");
			ex = _xpathXml.compile(xpath);
			fp = (Node)ex.evaluate(node,XPathConstants.NODE);
			if(fp != null){
				if(fp.getNodeName().equals("ReadableAlternativeFragmentRef") || 
						fp.getNodeName().equals("WriteadableAlternativeFragmentRef")){
					
					String altName = fp.getNodeName().substring(0,fp.getNodeName().length()-3);
					xpath = String.format("./Fragments/AlternativeFragments/%s[@name='%s']/*[string-length(@ref)!=0][1]",altName,fp.getAttributes().getNamedItem("ref").getNodeValue());
					
					ex = _xpathXml.compile(xpath);
					Node altFp = (Node)ex.evaluate(_rootXml,XPathConstants.NODE);
					if(altFp != null){
						fp = altFp;
					}
				}
				
				for(Entry<String, String> entry : _jumpIncludes.entrySet()){
					//protocol_fragment_https@https_fragment
					String[] arr = entry.getKey().split("@");
					if(arr.length > 1){
						String fragmentXmlName = arr[1].trim();
						if(fragmentXmlName.equals(fp.getAttributes().getNamedItem("ref").getNodeValue())){
							return "copytointernet";
						}
					}
				}
			}
		}
		return value;
	}
	
	private LinkedHashMap<String,String> handleNotificationMailFragmentsChildNodes(Node xmlNode,LinkedHashMap<String,String> mailFragmentParams,int level) throws Exception{
		if(xmlNode.hasChildNodes()){
			level++;
			
			NodeList childs = xmlNode.getChildNodes();
			for (int i = 0; i< childs.getLength(); i++) {
				Node child = childs.item(i);
				if (child.getNodeType() == Node.ELEMENT_NODE) {
					String xpath = String.format("./xs:element[@name='%s']/xs:annotation/xs:appinfo/FlatParameter[1]",child.getNodeName());
					XPathExpression ex = _xpathSchema.compile(xpath);
					Node fp = (Node)ex.evaluate(_rootSchema,XPathConstants.NODE);
					if(fp != null){
						if(fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_MAIL_NAME) != null){
							mailFragmentParams.put(fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_MAIL_NAME).getNodeValue(), getParameterValue(fp,child));
						}
						else if(fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME) != null){
							mailFragmentParams.put(fp.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_NAME).getNodeValue(), getParameterValue(fp,child));
						}
					}
					mailFragmentParams = handleNotificationMailFragmentsChildNodes(child,mailFragmentParams,level);
				}
			}
		}
		return mailFragmentParams;
	}
	
	private String getFragmentName(Fragment fragment, Node node){
		StringBuffer sb = new StringBuffer();
		
		if(fragment.equals(Fragment.protocolFragment)){
			sb.append("protocol_fragment_");
			sb.append(node.getNodeName().toLowerCase().replace("fragment",""));
		}
		else if(fragment.equals(Fragment.credentialStoreFragment)){
			sb.append("credentialstore_fragment");
		}
		else if(fragment.equals(Fragment.backgroundServiceFragment)){
			sb.append("background_service_fragment");
		}
		sb.append("@");
		sb.append(node.getAttributes().getNamedItem("name").getNodeValue());
		return sb.toString();
	}
	
	private String getProfileName(Node node,Node attr){
		/**
		StringBuffer sb = new StringBuffer(node.getNodeName().toLowerCase());
		sb.append("@");
		sb.append(attr.getNodeValue());
		return sb.toString();*/
		return attr.getNodeValue();
	}
	
	private String getFragmentInclude(Node node,String prefix) throws Exception{
		String include = null;
		
		String ref = node.getAttributes().getNamedItem("ref").getNodeValue();
		//JumpFragmentRef -> JumpFragment
		String name = node.getNodeName().substring(0,node.getNodeName().length()-3);
		
		String fragments ="ProtocolFragments";
		Fragment fragment = Fragment.protocolFragment;
		
		if(node.getNodeName().equalsIgnoreCase("CredentialStoreFragmentRef")){
			fragments = "CredentialStoreFragments";
			fragment = Fragment.credentialStoreFragment;
		}
		else if(node.getNodeName().equalsIgnoreCase("BackgroundServiceFragmentRef")){
			fragments = "NotificationFragments";
			fragment = Fragment.backgroundServiceFragment;
		}
		
		String xpath = String.format("./Fragments/%s/%s[@name='%s']",fragments,name,ref);
		XPathExpression ex = _xpathXml.compile(xpath);
		Node fp = (Node)ex.evaluate(_rootXml,XPathConstants.NODE);
		if(fp == null){
			//not found
			include = formatParameter(prefix+"include",ref);
			LOGGER.warn(String.format("Node %s[@ref = %s]. Not found referenced Fragments/%s/%s[@name='%s'].", 
					node.getNodeName(),
					ref,
					fragments,
					name,
					ref));
			_countWarnings++;
		}
		else{
			include = formatParameter(prefix+"include",getFragmentName(fragment,fp));
		}
		return include;
	}
	
	private String getParameterValue(Node flatParameter,Node node){
		String value = "";
		if(flatParameter.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_VALUE) == null){
			if(node.getFirstChild() == null){
				//value = ", !!! Value from XML is NULL";
			}
			else{
				//Value from XML;
				value = node.getFirstChild().getNodeValue();
				Node oppositeValue = flatParameter.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_OPPOSITE_VALUE);
				if(oppositeValue != null && oppositeValue.getNodeValue().equals("true")){
					value = value.equalsIgnoreCase("true") ? "false" : "true";
				}
			}
	
		}
		else{
			//Value from Schema appInfo";
			value = flatParameter.getAttributes().getNamedItem(SCHEMA_ATTRIBUTE_VALUE).getNodeValue();
		}
		return value;
	}
	
	private String formatParameter(String name, String value){
		return String.format("%-35s = %s",name,value);
	}
	
	private void writeLine(String line) throws Exception{
		_writer.write(line);
		_writer.newLine();
	}
	
	private void writeNewLine() throws Exception{
		_writer.newLine();
	}
	
	private static void setLogger(String path) throws Exception{
		if(!SOSString.isEmpty(path)){
			File file = new File(path);
			if (file.isFile() && file.canRead()) {
				PropertyConfigurator.configure(file.getCanonicalPath());
			}
		}
		if( !Logger.getRootLogger().getAllAppenders().hasMoreElements() ) {
			BasicConfigurator.configure();
		}
		LOGGER = Logger.getRootLogger();
	}
	
	private NamespaceContext getSchemaNamespaceContext(){
		return new NamespaceContext() {
			
			@Override
			public Iterator<String> getPrefixes(String namespaceURI) {
				return null;
			}
			
			@Override
			public String getPrefix(String namespaceURI) {
				return "xs";
			}
			
			@Override
			public String getNamespaceURI(String prefix) {
				return "http://www.w3.org/2001/XMLSchema";
			}
		};
	}
}
