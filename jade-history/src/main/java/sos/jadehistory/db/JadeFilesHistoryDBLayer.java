package sos.jadehistory.db;

import java.io.File;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.hibernate.ObjectNotFoundException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import sos.jadehistory.JadeFilesHistoryFilter;

import com.sos.hibernate.classes.DbItem;
import com.sos.hibernate.layer.SOSHibernateIntervalDBLayer;

/**
 * 
 * \class JadeFilesHistoryDBLayer \brief JadeFilesHistoryDBLayer -
 * 
 * \details
 * 
 * \section JadeFilesHistoryDBLayer.java_intro_sec Introduction
 * 
 * \section JadeFilesHistoryDBLayer.java_samples Some Samples
 * 
 * \code .... code goes here ... \endcode
 * 
 * <p style="text-align:center">
 * <br />
 * --------------------------------------------------------------------------- <br />
 * APL/Software GmbH - Berlin <br />
 * ##### generated by ClaviusXPress (http://www.sos-berlin.com) ######### <br />
 * ---------------------------------------------------------------------------
 * </p>
 * \author Uwe Risse \version 27.09.2011 \see reference
 * \author Santiago Aucejo \version 04.06.2014 \see reference
 * 
 * Created on 04.06.2014
 */

public class JadeFilesHistoryDBLayer extends SOSHibernateIntervalDBLayer implements Serializable{
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
    private final String      conClassName = "JadeHistoryFilesDBLayer";
    protected JadeFilesHistoryFilter filter       = null;
 


    public JadeFilesHistoryDBLayer(File configurationFile_) {
        super();
        this.setConfigurationFile(configurationFile_);
        this.resetFilter();

    }

    public JadeFilesHistoryDBItem get(String guid) {
        if (guid == null || "".equals(guid)) {
            return null;
        }
        
        try {
            return (JadeFilesHistoryDBItem) this.getSession().get(JadeFilesHistoryDBItem.class, guid);
        }
        catch (ObjectNotFoundException e) {
            return null;
        }
    }

    public void resetFilter() {
        filter = new JadeFilesHistoryFilter();
        this.filter = new JadeFilesHistoryFilter();
        this.filter.setDateFormat("yyyy-MM-dd HH:mm:ss");
        this.filter.setOrderCriteria("startTime");
        this.filter.setSortMode("desc");
        
    }
    
