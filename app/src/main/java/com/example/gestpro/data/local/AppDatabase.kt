package com.example.gestpro.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        UserEntity::class,
        PasswordResetTokenEntity::class,
        FarmEntity::class,
        PlotEntity::class,
        PlotInputEntity::class,
        EmployeeEntity::class
    ],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun passwordResetDao(): PasswordResetDao
    abstract fun farmDao(): FarmDao
    abstract fun employeeDao(): EmployeeDao
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `password_reset_tokens` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`userId` INTEGER NOT NULL, " +
                            "`tokenHash` TEXT NOT NULL, " +
                            "`expiresAt` INTEGER NOT NULL, " +
                            "`used` INTEGER NOT NULL)"
                )
                db.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS `index_password_reset_tokens_tokenHash` " +
                            "ON `password_reset_tokens` (`tokenHash`)"
                )
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `farm` (" +
                            "`id` INTEGER NOT NULL, " +
                            "`name` TEXT NOT NULL, " +
                            "`safra` TEXT NOT NULL, " +
                            "`cultivatedAreaHa` REAL NOT NULL, " +
                            "PRIMARY KEY(`id`))"
                )
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `plots` (" +
                            "`id` INTEGER NOT NULL, " +
                            "`number` INTEGER NOT NULL, " +
                            "`areaHa` REAL NOT NULL, " +
                            "PRIMARY KEY(`id`))"
                )
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `plot_inputs` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`plotId` INTEGER NOT NULL, " +
                            "`name` TEXT NOT NULL, " +
                            "`quantity` REAL NOT NULL, " +
                            "`unit` TEXT NOT NULL, " +
                            "`sortOrder` INTEGER NOT NULL, " +
                            "FOREIGN KEY(`plotId`) REFERENCES `plots`(`id`) " +
                            "ON UPDATE NO ACTION ON DELETE CASCADE)"
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_plot_inputs_plotId` " +
                            "ON `plot_inputs` (`plotId`)"
                )
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DELETE FROM plot_inputs")
                db.execSQL("DELETE FROM plots")
                db.execSQL("DELETE FROM farm")
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DELETE FROM plot_inputs")
                db.execSQL("DELETE FROM plots")
                db.execSQL("DELETE FROM farm")
            }
        }


        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS employees (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    name TEXT NOT NULL, role TEXT NOT NULL, phone TEXT NOT NULL,
                    birthDate INTEGER, contractType TEXT NOT NULL, admissionDate INTEGER,
                    salary REAL NOT NULL, status TEXT NOT NULL, notes TEXT NOT NULL,
                    photoPath TEXT)"""
                )
            }
        }

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE plots ADD COLUMN variety TEXT")
                db.execSQL("ALTER TABLE plots ADD COLUMN plantCount INTEGER")
                db.execSQL("ALTER TABLE plots ADD COLUMN registeredAt INTEGER")
                db.execSQL("ALTER TABLE plots ADD COLUMN notes TEXT")
                db.execSQL("ALTER TABLE plot_inputs ADD COLUMN appliedAt INTEGER")
                db.execSQL("UPDATE plots SET variety = 'Bourbon' WHERE id BETWEEN 1 AND 8")
                db.execSQL(
                    "UPDATE plots SET plantCount = CASE id " +
                        "WHEN 1 THEN 3550 WHEN 2 THEN 1000 WHEN 3 THEN 2000 WHEN 4 THEN 3000 " +
                        "WHEN 5 THEN 1500 WHEN 6 THEN 2500 WHEN 7 THEN 2000 WHEN 8 THEN 1800 " +
                        "END WHERE id BETWEEN 1 AND 8"
                )
            }
        }

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gestpro.db"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7)
                    .build()
                    .also { INSTANCE = it }
            }
    }
}