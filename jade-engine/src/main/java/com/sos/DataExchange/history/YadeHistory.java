package com.sos.DataExchange.history;

import java.nio.file.Path;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sos.DataExchange.Options.JADEOptions;
import com.sos.JSHelper.interfaces.IJobSchedulerEventHandler;
import com.sos.VirtualFileSystem.DataElements.SOSFileList;
import com.sos.VirtualFileSystem.DataElements.SOSFileListEntry;
import com.sos.hibernate.classes.SOSHibernateFactory;
import com.sos.hibernate.classes.SOSHibernateSession;
import com.sos.hibernate.exceptions.SOSHibernateException;
import com.sos.jade.db.DBItemYadeFiles;
import com.sos.jade.db.DBItemYadeTransfers;
import com.sos.jade.db.YadeDBLayer;
import com.sos.jitl.reporting.db.DBLayer;

import sos.util.SOSString;

public class YadeHistory {

    private static final Logger LOGGER = LoggerFactory.getLogger(YadeHistory.class);

    private static final String IDENTIFIER = YadeHistory.class.getSimpleName();
    private SOSHibernateFactory dbFactory;
    private YadeDBOperationHelper dbHelper = null;
    private IJobSchedulerEventHandler eventHandler;
    private Long transferId;
    private Long parentTransferId;
    private boolean hasException = false;
    private boolean isIntervention = false;
    private String filePathRestriction = null;

    public YadeHistory(IJobSchedulerEventHandler handler) {
        eventHandler = handler;
    }

    public void buildFactory(Path hibernateFile) {
        try {
            if (hibernateFile != null) {
                dbFactory = new SOSHibernateFactory(hibernateFile);
                dbFactory.setIdentifier(IDENTIFIER);
                dbFactory.setAutoCommit(false);
                dbFactory.addClassMapping(DBLayer.getYadeClassMapping());
                dbFactory.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                dbFactory.build();
            } else {
                dbFactory = null;
                LOGGER.warn("No ./config/reporting.hibernate.cfg.xml found on file system! Transfer history won�t be processed.");
            }
        } catch (Throwable ex) {
            LOGGER.error(String.format("[%s]%s", IDENTIFIER, ex.toString()), ex);
            hasException = true;
        }
    }

    public void closeFactory() {
        try {
            if (dbFactory != null) {
                dbFactory.close();
            }
        } catch (Throwable ex) {
            LOGGER.error(String.format("[%s]%s", IDENTIFIER, ex.toString()), ex);
            hasException = true;
        }
    }

    public void beforeTransfer(JADEOptions options, SOSFileList fileList) {
        if (hasException) {
            return;
        }
        if (dbFactory == null) {
            LOGGER.error(String.format("[%s]dbFactory is null", IDENTIFIER));
            return;
        }

        try {
            SOSHibernateSession dbSession = null;
            dbHelper = new YadeDBOperationHelper(options, eventHandler);
            try {
                dbSession = dbFactory.openStatelessSession(IDENTIFIER);
                dbSession.beginTransaction();
                if (parentTransferId != null) {
                    dbHelper.setParentTransferId(parentTransferId);
                    DBItemYadeTransfers existingTransfer = dbHelper.getTransfer(parentTransferId, dbSession);
                    if (existingTransfer != null && (existingTransfer.getJobChainNode() != null 
                            && existingTransfer.getJobChainNode().equals(options.getJobChainNodeName())) 
                            && (existingTransfer.getOrderId() != null && existingTransfer.getOrderId().equals(options.getOrderId())) 
                            && existingTransfer.getState() == 3) {
                        existingTransfer.setHasIntervention(true);
                        try {
                            dbSession.update(existingTransfer);
                        } catch (SOSHibernateException e) {
                            LOGGER.error(e.getMessage(), e);
                        }
                        transferId = dbHelper.storeInitialTransferInformations(fileList, dbSession, existingTransfer.getId());
                    } else {
                        transferId = dbHelper.storeInitialTransferInformations(fileList, dbSession);
                    }
                } else {
                    transferId = dbHelper.storeInitialTransferInformations(fileList, dbSession);
                }
                dbSession.commit();
                sendYadeEventOnStart();
            } catch (SOSHibernateException e) {
                try {
                    dbSession.rollback();
                } catch (Throwable e1) {
                }
                throw e;
            } finally {
                try {
                    dbSession.close();
                } catch (Throwable e1) {
                }
            }
        } catch (Throwable ex) {
            LOGGER.error(String.format("[%s]%s", IDENTIFIER, ex.toString()), ex);
        }
    }