    protected String getWhere() {

        String where = "";
        String and = "";

        if (filter.getCreatedFrom() != null) {
            where += and + " created >= :createdFrom";
            and = " and ";
        }

        if (filter.getCreatedTo() != null) {
            where += and + " created <= :createdTo ";
            and = " and ";
        }

        if (filter.getModifiedFrom() != null) {
            where += and + " modified >= :modifiedFrom";
            and = " and ";
        }

        if (filter.getModifiedTo() != null) {
            where += and + " modified <= :modifiedTo ";
            and = " and ";
        }

        if (filter.getTransferStartFrom() != null) {
            where += and + " transferStart >= :transferStartFrom";
            and = " and ";
        }

        if (filter.getTransferStartTo() != null) {
            where += and + " transferStart <= :transferStartTo";
            and = " and ";
        }

        if (filter.getTransferEndFrom() != null) {
            where += and + " transferEnd >= :transferEndFrom";
            and = " and ";
        }

        if (filter.getTransferEndTo() != null) {
            where += and + " transferEnd <= :transferEndTo";
            and = " and ";
        }

        if (filter.getCreatedBy() != null && !filter.getCreatedBy().equals("")) {
            where += and + " createdBy=:createdBy";
            and = " and ";
        }

        if (filter.getModifiedBy() != null && !filter.getModifiedBy().equals("")) {
            where += and + " modifiedBy=:modifiedBy";
            and = " and ";
        }

        if (filter.getGuid() != null && !"".equals(filter.getGuid())) {
            where += and + " guid=:guid";
            and = " and ";
        }

        if (filter.getPid() != null && !"".equals(filter.getPid())) {
            where += and + " pid=:pid";
            and = " and ";
        }

        if (filter.getPpid() != null && !"".equals(filter.getPpid())) {
            where += and + " ppid=:ppid";
            and = " and ";
        }

        if (filter.getLastErrorMessage() != null && !"".equals(filter.getLastErrorMessage())) {
            where += and + " lastErrorMessage=:lastErrorMessage";
            and = " and ";
        }

        if (filter.getStatus() != null && !"".equals(filter.getStatus())) {
            where += and + " status=:status";
            and = " and ";
        }

        if (filter.getLogFilename() != null && !"".equals(filter.getLogFilename())) {
            where += and + " logFilename=:logFilename";
            and = " and ";
        }

        if (filter.getJumpHost() != null && !"".equals(filter.getJumpHost())) {
            where += and + " jumpHost=:jumpHost";
            and = " and ";
        }

        if (filter.getJumpHostIp() != null && !"".equals(filter.getJumpHostIp())) {
            where += and + " jumpHostIp=:jumpHostIp";
            and = " and ";
        }

        if (filter.getJumpProtocol() != null && !"".equals(filter.getJumpProtocol())) {
            where += and + " jumpProtocol=:jumpProtocol";
            and = " and ";
        }

        if (filter.getJumpPort() != null && !"".equals(filter.getJumpPort())) {
            where += and + " jumpPort=:jumpPort";
            and = " and ";
        }

        if (filter.getJumpUser() != null && !"".equals(filter.getJumpUser())) {
            where += and + " jumpUser=:jumpUser";
            and = " and ";
        }

        if (filter.getOperation() != null && !"".equals(filter.getOperation())) {
            where += and + " operation=:operation";
            and = " and ";
        }

        if (filter.getPort() != null && !"".equals(filter.getPort())) {
            where += and + " port=:port";
            and = " and ";
        }

        if (filter.getProtocol() != null && !"".equals(filter.getProtocol())) {
            where += and + " protocol=:protocol";
            and = " and ";
        }

        if (filter.getJadeId() != null && !"".equals(filter.getJadeId())) {
            where += and + " jadeId=:jadeId";
            and = " and ";
        }

        if (filter.getTargetDir() != null && !"".equals(filter.getTargetDir())) {
            where += and + " targetDir=:targetDir";
            and = " and ";
        }

        if (filter.getTargetFilename() != null && !"".equals(filter.getTargetFilename())) {
            where += and + " targetFilename=:targetFilename";
            and = " and ";
        }

        if (filter.getTargetHost() != null && !"".equals(filter.getTargetHost())) {
            where += and + " targetHost=:targetHost";
            and = " and ";
        }

        if (filter.getTargetHostIp() != null && !"".equals(filter.getTargetHostIp())) {
            where += and + " targetHostIp=:targetHostIp";
            and = " and ";
        }

        if (filter.getTargetUser() != null && !"".equals(filter.getTargetUser())) {
            where += and + " targetUser=:targetUser";
            and = " and ";
        }

        if (filter.getMandator() != null && !"".equals(filter.getMandator())) {
            where += and + " history.jadeFilesDBItem.mandator=:mandator";
            and = " and ";
        }

        if (filter.getFileSize() != null && !"".equals(filter.getFileSize())) {
            where += and + " history.jadeFilesDBItem.fileSize=:fileSize";
            and = " and ";
        }

        if (filter.getSourceFile() != null && !"".equals(filter.getSourceFile())) {
            where += and + " history.jadeFilesDBItem.sourceFilename=:sourceFilename";
            and = " and ";
        }

        if (filter.getSourceHost() != null && !"".equals(filter.getSourceHost())) {
            where += and + " history.jadeFilesDBItem.sourceHost=:sourceHost";
            and = " and ";
        }

  


        if (where.trim().equals("")) {

        }
        else {
            where = "where " + where;
        }
        return where;

    }

    
    protected String getWhereFromTo() {

        String where = "";
        String and = "";

        if (filter.getCreatedFrom() != null) {
            where += and + " created >= :createdFrom";
            and = " and ";
        }

        if (filter.getCreatedTo() != null) {
            where += and + " created <= :createdTo ";
            and = " and ";
        }

 


        if (where.trim().equals("")) {

        }
        else {
            where = "where " + where;
        }
        return where;

    }    
    
