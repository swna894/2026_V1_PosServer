package com.swna.server.shop_config.entity;

import com.swna.server.shop.entity.Shop;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "shop_configs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShopConfig {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private Shop shop;

    private boolean printEnabled;
    private boolean autoPrint;

    private boolean cardPaymentEnabled;
    private boolean cashPaymentEnabled;

    private String cardTerminalIp;
    private Integer cardTerminalPort;

    private String excelExportPath;
    private String receiptSavePath;

    // =========================
    // Factory
    // =========================
    public static ShopConfig createDefault(Shop shop) {

        ShopConfig c = new ShopConfig();
        c.shop = shop;

        c.printEnabled = true;
        c.autoPrint = true;

        c.cardPaymentEnabled = true;
        c.cashPaymentEnabled = true;

        c.cardTerminalIp = "127.0.0.1";
        c.cardTerminalPort = 9100;

        c.excelExportPath = null;
        c.receiptSavePath = null;

        return c;
    }

    // =========================
    // Business (리팩토링 핵심)
    // =========================

    public void updatePaymentSetting(boolean cashEnabled, boolean cardEnabled) {
        this.cashPaymentEnabled = cashEnabled;
        this.cardPaymentEnabled = cardEnabled;
    }

    public void updateCardTerminal(String ip, int port) {
        this.cardTerminalIp = ip;
        this.cardTerminalPort = port;
    }

    public void updatePrintSetting(boolean enabled, boolean autoPrint) {
        this.printEnabled = enabled;
        this.autoPrint = autoPrint;
    }

    public void updateExportPath(String excelPath, String receiptPath) {
        this.excelExportPath = excelPath;
        this.receiptSavePath = receiptPath;
    }
}