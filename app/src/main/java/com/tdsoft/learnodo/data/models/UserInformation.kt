package com.tdsoft.learnodo.data.models

import com.google.firebase.database.IgnoreExtraProperties
import com.tdsoft.learnodo.common.UserTypes

@IgnoreExtraProperties
data class UserInformation(
    var userId: String? = null,
    var name: String? = null,
    var profileImage: String? = null,
    var address: String? = null,
    var email: String? = null,
    var phoneNumber: String? = null,
    @UserTypes var userType: Int = UserTypes.None,
    var registeredClass: ClassRoom? = null
)