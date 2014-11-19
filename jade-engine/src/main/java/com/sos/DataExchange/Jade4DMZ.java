package com.sos.DataExchange;

import java.util.HashMap;
import java.util.Properties;
import java.util.UUID;

import org.apache.log4j.Logger;

import sos.net.ssh.SOSSSHJob2;
import sos.net.ssh.SOSSSHJobOptions;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.JSHelper.Exceptions.JobSchedulerException;
import com.sos.VirtualFileSystem.DataElements.SOSFileList;
import com.sos.VirtualFileSystem.DataElements.SOSFileListEntry;
import com.sos.VirtualFileSystem.Factory.VFSFactory;
import com.sos.VirtualFileSystem.Interfaces.ISOSVFSHandler;
import com.sos.VirtualFileSystem.Options.SOSConnection2OptionsAlternate;
import com.sos.i18n.annotation.I18NResourceBundle;

/**
* \class Jade4DMZ
*
* \brief Jade4DMZ -
*
* \details
*
*
* \code
*   .... code goes here ...
* \endcode
*
* <p style="text-align:center">
* <br />---------------------------------------------------------------------------
* <br /> APL/Software GmbH - Berlin
* <br />##### generated by ClaviusXPress (http://www.sos-berlin.com) #########
* <br />---------------------------------------------------------------------------
* </p>
* \author KB
* \version $Id$
* \see reference
*
* Created on 06.06.2012 16:15:21
 */

/**
 * @author KB
 *
 */
@I18NResourceBundle(baseName = "SOSDataExchange", defaultLocale = "en")
public class Jade4DMZ extends  JadeBaseEngine implements Runnable {

	@SuppressWarnings("unused")
	private final String		conClassName				= "Jade4DMZ";
	private static final String	conSVNVersion				= "$Id$";
	private static final Logger	logger						= Logger.getLogger(Jade4DMZ.class);

//	private JADEOptions		objOptions					= null;
	private final SOSFileList	objSourceFileList			= null;

	private SOSSSHJob2			objM						= null;
	private final ISOSVFSHandler	objVFS						= null;
	protected SOSSSHJobOptions	objO						= null;
	protected String			strTempFolderNameOnDMZ		= null;

	private boolean				CreateTempFolderOnDMZ		= false;
	private boolean				StartTransferToDMZ			= false;
	private final boolean		StartTransferFromDMZ2Inet	= false;
	private boolean				StartTransferFromDMZ2Intranet	= false;
	private final boolean		RemoveFilesOnDMZ			= false;
	private boolean				RemoveTempFolderOnDMZ		= false;
	private boolean				RemoveFilesOnIntranet		= false;
	private boolean 		    RemoveFilesOnInet = false;
	
	private SOSFileList lstFilesTransferredFromIntranet = null;
	private long lngNoOfFilesTransferredFromIntranet = -1;
	private final SOSFileList lstFilesTransferredFromDMZ2Intranet = null;
	private final long lngNoOfFilesTransferredFromDMZ2Intranet = -1;
	private SOSFileList		transfFiles								= null;

	public Jade4DMZ() {
		//super("SOSDataExchange");
		init();
		//
	}

	/**
	 * @param settingsFile
	 * @param settingsSection
	 * @param logger
	 * @param arguments_
	 */
	public Jade4DMZ(final Properties pobjProperties) throws Exception {
		this();
		this.Options();
		// TODO Properties in die OptionsClasse weiterreichen
		// objOptions.setAllOptions(pobjProperties);
	}

	public Jade4DMZ(final JADEOptions pobjOptions) throws Exception {
		this();
		objOptions = pobjOptions;
	}

	public Jade4DMZ(final HashMap<String, String> pobjJSSettings) throws Exception {
		this();
		this.Options().setAllOptions(pobjJSSettings);
	}

	@Override
	public JADEOptions Options() {
		if (objOptions == null) {
			objOptions = new JADEOptions();
		}
		return objOptions;
	}

	public SOSSSHJobOptions SSHOptions() {
		if (objO == null) {
			objO = objM.Options();
		}
		return objO;
	}