    public void afterTransfer() {
        if (hasException) {
            return;
        }
        if (dbFactory == null) {
            LOGGER.error(String.format("[%s]dbFactory is null", IDENTIFIER));
            return;
        }
        if (dbHelper == null) {
            LOGGER.error(String.format("[%s]dbHelper is null", IDENTIFIER));
            return;
        }

        try {
            SOSHibernateSession dbSession = null;
            try {
                dbSession = dbFactory.openStatelessSession(IDENTIFIER);
                dbSession.beginTransaction();
                dbHelper.updateSuccessfulTransfer(dbSession);
                dbSession.commit();
            } catch (SOSHibernateException e) {
                try {
                    dbSession.rollback();
                } catch (Throwable e1) {
                }
                throw e;
            } finally {
                try {
                    dbSession.close();
                } catch (Throwable e1) {
                }
            }
        } catch (Throwable ex) {
            LOGGER.error(String.format("[%s]%s", IDENTIFIER, ex.toString()), ex);
            hasException = true;
        }
    }

    public void beforeFileTransfer(SOSFileList fileList) {
        if (hasException) {
            return;
        }
        if (dbFactory == null) {
            LOGGER.error(String.format("[%s]dbFactory is null", IDENTIFIER));
            return;
        }
        if (dbHelper == null) {
            LOGGER.error(String.format("[%s]dbHelper is null", IDENTIFIER));
            return;
        }

        try {
            SOSHibernateSession dbSession = null;
            try {
                dbSession = dbFactory.openStatelessSession(IDENTIFIER);
                dbSession.beginTransaction();
                dbHelper.updateTransfersNumOfFiles(dbSession, fileList.size());
                if (transferId != null) {
                    dbHelper.storeInitialFilesInformationToDB(transferId, dbSession, fileList);
                }
                dbSession.commit();
            } catch (SOSHibernateException e) {
                try {
                    dbSession.rollback();
                } catch (Throwable e1) {
                }
                throw e;
            } finally {
                try {
                    dbSession.close();
                } catch (Throwable e1) {
                }
            }
        } catch (Throwable ex) {
            LOGGER.error(String.format("[%s]%s", IDENTIFIER, ex.toString()), ex);
            hasException = true;
        }

    }

    public void afterFileTransfer(SOSFileList fileList) {
        if (hasException) {
            return;
        }
        if (dbFactory == null) {
            LOGGER.error(String.format("[%s]dbFactory is null", IDENTIFIER));
            return;
        }
        if (dbHelper == null) {
            LOGGER.error(String.format("[%s]dbHelper is null", IDENTIFIER));
            return;
        }

        try {
            SOSHibernateSession dbSession = null;
            try {
                dbSession = dbFactory.openStatelessSession(IDENTIFIER);
                dbSession.beginTransaction();
                for (SOSFileListEntry entry : fileList.getList()) {
                    dbHelper.updateFileInformationToDB(dbSession, entry, true, null);
                }
                dbHelper.updateTransfersNumOfFiles(dbSession, fileList.count());
                dbSession.commit();
            } catch (SOSHibernateException e) {
                try {
                    dbSession.rollback();
                } catch (Throwable e1) {
                }
                throw e;
            } finally {
                try {
                    dbSession.close();
                } catch (Throwable e1) {
                }
            }
        } catch (Throwable ex) {
            LOGGER.error(String.format("[%s]%s", IDENTIFIER, ex.toString()), ex);
            hasException = true;
        }
    }

    public void afterDMZFileTransfer(SOSFileList fileList, String targetDir) {
        if (hasException) {
            return;
        }
        if (dbFactory == null) {
            LOGGER.error(String.format("[%s]dbFactory is null", IDENTIFIER));
            return;
        }
        if (dbHelper == null) {
            LOGGER.error(String.format("[%s]dbHelper is null", IDENTIFIER));
            return;
        }

        try {
            SOSHibernateSession dbSession = null;
            try {
                dbSession = dbFactory.openStatelessSession(IDENTIFIER);
                dbSession.beginTransaction();
                dbHelper.storeInitialFilesInformationToDB(transferId, dbSession, fileList);
                dbHelper.updateTransfersNumOfFiles(dbSession, fileList.count());
                for (SOSFileListEntry entry : fileList.getList()) {
                    dbHelper.updateFileInformationToDB(dbSession, entry, true, targetDir);
                }
                dbSession.commit();
            } catch (SOSHibernateException e) {
                try {
                    dbSession.rollback();
                } catch (Throwable e1) {
                }
                throw e;
            } finally {
                try {
                    dbSession.close();
                } catch (Throwable e1) {
                }
            }
        } catch (Throwable ex) {
            LOGGER.error(String.format("[%s]%s", IDENTIFIER, ex.toString()), ex);
            hasException = true;
        }
    }

