package org.wilderkek.bereke.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference

const val API_URL = "http://192.168.1.6:8080"

lateinit var AUTH: FirebaseAuth
lateinit var USER_ID: String
lateinit var REF_STORAGE_ROOT: StorageReference
lateinit var REF_DATABASE_ROOT: DatabaseReference
lateinit var FF_DATABASE_ROOT: FirebaseFirestore
lateinit var USER : FirebaseAuth

internal const val EXTRA_SETUP = "extra.setup"
internal const val EXTRA_IMAGE = "extra.image"
internal const val RESULT_NAME = "images"

const val NODE_USERS = "user_profiles"
const val NODES = "simple_nodes"
const val NODE_WORKS = "work_notes"
const val NODE_VIP = "vip_notes"
const val NODE_TRANSPORT = "transport_notes"
const val NODE_HEALTH = "health_notes"
const val NODE_IMAGES_URLS = "images_urls"
const val NODE_BUY_SELL = "buy_sell_notes"
const val NODE_HOUSE = "house_notes"
const val NODE_SERVICES = "service_notes"
const val FOLDER_USER_IMAGES = "users_profile_images"
const val FOLDER_WORKS_IMAGE = "works_images"
const val FOLDER_TRANSPORT_IMAGE = "transport_images"
const val FOLDER_BEAUTY_AND_MEDICINE_IMAGES = "beauty_and_medicine_images"
const val FOLDER_NOTES_IMAGES = "notes_images"
const val FOLDER_TEST_IMAGES = "test_images"
const val FOLDER_BUY_SELL_IMAGES = "buy_sell_images"
const val FOLDER_FLATS_IMAGES = "flats_images"
const val FOLDER_SERVICES_IMAGES = "services_images"
const val NODE_LIKED_ADS = "pref_ads"
const val YANDEX_APP_METRIC = "614c99b2-a597-4af3-86fb-55947fddc3aa"


const val END_POINT= "works.json"
const val FB_BASE_URL = "https://tam-tam-8b2a7-default-rtdb.firebaseio.com/"

const val TIME_PROPERTY = "timeStamp"
const val TEXT_PROPERTY = "text"
const val PAGE_SIZE = 5


const val IMAGE_MAX_SIZE_AVATAR = 1024

const val FILTER_FAVORITE = "favorite"
const val FILTER_USER = "user"
const val FILTER_STATUS = "status"
const val FILTER_LIMIT = "limit"
const val FILTER_CATEGORY = "category"
const val FILTER_SUBCATEGORY = "subCategory"
const val FILTER_WORK_SPECIALITY = "workSpeciality"

const val FILTER_HOUSE_TYPE = "houseType"

const val FILTER_OBJECT_TYPE = "objectType"

const val PENDING = "pending"
const val APPROVED = "approved"
const val INACTIVE = "inactive"