	private void init() {

		@SuppressWarnings("unused")
		final String conMethodName = conClassName + "::init";

		// TODO �ber Option steuern
		/**
		 * Trilead ist leider nicht mulithreading-faehig und ausserdem extrem langsam
		 * JCraft kann threading ohne probleme
		 */
		VFSFactory.sFTPHandlerClassName = "com.sos.VirtualFileSystem.SFTP.SOSVfsSFtpJCraft";

		logger.info(conClassName + " --- " + conSVNVersion);
		objM = new SOSSSHJob2();
		objM.keepConnected = true;
		objO = objM.Options();

		this.Options();

	} // private void init

	@Override
	public void run() {
		@SuppressWarnings("unused")
		final String conMethodName = conClassName + "::Run";
		try {
			this.Execute();
		}
		catch (Exception e) {
			throw new JobSchedulerException("abort", e);
		}
	}

	public boolean Execute() {
		@SuppressWarnings("unused")
		final String conMethodName = conClassName + "::execute";
		boolean flgOK = true;

		objOptions.getTextProperties().put("version", conSVNVersion);

		CheckJumpSettings();
		EstablishSSHConnection();

		try {
			CreateTempFolderOnDMZ();

			if (objOptions.operation.isOperationCopyToInternet() || objOptions.operation.isOperationSendUsingDMZ()) { // Intranet via DMZ to Internet
				//oh: 2014-1-19 delete try/catch because of
				//https://change.sos-berlin.com/browse/JADE-224
				//https://change.sos-berlin.com/browse/JADE-225
//				try {
					StartTransferFromIntranet2DMZ();
					StartTransferFromDMZ2Inet();
 					RemoveFilesOnIntranet();
//				}
//				catch (Exception e) {
//					e.printStackTrace();
//					throw e;
//				}
			}
			else { // Internet via DMZ to Internet
				if (objOptions.operation.isOperationCopyFromInternet() || objOptions.operation.isOperationReceiveUsingDMZ()) {
 					StartTransferFromInet2DMZ();
					StartTransferFromDMZ2Intranet();
					RemoveFilesFromINet();
				}
				else {
					throw new JobSchedulerException(Messages.getMsg("Jade4DMZ-E-001"));
					// "jump host" / DMZ nur mit isOperationSendUsingDMZ oder isOperationReceiveUsingDMZ m�glich
				}
			}
		}
		catch (Exception e) {
//			e.printStackTrace();
			flgOK = false;
			//oh: 2014-1-19 throw exception because of
			//https://change.sos-berlin.com/browse/JADE-224
			//https://change.sos-berlin.com/browse/JADE-225
			throw e;
		}
		finally {
			RemoveFilesOnDMZ();
			RemoveTempFolderOnDMZ();
			objM.DisConnect();
		}
		return flgOK;
	}

	private void RemoveFilesFromINet() {

		if (objOptions.remove_files.value() == true) {
//			JSFile fleFileList = new JSFile.get;
//			lstFilesTransferredFromDMZ2Intranet.Write2File();
			/**
			 * 1) File �bertragen ins temp-Verzeichnis
			 * 2) starten client auf dmz mit delete-option und parameter filelist=
			 */

		}
		RemoveFilesOnInet = true;
	}

	private void StartTransferFromDMZ2Intranet() {
		JADEOptions obj2DMZ = (JADEOptions) objOptions.getClone();
		
 		setDMZasSource4Receive(obj2DMZ);
		  
		try {
			JadeEngine objJade = new JadeEngine(obj2DMZ);
			objJade.Execute();
			transfFiles = objJade.getFileList(); 
			objJade.Logout();

			lstFilesTransferredFromIntranet = objJade.getFileList();
//			lngNoOfFilesTransferredFromIntranet = lstFilesTransferredFromIntranet.count();
			StartTransferFromDMZ2Intranet = true;
		}
		catch (Exception e) {
			throw new JobSchedulerException("Transfer failed", e);
		}

	} 

	
	private void StartTransferFromInet2DMZ() {

		JADEOptions obj2DMZ = (JADEOptions) objOptions.getClone();

		SOSConnection2OptionsAlternate objSource = objOptions.Source();
		String strD = objSource.getOptionsAsCommandLine();

		logger.debug(obj2DMZ.DirtyString());
		setDMZasTarget4Receive(obj2DMZ);
		String strC = obj2DMZ.getOptionsAsCommandLine();

		strC += " -log_filename=" + getUniqueFileName("log");
		strC += " -createResultSet=true -ResultSetFileName=" + getUniqueFileName("fls");
		strC += " -target_protocol=local";
		
		String command = objOptions.jump_command.Value() + " " + strC + " " + strD;
		logger.debug(String.format("Command on DMZ: %s",command));
 		executeSSHCommand(command);
	}

