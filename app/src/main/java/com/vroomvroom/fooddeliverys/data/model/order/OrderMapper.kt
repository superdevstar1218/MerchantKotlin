package com.vroomvroom.fooddeliverys.data.model.order

import com.vroomvroom.fooddeliverys.data.model.cart.CartItemOptionEntity
import com.vroomvroom.fooddeliverys.data.model.cart.CartItemWithOptions
import com.vroomvroom.fooddeliverys.data.model.user.LocationEntity

object OrderMapper {

    fun mapToOrder(
        merchantId: String,
        payment: Payment,
        deliveryFee: Double,
        totalPrice: Double,
        locationEntity: LocationEntity,
        cartItems: List<CartItemWithOptions>
    ): Order {
        val deliveryAddress = DeliveryAddress(
            locationEntity.address,
            locationEntity.city,
            locationEntity.addInfo,
            listOf(locationEntity.latitude, locationEntity.longitude)
        )
        val orderDetail = OrderDetail(
            deliveryFee,
            totalPrice,
            mapToOrderProduct(cartItems)
        )
        return Order(null, payment, merchantId, deliveryAddress, orderDetail)
    }

    private fun mapToOrderProduct(cartItems: List<CartItemWithOptions>): List<OrderProduct> {
        return cartItems.mapIndexed {
            index: Int, it->
            OrderProduct(
                index.toString(),
                it.cartItem.productId,
                it.cartItem.name,
                it.cartItem.productImgUrl,
                it.cartItem.price,
                it.cartItem.quantity,
                it.cartItem.specialInstructions,
                mapToOptions(it.cartItemOptions)
            )
        }
    }

    private fun mapToOptions(
        cartItemOptions: List<CartItemOptionEntity>?
    ): List<OrderProductOption>? {
        return cartItemOptions?.map {
            OrderProductOption(
                it.name,
                it.additionalPrice,
                it.optionType
            )
        }
    }

}