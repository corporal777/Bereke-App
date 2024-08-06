package org.wilderkek.bereke.util.rxtakephoto

class PermissionNotGrantedException(val permission: String? = null) : Exception()
class GPSNotEnabledException() : Exception()