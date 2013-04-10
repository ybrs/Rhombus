package com.pardot.service.analytics;

import com.pardot.service.analytics.helpers.TestHelpers;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.pardot.service.tools.cobject.*;

import java.util.ArrayList;

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
			String expected = "CREATE TABLE testtype (id timeuuid PRIMARY KEY, filtered int,data1 varchar,data2 varchar,data3 varchar,instance bigint,type int,foreignid bigint);";
			assertEquals(expected, cql);
		}

		public void testMakeWideTableCreate() throws CObjectParseException{
			String json = TestHelpers.readFileToString(this.getClass(), "CObjectCQLGeneratorTestData.js");
			CDefinition def = new CDefinition(json);
			String cql1 = Subject.makeWideTableCreate(def, def.indexes.get("foreign"));
			String expected1 = "CREATE TABLE testtype__foreign (id timeuuid, filtered int,data1 varchar,data2 varchar,data3 varchar,instance bigint,type int,foreignid bigint, PRIMARY KEY ((foreignid),id) );";
			assertEquals(expected1, cql1);

			String cql2 = Subject.makeWideTableCreate(def, def.indexes.get("instance"));
			String expected2 = "CREATE TABLE testtype__instance (id timeuuid, filtered int,data1 varchar,data2 varchar,data3 varchar,instance bigint,type int,foreignid bigint, PRIMARY KEY ((type, instance),id) );";
			assertEquals(expected2, cql2);

			String cql3 = Subject.makeWideTableCreate(def, def.indexes.get("foreign_instance"));
			String expected3 = "CREATE TABLE testtype__foreign_instance (id timeuuid, filtered int,data1 varchar,data2 varchar,data3 varchar,instance bigint,type int,foreignid bigint, PRIMARY KEY ((foreignid, type, instance),id) );";
			assertEquals(expected3, cql3);
		}

		public void testMakeCQLforCreate() throws CObjectParseException {
			String json = TestHelpers.readFileToString(this.getClass(), "CObjectCQLGeneratorTestData.js");
			CDefinition def = new CDefinition(json);
			ArrayList<String> actual = Subject.makeCQLforCreate(def);
			assertEquals("Should generate CQL statements for the static table plus all indexes", 4, actual.size());
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

	public void testMakeWideTableCreate() throws CObjectParseException {
		Subject s = new Subject();
		s.testMakeWideTableCreate();
	}

	public void testMakeCQLforCreate() throws CObjectParseException {
		Subject s = new Subject();
		s.testMakeCQLforCreate();
	}


}
