package com.pardot.rhombus;

import com.pardot.rhombus.util.UuidUtil;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.UUID;

/**
 * Pardot, an ExactTarget company
 * User: Michael Frank
 * Date: 5/6/13
 */
public class UuidUtilTest {

	@Test
	public void testUuidUtil() {
		int namespace = 47;
		int name = 722338;
		UUID namespaceUuid = UuidUtil.namespaceUUID(namespace, name);

		assertEquals(UuidUtil.namespaceFromUUID(namespaceUuid).intValue(), namespace);
		assertEquals(UuidUtil.nameFromUUID(namespaceUuid).intValue(), name);

	}
}