    private void setWhere(Query query) {

        if (filter.getCreatedFrom() != null && !filter.getCreatedFrom().equals("")) {
            query.setTimestamp("createdFrom", filter.getCreatedFrom());
        }

        if (filter.getCreatedTo() != null && !filter.getCreatedTo().equals("")) {
            query.setTimestamp("createdTo", filter.getCreatedTo());
        }

        if (filter.getModifiedFrom() != null && !filter.getModifiedFrom().equals("")) {
            query.setTimestamp("modifiedFrom", filter.getModifiedFrom());
        }

        if (filter.getModifiedTo() != null && !filter.getModifiedTo().equals("")) {
            query.setTimestamp("modifiedTo", filter.getModifiedTo());
        }

        if (filter.getCreatedBy() != null && !filter.getCreatedBy().equals("")) {
            query.setText("createdBy", filter.getCreatedBy());
        }

        if (filter.getModifiedBy() != null && !filter.getModifiedBy().equals("")) {
            query.setText("modifiedBy", filter.getModifiedBy());
        }

        if (filter.getGuid() != null && !"".equals(filter.getGuid())) {
            query.setText("guid", filter.getGuid());
        }

        if (filter.getJadeId() != null && !"".equals(filter.getJadeId())) {
            query.setLong("jadeId", filter.getJadeId());
        }

        if (filter.getOperation() != null && !"".equals(filter.getOperation())) {
            query.setText("operation", filter.getOperation());
        }

        if (filter.getTransferStartFrom() != null && !"".equals(filter.getTransferStartFrom())) {
            query.setTimestamp("transferStartFrom", filter.getTransferStartFrom());
        }

        if (filter.getTransferStartTo() != null && !"".equals(filter.getTransferStartTo())) {
            query.setTimestamp("transferStartTo", filter.getTransferStartTo());
        }

        if (filter.getTransferEndFrom() != null && !"".equals(filter.getTransferEndFrom())) {
            query.setTimestamp("transferEndFrom", filter.getTransferEndFrom());
        }

        if (filter.getTransferEndTo() != null && !"".equals(filter.getTransferEndTo())) {
            query.setTimestamp("transferEndTo", filter.getTransferEndTo());
        }

        if (filter.getPid() != null && !"".equals(filter.getPid())) {
            query.setInteger("pid", filter.getPid());
        }

        if (filter.getPpid() != null && !"".equals(filter.getPpid())) {
            query.setInteger("ppid", filter.getPpid());
        }

        if (filter.getTargetHost() != null && !"".equals(filter.getTargetHost())) {
            query.setText("targetHost", filter.getTargetHost());
        }

        if (filter.getTargetHostIp() != null && !"".equals(filter.getTargetHostIp())) {
            query.setText("targetHostIp", filter.getTargetHostIp());
        }

        if (filter.getTargetUser() != null && !"".equals(filter.getTargetUser())) {
            query.setText("targetUser", filter.getTargetUser());
        }

        if (filter.getTargetDir() != null && !"".equals(filter.getTargetDir())) {
            query.setText("targetDir", filter.getTargetDir());
        }

        if (filter.getTargetFilename() != null && !"".equals(filter.getTargetFilename())) {
            query.setText("targetFilename", filter.getTargetFilename());
        }

        if (filter.getProtocol() != null && !"".equals(filter.getProtocol())) {
            query.setText("protocol", filter.getProtocol());
        }

        if (filter.getPort() != null && !"".equals(filter.getPort())) {
            query.setInteger("port", filter.getPort());
        }

        if (filter.getStatus() != null && !"".equals(filter.getStatus())) {
            query.setText("status", filter.getStatus());
        }

        if (filter.getLastErrorMessage() != null && !"".equals(filter.getLastErrorMessage())) {
            query.setText("lastErrorMessage", filter.getLastErrorMessage());
        }

        if (filter.getLogFilename() != null && !"".equals(filter.getLogFilename())) {
            query.setText("logFilename", filter.getLogFilename());
        }

        if (filter.getJumpHost() != null && !"".equals(filter.getJumpHost())) {
            query.setText("jumpHost", filter.getJumpHost());
        }

        if (filter.getJumpHostIp() != null && !"".equals(filter.getJumpHostIp())) {
            query.setText("jumpHostIp", filter.getJumpHostIp());
        }

        if (filter.getJumpUser() != null && !"".equals(filter.getJumpUser())) {
            query.setText("jumpUser", filter.getJumpUser());
        }

        if (filter.getJumpProtocol() != null && !"".equals(filter.getJumpProtocol())) {
            query.setText("jumpProtocol", filter.getJumpProtocol());
        }

        if (filter.getJumpPort() != null && !"".equals(filter.getJumpPort())) {
            query.setInteger("jumpPort", filter.getJumpPort());
        }

        if (filter.getJadeFilesDBItem() != null && filter.getJadeFilesDBItem().getId() != null) {
            query.setLong("jadeId", filter.getJadeFilesDBItem().getId());
        }

        if (filter.getMandator() != null && !"".equals(filter.getMandator())) {
            query.setText("mandator", filter.getMandator());
        }

        if (filter.getFileSize() != null && !"".equals(filter.getFileSize())) {
            query.setInteger("fileSize", filter.getFileSize());
        }

        if (filter.getSourceFile() != null && !"".equals(filter.getSourceFile())) {
            query.setText("sourceFilename", filter.getSourceFile());
        }

        if (filter.getSourceHost() != null && !"".equals(filter.getSourceHost())) {
            query.setText("sourceHost", filter.getSourceHost());
        }
    }

  
    public List<DbItem> getFilesHistoryFromTo(Date from, Date to){
        
        filter.setCreatedFrom(from); 
        filter.setCreatedTo(to);
      
        Session session = getSession();

        Transaction transaction = session.beginTransaction();
        Query query = session.createQuery("  from JadeFilesHistoryDBItem " + getWhere());

        if (filter.getCreatedFrom() != null) {
            query.setTimestamp("createdFrom", filter.getCreatedFrom());
        }
        
        if (filter.getCreatedTo() != null) {
            query.setTimestamp("createdTo", filter.getCreatedTo());
        }

        List<DbItem> resultset = query.list();

        return resultset;

    }    
    
    