	private void CreateTempFolderOnDMZ() {

		final String conMethodName = conClassName + "::CreateTempFolderOnDMZ";
		logger.trace(conMethodName);

		try {
			strTempFolderNameOnDMZ = getUniqueFileName("");
			executeSSHCommand("mkdir " + strTempFolderNameOnDMZ);
		}
		catch (Exception e) {
			throw new JobSchedulerException("transfer failed", e);
		}
		CreateTempFolderOnDMZ = true;
	} // private void CreateTempFolderOnDMZ

	private String getUniqueFileName(final String pstrFileNameExtension) {

		@SuppressWarnings("unused")
		final String	conMethodName	= conClassName + "::getUniqueFileName";

		String strUUID = UUID.randomUUID().toString();
		String strE = "";
		if (pstrFileNameExtension.length() > 0) {
			strE = "." + pstrFileNameExtension;
		}
		String strLogFileName = "/tmp/jade-" + strUUID + strE;

		return strLogFileName;
	} // private String getUniqueFileName

	private void StartTransferFromIntranet2DMZ() {

		@SuppressWarnings("unused")
		final String conMethodName = conClassName + "::StartTransferToDMZ";
		logger.trace(conMethodName);

		JADEOptions obj2DMZ = (JADEOptions) objOptions.getClone();
		setDMZasTarget(obj2DMZ);
        logger.debug(obj2DMZ.Target().host.Value());
		logger.debug(obj2DMZ.DirtyString());
		try {
			JadeEngine objJade = new JadeEngine(obj2DMZ);
			objJade.Execute();
			transfFiles = objJade.getFileList(); 
			objJade.Logout();
			lstFilesTransferredFromIntranet = objJade.getFileList();
			lngNoOfFilesTransferredFromIntranet = lstFilesTransferredFromIntranet.count();
			StartTransferToDMZ = true;
		}
		catch (Exception e) {
			throw new JobSchedulerException("Transfer failed", e);
		}
	} // private void StartTransferToDMZ

	private void setDMZasTarget4Receive(final JADEOptions objDMZOptions) {

		@SuppressWarnings("unused")
		final String conMethodName = conClassName + "::setDMZasTarget";

		objDMZOptions.operation.Value("copy");
		objDMZOptions.transactional.value(true);
		objDMZOptions.remove_files.value(false);   // delete on source platform at the end of the overall transfer
		objDMZOptions.port.Value(null);
		objDMZOptions.protocol.Value("");
		
		objDMZOptions.replacement.Value(null);
		objDMZOptions.scheduler_host.Value("");
		objDMZOptions.replacing.Value(null);
		objDMZOptions.atomic_prefix.Value("");
		objDMZOptions.atomic_suffix.Value("");
		objDMZOptions.settings.Value("");
		//oh 2014-10-30, add setNotDirty() otherwise jump reads settings file (https://change.sos-berlin.com/browse/SOSFTP-219) 
		objDMZOptions.settings.setNotDirty();
		objDMZOptions.profile.Value("");
		objDMZOptions.TargetDir.Value(strTempFolderNameOnDMZ);
		objDMZOptions.ClearJumpParameter();
	
	} // private void setDMZasTarget
	

