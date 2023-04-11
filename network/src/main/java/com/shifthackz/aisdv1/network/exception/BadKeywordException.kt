package com.shifthackz.aisdv1.network.exception

class BadKeywordException(val keyword: String) : Throwable("Keyword '$keyword' is not allowed")
