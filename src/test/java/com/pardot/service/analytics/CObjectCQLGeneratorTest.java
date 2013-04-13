package com.pardot.service.analytics;

import com.google.common.collect.Maps;
import com.pardot.service.analytics.helpers.TestHelpers;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.pardot.service.tools.cobject.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Pardot, An ExactTarget Company
 * User: robrighter
 * Date: 4/9/13
 */
public class CObjectCQLGeneratorTest  extends TestCase {

	public class Subject extends CObjectCQLGenerator {

		public void testMakeStaticTableCreate() throws CObjectParseException, IOException {
			String json = TestHelpers.readFileToString(this.getClass(), "CObjectCQLGeneratorTestData.js");
			CDefinition def = CDefinition.fromJsonString(json);
			String cql = Subject.makeStaticTableCreate(def);
			String expected = "CREATE TABLE testtype (id timeuuid PRIMARY KEY, filtered int,data1 varchar,data2 varchar,data3 varchar,instance bigint,type int,foreignid bigint);";
			assertEquals(expected, cql);
		}

		public void testMakeWideTableCreate() throws CObjectParseException, IOException {
			String json = TestHelpers.readFileToString(this.getClass(), "CObjectCQLGeneratorTestData.js");
			CDefinition def = CDefinition.fromJsonString(json);
			String cql1 = Subject.makeWideTableCreate(def, def.getIndexes().get("foreign"));
			String expected1 = "CREATE TABLE testtype__foreign (id timeuuid, filtered int,data1 varchar,data2 varchar,data3 varchar,instance bigint,type int,foreignid bigint, PRIMARY KEY ((foreignid),id) );";
			assertEquals(expected1, cql1);

			String cql2 = Subject.makeWideTableCreate(def, def.getIndexes().get("instance"));
			String expected2 = "CREATE TABLE testtype__instance (id timeuuid, filtered int,data1 varchar,data2 varchar,data3 varchar,instance bigint,type int,foreignid bigint, PRIMARY KEY ((type, instance),id) );";
			assertEquals(expected2, cql2);

			String cql3 = Subject.makeWideTableCreate(def, def.getIndexes().get("foreign_instance"));
			String expected3 = "CREATE TABLE testtype__foreign_instance (id timeuuid, filtered int,data1 varchar,data2 varchar,data3 varchar,instance bigint,type int,foreignid bigint, PRIMARY KEY ((foreignid, type, instance),id) );";
			assertEquals(expected3, cql3);
		}

		public void testMakeCQLforInsert() throws CQLGenerationException, CObjectParseException, IOException {
			String json = TestHelpers.readFileToString(this.getClass(), "CObjectCQLGeneratorTestData.js");
			CDefinition def = CDefinition.fromJsonString(json);
			Map<String,String> data = Maps.newHashMap();
			data.put("type","5");
			data.put("instance", "222222");
			data.put("filtered", "1");
			data.put("foreignid", "777");
			data.put("data1","This is data one");
			data.put("data2","This is data two");
			data.put("data3","This is data three");
			List<String> actual = Subject.makeCQLforInsert(def,data);
			assertEquals("Should generate CQL statements for the static table plus all indexes except the filtered index", 4, actual.size());
			data.put("filtered", "0");
			UUID uuid = UUID.fromString("ada375b0-a2d9-11e2-99a3-3f36d3955e43");
			actual = Subject.makeCQLforInsert(def,data,uuid,1,0);
			assertEquals("Should generate CQL statements for the static table plus all indexes including the filtered index", 5, actual.size());
			//static table
			assertEquals("INSERT INTO testtype (id, filtered, data1, data2, data3, instance, type, foreignid) VALUES (ada375b0-a2d9-11e2-99a3-3f36d3955e43, 0, 'This is data one', 'This is data two', 'This is data three', 222222, 5, 777) USING TIMESTAMP 1;", actual.get(0));
			//index 1
			assertEquals("INSERT INTO testtype__foreign_instance (id, filtered, data1, data2, data3, instance, type, foreignid) VALUES (ada375b0-a2d9-11e2-99a3-3f36d3955e43, 0, 'This is data one', 'This is data two', 'This is data three', 222222, 5, 777) USING TIMESTAMP 1;", actual.get(1));
			//index 2
			assertEquals("INSERT INTO testtype__instance (id, filtered, data1, data2, data3, instance, type, foreignid) VALUES (ada375b0-a2d9-11e2-99a3-3f36d3955e43, 0, 'This is data one', 'This is data two', 'This is data three', 222222, 5, 777) USING TIMESTAMP 1;",actual.get(2));
			//index 3
			assertEquals("INSERT INTO testtype__foreign (id, filtered, data1, data2, data3, instance, type, foreignid) VALUES (ada375b0-a2d9-11e2-99a3-3f36d3955e43, 0, 'This is data one', 'This is data two', 'This is data three', 222222, 5, 777) USING TIMESTAMP 1;",actual.get(3));
			//index 4
			assertEquals("INSERT INTO testtype__unfiltered_Instance (id, filtered, data1, data2, data3, instance, type, foreignid) VALUES (ada375b0-a2d9-11e2-99a3-3f36d3955e43, 0, 'This is data one', 'This is data two', 'This is data three', 222222, 5, 777) USING TIMESTAMP 1;",actual.get(4));

			//test with ttl
			actual = Subject.makeCQLforInsert(def,data,uuid,1,20);
			assertEquals("INSERT INTO testtype (id, filtered, data1, data2, data3, instance, type, foreignid) VALUES (ada375b0-a2d9-11e2-99a3-3f36d3955e43, 0, 'This is data one', 'This is data two', 'This is data three', 222222, 5, 777) USING TIMESTAMP 1 AND TTL 20;", actual.get(0));


		}

