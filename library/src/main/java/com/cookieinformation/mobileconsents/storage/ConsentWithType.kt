package com.cookieinformation.mobileconsents.storage

import com.cookieinformation.mobileconsents.ConsentItem

public data class ConsentWithType(val type: ConsentItem.Type, val consented: Boolean)