a => a.text("name")
.datalist("accounts", f => f.dictionary("account"))
.dropdown("accountId", "accounts").additionalPrivileges("readOrgData", "canAccessGlobalSettings")
