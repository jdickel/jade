package sos.scheduler.jade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sos.JSHelper.Basics.VersionInfo;
import com.sos.JSHelper.Options.SOSOptionJadeOperation;
import com.sos.JSHelper.Options.SOSOptionPortNumber;
import com.sos.JSHelper.Options.SOSOptionTransferType.enuTransferTypes;
import com.sos.VirtualFileSystem.Options.SOSConnection2Options;
import com.sos.i18n.annotation.I18NResourceBundle;

@I18NResourceBundle(baseName = "com.sos.scheduler.messages", defaultLocale = "en")
public class SFTPSendJob extends Jade4JessyBaseClass {

    private static final Logger LOGGER = LoggerFactory.getLogger(SFTPSendJob.class);

    @Override
    protected void setSpecialOptions() {
        objO.operation.setValue(SOSOptionJadeOperation.enuJadeOperations.send);
        objO.protocol.setValue(enuTransferTypes.sftp);
        objO.port.value(SOSOptionPortNumber.getStandardSFTPPort());
        SOSConnection2Options objConn = objO.getConnectionOptions();
        if (objConn != null) {
            objConn.getTarget().protocol.setValue(enuTransferTypes.sftp);
        }
    }

    @Override
    protected void showVersionInfo() {
        LOGGER.debug(VersionInfo.VERSION_STRING);
    }

}