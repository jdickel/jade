package sos.jadehistory.db;

import java.io.File;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import sos.jadehistory.JadeFilesFilter;

import com.sos.hibernate.classes.DbItem;
import com.sos.hibernate.classes.SOSHibernateConnection;
import com.sos.hibernate.layer.SOSHibernateIntervalDBLayer;

/**
 * 
 * \class JadeHistoryDBLayer \brief JadeHistoryDBLayer -
 * 
 * \details
 * 
 * \section JadeHistoryDBLayer.java_intro_sec Introduction
 * 
 * \section JadeHistoryDBLayer.java_samples Some Samples
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
 * 
 * Created on 12.12.2011 14:40:18
 */

public class JadeFilesDBLayer extends SOSHibernateIntervalDBLayer implements Serializable{
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
    private final String      conClassName = "JadeFilesDBLayer";
    protected JadeFilesFilter filter       = null;
    private Logger logger = Logger.getLogger(JadeFilesDBLayer.class);

    public JadeFilesDBLayer(String configurationFileName) {
        super();
        this.setConfigurationFileName(configurationFileName);
        this.initConnection(this.getConfigurationFileName());
        this.resetFilter();
    }

    public JadeFilesDBItem get(Long id) {
        if (id == null) {
            return null;
        }
        try {
        	connection.connect();
        	connection.beginTransaction();
            return (JadeFilesDBItem) ((Session)this.connection.getCurrentSession()).get(JadeFilesDBItem.class, id);
        } catch (Exception e) {
            return null;
        }
    }

