package com.vroomvroom.fooddeliverys.view.ui.home

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.vroomvroom.fooddeliverys.R
import com.vroomvroom.fooddeliverys.data.model.cart.CartItemMapper.mapToCartItemEntity
import com.vroomvroom.fooddeliverys.data.model.merchant.Merchant
import com.vroomvroom.fooddeliverys.data.model.merchant.Option
import com.vroomvroom.fooddeliverys.data.model.merchant.Product
import com.vroomvroom.fooddeliverys.databinding.FragmentProductBottomSheetBinding
import com.vroomvroom.fooddeliverys.utils.ClickType
import com.vroomvroom.fooddeliverys.utils.OnOptionClickListener
import com.vroomvroom.fooddeliverys.utils.Utils.clearFocus
import com.vroomvroom.fooddeliverys.view.ui.base.BaseBottomSheetFragment
import com.vroomvroom.fooddeliverys.view.ui.home.adapter.OptionSectionAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ProductBottomSheetFragment : BaseBottomSheetFragment<FragmentProductBottomSheetBinding>(
    FragmentProductBottomSheetBinding::inflate
), OnOptionClickListener {

    private val optionSectionAdapter by lazy { OptionSectionAdapter(this) }
    private val navArgs: ProductBottomSheetFragmentArgs by navArgs()
    private var quantity = 1
    private var required: Int? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        clearFocus(
            binding.root,
            binding.instructionInputEditText,
            requireActivity()
        )

        val product = navArgs.product
        Glide
            .with(this)
            .load(product.productImgUrl)
            .placeholder(R.drawable.ic_placeholder)
            .into(binding.bottomSheetProductImg)

        val currentMerchant = mainActivityViewModel.merchant
        binding.optionTypeRv.adapter = optionSectionAdapter
        binding.productPrice.text = getString(R.string.peso, "%.2f".format(product.price))
        binding.product = product
        optionSectionAdapter.submitList(product.optionTypes)
        required = product.optionTypes?.filter { it.required }?.size
        checkRequired()
        binding.bottomSheetTextDescription.isVisible = !product.description.isNullOrBlank()

        binding.btnAddToCart.setOnClickListener {

            homeViewModel.cartItem.observe(viewLifecycleOwner) { items ->
                if (items.isNotEmpty() &&
                    items.first().cartItem.cartMerchant.merchantId != currentMerchant.id) {
                    dialog.show(
                        getString(R.string.prompt),
                        getString(R.string.clear_cart_message),
                        getString(R.string.cancel),
                        getString(R.string.remove)
                    ) { type ->
                        when (type) {
                            ClickType.POSITIVE -> {
                                homeViewModel.deleteAllCartItem()
                                onButtonClicked(product, currentMerchant)
                                dialog.dismiss()
                                findNavController().popBackStack()
                            }
                            ClickType.NEGATIVE -> dialog.dismiss()
                        }
                    }
                } else {
                    onButtonClicked(product, currentMerchant)
                    findNavController().popBackStack()
                }
            }

        }

        binding.decreaseQuantity.setOnClickListener {
            if (quantity != 1) {
                quantity -= 1
                binding.quantity.text = quantity.toString()
            }
        }

        binding.increaseQuantity.setOnClickListener {
            quantity += 1
            binding.quantity.text = quantity.toString()
        }

    }

    private fun onButtonClicked(product: Product, currentMerchant: Merchant) {
        val optionTotalPrice = homeViewModel.choseOptions.map {(_, value) ->
            value }.sumOf { it.additionalPrice ?: 0.0}
        val cart = mapToCartItemEntity(
            product.id,
            product.name,
            product.productImgUrl,
            (product.price + optionTotalPrice) * quantity,
            quantity,
            binding.instructionInputEditText.text?.toString().orEmpty(),
            currentMerchant.id,
            currentMerchant.name,
            currentMerchant.location!![0],
            currentMerchant.location[1]
        )
        homeViewModel.insertCartItem(cart)
    }

    private fun checkRequired() {
        required?.let {
            if (homeViewModel.choseOptions.size < it) {
                binding.btnAddToCart.apply {
                    isEnabled = false
                    setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gray_a5a))
                }
            } else {
                binding.btnAddToCart.apply {
                    isEnabled = true
                    setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red_a30))
                }
            }
        }
    }

    override fun onClick(option: Option, optionType: String) {
        homeViewModel.choseOptions[optionType] = option
        checkRequired()
    }
}