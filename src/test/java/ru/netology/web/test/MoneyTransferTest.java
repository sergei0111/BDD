package ru.netology.web.test;


import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;

import ru.netology.web.page.DashboardPage;

import ru.netology.web.page.LoginPageV2;


import static com.codeborne.selenide.Selenide.open;

import static org.junit.jupiter.api.Assertions.assertAll;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static ru.netology.web.data.DataHelper.*;




public class MoneyTransferTest {




    DashboardPage dashboardPage;

    CardInfo firstCardInfo;

    CardInfo secondCardInfo;

    int firstCardBalance;

    int secondCardBalance;




    @BeforeEach

    void setup() {

        var loginPage = open("http://localhost:9999", LoginPageV2.class);

        var authInfo = getAuthInfo();

        var verificationPage = loginPage.validLogin(authInfo);

        var verificationCode = getVerificationCodeFor(authInfo);

        dashboardPage = verificationPage.validVerify(verificationCode);

        firstCardInfo = getFirstCardInfo();

        secondCardInfo = getSecondCardInfo();

        firstCardBalance = dashboardPage.getCardBalance(getMaskedNumber(firstCardInfo.getCardNumber()));

        secondCardBalance = dashboardPage.getCardBalance(getMaskedNumber(secondCardInfo.getCardNumber()));







    }




    @Test

    void shouldTransferMoneyFirstCardsToSecondCard() {

        var amount = generateValidAmount(firstCardBalance);

        var expectedFirstCardBalance = firstCardBalance - amount;

        var expectedSecondCardBalance = secondCardBalance + amount;

        var transferPage = dashboardPage.selectCardToTransfer(secondCardInfo);

        dashboardPage = transferPage.makeValidTransfer(String.valueOf(amount), firstCardInfo);

        dashboardPage.reloadDashboardPage();

        var actualBalanceFirstCard = dashboardPage.getCardBalance(getMaskedNumber(firstCardInfo.getCardNumber()));

        var actualBalanceSecondCard = dashboardPage.getCardBalance(1);

        assertAll(() -> assertEquals(expectedFirstCardBalance, actualBalanceFirstCard),

                () -> assertEquals(expectedSecondCardBalance, actualBalanceSecondCard));

    }




    @Test

    void shouldTransferMoneyFirstCardsToSecondCardAnderBalance() {

        var amount = generateInValidAmount(secondCardBalance);

        var transferPage = dashboardPage.selectCardToTransfer(firstCardInfo);

        transferPage.makeTransfer(String.valueOf(amount), secondCardInfo);

        transferPage.findErrorMassage("Выполнена попытка перевода суммы, превыщающей остаток на карте списания");

        dashboardPage.reloadDashboardPage();

        var actualBalanceFirstCard = dashboardPage.getCardBalance(getMaskedNumber(firstCardInfo.getCardNumber()));

        var actualBalanceSecondCard = dashboardPage.getCardBalance(getMaskedNumber(secondCardInfo.getCardNumber()));

        assertAll(() -> assertEquals(firstCardBalance, actualBalanceFirstCard),

                () -> assertEquals(secondCardBalance, actualBalanceSecondCard));

    }

}
