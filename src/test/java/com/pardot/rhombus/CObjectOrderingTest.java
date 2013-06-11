package com.pardot.rhombus;

import com.pardot.rhombus.cobject.CObjectOrdering;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static org.junit.Assert.*;

/**
 * Pardot, an ExactTarget company
 * User: Michael Frank
 * Date: 5/11/13
 */
public class CObjectOrderingTest {
	private static final Logger logger = LoggerFactory.getLogger(CObjectOrderingTest.class);

	@Test
	public void testFromString() {
		CObjectOrdering ordering;
		ordering = CObjectOrdering.fromString("ASC");
		assertEquals(CObjectOrdering.ASCENDING, ordering);
		ordering = CObjectOrdering.fromString("DESC");
		assertEquals(CObjectOrdering.DESCENDING, ordering);
	}
}
