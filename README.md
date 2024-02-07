<img src='doc/img/ee_cef_0.png'></img>

# [DEPRECATED] TARA-Client

> [!CAUTION]
> **Tarkvara ei ajakohastata. Uuem info asub https://e-gov.github.io/TARA-Doku/Naited lehel.**

TARA autentimisteenuse näidis klient

#### Rakenduse konfigureerimine
Rakendust on võimalik konfigureerida järgmiste parameetritega:

###### Teenusepakkuja
- `provider.protocol` - vaikimisi `http`
- `provider.domain` - vaikimisi `localhost`
- `provider.port` - vaikimisi `8450`

###### Klient
- `client.port` - vaikimisi `8451`
- `client.domain` - vaikimisi `localhost`
- `client.id` - vaikimisi `openIdDemo`
- `client.secret` - vaikimisi `secret`
- `client.locale` - vaikimisi `et`

#### Rakenduse käivitamine

```
mvn clean compile exec:java <-Dclient.*=*> <-Dprovider.*=*>
```
Testteenuse vastu konfigureeritud näide: 
```
mvn clean compile exec:java -Dprovider.protocol=https -Dprovider.domain=tara-test.ria.ee -Dprovider.port=443
```

#### Edasine juhis
* Peale rakenduse käivitamist avada interneti lehitsejas URL: https://localhost:8451/ui
Selle peale avaneb rakenduse esileht, kus on väikses kirjas `Login`. Sellele klikates suunatakse
kasutaja TARA autentimisrakendusse, kus juba valitud autentimismeetodiga toimub kasutaja 
autentimine. Peale edukat autentimist suunatakse kasutaja tagasi rakendusse (vt 
`DemoRestController` klassi ning `applicationUrl` parameetrit main klassis)
		
* Autentimise järgselt suunatakse kasutaja tagasi algsesse rakendusse ja autentimist valideerivasse endpointi
(`applicationUrl`). Seal on juba arendaja otsus, mis ja kuidas ta loob rakenduse sessiooni
