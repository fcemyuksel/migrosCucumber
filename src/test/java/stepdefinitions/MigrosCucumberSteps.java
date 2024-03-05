package stepdefinitions;

import Pages.MigrosPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.JavascriptExecutor; // JavascriptExecutor'ı ekledik
import utilities.ConfigReader;
import utilities.Driver;
import utilities.ReusableMethods;

public class MigrosCucumberSteps {

    WebDriver driver;
    MigrosPage migrosPage = new MigrosPage();
    Random random = new Random();
    JavascriptExecutor jse; // Burada sadece değişkeni tanımladık, atama işlemi constructor'da yapılacak

    public MigrosCucumberSteps() {
        this.driver = Driver.getDriver(); // Driver nesnesini tanımladık
        this.jse = (JavascriptExecutor) driver; // JavascriptExecutor'ı tanımladık
    }

    @Given("Kullanici https:\\/\\/www.migros.com.tr\\/ sitesine gider")
    public void kullanici_https_www_migros_com_tr_sitesine_gider() {
        driver.get(ConfigReader.getProperty("migrosUrl"));
    }

    @When("Kullanici pop-up ve cerezleri kapatir.")
    public void kullanici_pop_up_ve_cerezleri_kapatir() {
        ReusableMethods.bekle(3);
        migrosPage.cerezReddetElementi.click();
    }

    @When("Kullanici sitenin dogru oldugunu kontrol eder.")
    public void kullanici_sitenin_dogru_oldugunu_kontrol_eder() {
        Assert.assertTrue(migrosPage.uyeOlElementi.isDisplayed());
        ReusableMethods.bekle(3);
    }

    @When("Kullanici Migros Hemen sekmesini secer.")
    public void kullanici_migros_hemen_sekmesini_secer() {
        migrosPage.migrosHemenElementi.click();
        ReusableMethods.bekle(3);
    }

    @Then("Adres belirleme islemi gerceklestirilir")
    public void Adres_belirleme_islemi_gerceklestirilir() {
        migrosPage.adresTanimlamaElementi.click();
        migrosPage.adresimeGelsinElementi.click();
        ReusableMethods.bekle(3);
        migrosPage.mevcutKonumuKullan.click();
        ReusableMethods.bekle(3);
        migrosPage.konumEkleButonu.click();
        ReusableMethods.bekle(3);
        migrosPage.adresimDogruButonu.click();
        ReusableMethods.bekle(3);
    }

    @When("Kullanici kategoriler kismindan Et Baliki secer.")
    public void kullanici_kategoriler_kismindan_temel_gida_yi_secer() {
        jse.executeScript("arguments[0].scrollIntoView();", migrosPage.etBalikElementi);
        ReusableMethods.bekle(3);
        migrosPage.etBalikElementi.click();
        ReusableMethods.bekle(3);
    }

    @When("Temel gidadan alt kategori random olarak secilir")
    public void temel_gidadan_alt_kategori_random_olarak_secilir() {
        int randomIndex = ThreadLocalRandom.current().nextInt(migrosPage.urunlerElementiList.size());
        WebElement selectedProduct = migrosPage.urunlerElementiList.get(randomIndex);
        // Seçilen ürünün adını konsola yazdırma
        System.out.println("Seçilen ürün: " + selectedProduct.getText());
    }

    @And("Kullanici sepete gider tutarin belirlenen tutardan yuksek olmadigini teyit eder")
    public void kullanici_sepete_gider_tutarin_belirlenen_tutardan_yuksek_olmadigini_teyit_eder() {
        double butceLimiti = 1000.0;
        double toplamFiyat = 1.0;
        ReusableMethods.bekle(2);
        for (WebElement urun : migrosPage.urunlerElementiList) {
            String urunAdi = urun.getText();
            try {
                // urun fiyatini al
                WebElement urunFiyatiElement = urun.findElement(By.xpath("//span[@id='new-amount']"));
                String urunFiyatiString = urunFiyatiElement.getText()
                        .replaceAll("[^0-9.,]+", "")
                        .replace(",", ".")
                        .trim();
                double urunFiyati = Double.parseDouble(urunFiyatiString);

                // Toplam fiyati guncelle
                double guncelToplamFiyat = toplamFiyat + urunFiyati;

                // Butceyi asacak mi kontrol et
                if (guncelToplamFiyat < butceLimiti) {
                    // urunu sepete ekle
                    WebElement product = urun.findElement(By.xpath(".//*[@class='ng-fa-icon add-to-cart-button ng-star-inserted']"));
                    ReusableMethods.bekle(2);
                    jse.executeScript("arguments[0].click();", product);
                    System.out.println("Secilen urun: " + urunAdi);

                    // Toplam fiyati guncelle
                    toplamFiyat = guncelToplamFiyat;

                    System.out.println("alisveris sepeti tutari: "+guncelToplamFiyat +" TL");
                    System.out.println("-----------------------------------------------------");
                } else {
                    System.out.println("alisveris sepeti asim yapildigindaki tutar: "+guncelToplamFiyat +" TL");
                    System.out.println("Butce limiti asildi! Alısveris durduruldu.");
                    break;
                }
            } catch (NoSuchElementException e) {
                System.out.println("Hata: Element bulunamadi");
                e.printStackTrace();
            }
            ReusableMethods.bekle(2);
        }
        Driver.closeDriver();
    }

}
