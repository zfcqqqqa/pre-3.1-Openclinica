package org.akaza.openclinica;

import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.apache.commons.dbcp.BasicDataSource;
import org.dbunit.DataSourceBasedDBTestCase;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import java.io.File;
import java.util.Locale;
import java.util.Properties;

import javax.sql.DataSource;

public abstract class OcDbTestCase extends DataSourceBasedDBTestCase {

    Properties properties = new Properties();
    private final String dbName;
    private final String dbUrl;
    private final String dbUserName;
    private final String dbPassword;
    private final String dbDriverClassName;
    private final String locale;

    public OcDbTestCase() {
        super();
        loadProperties();
        dbName = properties.getProperty("dbName");
        dbUrl = properties.getProperty("dbUrl");
        dbUserName = properties.getProperty("dbUsername");
        dbPassword = properties.getProperty("dbPassword");
        dbDriverClassName = properties.getProperty("dbDriverClassName");
        locale = properties.getProperty("locale");
        initializeLocale();
        initializeQueriesInXml();
    }

    @Override
    protected IDataSet getDataSet() throws Exception {
        return new FlatXmlDataSet(OcDbTestCase.class.getResourceAsStream(getTestDataFilePath()));
    }

    @Override
    public DataSource getDataSource() {
        BasicDataSource ds = new BasicDataSource();
        ds.setAccessToUnderlyingConnectionAllowed(true);
        ds.setDriverClassName(dbDriverClassName);
        ds.setUsername(dbUserName);
        ds.setPassword(dbPassword);
        ds.setUrl(dbUrl);
        return ds;
    }

    private void loadProperties() {
        try {
            properties.load(OcDbTestCase.class.getResourceAsStream(getPropertiesFilePath()));
        } catch (Exception ioExc) {
            ioExc.printStackTrace();
        }
    }

    private void initializeLocale() {
        ResourceBundleProvider.updateLocale(new Locale(locale));
    }

    /**
     * Instantiates SQLFactory and all the xml files that contain the queries that
     * are used in our dao class 
     */
    private void initializeQueriesInXml() {
        String baseDir = System.getProperty("basedir");
        if (baseDir == null || "".equalsIgnoreCase(baseDir)) {
            throw new IllegalStateException(
                    "The system properties basedir were not made available to the application. Therefore we cannot locate the test properties file.");
        }
        SQLFactory.JUNIT_XML_DIR =
            baseDir + File.separator + "src" + File.separator + "main" + File.separator + "webapp" + File.separator + "properties" + File.separator;
        SQLFactory.getInstance().run(dbName);
    }

    private String getPropertiesFilePath() {
        return File.separator + "test.properties";
    }

    /**
     * Gets the path and the name of the xml file holding the data.
     * Example if your Class Name is called 
     *      org.akaza.openclinica.service.rule.expression.TestExample.java
     * you need an xml data file in resources folder under same path + testdata + same Class Name .xml
     *      org/akaza/openclinica/service/rule/expression/testdata/TestExample.xml
     *
     * @return path to data file
     */
    private String getTestDataFilePath() {
        StringBuffer path = new StringBuffer(File.separator);
        path.append(getClass().getPackage().getName().replace(".", File.separator));
        path.append(File.separator + "testdata" + File.separator);
        path.append(getClass().getSimpleName() + ".xml");
        return path.toString();
    }
}