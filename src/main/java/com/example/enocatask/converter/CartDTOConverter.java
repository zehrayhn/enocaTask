package com.example.enocatask.converter;

import com.example.enocatask.dto.CartDTO;
import com.example.enocatask.dto.CartItemDTO;
import com.example.enocatask.entities.Cart;
import com.example.enocatask.entities.CartItem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component
public class CartDTOConverter {
    public static CartDTO convertCartToDTO(Cart cart) {
        CartDTO cartDTO = new CartDTO();
        cartDTO.setCustomerId(cart.getCustomer().getId());
        cartDTO.setTotalAmount(cart.getTotalAmount());

        List<CartItemDTO> cartItemDTOList = new ArrayList<>();
        for (CartItem cartItem : cart.getItems()) {
            CartItemDTO cartItemDTO = convertCartItemToDTO(cartItem);
            cartItemDTOList.add(cartItemDTO);
        }
        cartDTO.setItems(cartItemDTOList);

        return cartDTO;
    }

    private static CartItemDTO convertCartItemToDTO(CartItem cartItem) {
        CartItemDTO cartItemDTO = new CartItemDTO();
        cartItemDTO.setProductId(cartItem.getProduct().getId());
        cartItemDTO.setQuantity(cartItem.getQuantity());
        return cartItemDTO;
    }
}


