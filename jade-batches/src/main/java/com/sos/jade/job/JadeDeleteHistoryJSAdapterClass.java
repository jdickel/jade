package com.sos.jade.job;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sos.scheduler.job.JobSchedulerJobAdapter;
import sos.spooler.Spooler;

import com.sos.JSHelper.Basics.IJSCommands;

public class JadeDeleteHistoryJSAdapterClass extends JobSchedulerJobAdapter implements IJSCommands {

    private final String conClassName = "JadeDeleteHistoryJSAdapterClass";  //$NON-NLS-1$
    private static final Logger LOGGER1 = LoggerFactory.getLogger(JadeDeleteHistoryJSAdapterClass.class);

    public void init() {
        @SuppressWarnings("unused")
        final String conMethodName = conClassName + "::init"; //$NON-NLS-1$
        doInitialize();
    }

    private void doInitialize() {
    } // doInitialize

    @Override
    public boolean spooler_init() {
        @SuppressWarnings("unused")
        final String conMethodName = conClassName + "::spooler_init"; //$NON-NLS-1$
        return super.spooler_init();
    }

    @Override
    public boolean spooler_process() throws Exception {
        @SuppressWarnings("unused")
        final String conMethodName = conClassName + "::spooler_process"; //$NON-NLS-1$

        try {
            super.spooler_process();
            doProcessing();
        } catch (Exception e) {
            return false;
        } finally {
        } // finally
          // return value for classic and order driven processing
          // TODO create method in base-class for this functionality
        return spooler_task.job().order_queue() != null;

    } // spooler_process

    @Override
    public void spooler_exit() {
        @SuppressWarnings("unused")
        final String conMethodName = conClassName + "::spooler_exit"; //$NON-NLS-1$
        super.spooler_exit();
    }

    private void doProcessing() throws Exception {
        IJSCommands objJSCommands = this;
        @SuppressWarnings("unused")
        final String conMethodName = conClassName + "::doProcessing"; //$NON-NLS-1$

        JadeDeleteHistory objR = new JadeDeleteHistory();
        JadeDeleteHistoryOptions objO = objR.getOptions();
        objO.setCurrentNodeName(getCurrentNodeName());
        objO.setAllOptions(getSchedulerParameterAsProperties(getParameters()));
        objO.checkMandatory();
        objR.setJSJobUtilites(this);

        Object objSp = objJSCommands.getSpoolerObject();
        Spooler objSpooler = (Spooler) objSp;

        String configuration_file = "";

        if (objO.getItem("configuration_file") != null) {
            LOGGER1.debug("configuration_file from param");
            configuration_file = objO.configuration_file.getValue();
        } else {
            LOGGER1.debug("configuration_file from scheduler");
            File f = new File(objSpooler.configuration_directory(), "hibernate.cfg.xml");
            configuration_file = f.getAbsolutePath();

        }

        objO.configuration_file.setValue(configuration_file);

        objO.configuration_file.setValue(configuration_file);
        objR.Execute();
    } // doProcessing

}