	private void setDMZasTarget(final JADEOptions objDMZOptions) {

		@SuppressWarnings("unused")
		final String conMethodName = conClassName + "::setDMZasTarget";

		objDMZOptions.operation.Value("copy");
		objDMZOptions.transactional.value(true);
		
		
		objDMZOptions.remove_files.value(false);   // delete on source platform at the end of the overall transfer

		 
		objDMZOptions.Target().replacement.Value(null);
		objDMZOptions.Target().replacing.Value(null);

		//Jump Parameter als Target setzen
		objDMZOptions.TargetDir.Value(strTempFolderNameOnDMZ);
		objDMZOptions.Target().Directory.Value(strTempFolderNameOnDMZ);
		objDMZOptions.Target().user.Value(objOptions.jump_user.Value());
		objDMZOptions.Target().password.Value(objOptions.jump_password.Value());
		objDMZOptions.Target().auth_method.Value(objOptions.jump_ssh_auth_method.Value());
		objDMZOptions.Target().auth_file.Value(objOptions.jump_ssh_auth_file.Value());
		objDMZOptions.Target().protocol.Value(objOptions.jump_protocol.Value());
		objDMZOptions.Target().host.Value(objOptions.jump_host.Value());
		//oh 2014-10-30, add port otherwise -> jump login failed 
		objDMZOptions.Target().port.Value(objOptions.jump_port.Value());
		
		//Change some other parameters.
		objDMZOptions.settings.Value("");
		//oh 2014-10-30, add setNotDirty() otherwise jump reads settings file (https://change.sos-berlin.com/browse/SOSFTP-219) 
		objDMZOptions.settings.setNotDirty();
		objDMZOptions.profile.Value("");
		objDMZOptions.atomic_prefix.Value("");
		objDMZOptions.atomic_suffix.Value("");
		

	} // private void setDMZasTarget
	
	
	private void setDMZasSource4Receive(final JADEOptions objDMZOptions) {

		@SuppressWarnings("unused")
		final String conMethodName = conClassName + "::setDMZasSource4Receive";
  		
		objDMZOptions.operation.Value("copy");
		objDMZOptions.transactional.value(true);
		objDMZOptions.remove_files.value(false);   // delete on source platform at the end of the overall transfer

	 	
		//Jump Parameter als Target setzen
		objDMZOptions.SourceDir.Value(strTempFolderNameOnDMZ);
		objDMZOptions.Source().Directory.Value(strTempFolderNameOnDMZ);
		objDMZOptions.Source().user.Value(objOptions.jump_user.Value());
		objDMZOptions.Source().password.Value(objOptions.jump_password.Value());
		objDMZOptions.Source().auth_method.Value(objOptions.jump_ssh_auth_method.Value());
		objDMZOptions.Source().auth_file.Value(objOptions.jump_ssh_auth_file.Value());
		objDMZOptions.Source().protocol.Value(objOptions.jump_protocol.Value());
		objDMZOptions.Source().host.Value(objOptions.jump_host.Value());
		//oh 2014-10-30, add port otherwise -> jump login failed
		objDMZOptions.Source().port.Value(objOptions.jump_port.Value());
		
		//Change some other parameters.
		objDMZOptions.settings.Value("");
		//oh 2014-10-30, add setNotDirty() otherwise jump reads settings file (https://change.sos-berlin.com/browse/SOSFTP-219) 
		objDMZOptions.settings.setNotDirty();
		objDMZOptions.profile.Value("");
		objDMZOptions.atomic_prefix.Value("");
		objDMZOptions.atomic_suffix.Value("");
		objDMZOptions.ClearJumpParameter();
		

	} // private void setDMZasSource4Receive
	
	
	private void setDMZasSource(final JADEOptions objDMZOptions) {

		@SuppressWarnings("unused")
		final String conMethodName = conClassName + "::setDMZasSource";

		//Source ist local
		objDMZOptions.SourceDir.Value(strTempFolderNameOnDMZ);
		objDMZOptions.Source().protocol.Value("local");
		

		objDMZOptions.scheduler_host.Value("");
		objDMZOptions.settings.Value(null);
		//oh 2014-10-30, add setNotDirty() otherwise jump reads settings file (https://change.sos-berlin.com/browse/SOSFTP-219) 
		objDMZOptions.settings.setNotDirty();
		objDMZOptions.profile.Value(null);
		objDMZOptions.ClearJumpParameter();
		
		//objDMZOptions.local_dir.Value(strTempFolderNameOnDMZ);
		objDMZOptions.operation.Value("copy");

 		objDMZOptions.remove_files.value(false);

	} // private void setDMZasSource

