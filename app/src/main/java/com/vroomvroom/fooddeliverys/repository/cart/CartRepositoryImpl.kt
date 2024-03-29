package com.vroomvroom.fooddeliverys.repository.cart

import com.vroomvroom.fooddeliverys.data.db.dao.CartItemDAO
import com.vroomvroom.fooddeliverys.data.model.cart.CartItemEntity
import com.vroomvroom.fooddeliverys.data.model.cart.CartItemMapper.mapFromDomainModelList
import com.vroomvroom.fooddeliverys.data.model.merchant.Option
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor (
    private val cartItemDAO: CartItemDAO
) : CartRepository {
    override suspend fun insertCartItem(cartItemEntity: CartItemEntity) {
        cartItemDAO.insertCartItem(cartItemEntity)
    }
    override suspend fun insertCartItemOptions(options: Map<String, Option>) {
        val cartItemOptions = mapFromDomainModelList(options)
            cartItemDAO.insertCartItemOptions(cartItemOptions)
    }
    override suspend fun updateCartItem(cartItemEntity: CartItemEntity) =
        cartItemDAO.updateCartItem(cartItemEntity)
    override suspend fun deleteCartItem(cartItemEntity: CartItemEntity) =
        cartItemDAO.deleteCartItem(cartItemEntity)
    override suspend fun deleteAllCartItem() = cartItemDAO.deleteAllCartItem()
    override suspend fun deleteAllCartItemOption() = cartItemDAO.deleteAllCartItemOption()
    override fun getAllCartItem() = cartItemDAO.getAllCartItem()
}