    public void resetFilter() {
        filter = new JadeFilesFilter();
        this.filter = new JadeFilesFilter();
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
        if (filter.getModificationDateFrom() != null) {
            where += and + " modificationDate >= :modificationDateFrom";
            and = " and ";
        }
        if (filter.getModificationDateTo() != null) {
            where += and + " modificationDate <= :modificationDateTo ";
            and = " and ";
        }
        if (filter.getMandator() != null && !filter.getMandator().equals("")) {
            where += and + " mandator=:mandator";
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
        if (filter.getSourceDir() != null && !filter.getSourceDir().equals("")) {
            where += and + " sourceDir=:sourceDir";
            and = " and ";
        }
        if (filter.getSourceFilename() != null && !filter.getSourceFilename().equals("")) {
            where += and + " sourceFilename like :sourceFilename";
            and = " and ";
        }
        if (filter.getSourceHost() != null && !filter.getSourceHost().equals("")) {
            where += and + " sourceHost=:sourceHost";
            and = " and ";
        }
        if (filter.getSourceHostIp() != null && !filter.getSourceHostIp().equals("")) {
            where += and + " sourceHostIp=:sourceHostIp";
            and = " and ";
        }
        if (filter.getSourceUser() != null && !filter.getSourceUser().equals("")) {
            where += and + " sourceUser=:sourceUser";
            and = " and ";
        }
        if (filter.getFileSize() != null) {
            where += and + " fileSize=:fileSize";
            and = " and ";
        }
        if (!where.trim().equals("")) {
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
        if (!where.trim().equals("")) {
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
        if (filter.getModificationDateFrom() != null && !filter.getModificationDateFrom().equals("")) {
            query.setTimestamp("modificationDateFrom", filter.getModificationDateFrom());
        }
        if (filter.getModificationDateTo() != null && !filter.getModificationDateTo().equals("")) {
            query.setTimestamp("modificationDateTo", filter.getModificationDateTo());
        }
        if (filter.getMandator() != null && !filter.getMandator().equals("")) {
            query.setText("mandator", filter.getMandator());
        }
        if (filter.getCreatedBy() != null && !filter.getCreatedBy().equals("")) {
            query.setText("createdBy", filter.getCreatedBy());
        }
        if (filter.getModifiedBy() != null && !filter.getModifiedBy().equals("")) {
            query.setText("modifiedBy", filter.getModifiedBy());
        }
        if (filter.getSourceDir() != null && !filter.getSourceDir().equals("")) {
            query.setText("sourceDir", filter.getSourceDir());
        }
        if (filter.getSourceFilename() != null && !filter.getSourceFilename().equals("")) {
            query.setText("sourceFilename", filter.getSourceFilename());
        }
        if (filter.getSourceHost() != null && !filter.getSourceHost().equals("")) {
            query.setText("sourceHost", filter.getSourceHost());
        }
        if (filter.getSourceHostIp() != null && !filter.getSourceHostIp().equals("")) {
            query.setText("sourceHostIp", filter.getSourceHostIp());
        }
        if (filter.getSourceUser() != null && !filter.getSourceUser().equals("")) {
            query.setText("sourceUser", filter.getSourceUser());
        }
        if (filter.getFileSize() != null) {
            query.setInteger("fileSize", filter.getFileSize());
        }
    }

    public int delete() {
        String q = "delete from JadeFilesHistoryDBItem e where e.jadeFilesDBItem.id IN (select id from JadeFilesDBItem " + getWhere() + ")";
        int row = 0;
        try {
        	if(connection == null){
        		initConnection(getConfigurationFileName());
        	}
        	connection.connect();
        	connection.beginTransaction();
	        Query query = connection.createQuery(q);
	        setWhere(query);
	        row = query.executeUpdate();
	        connection.commit();
	        String hql = "delete from JadeFilesDBItem " + getWhere();
        	connection.connect();
        	connection.beginTransaction();
	        query = connection.createQuery(hql);
	        setWhere(query);
	        row = query.executeUpdate();
	        connection.commit();
		} catch (Exception e) {
			logger.error("Error occurred while trying to delete items: ", e);
			e.printStackTrace();
		}
        return row;
    }

 

    public List<DbItem> getFilesFromTo(Date from, Date to)  {
        filter.setCreatedFrom(from); 
        filter.setCreatedTo(to);
        List<DbItem> resultset = null;
    	try {
			if(connection == null){
				initConnection(getConfigurationFileName());
			}
        	connection.connect();
			connection.beginTransaction();
			Query query = connection.createQuery("  from JadeFilesDBItem " + getWhere());
			if (filter.getCreatedFrom() != null) {
			   query.setTimestamp("createdFrom", filter.getCreatedFrom());
			}
			if (filter.getCreatedTo() != null) {
			   query.setTimestamp("createdTo", filter.getCreatedTo());
			}
			resultset = query.list();
		} catch (Exception e) {
			logger.error("Error occurred receiving Data for the given timeframe: ", e);
		}
        return resultset;
    }

 

    public List<JadeFilesHistoryDBItem> getFilesHistoryById(Long jadeId) throws ParseException {
        List<JadeFilesHistoryDBItem> resultset = null;
		try {
			if(connection == null){
				initConnection(getConfigurationFileName());
			}
        	connection.connect();
			connection.beginTransaction();
			Query query = connection.createQuery("  from JadeFilesHistoryDBItem where jadeId=:jadeId");
			query.setLong("jadeId", jadeId);
			resultset = query.list();
		} catch (Exception e) {
			logger.error("Error occurred getting DBItem: ", e);
		}
        return resultset;
    	
    }
    
    public List<JadeFilesDBItem> getFiles() throws ParseException {
        List<JadeFilesDBItem> resultset = null;
		try {
			if(connection == null){
				initConnection(getConfigurationFileName());
			}
        	connection.connect();
			connection.beginTransaction();
			Query query = connection.createQuery("  from JadeFilesDBItem " + getWhere());
			setWhere(query);
			resultset = query.list();
		} catch (Exception e) {
			logger.error("Error occurred receiving DBItems: ", e);
		}
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
    public JadeFilesFilter getFilter() {
        return filter;
    }

    public void setFilter(JadeFilesFilter filter) {
        this.filter = filter;
    }

    @Override
    public void onAfterDeleting(DbItem h) {        
    }

    @Override
    public List<DbItem> getListOfItemsToDelete()  {
         return getFilesFromTo(filter.getCreatedFrom(),filter.getCreatedTo());
    }

    @Override
    public long deleteInterval() {
        String q = "delete from JadeFilesHistoryDBItem e where e.jadeFilesDBItem.id IN (select id from JadeFilesDBItem " + getWhereFromTo() + ")";
        int row = 0;
		try {
			if(connection == null){
				initConnection(getConfigurationFileName());
			}
        	connection.connect();
			connection.beginTransaction();
			Query query = connection.createQuery(q);
			if (filter.getCreatedFrom() != null) {
			    query.setTimestamp("createdFrom", filter.getCreatedFrom());
			}
			if (filter.getCreatedTo() != null) {
			    query.setTimestamp("createdTo", filter.getCreatedTo());
			}
			row = query.executeUpdate();
	        connection.commit();
			String hql = "delete from JadeFilesDBItem " + getWhereFromTo();
        	connection.connect();
			connection.beginTransaction();
			query = connection.createQuery(hql);
			if (filter.getCreatedFrom() != null) {
			    query.setTimestamp("createdFrom", filter.getCreatedFrom());
			}
			if (filter.getCreatedTo() != null) {
			    query.setTimestamp("createdTo", filter.getCreatedTo());
			}
			row = query.executeUpdate();
	        connection.commit();
		} catch (Exception e) {
			logger.error("Error occurred while trying to delete interval: ", e);
		}
        return row;       
    }

}
