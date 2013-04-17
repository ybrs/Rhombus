package com.pardot.service.analytics;

import com.datastax.driver.core.utils.UUIDs;
import com.google.common.collect.Lists;
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
			String expected1 = "CREATE TABLE testtype__foreign (id timeuuid, shardid bigint, filtered int,data1 varchar,data2 varchar,data3 varchar,instance bigint,type int,foreignid bigint, PRIMARY KEY ((shardid, foreignid),id) );";
			assertEquals(expected1, cql1);

			String cql2 = Subject.makeWideTableCreate(def, def.getIndexes().get("instance"));
			String expected2 = "CREATE TABLE testtype__instance (id timeuuid, shardid bigint, filtered int,data1 varchar,data2 varchar,data3 varchar,instance bigint,type int,foreignid bigint, PRIMARY KEY ((shardid, type, instance),id) );";
			assertEquals(expected2, cql2);

			String cql3 = Subject.makeWideTableCreate(def, def.getIndexes().get("foreign_instance"));
			String expected3 = "CREATE TABLE testtype__foreign_instance (id timeuuid, shardid bigint, filtered int,data1 varchar,data2 varchar,data3 varchar,instance bigint,type int,foreignid bigint, PRIMARY KEY ((shardid, foreignid, type, instance),id) );";
			assertEquals(expected3, cql3);
		}

		public void testMakeCQLforInsert() throws CQLGenerationException, CObjectParseException, IOException {
			String json = TestHelpers.readFileToString(this.getClass(), "CObjectCQLGeneratorTestData.js");
			CDefinition def = CDefinition.fromJsonString(json);
			Map<String, String> data = TestHelpers.getTestObject(0);
			CQLStatementIterator result = Subject.makeCQLforInsert(def,data);
			assertEquals("Should generate CQL statements for the static table plus all indexes except the filtered index", 4, result.size());
			data.put("filtered", "0");
			UUID uuid = UUID.fromString("ada375b0-a2d9-11e2-99a3-3f36d3955e43");
			result = Subject.makeCQLforInsert(def,data,uuid,1,0);
			List<String> actual = toList(result);

			assertEquals("Should generate CQL statements for the static table plus all indexes including the filtered index", 5, actual.size());
			//static table
			assertEquals("INSERT INTO testtype (id, filtered, data1, data2, data3, instance, type, foreignid) VALUES (ada375b0-a2d9-11e2-99a3-3f36d3955e43, 0, 'This is data one', 'This is data two', 'This is data three', 222222, 5, 777) USING TIMESTAMP 1;", actual.get(0));
			//index 1
			assertEquals("INSERT INTO testtype__foreign_instance (id, shardid, filtered, data1, data2, data3, instance, type, foreignid) VALUES (ada375b0-a2d9-11e2-99a3-3f36d3955e43, 160, 0, 'This is data one', 'This is data two', 'This is data three', 222222, 5, 777) USING TIMESTAMP 1;", actual.get(1));
			//index 2
			assertEquals("INSERT INTO testtype__instance (id, shardid, filtered, data1, data2, data3, instance, type, foreignid) VALUES (ada375b0-a2d9-11e2-99a3-3f36d3955e43, 160, 0, 'This is data one', 'This is data two', 'This is data three', 222222, 5, 777) USING TIMESTAMP 1;",actual.get(2));
			//index 3
			assertEquals("INSERT INTO testtype__foreign (id, shardid, filtered, data1, data2, data3, instance, type, foreignid) VALUES (ada375b0-a2d9-11e2-99a3-3f36d3955e43, 1, 0, 'This is data one', 'This is data two', 'This is data three', 222222, 5, 777) USING TIMESTAMP 1;",actual.get(3));
			//index 4
			assertEquals("INSERT INTO testtype__unfiltered_Instance (id, shardid, filtered, data1, data2, data3, instance, type, foreignid) VALUES (ada375b0-a2d9-11e2-99a3-3f36d3955e43, 160, 0, 'This is data one', 'This is data two', 'This is data three', 222222, 5, 777) USING TIMESTAMP 1;",actual.get(4));

			//test with ttl
			result = Subject.makeCQLforInsert(def,data,uuid,1,20);
			actual = toList(result);
			assertEquals("INSERT INTO testtype (id, filtered, data1, data2, data3, instance, type, foreignid) VALUES (ada375b0-a2d9-11e2-99a3-3f36d3955e43, 0, 'This is data one', 'This is data two', 'This is data three', 222222, 5, 777) USING TIMESTAMP 1 AND TTL 20;", actual.get(0));
		}

		public void testMakeCQLforCreate() throws CObjectParseException, IOException {
			String json = TestHelpers.readFileToString(this.getClass(), "CObjectCQLGeneratorTestData.js");
			CDefinition def = CDefinition.fromJsonString(json);
			CQLStatementIterator actual = Subject.makeCQLforCreate(def);
			assertEquals("Should generate CQL statements for the static table plus all indexes", 5, actual.size());
		}

		public void testMakeCQLforGet() throws CObjectParseException,CObjectParseException, CQLGenerationException, IOException {
			String json = TestHelpers.readFileToString(this.getClass(), "CObjectCQLGeneratorTestData.js");
			CDefinition def = CDefinition.fromJsonString(json);

			//Static Table Get
			CQLStatementIterator actual = Subject.makeCQLforGet(def,UUID.fromString("ada375b0-a2d9-11e2-99a3-3f36d3955e43"));
			assertEquals("Static gets should return bounded query iterator", true,actual.isBounded());
			assertEquals("Static gets should return an iterator with 1 statement", 1,actual.size());
			String expected = "SELECT * FROM testtype WHERE id = ada375b0-a2d9-11e2-99a3-3f36d3955e43;";
			assertEquals("Should generate proper CQL for static table get by ID",expected,toList(actual).get(0));

			//Wide table bounded
			Map<String,String> indexkeys = Maps.newHashMap();
			indexkeys.put("foreignid","777");
			indexkeys.put("type", "5");
			indexkeys.put("instance", "222222");
			actual = Subject.makeCQLforGet(def,"foreign_instance", indexkeys, 10);
			assertEquals("Should be unbounded query list", false, actual.isBounded());


			//Wide table exclusive slice
			indexkeys = Maps.newHashMap();
			indexkeys.put("foreignid","777");
			indexkeys.put("type", "5");
			indexkeys.put("instance", "222222");
			UUID start = UUID.fromString("a8a2abe0-a251-11e2-bcbb-adf1a79a327f");
			UUID stop = UUID.fromString("ada375b0-a2d9-11e2-99a3-3f36d3955e43");
			actual = Subject.makeCQLforGet(def,"foreign_instance", indexkeys,CObjectOrdering.DESCENDING, start, stop,10, false);
			expected = "SELECT * FROM testtype__foreign_instance WHERE shardid = 160 AND instance = 222222 AND type = 5 AND foreignid = 777 AND id > a8a2abe0-a251-11e2-bcbb-adf1a79a327f AND id < ada375b0-a2d9-11e2-99a3-3f36d3955e43 ORDER BY id DESC LIMIT 10 ALLOW FILTERING;";
			assertEquals("Should generate proper CQL for wide table get by index values",expected,actual.next());
			assertTrue("Should be bounded query iterator", actual.isBounded());
			assertTrue("Should be none remaining in the iterator", !actual.hasNext());


			//wide table inclusive slice ascending bounded
			start = UUID.fromString("b4c10d80-15f0-11e0-8080-808080808080"); // 1/1/2011 long startd = 1293918439000L;
			stop = UUID.fromString("2d87f48f-34c2-11e1-7f7f-7f7f7f7f7f7f"); //1/1/2012 long endd = 1325454439000L;
			actual = Subject.makeCQLforGet(def,"foreign_instance", indexkeys,CObjectOrdering.ASCENDING, start, stop,10, true);
			assertEquals("Should be proper size for range", 13, actual.size()); //All of 2011 plus the first month of 2012
			expected = "SELECT * FROM testtype__foreign_instance WHERE shardid = 133 AND instance = 222222 AND type = 5 AND foreignid = 777 AND id >= b4c10d80-15f0-11e0-8080-808080808080 AND id <= 2d87f48f-34c2-11e1-7f7f-7f7f7f7f7f7f ORDER BY id ASC LIMIT 10 ALLOW FILTERING;";
			assertEquals("Should generate proper CQL for wide table get by index values",expected,actual.next());
			expected = "SELECT * FROM testtype__foreign_instance WHERE shardid = 134 AND instance = 222222 AND type = 5 AND foreignid = 777 AND id >= b4c10d80-15f0-11e0-8080-808080808080 AND id <= 2d87f48f-34c2-11e1-7f7f-7f7f7f7f7f7f ORDER BY id ASC LIMIT 10 ALLOW FILTERING;";
			assertEquals("Should generate proper CQL for wide table get by index values",expected,actual.next());
			expected = "SELECT * FROM testtype__foreign_instance WHERE shardid = 135 AND instance = 222222 AND type = 5 AND foreignid = 777 AND id >= b4c10d80-15f0-11e0-8080-808080808080 AND id <= 2d87f48f-34c2-11e1-7f7f-7f7f7f7f7f7f ORDER BY id ASC LIMIT 5 ALLOW FILTERING;";
			assertTrue("Should have next when hinted less than the limit",actual.hasNext(5));
			assertEquals("Should generate proper Limit adjustment when given the amount hint",expected,actual.next());
			assertTrue("Should have no next when hinted more than or equal to the limit",!actual.hasNext(10));

			//wide table inclusive slice descending bounded
			start = UUID.fromString("b4c10d80-15f0-11e0-8080-808080808080"); // 1/1/2011 long startd = 1293918439000L;
			stop = UUID.fromString("2d87f48f-34c2-11e1-7f7f-7f7f7f7f7f7f"); //1/1/2012 long endd = 1325454439000L;
			actual = Subject.makeCQLforGet(def,"foreign_instance", indexkeys,CObjectOrdering.DESCENDING, start, stop,10, true);
			assertEquals("Descending: Should be proper size for range", 13, actual.size()); //All of 2011 plus the first month of 2012
			expected = "SELECT * FROM testtype__foreign_instance WHERE shardid = 145 AND instance = 222222 AND type = 5 AND foreignid = 777 AND id >= b4c10d80-15f0-11e0-8080-808080808080 AND id <= 2d87f48f-34c2-11e1-7f7f-7f7f7f7f7f7f ORDER BY id DESC LIMIT 10 ALLOW FILTERING;";
			assertEquals("Descending: Should generate proper CQL for wide table get by index values",expected,actual.next());
			expected = "SELECT * FROM testtype__foreign_instance WHERE shardid = 144 AND instance = 222222 AND type = 5 AND foreignid = 777 AND id >= b4c10d80-15f0-11e0-8080-808080808080 AND id <= 2d87f48f-34c2-11e1-7f7f-7f7f7f7f7f7f ORDER BY id DESC LIMIT 10 ALLOW FILTERING;";
			assertEquals("Descending: Should generate proper CQL for wide table get by index values",expected,actual.next());
			expected = "SELECT * FROM testtype__foreign_instance WHERE shardid = 143 AND instance = 222222 AND type = 5 AND foreignid = 777 AND id >= b4c10d80-15f0-11e0-8080-808080808080 AND id <= 2d87f48f-34c2-11e1-7f7f-7f7f7f7f7f7f ORDER BY id DESC LIMIT 5 ALLOW FILTERING;";
			assertTrue("Descending: Should have next when hinted less than the limit",actual.hasNext(5));
			assertEquals("Descending: Should generate proper Limit adjustment when given the amount hint",expected,actual.next());
			assertTrue("Should have no next when hinted more than or equal to the limit",!actual.hasNext(10));

		}


	}

	public static List<String> toList(CQLStatementIterator i){
		List<String> ret = Lists.newArrayList();
		if(!i.isBounded()){
			return ret;
		}
		while(i.hasNext()){
			ret.add(i.next());
		}
		return ret;
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
