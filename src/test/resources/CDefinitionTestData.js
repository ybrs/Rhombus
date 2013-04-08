{
	"name": "testdefinition",
	"fields": {
		"campaignId": "bigint",
		"accountId": "bigint",
		"prospectId": "bigint",
		"isFiltered": "bigint",
		"paidSearchAd": "varchar"
	},
	"indexes" : {
		"account": {
			"key": "accountId:uuid",
			"filters": ["CIndexFilterExcludeFiltered"]
		},
		"account_all":  {
			"key": "accountId:uuid",
			"filters": ["CIndexFilterIncludeAll"]
		}
	}
}