package com.sos.jade.db;

// com.sos.scheduler.history

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.*;

import org.hibernate.Session;

 

@Entity
@Table(name = "SOSJADE_TRANSFERS_DETAILS")
public class JadeTransferDetailDBItem {

    private Long transferDetailsId;
    private Long transferId;

    private String sourceFilename;
    private String targetFilename;
    private String md5;

    private String pid;
    private Integer status;
    private Integer commandType;
    private String command;
    private String lastErrorMessage;
    private Long fileSize;

    private Date startTime;
    private Date endTime;

    private Date created;
    private String createdBy;
    private Date modified;
    private String modifiedBy;

    private Session session;
    private JadeTransferDBItem jadeFilesDBItem;

    public JadeTransferDetailDBItem() {

    }

    public JadeTransferDetailDBItem(Session session_) {
        this.session = session_;
    }

    @ManyToOne(optional = true)
    @JoinColumn(name = "`TRANSFER_ID`")
    public JadeTransferDBItem getJadeTransferDBItem() {
        return this.jadeFilesDBItem;
    }

    public void setJadeTransferDBItem(JadeTransferDBItem jadeFilesDBItem) {
        this.jadeFilesDBItem = jadeFilesDBItem;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "[TRANSFER_DETAILS_ID]", nullable = false)
    public Long getTransferDetailsId() {
        return transferDetailsId;
    }

    @Column(name = "[TRANSFER_DETAILS_ID]", nullable = false)
    public void setTransferDetailsId(Long transferDetailsId) {
        this.transferDetailsId = transferDetailsId;
    }

    @Column(name = "[TRANSFER_ID]", nullable = false, updatable = false, insertable = false)
    public void setTransferId(Long transferId) {
        this.transferId = transferId;
    }

    @Column(name = "[TRANSFER_ID]", nullable = false, updatable = false, insertable = false)
    public Long getTransferId() {
        return transferId;
    }

    @Column(name = "[FILE_SIZE]", nullable = false)
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    @Column(name = "[FILE_SIZE]", nullable = false)
    public void setFileSize(String fileSize) {
        try {
            this.fileSize = Long.parseLong(fileSize);
        } catch (NumberFormatException e) {
            this.fileSize = new Long(0);
        }
    }

    @Column(name = "[FILE_SIZE]", nullable = false)
    public Long getFileSize() {
        return fileSize;
    }

    @Column(name = "[COMMAND_TYPE]", nullable = false)
    public void setCommandType(Integer commandType) {
        this.commandType = commandType;
    }

    @Column(name = "[COMMAND_TYPE]", nullable = false)
    public Integer getCommandType() {
        return commandType;
    }

    @Lob
    @Column(name = "[COMMAND]", nullable = false)
    public void setCommand(String command) {
        this.command = command;
    }

    @Lob
    @Column(name = "[COMMAND]", nullable = false)
    public String getCommand() {
        return command;
    }

    @Column(name = "[PID]", nullable = false)
    public String getPid() {
        return pid;
    }

    @Column(name = "[PID]", nullable = false)
    public void setPid(String pid) {
        this.pid = pid;
    }

    @Lob
    @Column(name = "[LAST_ERROR_MESSAGE]", nullable = true)
    public void setLastErrorMessage(String lastErrorMessage) {
        this.lastErrorMessage = lastErrorMessage;
    }

    @Lob
    @Column(name = "[LAST_ERROR_MESSAGE]", nullable = true)
    public String getLastErrorMessage() {
        return lastErrorMessage;
    }

    @Column(name = "[TARGET_FILENAME]", nullable = false)
    public void setTargetFilename(String targetFilename) {
        this.targetFilename = targetFilename;
    }

    @Column(name = "[TARGET_FILENAME]", nullable = false)
    public String getTargetFilename() {
        return targetFilename;
    }

    @Column(name = "[SOURCE_FILENAME]", nullable = false)
    public void setSourceFilename(String sourceFilename) {
        this.sourceFilename = sourceFilename;
    }

    @Column(name = "[SOURCE_FILENAME]", nullable = false)
    public String getSourceFilename() {
        return sourceFilename;
    }

    @Column(name = "[MD5]", nullable = false)
    public void setMd5(String md5) {
        this.md5 = md5;
    }

    @Column(name = "[MD5]", nullable = false)
    public String getMd5() {
        return md5;
    }

    @Column(name = "[STATUS]", nullable = false)
    public void setStatus(Integer status) {
        this.status = status;
    }

    @Column(name = "[STATUS]", nullable = false)
    public Integer getStatus() {
        return status;
    }

    @Column(name = "[START_TIME]", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @Column(name = "[START_TIME]", nullable = false)
    public Date getStartTime() {
        return startTime;
    }

    @Column(name = "[END_TIME]", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    @Column(name = "[END_TIME]", nullable = false)
    public Date getEndTime() {
        return endTime;
    }

    @Column(name = "[MODIFIED_BY]", nullable = false)
    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @Column(name = "[MODIFIED_BY]", nullable = false)
    public String getModifiedBy() {
        return modifiedBy;
    }

    @Column(name = "[CREATED_BY]", nullable = false)
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Column(name = "[CREATED_BY]", nullable = false)
    public String getCreatedBy() {
        return createdBy;
    }

    @Column(name = "[CREATED]", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreated() {
        return created;
    }

    @Column(name = "[CREATED]", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    public void setCreated(Date created) {
        this.created = created;
    }

    @Column(name = "[MODIFIED]", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getModified() {
        return modified;
    }

    @Column(name = "[MODIFIED]", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    public void setModified(Date modified) {
        this.modified = modified;
    }

    @Transient
    public String getStatusValue() {
        return String.valueOf(status);
    }

    @Transient
    public String getSizeValue() {
        return String.valueOf(fileSize);
    }

    @Transient
    public String getStartTimeIso() {
        if (this.getStartTime() == null) {
            return "";
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");
            String getStartTimeIso = formatter.format(this.getStartTime());
            return getStartTimeIso;
        }
    }

    @Transient
    public String getEndTimeIso() {
        if (this.getEndTime() == null) {
            return "";
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");
            String getEndTimeIso = formatter.format(this.getEndTime());
            return getEndTimeIso;
        }
    }

    public void save() {
        session.save(this);
        session.save(this.getJadeTransferDBItem());
    }

    @Transient
    public void setSession(Session session) {
        this.session = session;
    }
}
