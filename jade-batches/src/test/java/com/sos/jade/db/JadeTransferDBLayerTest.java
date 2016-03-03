package com.sos.jade.db;

import static org.junit.Assert.*;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/** @author Uwe Risse  */

public class JadeTransferDBLayerTest {

    private static final Logger LOGGER = Logger.getLogger(JadeTransferDBLayerTest.class);
    private JadeTransferDBLayer jadeTransferDBLayer;
    private String configurationFilename = "c:/temp/hibernate.cfg.xml";
    private File configurationFile;

    @Before
    public void setUp() throws Exception {
        configurationFile = new File(configurationFilename);
        jadeTransferDBLayer = new JadeTransferDBLayer(configurationFile);
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
        // Test mit delete eines Bereiches
        jadeTransferDBLayer.beginTransaction();
        jadeTransferDBLayer.setDateFormat("yyyy-MM-dd hh:mm");
        jadeTransferDBLayer.setCreatedFrom("2011-01-01 00:00");
        jadeTransferDBLayer.setCreatedTo("2011-10-01 00:00");
        jadeTransferDBLayer.deleteFromTo();
        jadeTransferDBLayer.commit();
        List transferList = jadeTransferDBLayer.getTransferList(0);
        assertEquals(0, transferList.size());
        JadeTransferDBLayer d = new JadeTransferDBLayer(configurationFile);
        d.beginTransaction();
        JadeTransferDBItem jadeTransferDBItem = this.getNewTransferItem();
        jadeTransferDBItem.setStatus(47);
        d.save(jadeTransferDBItem);
        d.delete(jadeTransferDBItem);
        d.save(jadeTransferDBItem);
        d.delete(jadeTransferDBItem);
        d.commit();
        d.beginTransaction();
        Query query = d.createQuery("  from JadeTransferDBItem where status = :status");
        query.setParameter("status", 47);
        transferList = query.list();
        assertEquals(0, transferList.size());
    }

    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testFilesSelectFromTo() throws ParseException {
        JadeTransferDBLayer jadeTransferDBLayer = new JadeTransferDBLayer(configurationFile);
        jadeTransferDBLayer.setDateFormat("dd.MM.yyyy hh:mm");
        jadeTransferDBLayer.setCreatedFrom("07.09.2001 00:00");
        jadeTransferDBLayer.setCreatedTo("07.09.2021 00:00");
        try {
            List<JadeTransferDBItem> resultList = jadeTransferDBLayer.getTransfersFromTo();
            for (int i = 0; i < resultList.size(); i++) {
                JadeTransferDBItem transfer = (JadeTransferDBItem) resultList.get(i);
                if (transfer != null) {
                    if (i == 0) {
                        if (transfer.getSourceHost() == null) {

                        } else {
                            assertEquals("mySourceHost", transfer.getSourceHost());
                        }
                    }
                    LOGGER.info("History: " + transfer.getTransferId());
                }
            }

        } catch (ParseException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
