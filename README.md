# tournament_API

## Description

Cette API permet de gérer un tournoi. Elle permet d'ajouter des joueurs, de modifier leur score et de les supprimer. Il est également possible d'obtenir la liste des joueurs triée en fonction de leur score.

## Installation

Pour lancer l'API, il faut clone le repository et lancer le bash start_API.bat.

## Usage

L'API possède les endpoints suivants :

- GET /player : Récupère tous les joueurs du tournoi, classés par leur score.
- POST /player : Crée un nouveau joueur.
- DELETE /player : Supprime tous les joueurs.
- DELETE /player/byId/{id?} : Supprime un joueur par son ID.
- GET /player/byId/{id?} : Récupère un joueur par son ID.
- PATCH /player/byId/{id?} : Met à jour un joueur par son ID.
- GET /player/byPseudo/{pseudo?} : Récupère un joueur par son pseudo.
- PATCH /player/byPseudo/{pseudo?} : Met à jour un joueur par son pseudo.

## Développement

### Configuration

La configuration du serveur se trouve dans le fichier application.yaml. Les champs uri et database peuvent être modifiés pour utiliser votre propre base de données. J'ai initialement essayé de passer par les options VM pour ne pas mettre les données en clair, mais je n'ai pas réussi à les utiliser.

### Structure des données

Pour le stockage des données, j'ai utilisé une base MongoDB. 
Le principal point d'interrogation concernait le fait de stocker ou non la valeur de rank.  
Si on choisit de la stocker, cela rend plus facile les actions visant à récupérer l'information dans la base. Le point négatif est que lorsque l'on modifie le score d'un joueur, il faut modifier le rank de plusieurs joueurs et donc multiplier les modifications en base.  
Si on choisit de ne pas la stocker, il faut recalculer le rank à chaque fois que l'on affiche les informations d'un joueur ou la liste des joueurs. Cela peut être plus coûteux en termes de performance, mais cela permet de ne pas multiplier les modifications en base.  
  
J'ai donc choisi de ne pas stocker la valeur de rank. En effet, le nombre de joueurs est limité et le tri peut se faire directement avec la requête ce qui permet, j'imagine, d'améliorer un peu les performances.
  
Pour avoir le meilleur des deux mondes, il peut être envisageable de stocker la valeur de rank mais de mettre à jour cette valeur seulement de temps et temps et pas après chaque modification. Cependant, cela ne semble pas être dans l'esprit de l'exercice où il semble plus intéressant de voir les modifications en direct.

### Tests

Pour tester l'application, le plus compliquer a été la gestion de la base de données. Ayant déjà eu du mal avec la configuration de la base de l'application, je n'ai pas voulu créer une base dédiée aux tests.
J'avais donc le choix entre créer une base intégrée ou utiliser des mock de la base.
  
J'ai choisi la deuxième option, car cela me permettait d'utiliser des mock. Je n'avais pas eu beaucoup d'occasion d'en utiliser avant et c'était donc l'opportunité de m'améliorer sur ce point.
Après coup, peut-être que ceci n'était pas la meilleure option. En effet, j'ai eu pas mal de problème avec les mock. Notamment la configuration de l'application pour les tests des routes.
J'ai également eu des problèmes avec les any() principalement pour récupérer la liste triée. Je n'ai d'ailleurs pas réussi à bien mock cette partie du code et je l'ai par conséquent mise dans une fonction auxiliaire afin de pouvoir continuer mes tests.

### Limitations

En arrivant à la partie front du projet, j'ai pu apercevoir les limitations de mon modèle. En effet, j'ai pu créer les éléments nécessaires pour visualiser les joueurs, cependant, il n'est pas possible de mettre à jour les données d'un joueur.

En effet, je stocke des Player avec un id, un pseudo et un score, mais j'affiche un PlayerWithRank avec un pseudo, un score et un rank mais pas d'id. J'ai fait cela afin d'afficher le pseudo, le score et le rank sans afficher l'id comme je le comprends dans l'énoncé. Cependant, cela m'empêche de faire des modifications depuis le front, car l'id n'est alors pas disponible.

Pour corriger cela, il faudrait ajouter l'id dans le PlayerWithRank et donc l'afficher, ou supprimer l'id de Player et donc utiliser le pseudo comme identifiant. J'aurais naturellement plutôt choisi la première option. En effet, il est toujours possible de ne pas afficher l'id dans le front et il permet de s'assurer plus facilement de l'unicité des joueurs.
Actuellement, il est cependant possible d'utiliser les deux méthodes, car j'ai ajouté une vérification de l'unicité du pseudo lors de la création d'un joueur.

### Swagger-UI

Dans le tutoriel que j'ai suivi pour apprendre à utiliser les différents outils, ils utilisaient Swagger-UI et j'ai trouvé ça utile pour tester manuellement mon application. J'ai hésité à le retirer, mais je ne sais pas si ça peut vous être utile donc je l'ai finalement laissé.
