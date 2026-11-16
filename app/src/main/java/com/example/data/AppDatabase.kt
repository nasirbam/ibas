package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "saved_pay_bills")
data class SavedPayBillEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val monthLabel: String,
    val billTypeLabel: String,
    val grade: Int,
    val basicPay: Int,
    val grossPay: Int,
    val totalDeduction: Int,
    val netPayable: Int,
    val billTextSummary: String,
    val createdAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "saved_applications")
data class SavedApplicationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val appTypeCode: String,
    val title: String,
    val dateRangeLabel: String,
    val daysCount: Int,
    val letterBody: String,
    val createdAtMillis: Long = System.currentTimeMillis()
)

@Dao
interface IbasDao {
    @Query("SELECT * FROM saved_pay_bills ORDER BY createdAtMillis DESC")
    fun getAllPayBills(): Flow<List<SavedPayBillEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayBill(bill: SavedPayBillEntity)

    @Query("DELETE FROM saved_pay_bills WHERE id = :id")
    suspend fun deletePayBill(id: Int)

    @Query("SELECT * FROM saved_applications ORDER BY createdAtMillis DESC")
    fun getAllApplications(): Flow<List<SavedApplicationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplication(app: SavedApplicationEntity)

    @Query("DELETE FROM saved_applications WHERE id = :id")
    suspend fun deleteApplication(id: Int)
}

@Database(
    entities = [SavedPayBillEntity::class, SavedApplicationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ibasDao(): IbasDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ibas_sahayak_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class IbasRepository(private val dao: IbasDao) {
    val savedPayBills: Flow<List<SavedPayBillEntity>> = dao.getAllPayBills()
    val savedApplications: Flow<List<SavedApplicationEntity>> = dao.getAllApplications()

    suspend fun savePayBill(bill: SavedPayBillEntity) = dao.insertPayBill(bill)
    suspend fun deletePayBill(id: Int) = dao.deletePayBill(id)

    suspend fun saveApplication(app: SavedApplicationEntity) = dao.insertApplication(app)
    suspend fun deleteApplication(id: Int) = dao.deleteApplication(id)
}
