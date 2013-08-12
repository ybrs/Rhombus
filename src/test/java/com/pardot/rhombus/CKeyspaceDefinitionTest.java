package com.pardot.rhombus;

import com.datastax.driver.core.ConsistencyLevel;
import com.pardot.rhombus.cobject.CDefinition;
import com.pardot.rhombus.cobject.CField;
import com.pardot.rhombus.cobject.CKeyspaceDefinition;
import com.pardot.rhombus.helpers.TestHelpers;
import junit.framework.TestCase;

import java.io.IOException;
import java.util.Map;

/**
 * Pardot, An ExactTarget Company
 * User: robrighter
 * Date: 4/5/13
 */
public class CKeyspaceDefinitionTest extends TestCase{

    public void testFields() throws IOException {
        CKeyspaceDefinition def = CKeyspaceDefinition.fromJsonFile("pikeyspace-functional.js");
		assertEquals(ConsistencyLevel.QUORUM, def.getConsistencyLevel());
    }

}

