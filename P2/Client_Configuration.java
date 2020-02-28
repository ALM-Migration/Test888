/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpackage.connections;

//import com.kpit.integrity.wizard.core.TYPE;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Properties;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.log4j.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author mi297
 */
public class Client_Configuration {

    private static final Logger logger = Logger.getLogger(Client_Configuration.class.getName());
    // private HashMap<Systems, LinkedHashSet<Systems>> businessToTechMap = new LinkedHashMap<>();
    //private Set<Systems> BA_NodesWith_TA_as_Child;
    private static Client_Configuration currentInstance = null;
    private Properties properties = null;
    private final String key = "97059FD85F70789E59ABC65B9FCFAD2A";

    /*   static
    {
        //initialize logger 
        InputStream stream = Client_Configuration.class.getResourceAsStream("/log4j.properties");
        PropertyConfigurator.configure(stream);

    }
     */
    public static Client_Configuration getInstance() {
        if (currentInstance == null) {
            currentInstance = new Client_Configuration();
        }
        return currentInstance;
    }

    private Client_Configuration() {
        try {

            this.properties = new Properties();
            loadUserPropertiesFile();
        } catch (Exception ex) {
            logger.error("Error Occoured while loading default Configurations", ex);
            System.exit(404);
        }
    }

    /**
     * This function parses Systems.xml file and stores systems in Map as
     * business_area,technical_area
     *
     * @param filePath
     */
    // @Deprecated
    private void parseSystemsXMLDocument(String filePath) throws Exception {
        //erroronous method
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = factory.newDocumentBuilder();

            Document document = db.parse(Client_Configuration.class.getClassLoader().getResourceAsStream(filePath));
            // Document document = db.parse(new File("Systems.xml"));
            NodeList nodeList = document.getElementsByTagName("BusinessArea");
            int listSize = nodeList.getLength();
            Node node = null;
            Node techAreaNode = null;
            Element element = null;
            String businessAreaName;
            String techAreaName;

            NodeList techList;
            int techListSize;
            for (int i = 0; i < listSize; i++) {
                node = nodeList.item(i);
                if (node.getNodeType() == Element.ELEMENT_NODE) {

                    element = (Element) node;
                    businessAreaName = element.getAttribute("name");

                    //Systems ba = new Systems(TYPE.BUSINESS_AREA, businessAreaName);
                    //remove t businessToTechMap.put(ba, new LinkedHashSet<Systems>());
                    techList = node.getChildNodes();
                    techListSize = techList.getLength();
                    for (int j = 0; j < techListSize; j++) {
                        techAreaNode = techList.item(j);
                        if (techAreaNode.getNodeType() == Element.ELEMENT_NODE) {
                            element = (Element) techAreaNode;
                            techAreaName = element.getAttribute("name");
                            // System.out.println("\t\t" + techAreaName);
                            //  Systems ta = new Systems(TYPE.TECHNICAL_AREA, techAreaName);
                            // businessToTechMap.get(ba).add(ta);
                        }
                    }
                }
            }

        } /*catch (ParserConfigurationException | SAXException | IOException ex)  /*catch (ParserConfigurationException | SAXException | IOException ex) 
         */ catch (Exception ex) {
            // ex.printStackTrace();
            throw new Exception("error occured while loading xml file", ex);

        }

    }