	private void StartTransferFromDMZ2Inet() {

		final String conMethodName = conClassName + "::StartTransferFromDMZ2Inet";
		logger.trace(conMethodName);

		String strB = objOptions.getOptionsAsCommandLine();
		JADEOptions obj2Inet = new JADEOptions();
		//oh: 2014-1-19 delete try/catch because of
		//https://change.sos-berlin.com/browse/JADE-224
		//https://change.sos-berlin.com/browse/JADE-225
//		try {
			SOSConnection2OptionsAlternate objTarget = objOptions.Target();
			String strD = objTarget.getOptionsAsCommandLine();
			obj2Inet.CommandLineArgs(strB);
			obj2Inet.settings.Value(null);
			obj2Inet.profile.Value(null);

			obj2Inet.host.Value(objTarget.host.Value());
			if (obj2Inet.TargetDir.isDirty()) {
				obj2Inet.remote_dir.Value(obj2Inet.TargetDir.Value());
			}
			setDMZasSource(obj2Inet);
			String strE = "-source_protocol=local";//strD.replaceAll("target_", "");
			String strC = obj2Inet.getOptionsAsCommandLine();
			String strUUID = UUID.randomUUID().toString();
			String strLogFileName = "/tmp/jade-" + strUUID + ".log";
			strE += " -log_filename=" + strLogFileName;
			String command = objOptions.jump_command.Value() + " " + strC + " " + strD + " " + strE;
			logger.debug(String.format("Command on DMZ: %s",command));

			executeSSHCommand(command);
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
	} // private void StartTransferFromDMZ2Inet
	
	

	private void RemoveFilesOnDMZ() {

		@SuppressWarnings("unused")
		final String conMethodName = conClassName + "::RemoveFilesOnDMZ";
		logger.trace(conMethodName);

		if (StartTransferToDMZ == true) {
			// remove Files
		}
	} // private void RemoveFilesOnDMZ

	private void RemoveTempFolderOnDMZ() {

		final String conMethodName = conClassName + "::RemoveTempFolderOnDMZ";
		logger.trace(conMethodName);
		if (CreateTempFolderOnDMZ == true) {
			executeSSHCommand("rm -f -R " + strTempFolderNameOnDMZ);
			CreateTempFolderOnDMZ = false;
			RemoveTempFolderOnDMZ = true;
		}
	} // private void RemoveTempFolderOnDMZ

	private void executeSSHCommand(final String pstrCommand) {

		@SuppressWarnings("unused")
		final String conMethodName = conClassName + "::executeCommand";

		try {
			logger.debug(pstrCommand);
			//TODO: Log4j konfiguration
			//objO.RaiseExceptionOnError.Value("false");
			//oh: 2014-1-19 why raiseExceptionOnError = false 
			//https://change.sos-berlin.com/browse/JADE-224
			//https://change.sos-berlin.com/browse/JADE-225
			objO.RaiseExceptionOnError.value(true);
			objO.ignore_stderr.Value("true");
			objO.command.Value(pstrCommand);
			objM.Execute();

			String strStdOut = objM.getStdOut().toString();
			String strStdErr = objM.getStdErr().toString();
			objM.Clear();
		}
		catch (Exception e) {
			throw new JobSchedulerException("Transfer failed", e);
		}

	} // private void executeCommand

	private void RemoveFilesOnIntranet() {

		final String conMethodName = conClassName + "::RemoveFilesFromIntranet";
		logger.debug(conMethodName);

		if (objOptions.remove_files.value() == true) {
			for (SOSFileListEntry objFile : lstFilesTransferredFromIntranet.List()) {
				if (objFile.FileExists() == true) {
					objFile.DeleteSourceFile();
				}
			}
		}
		RemoveFilesOnIntranet = true;
	} // private void RemoveFilesFromIntranet

	private void CheckJumpSettings() {

		final String conMethodName = conClassName + "::CheckJumpSettings";
		logger.trace(conMethodName);

	} // private void CheckJumpSettings

	private void EstablishSSHConnection() {

		final String conMethodName = conClassName + "::EstablishSSHConnection";

		try {
			objO.auth_file = objOptions.getjump_ssh_auth_file();
			objO.auth_method = objOptions.getjump_ssh_auth_method();
			objO.host = objOptions.getjump_host();
			objO.user = objOptions.getjump_user();
			objO.password = objOptions.getjump_password();
			objO.port = objOptions.getjump_port();

			logger.info(objO.toString());

			// TODO: connect as method in SSHJob2

			objM.Connect();
		}
		catch (Exception e) {
			logger.error(conMethodName + ": " + "Error occured ..." + e.getMessage());
			throw new JobSchedulerException("Transfer failed", e);
		}
	} // private void EstablishSSHConnection
	
	public SOSFileList getFileList(){
		return transfFiles;
	}
 
 
	
}
