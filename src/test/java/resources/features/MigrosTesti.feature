Feature: US01 Migros Testi
  @wip

  Scenario: Migros sayfasinda alisveris yapar

    Given Kullanici https://www.migros.com.tr/ sitesine gider
    When Kullanici pop-up ve cerezleri kapatir.
    When Kullanici sitenin dogru oldugunu kontrol eder.
    Then Kullanici Migros Hemen sekmesini secer.
    Then Adres belirleme islemi gerceklestirilir
    When Kullanici kategoriler kismindan Et Baliki secer.
    And  Kullanici sepete rastgele urun ekler ve tutarin belirlenen tutardan yuksek olmadigini teyit eder