//    public Set<Systems> getBusinessAreas()
//    {
//
//        return BA_NodesWith_TA_as_Child;
//    }
    /**
     *
     * @param businessAreaToSearch
     * @return all the technical area in @param business area
     */
    /**
     * @param key this value will be searched in properties file
     * @return value represented by key in properties file
     */
    public String getPropertyValue(String key) {
        String value = this.properties.getProperty(key);

        return value;
    }

    /**
     *
     * @param key this value will be searched in properties file
     * @param defaultValue if property found is null then it returns default
     * value
     * @return
     */
    public String getPropertyValue(String key, String defaultValue) {
        String value = this.properties.getProperty(key);
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }

    /**
     * @param this loads dvp&r.properties file in the class object properties
     *
     */
    private void loadDefaultPropertiesFile() {
        logger.debug("loading default properties file from jar");
        InputStream inputStream = null;

        try {
            inputStream = Client_Configuration.class.getClassLoader().getResourceAsStream(Client_Constant.DEFAULT_PROPERTIES_FILE_NAME);
            if (inputStream != null) {
                this.properties.load(inputStream);

            } else {
                throw new NullPointerException("Unable to locate properties file");
            }
        } catch (Exception e) {
            logger.debug("Error while loading properties file.", e);

        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    logger.debug(ex);
                }
            }
        }
    }
    // to get userid/password from properties file 
    //e.g. C:\Users\oz931\.dvprWizard\authentication.properties

    public boolean readUserAuthenticationPropertiesFile() {
        logger.debug("loading default properties file from jar");
        InputStream inputStream = null;
        Properties userProperties = new Properties();
        Map<String, String> env = System.getenv();
        String userDirectoryName = env.get(Client_Constant.USER_HOME_DIRECTORY_ENVOIRNMENT_VARIABLE);
        String userPropFilePath = userDirectoryName + File.separator + Client_Constant.WIZARD_DIRECTORY_NAME + File.separator + Client_Constant.AUTHENTICATION_PROPERTIES_FILE_NAME;
        // //e.g., C:\Users\oz931\.dvprWizard\authentication.properties

        try {

            File usrFile = new File(userPropFilePath);
            if (usrFile.exists() && !usrFile.isDirectory()) {
                inputStream = new FileInputStream(usrFile);
                userProperties.load(inputStream);
                Client_Constant.CONFIG_USER_NAME = userProperties.getProperty("integrity.auth.user");
                Client_Constant.CONFIG_PASSWORD = userProperties.getProperty("integrity.auth.password");
                logger.info("CONFIG_USER_NAME : " + userProperties.getProperty("integrity.auth.user"));
                logger.info("CONFIG_PASSWORD : " + userProperties.getProperty("integrity.auth.password"));
                if (Client_Constant.CONFIG_USER_NAME == null || Client_Constant.CONFIG_PASSWORD == null) {
                    return false;
                } else {

                    Client_Constant.CONFIG_PASSWORD = GeneratePlainPassword.getPassword(key, Client_Constant.CONFIG_PASSWORD);

                    return true;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            logger.error("Error while loading AUTHENTICATION_PROPERTIES_FILE properties file :" + e.getMessage(), e);
            return false;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    logger.debug(ex);
                }
            }
        }

    }

    // to set userid and password- kaustubh
    public void setUserAuthenticationPropertiesFile(String user, String pass) {
        logger.debug("set user/password in properties file");
        OutputStream outputStream = null;
        Properties userProperties = new Properties();
        Map<String, String> env = System.getenv();
        String userDirectoryName = env.get(Client_Constant.USER_HOME_DIRECTORY_ENVOIRNMENT_VARIABLE);
        String userPropFilePath = userDirectoryName + File.separator + Client_Constant.WIZARD_DIRECTORY_NAME + File.separator + Client_Constant.AUTHENTICATION_PROPERTIES_FILE_NAME;
        //e.g., C:\Users\oz931\.dvprWizard\authentication.properties
        try {

            File usrFile = new File(userPropFilePath);
            if (usrFile.exists() && !usrFile.isDirectory()) {
                logger.debug("AuthenticationPropertiesFile Exists on location");
            } else {
                logger.debug("AuthenticationPropertiesFile not Exists on location");
            }
            outputStream = new FileOutputStream(usrFile);
            userProperties.setProperty("integrity.auth.user", user);//integrity.auth.user="USER_NAME"
            pass = GenerateEncryptionPassword.setEncryptedPassword(key, pass);
            userProperties.setProperty("integrity.auth.password", pass);//integrity.auth.password="pass"
            // save properties to project root folder
            userProperties.store(outputStream, null);

        } catch (Exception e) {
            logger.error("Error while set AUTHENTICATION_PROPERTIES_FILE  properties file :" + e.getMessage(), e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ex) {
                    logger.debug(ex);
                }
            }
        }

    }

    private void loadUserPropertiesFile() {
        logger.debug("loading default properties file from jar");
        InputStream inputStream = null;
        Properties userProperties = new Properties();
        Map<String, String> env = System.getenv();
        String userDirectoryName = env.get(Client_Constant.USER_HOME_DIRECTORY_ENVOIRNMENT_VARIABLE);
        String userPropFilePath = userDirectoryName + File.separator + Client_Constant.WIZARD_DIRECTORY_NAME + File.separator + Client_Constant.USER_PROPERTIES_FILE_NAME;
        //e.g., C:\Users\ns077\.dvprWizard\dvp&r.properties
        FileOutputStream os = null;
        try {

            File usrFile = new File(userPropFilePath);

            if (!usrFile.exists()) {
                File parentFile = usrFile.getParentFile();
                if (!parentFile.exists()) {
                    parentFile.mkdir();
                }
                usrFile.createNewFile();
            }
            if (usrFile.exists() && !usrFile.isDirectory()) {
                inputStream = new FileInputStream(usrFile);
                userProperties.load(inputStream);
                //load user properties
                this.properties.putAll(userProperties);

            } else {
                logger.info("User Properties file not found.. creating new file: " + userPropFilePath);
                usrFile.createNewFile();
                os = new FileOutputStream(usrFile);
                //add properties from dvp&_default.properties to user properties file
                userProperties.putAll(this.properties);
                userProperties.remove(Client_Constant.PROPERTY_INTEGRITY_AUTH_USER);
                userProperties.remove(Client_Constant.PROPERTY_INTEGRITY_AUTH_PASSWORD);
                userProperties.store(os, null);

            }
        } // inputStream = Configuration.class.getClassLoader().getResourceAsStream(Constant.DEFAULT_PROPERTIES_FILE_NAME);
        catch (Exception e) {
            logger.error("Error while loading user properties file :" + e.getMessage(), e);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException ex) {
                    logger.debug(ex);
                }
            }

            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    logger.debug(ex);
                }
            }
        }
    }

    public static String encrypt(String strClearText, String strKey) throws Exception {
        String strData = "";

        try {
            SecretKeySpec skeyspec = new SecretKeySpec(strKey.getBytes(), "Blowfish");
            Cipher cipher = Cipher.getInstance("Blowfish");
            cipher.init(Cipher.ENCRYPT_MODE, skeyspec);
            byte[] encrypted = cipher.doFinal(strClearText.getBytes());
            strData = new String(encrypted);

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        }
        return strData;
    }

    public static String decrypt(String strEncrypted, String strKey) throws Exception {
        String strData = "";

        try {
            SecretKeySpec skeyspec = new SecretKeySpec(strKey.getBytes(), "Blowfish");
            Cipher cipher = Cipher.getInstance("Blowfish");
            cipher.init(Cipher.DECRYPT_MODE, skeyspec);
            byte[] decrypted = cipher.doFinal(strEncrypted.getBytes());
            strData = new String(decrypted);

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        }
        return strData;
    }

}
