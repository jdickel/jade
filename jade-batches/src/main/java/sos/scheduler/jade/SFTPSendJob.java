package sos.scheduler.jade;

import com.sos.JSHelper.Basics.VersionInfo;
import com.sos.JSHelper.Options.SOSOptionJadeOperation;
import com.sos.JSHelper.Options.SOSOptionPortNumber;
import com.sos.JSHelper.Options.SOSOptionTransferType.enuTransferTypes;
import com.sos.VirtualFileSystem.Options.SOSConnection2Options;
import com.sos.i18n.annotation.I18NResourceBundle;
import org.apache.log4j.Logger;

/** \file SFTPSendJob.java \class SFTPSendJob
 *
 * \brief AdapterClass of SOSDEx for the SOSJobScheduler
 *
 * This Class SFTPSendJob works as an adapter-class between the SOS JobScheduler
 * and the worker-class SOSDEx.
 *
 * 
 *
 * see \see
 * J:\E\java\development\com.sos.scheduler\src\sos\scheduler\jobdoc\SOSDEx.xml
 * for more details.
 *
 * \verbatim ; mechanicaly created by
 * C:\Users\KB\eclipse\sos.scheduler.xsl\JSJobDoc2JSAdapterClass.xsl from
 * http://www.sos-berlin.com at 20100930175652 \endverbatim */
@I18NResourceBundle(baseName = "com.sos.scheduler.messages", defaultLocale = "en")
public class SFTPSendJob extends Jade4JessyBaseClass {

    private final String conSVNVersion = "$Id: SFTPSendJob.java 22360 2014-02-05 09:19:04Z oh $";
    @SuppressWarnings("unused")
    private final String conClassName = this.getClass().getSimpleName();
    private final Logger logger = Logger.getLogger(this.getClass());

    @Override
    protected void setSpecialOptions() {
        objO.operation.Value(SOSOptionJadeOperation.enuJadeOperations.send);
        objO.protocol.Value(enuTransferTypes.sftp);
        objO.port.value(SOSOptionPortNumber.getStandardSFTPPort());
        // objO.ssh_auth_method.Value(enuAuthenticationMethods.publicKey);
        SOSConnection2Options objConn = objO.getConnectionOptions();
        if (objConn != null) {
            objConn.Target().protocol.Value(enuTransferTypes.sftp);
            // objConn.Target().port.value(SOSOptionPortNumber.getStandardSFTPPort());
            // objConn.Target().ssh_auth_method.Value(enuAuthenticationMethods.publicKey);
        }
    }

    @Override
    protected void showVersionInfo() {
        logger.debug(VersionInfo.VERSION_STRING);
        logger.debug(conSVNVersion);
    }
}
