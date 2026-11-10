package com.abug.cart.application.port.in;

import com.abug.cart.domain.UserId;

import java.util.List;


public record CreateCartCommand(UserId userId, List<CartItemData> items) {}
