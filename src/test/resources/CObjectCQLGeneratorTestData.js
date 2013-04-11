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
			"key": "foreignid:type:instance",
			"filters": ["CIndexFilterIncludeAll"]
		},
		"unfiltered_Instance":  {
			"key": "type:instance",
			"filters": ["CIndexFilterExcludeFiltered"]
		}

	}
}