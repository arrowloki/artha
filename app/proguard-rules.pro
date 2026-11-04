# Room SQLite & SQLCipher
-keep class androidx.room.** { *; }
-keep class net.sqlcipher.** { *; }
-dontwarn net.sqlcipher.**

# Keep Artha Entities & DAOs
-keep class com.artha.database.entity.** { *; }
-keep class com.artha.database.dao.** { *; }
-keep class com.artha.model.** { *; }

# WorkManager
-keep class androidx.work.** { *; }
-keep class com.artha.data.worker.** { *; }

# Compose & Lifecycle
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
-dontwarn kotlinx.coroutines.**
