{
	"name": "testtype",
	"fields": [
		{"name": "foreignid", "type": "bigint"},
		{"name": "type", "type": "int"},
		{"name": "instance", "type": "bigint"},
		{"name": "filtered", "type": "int"},
		{"name": "data1", "type": "varchar"},
		{"name": "data2", "type": "varchar"},
		{"name": "data3", "type": "varchar"}
	],
	"indexes" : [
		{
			"name": "foreign",
			"key": "foreignid",
			"filters" : [{"type": "CIndexFilterIncludeAll"}]
		},
		{
			"name": "instance",
			"key": "type:instance",
			"filters" : [{"type": "CIndexFilterIncludeAll"}]
		},
		{
			"name": "foreign_instance",
			"key": "foreignid:type:instance",
			"filters" : [{"type": "CIndexFilterIncludeAll"}]
		},
		{
			"name": "unfiltered_Instance",
			"key": "type:instance",
			"filters" : [{"type": "CIndexFilterExcludeFiltered"}]
		}
	]
}