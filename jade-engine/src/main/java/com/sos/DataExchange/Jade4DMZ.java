package com.sos.DataExchange;

import java.util.UUID;

import org.apache.log4j.Logger;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.JSHelper.Exceptions.JobSchedulerException;
import com.sos.VirtualFileSystem.DataElements.SOSFileList;
import com.sos.VirtualFileSystem.Options.SOSConnection2OptionsAlternate;
import com.sos.i18n.annotation.I18NResourceBundle;

/** @author Robert Ehrlich */
@I18NResourceBundle(baseName = "SOSDataExchange", defaultLocale = "en")
public class Jade4DMZ extends JadeBaseEngine implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(Jade4DMZ.class);
    private SOSFileList fileList = null;
    private String uuid = null;
    private String sourceListFilename = null;
    private String historyFilename = null;

    private enum Operation {
        copyToInternet, copyFromInternet
    }

    public void Execute() {
        String dir = normalizeDirectoryPath(getOptions().jumpDir.Value());
        String uuid = "jade-dmz-" + getUUID();
        String subDir = dir + uuid;
        Operation operation = null;
        try {
            if (objOptions.operation.isOperationCopyToInternet() || objOptions.operation.isOperationSendUsingDMZ()) {
                operation = Operation.copyToInternet;
            } else if (objOptions.operation.isOperationCopyFromInternet() || objOptions.operation.isOperationReceiveUsingDMZ()) {
                operation = Operation.copyFromInternet;
            } else {
                throw new JobSchedulerException(Messages.getMsg("Jade4DMZ-E-001"));
            }
            transfer(operation, subDir);
        } catch (JobSchedulerException e) {
            throw e;
        } catch (Exception e) {
            throw new JobSchedulerException("Transfer failed", e);
        }
    }

    /** @param operation
     * @param dir */
    private void transfer(Operation operation, String dir) {
        LOGGER.info(String.format("operation = %s, jump dir = %s", operation, dir));
        JadeEngine jade = null;
        fileList = null;
        try {
            jade = new JadeEngine(getTransferOptions(operation, dir));
            jade.Execute();
            if (operation.equals(Operation.copyFromInternet) && objOptions.removeFiles.value()) {
                jade.executeCommandOnSource(getJadeOnDMZCommand4RemoveSource());
            }
            fileList = jade.getFileList();
        } catch (Exception e) {
            throw new JobSchedulerException("Transfer failed", e);
        } finally {
            removeDirOnDMZ(jade, operation, dir);
            try {
                if (jade != null) {
                    jade.Logout();
                }
            } catch (Exception e) {
                LOGGER.warn(String.format("Logout failed: %s", e.toString()));
            }
        }
    }

    private SOSConnection2OptionsAlternate createJumpOptions(String dir) {
        SOSConnection2OptionsAlternate options = new SOSConnection2OptionsAlternate();
        options.protocol.Value("sftp");
        options.host.Value(objOptions.jumpHost.Value());
        options.port.Value(objOptions.jumpPort.Value());
        options.user.Value(objOptions.jumpUser.Value());
        options.password.Value(objOptions.jumpPassword.Value());
        options.sshAuthMethod.Value(objOptions.jumpSshAuthMethod.Value());
        options.sshAuthFile.Value(objOptions.jumpSshAuthFile.Value());
        options.strictHostKeyChecking.value(objOptions.jumpStrictHostkeyChecking.value());
        options.directory.Value(dir);
        return options;
    }

    private SOSConnection2OptionsAlternate setJumpProxy(SOSConnection2OptionsAlternate options) {
        options.proxyProtocol.Value(objOptions.jumpProxyProtocol.Value());
        options.proxyHost.Value(objOptions.jumpProxyHost.Value().trim());
        options.proxyPort.Value(objOptions.jumpProxyPort.Value().trim());
        options.proxyUser.Value(objOptions.jumpProxyUser.Value().trim());
        options.proxyPassword.Value(objOptions.jumpProxyPassword.Value());
        return options;
    }

    private JADEOptions createPostTransferOptions(String dir) {
        // From DMZ to Internet as PostTransferCommands
        JADEOptions options = new JADEOptions();
        options = addJADEOptionsForTarget(options);
        SOSConnection2OptionsAlternate sourceOptions = setLocalOptionsPrefixed("source_", dir);
        SOSConnection2OptionsAlternate targetOptions = objOptions.Target();
        options.getConnectionOptions().Source(sourceOptions);
        options.getConnectionOptions().Target(targetOptions);
        return options;
    }

    private JADEOptions createPreTransferOptions(String dir) {
        // From Internet to DMZ as PreTransferCommands
        JADEOptions options = new JADEOptions();
        options = addJADEOptionsForSource(options);
        SOSConnection2OptionsAlternate sourceOptions = objOptions.Source();
        SOSConnection2OptionsAlternate targetOptions = setLocalOptionsPrefixed("target_", dir);
        options.getConnectionOptions().Source(sourceOptions);
        options.getConnectionOptions().Target(targetOptions);
        // Remove source files at Internet as PostTransferCommands
        options.removeFiles.value(false);
        if (objOptions.removeFiles.value() || objOptions.resultSetFileName.isDirty()) {
            options.resultSetFileName.Value(getSourceListFilename());
        }
        return options;
    }

    /** Transfer from Intranet/Internet to DMZ
     * 
     * @param operation
     * @param dir
     * @return */
    private JADEOptions getTransferOptions(Operation operation, String dir) throws Exception {
        LOGGER.debug(String.format("operation = %s, jump dir = %s", operation, dir));
        JADEOptions options = null;
        JADEOptions jumpCommandOptions;
        SOSConnection2OptionsAlternate jumpOptions = createJumpOptions(dir);
        jumpOptions = setJumpProxy(jumpOptions);
        if (operation.equals(Operation.copyToInternet)) {
            options = createTransferToDMZOptions(dir);
            jumpCommandOptions = createPostTransferOptions(dir);
            jumpOptions.PostTransferCommands.Value(getJadeOnDMZCommand(jumpCommandOptions));
            jumpOptions = setDestinationOptionsPrefix("target_", jumpOptions);
            options.getConnectionOptions().Source(objOptions.Source());
            options.getConnectionOptions().Target(jumpOptions);
        } else {
            options = createTransferFromDMZOptions(dir);
            jumpCommandOptions = createPreTransferOptions(dir);
            jumpOptions.PreTransferCommands.Value(getJadeOnDMZCommand(jumpCommandOptions));
            jumpOptions = setDestinationOptionsPrefix("source_", jumpOptions);
            options.getConnectionOptions().Source(jumpOptions);
            options.getConnectionOptions().Target(objOptions.Target());
        }
        options.setDmzOption("operation", operation.name());
        options.setDmzOption("history", getHistoryFilename());
        options.setDmzOption("resultfile", getSourceListFilename());
        return options;
    }

    /** @param prefix
     * @param options
     * @return */
    private SOSConnection2OptionsAlternate setDestinationOptionsPrefix(String prefix, SOSConnection2OptionsAlternate options) {
        options.protocol.setPrefix(prefix);
        options.host.setPrefix(prefix);
        options.port.setPrefix(prefix);
        options.user.setPrefix(prefix);
        options.password.setPrefix(prefix);
        options.sshAuthMethod.setPrefix(prefix);
        options.sshAuthFile.setPrefix(prefix);
        options.proxyProtocol.setPrefix(prefix);
        options.proxyHost.setPrefix(prefix);
        options.proxyPort.setPrefix(prefix);
        options.proxyUser.setPrefix(prefix);
        options.proxyPassword.setPrefix(prefix);
        options.PreTransferCommands.setPrefix(prefix);
        options.PostTransferCommands.setPrefix(prefix);
        options.directory.setPrefix(prefix);

        return options;
    }

    /** @param prefix
     * @param dir
     * @return */
    private SOSConnection2OptionsAlternate setLocalOptionsPrefixed(String prefix, String dir) {
        SOSConnection2OptionsAlternate options = new SOSConnection2OptionsAlternate();
        options.protocol.Value("local");
        options.host.Value(objOptions.jumpHost.Value());
        options.directory.Value(dir);
        options.protocol.setPrefix(prefix);
        options.host.setPrefix(prefix);
        options.directory.setPrefix(prefix);
        return options;
    }

    /** Settings for receive from DMZ
     * 
     * @param dir
     * @return */
    private JADEOptions createTransferFromDMZOptions(String dir) {
        JADEOptions options = new JADEOptions();
        options = addJADEOptionsOnClient(options);
        options = addJADEOptionsForTarget(options);
        options.sourceDir.Value(dir);
        options.targetDir.Value(objOptions.Target().directory.Value());
        return options;
    }

    /** Transfer from Intranet/Internet to DMZ without changes
     * 
     * @param targetDir
     * @return */
    private JADEOptions createTransferToDMZOptions(String dir) {
        JADEOptions options = new JADEOptions();
        options = addJADEOptionsOnClient(options);
        options = addJADEOptionsForSource(options);
        options.sourceDir.Value(objOptions.Source().directory.Value());
        options.targetDir.Value(dir);
        options.removeFiles = objOptions.removeFiles;
        return options;
    }

    /** @param options
     * @return */
    private JADEOptions addJADEOptionsForSource(JADEOptions options) {
        options.operation.Value("copy");
        options.transactional.value(true);
        options.atomicPrefix = objOptions.atomicPrefix;
        options.atomicSuffix = objOptions.atomicSuffix;
        options.bufferSize = objOptions.bufferSize;
        options.checkSteadyCount = objOptions.checkSteadyCount;
        options.checkSteadyStateInterval = objOptions.checkSteadyStateInterval;
        options.checkSteadyStateOfFiles = objOptions.checkSteadyStateOfFiles;
        options.pollingWait4SourceFolder = objOptions.pollingWait4SourceFolder;
        options.pollingEndAt = objOptions.pollingEndAt;
        options.pollingServerPollForever = objOptions.pollingServerPollForever;
        options.pollingServerDuration = objOptions.pollingServerDuration;
        options.pollKeepConnection = objOptions.pollKeepConnection;
        options.pollInterval = objOptions.pollInterval;
        options.pollMinfiles = objOptions.pollMinfiles;
        options.pollingDuration = objOptions.pollingDuration;
        options.pollTimeout = objOptions.pollTimeout;
        options.maxFiles = objOptions.maxFiles;
        options.fileListName = objOptions.fileListName;
        options.filePath = objOptions.filePath;
        options.fileSpec = objOptions.fileSpec;
        options.forceFiles = objOptions.forceFiles;
        options.recursive = objOptions.recursive;
        options.skipTransfer = objOptions.skipTransfer;
        options.keepModificationDate = objOptions.keepModificationDate;
        options.verbose = objOptions.verbose;
        options.zeroByteTransfer = objOptions.zeroByteTransfer;
        options.checkIntegrityHash = objOptions.checkIntegrityHash;
        options.integrityHashType = objOptions.integrityHashType;

        return options;
    }

    /** @param options
     * @return */
    private JADEOptions addJADEOptionsForTarget(JADEOptions options) {
        options.operation.Value("copy");
        options.transactional.value(true);
        options.fileSpec.Value(".*");
        options.atomicPrefix = objOptions.atomicPrefix;
        options.atomicSuffix = objOptions.atomicSuffix;
        options.bufferSize = objOptions.bufferSize;
        options.makeDirs = objOptions.makeDirs;
        options.appendFiles = objOptions.appendFiles;
        options.checkInterval = objOptions.checkInterval;
        options.checkRetry = objOptions.checkRetry;
        options.checkSize = objOptions.checkSize;
        options.forceFiles = objOptions.forceFiles;
        options.overwriteFiles = objOptions.overwriteFiles;
        options.recursive = objOptions.recursive;
        options.skipTransfer = objOptions.skipTransfer;
        options.keepModificationDate = objOptions.keepModificationDate;
        options.verbose = objOptions.verbose;
        options.zeroByteTransfer = objOptions.zeroByteTransfer;
        options.checkIntegrityHash = objOptions.checkIntegrityHash;
        options.createIntegrityHashFile = objOptions.createIntegrityHashFile;
        options.integrityHashType = objOptions.integrityHashType;
        return options;
    }

    /** @param options
     * @return */
    private JADEOptions addJADEOptionsOnClient(JADEOptions options) {
        options.mailOnSuccess = objOptions.mailOnSuccess;
        options.mailOnError = objOptions.mailOnError;
        options.mailOnEmptyFiles = objOptions.mailOnEmptyFiles;
        options.sendTransferHistory = objOptions.sendTransferHistory;
        options.backgroundServiceHost = objOptions.backgroundServiceHost;
        options.backgroundServiceJobChainName = objOptions.backgroundServiceJobChainName;
        options.backgroundServicePort = objOptions.backgroundServicePort;
        options.schedulerHost = objOptions.schedulerHost;
        options.schedulerJobChain = objOptions.schedulerJobChain;
        options.schedulerPort = objOptions.schedulerPort;
        options.schedulerTransferMethod = objOptions.schedulerTransferMethod;
        options.history = objOptions.history;
        options.historyFileName = objOptions.historyFileName;
        options.historyRepeat = objOptions.historyRepeat;
        options.historyRepeatInterval = objOptions.historyRepeatInterval;
        options.historyFileAppendMode = objOptions.historyFileAppendMode;
        options.mandator = objOptions.mandator;
        options.compressFiles = objOptions.compressFiles;
        options.compressedFileExtension = objOptions.compressedFileExtension;
        options.cumulateFiles = objOptions.cumulateFiles;
        options.cumulativeFileName = objOptions.cumulativeFileName;
        options.cumulativeFileSeparator = objOptions.cumulativeFileSeparator;
        options.cumulativeFileDelete = objOptions.cumulativeFileDelete;
        options.expectedSizeOfResultSet = objOptions.expectedSizeOfResultSet;
        options.raiseErrorIfResultSetIs = objOptions.raiseErrorIfResultSetIs;
        options.log_filename = objOptions.log_filename;
        options.log4jPropertyFileName = objOptions.log4jPropertyFileName;
        options.resultSetFileName = objOptions.resultSetFileName;
        return options;
    }

    /** @param options
     * @return */
    private String getJadeOnDMZCommand(JADEOptions options) {
        options.file.setNotDirty();
        options.user.DefaultValue("");
        options.history.Value(getHistoryFilename());
        options.Source().user.DefaultValue("");
        options.Target().user.DefaultValue("");
        StringBuilder command = new StringBuilder(objOptions.jumpCommand.Value() + " ");
        command.append("-SendTransferHistory=false ");
        command.append(options.getOptionsAsQuotedCommandLine());
        command.append(options.Source().getOptionsAsQuotedCommandLine());
        command.append(options.Target().getOptionsAsQuotedCommandLine());
        return command.toString();
    }

    /** @param options
     * @return */
    private String getJadeOnDMZCommand4RemoveSource() {
        JADEOptions opts = new JADEOptions();
        opts.operation.Value("delete");
        opts.verbose = objOptions.verbose;
        opts.fileListName.Value(getSourceListFilename());
        opts.forceFiles.value(false);
        opts.user.DefaultValue("");
        objOptions.user.DefaultValue("");
        objOptions.Source().user.DefaultValue("");
        objOptions.Source().directory.setNotDirty();
        objOptions.Source().postCommand.setNotDirty();
        objOptions.Source().PostTransferCommands.setNotDirty();
        objOptions.Source().PostFtpCommands.setNotDirty();
        objOptions.Source().preCommand.setNotDirty();
        objOptions.Source().PreFtpCommands.setNotDirty();
        objOptions.Source().PreTransferCommands.setNotDirty();
        objOptions.Source().tfnPostCommand.setNotDirty();
        StringBuilder command = new StringBuilder(objOptions.jumpCommand.Value() + " ");
        command.append("-SendTransferHistory=false ");
        command.append(opts.getOptionsAsQuotedCommandLine());
        command.append(objOptions.Source().getOptionsAsQuotedCommandLine());
        return command.toString();
    }

    private void removeDirOnDMZ(JadeEngine jade, Operation operation, String dir) {
        try {
            if (jade == null) {
                return;
            }
            String command = getRemoveDirCommand(dir);
            LOGGER.info(command);
            if (operation.equals(Operation.copyToInternet)) {
                jade.executeCommandOnTarget(command);
            } else {
                jade.executeCommandOnSource(command);
            }
        } catch (Exception ex) {
            LOGGER.warn(String.format("%s", ex.toString()));
        }
    }

    /** @param dir
     * @return */
    private String getRemoveDirCommand(String dir) {
        if (objOptions.jumpPlatform.isWindows()) {
            dir = dir.replace('/', '\\');
            return "rmdir \"" + dir + "\" /s /q;del /F /Q " + dir + "* 2>nul";
        } else {
            return "rm -f -R " + dir + "*";
        }
    }

    public SOSFileList getFileList() {
        return fileList;
    }

    @Override
    public JADEOptions getOptions() {
        if (objOptions == null) {
            objOptions = new JADEOptions();
        }
        return objOptions;
    }

    @Override
    public void run() {
        try {
            Execute();
        } catch (JobSchedulerException e) {
            throw e;
        } catch (Exception e) {
            throw new JobSchedulerException("abort", e);
        }
    }

    /** @param path
     * @return */
    private String normalizeDirectoryPath(String path) {
        path = path.replaceAll("\\\\", "/");
        return path.endsWith("/") ? path : path + "/";
    }

    private String getUUID() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
        return uuid;
    }

    private String getSourceListFilename() {
        if (sourceListFilename == null) {
            sourceListFilename = normalizeDirectoryPath(objOptions.jumpDir.Value()) + "jade-dmz-" + getUUID() + ".source.tmp";
        }
        return sourceListFilename;
    }

    private String getHistoryFilename() {
        if (historyFilename == null) {
            historyFilename = normalizeDirectoryPath(objOptions.jumpDir.Value()) + "jade-dmz-" + getUUID() + ".history.csv";
        }
        return historyFilename;
    }

}