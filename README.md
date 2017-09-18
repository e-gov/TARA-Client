# TARA-Client
TARA autentimisteenuse näidis klient

Rakenduse käivitamine:
* Main.java -> public static void main(String... a)

        #ApplicationId ja ApplicationSercert on rakenduse põhised parameetrid. OpenIdDemo ja secret on serveris näidise jaoks konfigureeritud parameetrid.
		* Properties.setApplicationId("openIdDemo");
		* Properties.setApplicationSecret("secret");
		
		#ServiceProviderUrl - autentimisteenust pakkuva serveri URL
		* Properties.setServiceProviderUrl("http://localhost:8080/oidc");
		#ApplicationUrl - callBack URL rakendusse. Ehk kuhu suunab server päringu peale autentimist. 
		* Properties.setApplicationUrl("https://localhost:8451/oauth/response");
		
* Autentimise järgselt suunatakse kasutaja tagasi algsesse rakendusse ja autentimist valideerivasse endpointi (applicationUrl). Seal on juba arendaja otsus, mis ja kuidas ta rakenduse
spetsiifiliselt loob sessiooni.
