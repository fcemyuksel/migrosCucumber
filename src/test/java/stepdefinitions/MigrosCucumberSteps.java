package stepdefinitions;

import Pages.MigrosPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.log4j.Logger;
import org.junit.Assert;
import java.util.*;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.JavascriptExecutor; // JavascriptExecutor'ı ekledik
import utilities.ConfigReader;
import utilities.Driver;
import utilities.ReusableMethods;

public class MigrosCucumberSteps {

    WebDriver driver;
    MigrosPage migrosPage = new MigrosPage();
    Random random = new Random();
    final static Logger logger = Logger.getLogger(MigrosCucumberSteps.class);
    public static void clickElement(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor)Driver.getDriver();

        js.executeScript("arguments[0].setAttribute('style', 'background: yellow; border: 2px solid blue;');", element);
        element.click();
    }
    JavascriptExecutor jse;
    public MigrosCucumberSteps() {
        this.driver = Driver.getDriver(); // Driver nesnesini tanımladık
        this.jse = (JavascriptExecutor) driver; // JavascriptExecutor'ı tanımladık
        logger.info("Migros alisveris testi baslatildi.");
    }

    @Given("Kullanici https:\\/\\/www.migros.com.tr\\/ sitesine gider")
    public void kullanici_https_www_migros_com_tr_sitesine_gider() {
        driver.get(ConfigReader.getProperty("migrosUrl"));
        logger.info("Migros Url'ine gidildi.");
    }

    @When("Kullanici pop-up ve cerezleri kapatir.")
    public void kullanici_pop_up_ve_cerezleri_kapatir() {
        ReusableMethods.bekle(3);
        clickElement(migrosPage.cerezReddetElementi);
        logger.info("popup ve cerezler kapatildi");
    }

    @When("Kullanici sitenin dogru oldugunu kontrol eder.")
    public void kullanici_sitenin_dogru_oldugunu_kontrol_eder() {
        Assert.assertTrue(migrosPage.uyeOlElementi.isDisplayed());
        ReusableMethods.bekle(3);
        logger.info("kullanicinin dogru sayfada oldugu teyit edildi");
    }

    @When("Kullanici Migros Hemen sekmesini secer.")
    public void kullanici_migros_hemen_sekmesini_secer() {
        clickElement(migrosPage.migrosHemenElementi);
        ReusableMethods.bekle(3);
        logger.info("migros hemen sekmesine gidildi");
    }

    @Then("Adres belirleme islemi gerceklestirilir")
    public void Adres_belirleme_islemi_gerceklestirilir() {
        clickElement(migrosPage.adresTanimlamaElementi);
        clickElement(migrosPage.adresimeGelsinElementi);
        ReusableMethods.bekle(3);
        migrosPage.mevcutKonumuKullan.click();
        ReusableMethods.bekle(3);
        migrosPage.konumEkleButonu.click();
        ReusableMethods.bekle(3);
        migrosPage.adresimDogruButonu.click();
        ReusableMethods.bekle(3);
        logger.info("adres belirleme islemleri gerceklestirildi");
    }

    @When("Kullanici kategoriler kismindan Et Baliki secer.")
    public void kullanici_kategoriler_kismindan_temel_gida_yi_secer() {
        //jse.executeScript("arguments[0].scrollIntoView();", migrosPage.etBalikElementi);
       // ReusableMethods.bekle(3);
        migrosPage.etBalikElementi.click();
        ReusableMethods.bekle(3);
        logger.info("kategorilerden et-balik-tavuk secildi");
    }

      @And("Kullanici sepete rastgele urun ekler ve tutarin belirlenen tutardan yuksek olmadigini teyit eder")
    public void kullanici_sepete_gider_tutarin_belirlenen_tutardan_yuksek_olmadigini_teyit_eder() {
        double butceLimiti = 1000.0;
        double toplamFiyat = 0.0;
        Random random = new Random();
        ReusableMethods.bekle(2);

        // Tüm ürünlerin fiyatlarını içerecek bir liste oluştur
        List<WebElement> urunlerListesi = migrosPage.urunlerElementiList;

        while (toplamFiyat < butceLimiti) {
            // Rastgele bir ürün seç
            int randomIndex = random.nextInt(urunlerListesi.size());
            WebElement randomUrun = urunlerListesi.get(randomIndex);

            String urunAdi = randomUrun.getText();
            try {
                // Ürün fiyatını al
                WebElement urunFiyatiElement = randomUrun.findElement(By.xpath(".//span[@id='new-amount']"));
                String urunFiyatiString = urunFiyatiElement.getText()
                        .replaceAll("[^0-9.,]+", "")
                        .replace(",", ".")
                        .trim();
                double urunFiyati = Double.parseDouble(urunFiyatiString);

                // Toplam fiyatı güncelle
                toplamFiyat += urunFiyati;

                // Butçeyi aşacak mı kontrol et
                if (toplamFiyat < butceLimiti) {
                    // Ürünü sepete ekle
                    WebElement product = randomUrun.findElement(By.xpath(".//*[@class='ng-fa-icon add-to-cart-button ng-star-inserted']"));
                    ReusableMethods.bekle(2);
                    jse.executeScript("arguments[0].click();", product);
                    System.out.println("Seçilen ürün: " + urunAdi);

                    System.out.println("Alışveriş sepeti tutarı: " + toplamFiyat + " TL");
                    System.out.println("-----------------------------------------------------");
                } else {
                    System.out.println("Alışveriş sepeti aşıldığında tutar: " + toplamFiyat + " TL");
                    System.out.println("Bütçe limiti aşıldı! Alışveriş durduruldu.");
                    break;
                }
            } catch (NoSuchElementException e) {
                System.out.println("Hata: Element bulunamadı");
                e.printStackTrace();
                logger.info("Aranan urun bulunamadi");
            }
            ReusableMethods.bekle(2);
            logger.info("ürünlerden rastgele urunler verilen limtler kadar secildi");
        }

        logger.info("test bitirildi");
        Driver.closeDriver();
    }

}

