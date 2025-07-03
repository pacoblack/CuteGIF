package com.cv.pic.db

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update

/**
 * 产品实体
 */
@Entity(tableName = "products")
data class Product(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String,
  val description: String,
  val price: Double,
  val stockQuantity: Int = 0
)


/**
 * 产品数据访问对象
 */
@Dao
interface ProductDao {
  @Insert
  suspend fun insert(product: Product): Long

  @Update
  suspend fun update(product: Product)

  @Query("SELECT * FROM products WHERE id = :id")
  suspend fun getById(id: Long): Product?

  @Query("SELECT * FROM products ORDER BY name ASC")
  suspend fun getAll(): List<Product>

  @Query("DELETE FROM products WHERE id = :id")
  suspend fun delete(id: Long)

  @Query("UPDATE products SET stockQuantity = stockQuantity - :quantity WHERE id = :id")
  suspend fun reduceStock(id: Long, quantity: Int)
}