    public void onException(Exception e) {
        if (hasException) {
            return;
        }
        if (dbFactory == null) {
            LOGGER.error(String.format("[%s]dbFactory is null", IDENTIFIER));
            return;
        }
        if (dbHelper == null) {
            LOGGER.error(String.format("[%s]dbHelper is null", IDENTIFIER));
            return;
        }

        try {
            SOSHibernateSession dbSession = null;
            try {
                dbSession = dbFactory.openStatelessSession(IDENTIFIER);
                dbSession.beginTransaction();
                dbHelper.updateFailedTransfer(dbSession, String.format("%1$s: %2$s", e.getClass().getSimpleName(), e.getMessage()));
                dbSession.commit();
            } catch (SOSHibernateException ex) {
                try {
                    dbSession.rollback();
                } catch (Throwable e1) {
                }
                throw ex;
            } finally {
                try {
                    dbSession.close();
                } catch (Throwable e1) {
                }
            }
        } catch (Throwable ex1) {
            LOGGER.error(String.format("[%s]%s", IDENTIFIER, ex1.toString()), ex1);
            hasException = true;
        }
    }

    public void onFileTransferException(SOSFileList fileList) {
        if (hasException) {
            return;
        }
        if (dbFactory == null) {
            LOGGER.error(String.format("[%s]dbFactory is null", IDENTIFIER));
            return;
        }
        if (dbHelper == null) {
            LOGGER.error(String.format("[%s]dbHelper is null", IDENTIFIER));
            return;
        }

        try {
            SOSHibernateSession dbSession = null;
            try {
                dbSession = dbFactory.openStatelessSession(IDENTIFIER);
                dbSession.beginTransaction();
                for (SOSFileListEntry entry : fileList.getList()) {
                    if (dbFactory != null) {
                        dbHelper.updateFileInformationToDB(dbSession, entry, true, null);
                    }
                }
                dbSession.commit();
            } catch (SOSHibernateException e) {
                try {
                    dbSession.rollback();
                } catch (Throwable e1) {
                }
                throw e;
            } finally {
                try {
                    dbSession.close();
                } catch (Throwable e1) {
                }
            }
        } catch (Throwable ex) {
            LOGGER.error(String.format("[%s]%s", IDENTIFIER, ex.toString()), ex);
            hasException = true;
        }
    }

    public void onDMZFileTransferException(SOSFileList fileList, String targetDir) {
        if (hasException) {
            return;
        }
        if (dbFactory == null) {
            LOGGER.error(String.format("[%s]dbFactory is null", IDENTIFIER));
            return;
        }
        if (dbHelper == null) {
            LOGGER.error(String.format("[%s]dbHelper is null", IDENTIFIER));
            return;
        }

        try {
            SOSHibernateSession dbSession = null;
            try {
                dbSession = dbFactory.openStatelessSession(IDENTIFIER);
                dbSession.beginTransaction();
                dbHelper.updateTransfersNumOfFiles(dbSession, fileList.count());
                for (SOSFileListEntry entry : fileList.getList()) {
                    dbHelper.updateFileInformationToDB(dbSession, entry, true, targetDir);
                }
                dbSession.commit();
            } catch (SOSHibernateException e) {
                try {
                    dbSession.rollback();
                } catch (Throwable e1) {
                }
                throw e;
            } finally {
                try {
                    dbSession.close();
                } catch (Throwable e1) {
                }
            }
        } catch (Throwable ex) {
            LOGGER.error(String.format("[%s]%s", IDENTIFIER, ex.toString()), ex);
            hasException = true;
        }
    }

    public void setFileRestriction(JADEOptions options) {
        if (hasException) {
            return;
        }
        if (dbFactory == null) {
            LOGGER.error(String.format("[%s]dbFactory is null", IDENTIFIER));
            return;
        }

        if (filePathRestriction != null) {
            LOGGER.info("*** transfer was restarted with a reduced fileList");
            LOGGER.info("*** with the filePathRestriction: " + filePathRestriction);
            if (options.fileListName.isNotEmpty()) {
                options.fileListName.setNull();
            }
            options.filePath.setValue(filePathRestriction);
        }
    }

