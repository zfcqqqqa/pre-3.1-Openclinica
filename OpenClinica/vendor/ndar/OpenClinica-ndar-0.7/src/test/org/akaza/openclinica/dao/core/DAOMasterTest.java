/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */ 
package test.org.akaza.openclinica.dao.core;

import java.io.File;
import java.io.FileInputStream;

import org.akaza.openclinica.dao.core.DAODigester;
import test.org.akaza.openclinica.dao.submit.CRFVersionDAOTest;
import test.org.akaza.openclinica.dao.managestudy.StudyDAOTest;
import test.org.akaza.openclinica.dao.login.UserAccountDAOTest;
import org.xml.sax.SAXException;

/**
 * @author jxu
 *  
 */
public class DAOMasterTest extends DAOTestBase {
  

  private DAODigester digester = new DAODigester();

  public DAOMasterTest(String name) {
    super(name);
  }

  public static void main(String[] args) {
    junit.swingui.TestRunner.run(UserAccountDAOTest.class);
    junit.swingui.TestRunner.run(StudyDAOTest.class);
    junit.swingui.TestRunner.run(CRFVersionDAOTest.class);
    //  can add more....
  }

  protected void setUp() throws Exception {
    super.setUp();
    digester.setInputStream(new FileInputStream(SQL_DIR + "crfversion_dao.xml"));
    digester.setInputStream(new FileInputStream(SQL_DIR + "study_dao.xml"));
    digester.setInputStream(new FileInputStream(SQL_DIR + "useraccount_dao.xml"));
    //can add more....

    try {
      digester.run();
    } catch (SAXException saxe) {
      saxe.printStackTrace();
    }

    super.ds = setupTestDataSource();

  }

  /**
   * Tests finding all records from a table
   * 
   * @throws Exception
   */
  public void testFindAll() throws Exception {
    return;
  }

}