package com.skillexchange.api.controller;

import com.skillexchange.api.dto.wallet.WalletSummaryResponse;
import com.skillexchange.api.security.AppUserPrincipal;
import com.skillexchange.api.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping
    public WalletSummaryResponse summary(@AuthenticationPrincipal AppUserPrincipal principal) {
        return walletService.summary(principal.getId());
    }
}

