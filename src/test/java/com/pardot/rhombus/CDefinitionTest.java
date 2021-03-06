package com.pardot.rhombus;

import com.pardot.rhombus.cobject.CDefinition;
import com.pardot.rhombus.cobject.CField;
import com.pardot.rhombus.helpers.TestHelpers;
import junit.framework.TestCase;

import java.io.IOException;
import java.util.Map;

/**
 * Pardot, An ExactTarget Company
 * User: robrighter
 * Date: 4/5/13
 */
public class CDefinitionTest extends TestCase{

    public void testFields() throws IOException {
        String json = TestHelpers.readFileToString(this.getClass(), "CObjectCQLGeneratorTestData.js");
        CDefinition def = CDefinition.fromJsonString(json);
        Map<String, CField> fields = def.getFields();
        //Make sure the size is correct
        assertEquals(7, fields.size());
        //Check the first field
        CField field = fields.get("foreignid");
        assertEquals("foreignid", field.getName());
        assertEquals(CField.CDataType.BIGINT, field.getType());
    }

}

