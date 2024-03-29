package com.vroomvroom.fooddeliverys.view.ui.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vroomvroom.fooddeliverys.data.model.cart.CartItemWithOptions
import com.vroomvroom.fooddeliverys.data.model.order.Payment
import com.vroomvroom.fooddeliverys.data.model.user.LocationEntity
import com.vroomvroom.fooddeliverys.repository.order.OrderRepository
import com.vroomvroom.fooddeliverys.view.resource.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _order by lazy { MutableLiveData<Resource<String>>() }
    val order: LiveData<Resource<String>>
        get() = _order
    val isLocationConfirmed by lazy { MutableLiveData(false) }
    var subtotal = 0.0

    fun createOrder(
        merchantId: String,
        payment: Payment,
        deliveryFee: Double,
        totalPrice: Double,
        locationEntity: LocationEntity,
        cartItems: List<CartItemWithOptions>
    ) {
        _order.postValue(Resource.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            val response = orderRepository.createOrders(
                merchantId, payment, deliveryFee, totalPrice, locationEntity, cartItems)
            { data ->
                when (data) {
                    is Resource.Success -> {
                        _order.postValue(data)
                    }
                    is Resource.Error -> {
                        _order.postValue(data)
                    }
                    else -> {
                        _order.postValue(data)
                    }
                }
            }
        }
    }

}