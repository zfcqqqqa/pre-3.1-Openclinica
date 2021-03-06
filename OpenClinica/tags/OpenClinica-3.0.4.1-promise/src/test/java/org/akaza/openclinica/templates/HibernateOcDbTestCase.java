package org.akaza.openclinica.templates;

import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.apache.commons.dbcp.BasicDataSource;
import org.dbunit.DataSourceBasedDBTestCase;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.io.File;
import java.util.Locale;
import java.util.Properties;

import javax.sql.DataSource;

public abstract class HibernateOcDbTestCase extends DataSourceBasedDBTestCase {

    protected final Logger logger = LoggerFactory.getLogger(getClass().getName());
    private PlatformTransactionManager transactionManager;
    private ApplicationContext context;

    Properties properties = new Properties();
    private final String dbName;
    private final String dbUrl;
    private final String dbUserName;
    private final String dbPassword;
    private final String dbDriverClassName;
    private final String locale;

    public HibernateOcDbTestCase() {
        super();
        loadProperties();
        dbName = properties.getProperty("dbName");
        dbUrl = properties.getProperty("url");
        dbUserName = properties.getProperty("username");
        dbPassword = properties.getProperty("password");
        dbDriverClassName = properties.getProperty("driver");
        locale = properties.getProperty("locale");
        initializeLocale();
        initializeQueriesInXml();
        setUpContext();

    }

    @Override
    protected void setUp() throws Exception {
        // TODO Auto-generated method stub
        super.setUp();
    }

    private void setUpContext() {
        // Loading the applicationContext under test/resources first allows test.properties to be loaded first.Hence we can
        // use different settings.
        context =
            new ClassPathXmlApplicationContext(
                    new String[] { "classpath*:applicationContext*.xml", "classpath*:org/akaza/openclinica/applicationContext*.xml", });
        transactionManager = (PlatformTransactionManager) context.getBean("transactionManager");
        transactionManager.getTransaction(new DefaultTransactionDefinition());
    }

    @Override
    protected IDataSet getDataSet() throws Exception {
        return new FlatXmlDataSet(HibernateOcDbTestCase.class.getResourceAsStream(getTestDataFilePath()));
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

    public ApplicationContext getContext() {
        return context;
    }

    private void loadProperties() {
        try {
            properties.load(HibernateOcDbTestCase.class.getResourceAsStream(getPropertiesFilePath()));
        } catch (Exception ioExc) {
            ioExc.printStackTrace();
        }
    }

    private void initializeLocale() {
        ResourceBundleProvider.updateLocale(new Locale(locale));
    }

    /**
     * Instantiates SQLFactory and all the xml files that contain the queries that are used in our dao class
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
        return "/test.properties";
    }

    /**
     * Gets the path and the name of the xml file holding the data. Example if your Class Name is called
     * org.akaza.openclinica.service.rule.expression.TestExample.java you need an xml data file in resources folder under same path + testdata + same Class Name
     * .xml org/akaza/openclinica/service/rule/expression/testdata/TestExample.xml
     * 
     * @return path to data file
     */
    private String getTestDataFilePath() {
        StringBuffer path = new StringBuffer("/");
        path.append(getClass().getPackage().getName().replace(".", "/"));
        path.append("/testdata/");
        path.append(getClass().getSimpleName() + ".xml");
        return path.toString();
    }

    public String getDbName() {
        return dbName;
    }
}