package com.pardot.service.analytics;

import com.pardot.service.analytics.helpers.TestHelpers;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.pardot.service.tools.cobject.*;

/**
 * Pardot, An ExactTarget Company
 * User: robrighter
 * Date: 4/9/13
 */
public class CObjectCQLGeneratorTest  extends TestCase {

	public class Subject extends CObjectCQLGenerator {

		public void testMakeStaticTableCreate() throws CObjectParseException{
			String json = TestHelpers.readFileToString(this.getClass(), "CObjectCQLGeneratorTestData.js");
			CDefinition def = new CDefinition(json);
			String cql = Subject.makeStaticTableCreate(def);
			String expected = "CREATE TABLE testtype (id timeuuid PRIMARY KEY, foreignid bigint,type int,instance bigint,filtered int,data1 varchar,data2 varchar,data3 varchar);";
			assertEquals(expected, cql);
		}


	}

	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public CObjectCQLGeneratorTest( String testName ) {
		super( testName );
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite( CObjectCQLGeneratorTest.class );
	}

	public void testMakeStaticTableCreate() throws CObjectParseException{
		Subject s = new Subject();
		s.testMakeStaticTableCreate();
	}


}
