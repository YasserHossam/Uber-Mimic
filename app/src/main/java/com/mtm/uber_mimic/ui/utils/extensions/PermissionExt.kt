package com.mtm.uber_mimic.tools.location.exceptions

import android.annotation.SuppressLint
import android.content.Context
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import kotlinx.coroutines.CompletableDeferred
import java.util.*


suspend fun Context.permissionsCheck(
    permissions: Array<String>
): Boolean {
    val response = CompletableDeferred<Boolean>()
    Permissions.check(this, permissions, null, null, object : PermissionHandler() {
        @SuppressLint("MissingPermission")
        override fun onGranted() {
            response.complete(true)
        }

        override fun onDenied(context: Context?, deniedPermissions: ArrayList<String>?) {
            super.onDenied(context, deniedPermissions)
            response.complete(false)
        }

        override fun onBlocked(context: Context?, blockedList: ArrayList<String>?): Boolean {
            val result = super.onBlocked(context, blockedList)
            response.complete(false)
            return result
        }

        override fun onJustBlocked(
            context: Context?,
            justBlockedList: ArrayList<String>?,
            deniedPermissions: ArrayList<String>?
        ) {
            super.onJustBlocked(context, justBlockedList, deniedPermissions)
            response.complete(false)
        }
    })
    return response.await()
}