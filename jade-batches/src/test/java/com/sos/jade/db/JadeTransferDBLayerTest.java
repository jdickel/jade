package com.sos.jade.db;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author Uwe Risse */
public class JadeTransferDBLayerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(JadeTransferDBLayerTest.class);
    private JadeTransferDBLayer jadeTransferDBLayer;
    private String configurationFilename = "c:/temp/hibernate.cfg.xml";

    @Before
    public void setUp()  {
        jadeTransferDBLayer = new JadeTransferDBLayer(configurationFilename);
    }

    private JadeTransferDBItem getNewTransferItem() {
        JadeTransferDBItem transferItem = new JadeTransferDBItem();
        transferItem.setMandator("myMandator");
        transferItem.setSourceHost("mySourceHost");
        transferItem.setSourceHostIp("mySourceHostIp");
        transferItem.setSourceUser("mySourceUser");
        transferItem.setSourceDir("mySourceDir");
        transferItem.setFileSize(new Long(1));
        transferItem.setProtocolType(1);
        transferItem.setPort(4711);
        transferItem.setTargetHost("myTargetHost");
        transferItem.setTargetHostIp("myTargetHostIp");
        transferItem.setTargetUser("myTargetUser");
        transferItem.setTargetDir("myTargetDir");
        transferItem.setTargetDir("myTargetDir");
        transferItem.setStartTime(new Date());
        transferItem.setEndTime(new Date());
        transferItem.setFilesCount(2);
        transferItem.setStatus(1);
        transferItem.setProfileName("myProfileName");
        transferItem.setProfile("myProfile");
        transferItem.setLog("myLog");
        transferItem.setLastErrorMessage("myLastErrorMessage");
        transferItem.setCommandType(3);
        transferItem.setCommand("myCommand");
        transferItem.setModifiedBy("myModifiedBy");
        transferItem.setModified(new Date());
        transferItem.setCreatedBy("myCreatedBy");
        transferItem.setCreated(new Date());
        return transferItem;
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testDeleteFromTo() throws ParseException {
        try {
            jadeTransferDBLayer.getSession().connect();
            jadeTransferDBLayer.getSession().beginTransaction();
            jadeTransferDBLayer.setDateFormat("yyyy-MM-dd hh:mm");
            jadeTransferDBLayer.setCreatedFrom("2011-01-01 00:00");
            jadeTransferDBLayer.setCreatedTo("2011-10-01 00:00");
            jadeTransferDBLayer.deleteFromTo();
            jadeTransferDBLayer.getSession().commit();
            List transferList = jadeTransferDBLayer.getTransferList(0);
            assertEquals(0, transferList.size());
            JadeTransferDBLayer d = new JadeTransferDBLayer(configurationFilename);
            d.getSession().connect();
            d.getSession().beginTransaction();
            JadeTransferDBItem jadeTransferDBItem = this.getNewTransferItem();
            jadeTransferDBItem.setStatus(47);
            d.getSession().save(jadeTransferDBItem);
            d.getSession().delete(jadeTransferDBItem);
            d.getSession().save(jadeTransferDBItem);
            d.getSession().delete(jadeTransferDBItem);
            d.getSession().commit();
            d.getSession().connect();
            d.getSession().beginTransaction();
            Query query = d.getSession().createQuery("  from JadeTransferDBItem where status = :status");
            query.setParameter("status", 47);
            transferList = query.list();
            assertEquals(0, transferList.size());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testFilesSelectFromTo() throws Exception {
        JadeTransferDBLayer jadeTransferDBLayer = new JadeTransferDBLayer(configurationFilename);
        jadeTransferDBLayer.setDateFormat("dd.MM.yyyy hh:mm");
        jadeTransferDBLayer.setCreatedFrom("07.09.2001 00:00");
        jadeTransferDBLayer.setCreatedTo("07.09.2021 00:00");
        try {
            List<JadeTransferDBItem> resultList = jadeTransferDBLayer.getTransfersFromTo();
            for (int i = 0; i < resultList.size(); i++) {
                JadeTransferDBItem transfer = (JadeTransferDBItem) resultList.get(i);
                if (transfer != null) {
                    if (i == 0) {
                        if (transfer.getSourceHost() != null) {
                            assertEquals("mySourceHost", transfer.getSourceHost());
                        }
                    }
                    LOGGER.info("History: " + transfer.getTransferId());
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
