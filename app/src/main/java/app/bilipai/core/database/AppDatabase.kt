package app.bilipai.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import app.bilipai.core.database.dao.SearchHistoryDao
import app.bilipai.core.database.entity.SearchHistory
import app.bilipai.core.database.entity.BlockedUp
import app.bilipai.core.database.dao.BlockedUpDao
import app.bilipai.core.database.dao.CommentFraudDao
import app.bilipai.core.database.entity.CommentFraudRecord

@Database(
    entities = [SearchHistory::class, BlockedUp::class, CommentFraudRecord::class],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun blockedUpDao(): BlockedUpDao
    abstract fun commentFraudDao(): CommentFraudDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .addMigrations(MIGRATION_3_4, MIGRATION_6_7)
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE comment_fraud_records ADD COLUMN proto3_payload TEXT")
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE blocked_ups ADD COLUMN level INTEGER")
                db.execSQL("ALTER TABLE blocked_ups ADD COLUMN sign TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE blocked_ups ADD COLUMN vipLabel TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE blocked_ups ADD COLUMN officialTitle TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE blocked_ups ADD COLUMN follower INTEGER")
                db.execSQL("ALTER TABLE blocked_ups ADD COLUMN archiveCount INTEGER")
                db.execSQL("ALTER TABLE blocked_ups ADD COLUMN isDeleted INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE blocked_ups ADD COLUMN lastSyncedAt INTEGER")
            }
        }
    }
}
