package org.akaza.openclinica.logic.expressionTree;

import org.akaza.openclinica.exception.OpenClinicaSystemException;
import org.joda.time.DateMidnight;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import junit.framework.TestCase;

public class OpenClinicaExpressionParserTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();

    }

    public void testDateArithmeticParseAndTestEvaluateExpression() throws OpenClinicaSystemException {
        String expressionA = "2008-12-03 + 3 gt 2008-12-04";
        String expressionB = "2008-12-03 - 3 lt 2008-12-04";
        String expressionC = "3 + 2008-12-03 gt 2008-12-04";
        String expressionD = "2008-12-04 lt 3 + 2008-12-03";
        String expressionE = "2008-12-03 +3 gt 2008-12-04";
        String expressionF = "2008-12-03 -12 lt 2008-12-04";
        String expressionG = "2008-12-04 eq 2008-12-03 + 1";
        String expressionH = "2008-12-03 + 1.5 eq 2008-12-04";
        String expressionI = "2008-12-04 lt \"3\" + 2008-12-03";
        String expressionJ = "2008-12-03 + 3 eq 3 + 2008-12-03";

        OpenClinicaExpressionParser expressionParserA = new OpenClinicaExpressionParser();
        OpenClinicaExpressionParser expressionParserB = new OpenClinicaExpressionParser();
        OpenClinicaExpressionParser expressionParserC = new OpenClinicaExpressionParser();
        OpenClinicaExpressionParser expressionParserD = new OpenClinicaExpressionParser();
        OpenClinicaExpressionParser expressionParserE = new OpenClinicaExpressionParser();
        OpenClinicaExpressionParser expressionParserF = new OpenClinicaExpressionParser();
        OpenClinicaExpressionParser expressionParserG = new OpenClinicaExpressionParser();
        OpenClinicaExpressionParser expressionParserH = new OpenClinicaExpressionParser();
        OpenClinicaExpressionParser expressionParserI = new OpenClinicaExpressionParser();
        OpenClinicaExpressionParser expressionParserJ = new OpenClinicaExpressionParser();

        String resultA = expressionParserA.parseAndTestEvaluateExpression(expressionA);
        String resultB = expressionParserB.parseAndTestEvaluateExpression(expressionB);
        String resultC = expressionParserC.parseAndTestEvaluateExpression(expressionC);
        String resultD = expressionParserD.parseAndTestEvaluateExpression(expressionD);
        String resultE = expressionParserE.parseAndTestEvaluateExpression(expressionE);
        String resultF = expressionParserF.parseAndTestEvaluateExpression(expressionF);
        String resultG = expressionParserG.parseAndTestEvaluateExpression(expressionG);
        String resultH = expressionParserH.parseAndTestEvaluateExpression(expressionH);
        String resultI = expressionParserI.parseAndTestEvaluateExpression(expressionI);
        String resultJ = expressionParserJ.parseAndTestEvaluateExpression(expressionJ);

        assertEquals("The result should be true", "true", resultA);
        assertEquals("The result should be true", "true", resultB);
        assertEquals("The result should be true", "true", resultC);
        assertEquals("The result should be true", "true", resultD);
        assertEquals("The result should be true", "true", resultE);
        assertEquals("The result should be true", "true", resultF);
        assertEquals("The result should be true", "true", resultG);
        assertEquals("The result should be true", "true", resultH);
        assertEquals("The result should be true", "true", resultI);
        assertEquals("The result should be true", "true", resultJ);
    }

    public void testDateArithmeticParseAndTestEvaluateExpressionFail() throws OpenClinicaSystemException {
        String expressionA = "2008-12-03 + \"kk\" gt 2008-12-04";
        // String expressionB = "2008-12-03 - 2008-12-05 lt 2008-12-04";
        String expressionC = "\"2008-12-03\" + 2008-12-03 gt 2008-12-04";
        String expressionD = "2008-12-03 * 2 gt 2008-12-04";
        String expressionE = "2008-12-03 / 2 gt 2008-12-04";

        OpenClinicaExpressionParser expressionParserA = new OpenClinicaExpressionParser();
        // OpenClinicaExpressionParser expressionParserB = new OpenClinicaExpressionParser();
        OpenClinicaExpressionParser expressionParserC = new OpenClinicaExpressionParser();
        OpenClinicaExpressionParser expressionParserD = new OpenClinicaExpressionParser();
        OpenClinicaExpressionParser expressionParserE = new OpenClinicaExpressionParser();

        try {
            expressionParserA.parseAndTestEvaluateExpression(expressionA);
        } catch (OpenClinicaSystemException e) {
            assertEquals("OpenClinica system exception", "2008-12-03 and kk cannot be used with the PLUS operator", e.getMessage());
        }

        /*
         * try { expressionParserB.parseAndTestEvaluateExpression(expressionB); } catch (OpenClinicaSystemException e) {
         * assertEquals("OpenClinica system exception", "2008-12-03 and 2008-12-05 cannot be used with the MINUS operator", e.getMessage()); }
         */

        try {
            expressionParserC.parseAndTestEvaluateExpression(expressionC);
        } catch (OpenClinicaSystemException e) {
            assertEquals("OpenClinica system exception", "2008-12-03 and 2008-12-03 cannot be used with the PLUS operator", e.getMessage());
        }

        try {
            expressionParserD.parseAndTestEvaluateExpression(expressionD);
        } catch (OpenClinicaSystemException e) {
            assertEquals("OpenClinica system exception", "2008-12-03 and 2.0 cannot be used with the MULTIPLY operator", e.getMessage());
        }

        try {
            expressionParserE.parseAndTestEvaluateExpression(expressionE);
        } catch (OpenClinicaSystemException e) {
            assertEquals("OpenClinica system exception", "2008-12-03 and 2.0 cannot be used with the DIVIDE operator", e.getMessage());
        }
    }

    public void testDateArithmeticParseAndEvaluateExpression() throws OpenClinicaSystemException {
        String expressionA = "2008-12-03 + 3 gt 2008-12-04";
        String expressionB = "2008-12-03 - 3 lt 2008-12-04";
        String expressionC = "3 + 2008-12-03 gt 2008-12-04";
        String expressionD = "2008-12-04 lt 3 + 2008-12-03";
        String expressionE = "2008-12-03 +3 gt 2008-12-04";
        String expressionF = "2008-12-03 -12 lt 2008-12-04";
        String expressionG = "2008-12-04 eq 2008-12-03 + 1";
        String expressionH = "2008-12-03 + 1.5 eq 2008-12-04";
        String expressionI = "2008-12-03 - 2008-12-01 eq 2";
        String expressionJ = "2009-03-02 - 2009-02-25 eq 5";
        String expressionK = "2008-12-01 - 2008-12-03";
        String expressionL = "2008-12-01 - 2008-12-03 gt 1";
        String expressionM = "2008-12-01 - 2008-12-03 gt 1 and 1 lt 2008-12-01 - 2008-12-03";

        OpenClinicaExpressionParser expressionParserA = new OpenClinicaExpressionParser();
        OpenClinicaExpressionParser expressionParserB = new OpenClinicaExpressionParser();
        OpenClinicaExpressionParser expressionParserC = new OpenClinicaExpressionParser();
        OpenClinicaExpressionParser expressionParserD = new OpenClinicaExpressionParser();
        OpenClinicaExpressionParser expressionParserE = new OpenClinicaExpressionParser();
        OpenClinicaExpressionParser expressionParserF = new OpenClinicaExpressionParser();
        OpenClinicaExpressionParser expressionParserG = new OpenClinicaExpressionParser();
        OpenClinicaExpressionParser expressionParserH = new OpenClinicaExpressionParser();
        OpenClinicaExpressionParser expressionParserI = new OpenClinicaExpressionParser();
        OpenClinicaExpressionParser expressionParserJ = new OpenClinicaExpressionParser();
        OpenClinicaExpressionParser expressionParserK = new OpenClinicaExpressionParser();
        OpenClinicaExpressionParser expressionParserL = new OpenClinicaExpressionParser();
        OpenClinicaExpressionParser expressionParserM = new OpenClinicaExpressionParser();

        String resultA = expressionParserA.parseAndEvaluateExpression(expressionA);
        String resultB = expressionParserB.parseAndEvaluateExpression(expressionB);
        String resultC = expressionParserC.parseAndEvaluateExpression(expressionC);
        String resultD = expressionParserD.parseAndEvaluateExpression(expressionD);
        String resultE = expressionParserE.parseAndEvaluateExpression(expressionE);
        String resultF = expressionParserF.parseAndEvaluateExpression(expressionF);
        String resultG = expressionParserG.parseAndEvaluateExpression(expressionG);
        String resultH = expressionParserH.parseAndEvaluateExpression(expressionH);
        String resultI = expressionParserI.parseAndEvaluateExpression(expressionI);
        String resultJ = expressionParserJ.parseAndEvaluateExpression(expressionJ);
        String resultK = expressionParserK.parseAndEvaluateExpression(expressionK);
        String resultL = expressionParserL.parseAndEvaluateExpression(expressionL);
        String resultM = expressionParserM.parseAndEvaluateExpression(expressionM);

        assertEquals("The result should be true", "true", resultA);
        assertEquals("The result should be true", "true", resultB);
        assertEquals("The result should be true", "true", resultC);
        assertEquals("The result should be true", "true", resultD);
        assertEquals("The result should be true", "true", resultE);
        assertEquals("The result should be true", "true", resultF);
        assertEquals("The result should be true", "true", resultG);
        assertEquals("The result should be true", "true", resultH);
        assertEquals("The result should be true", "true", resultI);
        assertEquals("The result should be true", "true", resultJ);
        assertEquals("The result should be true", "2", resultK);
        assertEquals("The result should be true", "true", resultL);
        assertEquals("The result should be true", "true", resultM);
    }

    public void testCurrentDateFunctionParseAndEvaluateExpression() throws OpenClinicaSystemException {
        DateMidnight dm = new DateMidnight();
        DateTimeFormatter fmt = ISODateTimeFormat.date();

        String dateA = fmt.print(dm);
        String expressionA = "_CURRENT_DATE eq " + dateA;
        dm = dm.plusDays(5);
        String dateB = fmt.print(dm);
        String expressionB = "_CURRENT_DATE + 5 eq " + dateB;

        OpenClinicaExpressionParser expressionParserA = new OpenClinicaExpressionParser();
        OpenClinicaExpressionParser expressionParserB = new OpenClinicaExpressionParser();

        String resultA = expressionParserA.parseAndEvaluateExpression(expressionA);
        String resultB = expressionParserB.parseAndEvaluateExpression(expressionB);

        assertEquals("The result should be true", "true", resultA);
        assertEquals("The result should be true", "true", resultB);

    }

}
