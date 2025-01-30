Bienvenue au sein du magnifique projet de Carla et William, Tu es prêt ? C'est parti !!!

1- Récupérer notre projet sur ton ordinateur : git clone

2- Lance le super docker-compose de notre projet avec un : docker compose up --build

3- Attend un petit peu que tout s'installe…

4- Ca y est tout est en place ? 

5- Lance notre site à partir de ce lien web : http://localhost:8082/login

6- Connexion/Inscription, tu as la possibilité de te connecter ou de t'inscrire !

	Si tu souhaites te connecter avec un ID initialisé dés le lancement de l'API :
		ID étudiant : 6000000
		ID formateur : 1000000

7- Une fois sur l'accueil Etudiant ou Formateur, Prépare toi un ptit café et navigue de page en page mais surtout amuse toi ;)

##################################################

POINT IMPORTANT !

Actuellement, notre Batch est programmé pour s'exécuter tous les vendredis à 17h.
Si tu veux le tester immédiatement, modifie son exécution dans le fichier concerné en ajustant la ligne suivante :
		
	@Scheduled(cron = "0 00 17 * * FRI") sur le moment de ton choix.

Remplace "0 00 17 * * FRI" par un autre moment qui te convient, et relance l’application !

N'hésite pas à nous contacter, on sera ravis de t’aider ! 😉

##################################################