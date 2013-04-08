package com.pardot.service.analytics;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pardot.service.analytics.helpers.TestHelpers;
import com.pardot.service.tools.cobject.CField;
import com.pardot.service.tools.cobject.CIndex;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.pardot.service.tools.cobject.CDefinition;
import org.apache.commons.io.*;
import java.io.FileInputStream;
import java.util.ArrayList;

/**
 * Pardot, An ExactTarget Company
 * User: robrighter
 * Date: 4/5/13
 */
public class CDefinitionTest extends TestCase{

	public class Subject extends CDefinition {

		public void testGenerateFields(){
			String json = TestHelpers.readFileToString(this.getClass(),"CFieldsTestData.js");
			assertTrue("Invalid Test File", !json.equals(""));
			try{
				java.net.URL location = Test.class.getProtectionDomain().getCodeSource().getLocation();
				System.out.println(location.getFile());
				ObjectMapper mapper = new ObjectMapper();
				JsonNode j =  mapper.readTree(json);
				ArrayList<CField> result = this.generateFields(j);
				assertTrue("Should have 3 fields in result", result.size() == 3);
				assertTrue("First name should be accountId",result.get(0).name.equals("accountId"));
				assertTrue("First type should be bigint",result.get(0).type == CField.CDataType.BIGINT);
				assertTrue("Second name should be accountId",result.get(1).name.equals("fieldAsTime"));
				assertTrue("Second type should be bigint",result.get(1).type == CField.CDataType.TIMEUUID);
				assertTrue("Third name should be accountId",result.get(2).name.equals("fieldWithInvalidType"));
				assertTrue("Third type should be bigint",result.get(2).type == CField.CDataType.VARCHAR);
			}
			catch(Exception e){
				assertTrue(e.toString(), false);
			}
		}

		public void testGenerateIndexes(){
			String json = TestHelpers.readFileToString(this.getClass(),"CIndexTestData.js");
			assertTrue("Invalid Test File", !json.equals(""));
			try{
				java.net.URL location = Test.class.getProtectionDomain().getCodeSource().getLocation();
				System.out.println(location.getFile());
				ObjectMapper mapper = new ObjectMapper();
				JsonNode j =  mapper.readTree(json);
				ArrayList<CIndex> result = this.generateIndexes(j);
				assertTrue("Should have 2 fields in result", result.size() == 2);
				assertEquals("First item's name should be account", "account",result.get(0).name);
				assertEquals("First item's key should be accountId:uuid", "accountId:uuid",result.get(0).key);
				assertEquals("Second item's name should be account_filtered", "account_filtered", result.get(1).name);
				assertEquals("Second item's key should be accountId:uuid", "accountId:uuid",result.get(1).key);
				//verify the filters
				assertEquals("first item's filter list length should be 1", 1, result.get(0).filters.size());
				assertEquals("Filter classname should be as given in json file", "com.pardot.service.tools.cobject.filters.CIndexFilterIncludeAll" , result.get(0).filters.get(0).getClass().getName());
				assertEquals("second item's filter list length should be 1", 2, result.get(1).filters.size());
				assertEquals("Filter classname should be as given in json file", "com.pardot.service.tools.cobject.filters.CIndexFilterExcludeFiltered" , result.get(1).filters.get(0).getClass().getName());
				assertEquals("Filter classname should be as given in json file", "com.pardot.service.tools.cobject.filters.CIndexFilterIncludeAll" , result.get(1).filters.get(1).getClass().getName());
			}
			catch(Exception e){
				assertTrue(e.toString(), false);
			}
		}

		public void testParseJson(){
			String json = TestHelpers.readFileToString(this.getClass(),"CDefinitionTestData.js");
			assertTrue("Invalid Test File", !json.equals(""));
			try{
				this.parseJson(json);
				assertEquals("Should parse proper object name", "testdefinition", this.name);
				assertEquals("Should parse the right number of fields", 5, this.fields.size());
				assertEquals("Should parse the right number of indexes" , 2, this.indexes.size());
			}
			catch(Exception e){
				assertTrue(e.toString(), false);
			}
		}

	}

	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public CDefinitionTest( String testName ) {
		super( testName );
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite( CDefinitionTest.class );
	}

	public void testGenerateFields() {
		Subject s = new Subject();
		s.testGenerateFields();
	}

	public void testGenerateIndexes() {
		Subject s = new Subject();
		s.testGenerateIndexes();
	}

	public void testParseJson() {
		Subject s = new Subject();
		s.testParseJson();
	}
}

