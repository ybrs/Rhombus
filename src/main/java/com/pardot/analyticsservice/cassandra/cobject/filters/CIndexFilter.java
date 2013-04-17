package com.pardot.analyticsservice.cassandra.cobject.filters;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Map;

/**
 * Pardot, An ExactTarget Company.
 * User: robrighter
 * Date: 4/4/13
 */

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CIndexFilterExcludeFiltered.class, name = "CIndexFilterExcludeFiltered"),
        @JsonSubTypes.Type(value = CIndexFilterIncludeAll.class, name = "CIndexFilterIncludeAll")
})
public abstract class CIndexFilter {

	public abstract boolean isIncluded(Map<String,String> obj);

}