    public List<JadeFilesHistoryDBItem> getFilesHistoryById(Long jadeId) throws ParseException {
        Session session = getSession();

        Transaction transaction = session.beginTransaction();
        Query query = session.createQuery("  from JadeFilesHistoryDBItem where jadeId=:jadeId");
        query.setLong("jadeId", jadeId);
        List<JadeFilesHistoryDBItem> resultset = query.list();

        transaction.commit();
        return resultset;
    }
    
    public JadeFilesDBItem getJadeFileItemById(Long jadeId) throws ParseException {
        Session session = getSession();

        Transaction transaction = session.beginTransaction();
        Query query = session.createQuery("  from JadeFilesDBItem where id=:jadeId");
        query.setLong("jadeId", jadeId);
        List<JadeFilesDBItem> resultset = query.list();

        transaction.commit();
        // id is unique, therefore only one item has to be returned
        return resultset.get(0);
    }
    
    public List<JadeFilesHistoryDBItem> getHistoryFiles() throws ParseException {
        
        Session session = getSession();

        Transaction transaction = session.beginTransaction();
        Query query = session.createQuery("  from JadeFilesHistoryDBItem history " + getWhere());
        setWhere(query);
        List<JadeFilesHistoryDBItem> resultset = query.list();

        transaction.commit();
        return resultset;

    }
    
    public List<JadeFilesHistoryDBItem> getHistoryFilesOrderedByTimestamp() throws ParseException {
        
        Session session = getSession();

        Transaction transaction = session.beginTransaction();
        Query query = session.createQuery("  from JadeFilesHistoryDBItem history " + getWhere() + " order by transferStart desc");
        setWhere(query);
        List<JadeFilesHistoryDBItem> resultset = query.list();

        transaction.commit();
        return resultset;

    }
    
    
    public void setCreatedFrom(Date createdFrom) {
        filter.setCreatedFrom(createdFrom);
    }

    public void setCreatedTo(Date createdTo) {
        filter.setCreatedTo(createdTo);
    }

    public void setDateFormat(String dateFormat) {
        filter.setDateFormat(dateFormat);
    }

    public void setCreatedFrom(String createdFrom) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(filter.getDateFormat());
        setCreatedFrom(formatter.parse(createdFrom));
    }

    public void setCreatedTo(String createdTo) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(filter.getDateFormat());
        setCreatedTo(formatter.parse(createdTo));
    }

    @Override
    public JadeFilesHistoryFilter getFilter() {
        return filter;
    }

    public void setFilter(JadeFilesHistoryFilter filter) {
        this.filter = filter;
    }

    @Override
    public void onAfterDeleting(DbItem h) {        
    }

    @Override
    public List<DbItem> getListOfItemsToDelete()  {
        TimeZone.setDefault(TimeZone.getTimeZone("Etc/UTC"));
         return getFilesHistoryFromTo(filter.getCreatedFrom(),filter.getCreatedTo());
             
    }
    @Override
    public long deleteInterval() {
         return 0;
    }

}
