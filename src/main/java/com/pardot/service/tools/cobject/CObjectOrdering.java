package com.pardot.service.tools.cobject;

/**
 * Pardot, An ExactTarget Company
 * User: robrighter
 * Date: 4/15/13
 */
public enum CObjectOrdering {
	ASCENDING {
		@Override
		public String toString(){
			return "ASC";
		}
	},
	DESCENDING {
		@Override
		public String toString(){
			return "DESC";
		}
	}
}
