# Définition de la commande par défaut qui sera exécutée si aucune cible n'est spécifiée.
all: compile run

# Cible pour compiler le projet.
compile:
	sbt compile

# Cible pour exécuter le projet.
run:
	sbt run

# Cible pour nettoyer le projet (effacer les fichiers générés).
clean:
	sbt clean

# Cible pour tester le projet.
test:
	sbt test