package com.tdsoft.learnodo.common

import com.google.firebase.auth.FirebaseUser
import com.tdsoft.learnodo.data.models.UserInformation

data class CommonResponse(
    val isSuccess: Boolean,
    val message: String,
    val dbUser: FirebaseUser? = null,
    val userInformationObj: UserInformation? = null
)