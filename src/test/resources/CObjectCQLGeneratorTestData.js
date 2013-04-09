{
	"name": "testtype",
	"fields": {
		"foreignid": "bigint",
		"type": "int",
		"instance": "bigint",
		"filtered": "int",
		"data1": "varchar",
		"data2": "varchar",
		"data3": "varchar"
	},
	"indexes" : {
		"foreign": {
			"key": "foreignid",
			"filters": ["CIndexFilterIncludeAll"]
		},
		"instance":  {
			"key": "type:instance",
			"filters": ["CIndexFilterIncludeAll"]
		},
		"foreign_instance":  {
			"key": "foreign:type:instance",
			"filters": ["CIndexFilterIncludeAll"]
		}
	}
}