		public void testMakeCQLforCreate() throws CObjectParseException, IOException {
			String json = TestHelpers.readFileToString(this.getClass(), "CObjectCQLGeneratorTestData.js");
			CDefinition def = CDefinition.fromJsonString(json);
			List<String> actual = Subject.makeCQLforCreate(def);
			assertEquals("Should generate CQL statements for the static table plus all indexes", 5, actual.size());
		}

		public void testMakeCQLforGet() throws CObjectParseException,CObjectParseException, CQLGenerationException, IOException {
			String json = TestHelpers.readFileToString(this.getClass(), "CObjectCQLGeneratorTestData.js");
			CDefinition def = CDefinition.fromJsonString(json);

			//Static Table Get
			String actual = Subject.makeCQLforGet(def,UUID.fromString("ada375b0-a2d9-11e2-99a3-3f36d3955e43"));
			String expected = "SELECT * FROM testtype WHERE id = ada375b0-a2d9-11e2-99a3-3f36d3955e43;";
			assertEquals("Should generate proper CQL for static table get by ID",expected,actual);

			//Wide table simple
			Map<String,String> indexkeys = Maps.newHashMap();
			indexkeys.put("foreignid","777");
			indexkeys.put("type", "5");
			indexkeys.put("instance", "222222");
			actual = Subject.makeCQLforGet(def,"foreign_instance", indexkeys, 10);
			expected = "SELECT * FROM testtype__foreign_instance WHERE instance = 222222 AND type = 5 AND foreignid = 777 ORDER BY id ASC LIMIT 10 ALLOW FILTERING;";
			assertEquals("Should generate proper CQL for wide table get by index values",expected,actual);

			//Wide table exclusive slice
			indexkeys = Maps.newHashMap();
			indexkeys.put("foreignid","777");
			indexkeys.put("type", "5");
			indexkeys.put("instance", "222222");
			UUID start = UUID.fromString("a8a2abe0-a251-11e2-bcbb-adf1a79a327f");
			UUID stop = UUID.fromString("ada375b0-a2d9-11e2-99a3-3f36d3955e43");
			actual = Subject.makeCQLforGet(def,"foreign_instance", indexkeys,COrdering.DESCENDING, start, stop,10, false);
			expected = "SELECT * FROM testtype__foreign_instance WHERE instance = 222222 AND type = 5 AND foreignid = 777 AND id > a8a2abe0-a251-11e2-bcbb-adf1a79a327f AND id < ada375b0-a2d9-11e2-99a3-3f36d3955e43 ORDER BY id DESC LIMIT 10 ALLOW FILTERING;";
			assertEquals("Should generate proper CQL for wide table get by index values",expected,actual);

			//wide table inclusive slice
			actual = Subject.makeCQLforGet(def,"foreign_instance", indexkeys,COrdering.ASCENDING, start, stop,10, true);
			expected = "SELECT * FROM testtype__foreign_instance WHERE instance = 222222 AND type = 5 AND foreignid = 777 AND id >= a8a2abe0-a251-11e2-bcbb-adf1a79a327f AND id <= ada375b0-a2d9-11e2-99a3-3f36d3955e43 ORDER BY id ASC LIMIT 10 ALLOW FILTERING;";
			assertEquals("Should generate proper CQL for wide table get by index values",expected,actual);
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

	public void testMakeStaticTableCreate() throws CObjectParseException, IOException {
		Subject s = new Subject();
		s.testMakeStaticTableCreate();
	}

	public void testMakeWideTableCreate() throws CObjectParseException, IOException {
		Subject s = new Subject();
		s.testMakeWideTableCreate();
	}

	public void testMakeCQLforCreate() throws CObjectParseException, IOException {
		Subject s = new Subject();
		s.testMakeCQLforCreate();
	}

	public void testMakeCQLforInsert() throws CQLGenerationException, CObjectParseException, IOException {
		Subject s = new Subject();
		s.testMakeCQLforInsert();
	}

	public void testMakeCQLforGet() throws CQLGenerationException, CObjectParseException, IOException {
		Subject s = new Subject();
		s.testMakeCQLforGet();
	}


}
