package com.sos.DataExchange.jaxb.configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.junit.Test;

public class testJADEConfiguration {

    private static final Logger LOGGER = Logger.getLogger(testJADEConfiguration.class);

    public testJADEConfiguration() {
        // nothing to do
    }

    @Test
    public void test() {
        try {
            // create a JAXBContext capable of handling classes generated into
            JAXBContext jc = JAXBContext.newInstance(ConfigurationElement.class);

            // create an Unmarshaller
            Unmarshaller u = jc.createUnmarshaller();

            // unmarshal an instance document into a tree of Java content
            ConfigurationElement objJADEConfig = (ConfigurationElement) u.unmarshal(new FileInputStream("ConfigurationExample.jadeconf"));
            Vector<Object> objProfileOrProfiles = (Vector<Object>) objJADEConfig.getIncludeOrProfileOrProfiles();
            for (Object object : objProfileOrProfiles) {
                if (object instanceof JADEProfile) {
                    iterateProfile(object);
                } else if (object instanceof JADEProfiles) {
                    iterateProfiles(object);
                }
            }
        } catch (JAXBException je) {
            LOGGER.error("", je);
        } catch (IOException ioe) {
            LOGGER.error("", ioe);
        }
    }

    private void iterateProfiles(final Object objP) {
        JADEProfiles objProfiles = (JADEProfiles) objP;
        LOGGER.info("Profiles name= " + objProfiles.getName());
        List<Object> objProfileOrProfiles = objProfiles.getIncludeOrProfile();
        for (Object object : objProfileOrProfiles) {
            if (object instanceof JADEProfile) {
                iterateProfile(object);
            }
        }
    }

    private void iterateProfile(final Object objP) {
        if (objP instanceof JADEProfile) {
            JADEProfile objProfile = (JADEProfile) objP;
            LOGGER.info("--- Profile name = " + objProfile.getName());
            for (Object object2 : objProfile.getIncludeOrIncludesOrParams()) {
                if (object2 instanceof JADEParam) {
                    JADEParam objParam = (JADEParam) object2;
                    LOGGER.info(" ... Param name = " + objParam.getName());
                    Object objV = objParam.getIncludeOrValues();
                    if (objV instanceof JADEParamValues) {
                        JADEParamValues objValues = (JADEParamValues) objV;
                        for (Object objV2 : objValues.getValue()) {
                            Value objValue = (Value) objV2;
                            LOGGER.info(String.format(" +++ value '%1$2' with prefix '%2$s'", objValue.getVal(), objValue.getPrefix()));
                        }
                    }
                } else if (object2 instanceof JADEParams) {
                    JADEParams objParams = (JADEParams) object2;
                    for (Object object3 : objParams.getParamOrParams()) {
                        if (object3 instanceof JADEParam) {
                            JADEParam objParam = (JADEParam) object3;
                            LOGGER.info("Param name = " + objParam.getName());
                            for (Object objV2 : objParam.getIncludeOrValues()) {
                                if (objV2 instanceof JADEParamValues) {
                                    JADEParamValues objValues = (JADEParamValues) objV2;
                                    for (Object objV3 : objValues.getValue()) {
                                        Value objValue = (Value) objV3;
                                        LOGGER.info(String.format(" +++ value '%1$s' with prefix '%2$s'", objValue.getVal(), objValue.getPrefix()));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testProfiles() {
        try {
            // create a JAXBContext capable of handling classes generated into
            JAXBContext jc = JAXBContext.newInstance(JADEProfiles.class);

            // create an Unmarshaller
            Unmarshaller u = jc.createUnmarshaller();

            // unmarshal an instance document into a tree of Java content
            ConfigurationElement objJADEConfig = (ConfigurationElement) u.unmarshal(new FileInputStream("ConfigurationExample.jadeconf"));

            // create a Marshaller and marshal to a file
            Marshaller m = jc.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.marshal(objJADEConfig, System.out);

        } catch (JAXBException je) {
            LOGGER.error("", je);
        } catch (IOException ioe) {
            LOGGER.error("", ioe);
        }
    }

}