    public void updateFileInDB(Map<String, String> values) {
        if (hasException) {
            return;
        }
        if (dbFactory == null) {
            LOGGER.error(String.format("[%s]dbFactory is null", IDENTIFIER));
            return;
        }
        if (eventHandler == null) {
            LOGGER.error(String.format("[%s]eventHandler is null", IDENTIFIER));
            return;
        }
        if(values == null || values.size() == 0){
            return;
        }
        
        String filePath = values.get("sourcePath");
        if(SOSString.isEmpty(filePath)){
            return;
        }
                
        try {
            SOSHibernateSession dbSession = null;
            try {
                dbSession = dbFactory.openStatelessSession(IDENTIFIER);
                YadeDBLayer dbLayer = new YadeDBLayer(dbSession);
                DBItemYadeFiles file = null;
                dbSession.beginTransaction();
                file = dbLayer.getTransferFileFromDbByConstraint(transferId, filePath);
                if (file != null) {
                    if (parentTransferId != null) {
                        DBItemYadeFiles intervenedFile = null;
                        filePath = values.get("sourcePath");
                        intervenedFile = dbLayer.getTransferFileFromDbByConstraint(parentTransferId, filePath);
                        if (intervenedFile != null) {
                            intervenedFile.setInterventionTransferId(transferId);
                            dbSession.update(intervenedFile);
                        }
                    }
                    for (String key : values.keySet()) {
                        // key = propertyName
                        // values.get(key) = propertyValue
                        switch (key) {
                        case "sourcePath":
                            file.setSourcePath(values.get(key));
                            break;
                        case "targetPath":
                            file.setTargetPath(values.get(key));
                            break;
                        case "state":
                            file.setState(Integer.parseInt(values.get(key)));
                            break;
                        case "errorCode":
                            file.setErrorCode(values.get(key));
                            break;
                        case "errorMessage":
                            file.setErrorMessage(values.get(key));
                            break;
                        default:
                            break;
                        }
                    }
                    dbSession.update(file);
                    Map<String, String> eventValues = new HashMap<String, String>();
                    eventValues.put("fileId", file.getId().toString());
                    eventHandler.sendEvent("YADEFileStateChanged", eventValues);
                }
                dbSession.commit();
            } catch (SOSHibernateException e) {
                try {
                    dbSession.rollback();
                } catch (Throwable e1) {
                }
                throw e;
            } finally {
                try {
                    dbSession.close();
                } catch (Throwable e1) {
                }
            }
        } catch (Throwable ex) {
            LOGGER.error(String.format("[%s]%s", IDENTIFIER, ex.toString()), ex);
            hasException = true;
        }

    }

    private void sendYadeEvent(String message) {
        if (hasException) {
            return;
        }
        if (dbFactory == null) {
            LOGGER.error(String.format("[%s]dbFactory is null", IDENTIFIER));
            return;
        }
        if (eventHandler == null) {
            LOGGER.error(String.format("[%s]eventHandler is null", IDENTIFIER));
            return;
        }
        if (transferId != null) {
            try {
                Map<String, String> values = new HashMap<String, String>();
                values.put("transferId", transferId.toString());
                eventHandler.sendEvent(message, values);
            } catch (Throwable ex) {
                LOGGER.error(String.format("[%s]%s", IDENTIFIER, ex.toString()), ex);
                hasException = true;
            }
        }
    }

    public Long getTransferId() {
        return transferId;
    }

    public void setTransferId(Long transferId) {
        this.transferId = transferId;
    }

    public Long getParentTransferId() {
        return parentTransferId;
    }

    public void setParentTransferId(Long parentTransferId) {
        this.parentTransferId = parentTransferId;
    }

    public boolean isIntervention() {
        return isIntervention;
    }

    public void setIntervention(boolean isIntervention) {
        this.isIntervention = isIntervention;
    }

    public String getFilePathRestriction() {
        return filePathRestriction;
    }

    public void setFilePathRestriction(String filePathRestriction) {
        this.filePathRestriction = filePathRestriction;
    }

    public void sendYadeEventOnEnd() {
        sendYadeEvent("YADETransferFinished");
    }

    public void sendYadeEventOnStart() {
        sendYadeEvent("YADETransferStarted");
    }

}
