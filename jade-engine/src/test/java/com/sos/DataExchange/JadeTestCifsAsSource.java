package com.sos.DataExchange;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.sos.JSHelper.Options.SOSOptionTransferType.enuTransferTypes;

public class JadeTestCifsAsSource extends JadeTestBase {

    public JadeTestCifsAsSource() {
        enuSourceTransferType = enuTransferTypes.smb;
        enuTargetTransferType = enuTransferTypes.local;
    }

    /** \brief setUp
     *
     * \details
     *
     * \return void
     *
     * @throws java.lang.Exception */
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        objTestOptions.targetDir.setValue(strTestPathName);
        objTestOptions.getTarget().protocol.setValue(enuTargetTransferType);

        objTestOptions.getSource().protocol.setValue(enuSourceTransferType);

        objTestOptions.sourceDir.setValue("test/jadetest/SOSDEX/");
        objTestOptions.getSource().host.setValue("wilma.sos");
        objTestOptions.getSource().user.setValue("test");
        objTestOptions.getSource().password.setValue("12345");
    }

    @Override
    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSend2file_spec() throws Exception {
        super.testSend2file_spec();
    }

    @Override
    @Test
    public void testCopyMultipleFiles() throws Exception {
        super.testCopyMultipleFiles();
    }

    @Override
    @Test
    public void testCopyMultipleFilesThreaded() throws Exception {
        super.testCopyMultipleFilesThreaded();
    }

    @Override
    @Test
    public void testBigCopyThreaded() throws Exception {
        this.testBigCopy();
    }

    @Override
    @Test
    public void testCopyMultipleResultList() throws Exception {
        super.testCopyMultipleResultList();
    }

    @Override
    @Test
    @Ignore("Test set to Ignore for later examination")
    public void testSendAndDeleteMultipleFiles() throws Exception {
        super.testSendAndDeleteMultipleFiles();
    }

    @Override
    @Test
    public void testRenameFiles() throws Exception {
        super.testRenameFiles();
    }

    @Override
    @Test
    public void testSend() throws Exception {
        super.testSend();
    }

    @Test
    public void testSendRecursive() throws Exception {
        @SuppressWarnings("unused")
        final String conMethodName = CLASS_NAME + "::testSendRecursive";
        objTestOptions.recursive.value(true);
        objTestOptions.fileSpec.setValue("1\\.txt$");
        super.testSendFileSpec2();
    }

    @Override
    @Test
    public void testCopyAndRenameSourceAndTarget() throws Exception {
        super.testCopyAndRenameSourceAndTarget();
    }

}
