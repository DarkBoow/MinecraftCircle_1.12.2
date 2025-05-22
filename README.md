# SamplePlugin

SamplePlugin est un plugin Spigot destiné à la génération et à l\'utilisation de waypoints pour réaliser des timelapses automatisés. Il a été développé pour l\'API Spigot 1.16 et cible Java 8.

## Fonctionnement
- À l\'initialisation, le plugin crée un fichier `saves.yml` dans le dossier de données et y génère une série de waypoints autour du point d\'origine. Chaque waypoint contient des coordonnées, une orientation ainsi qu\'un marqueur temporel.
- La commande `/tstart` téléporte le joueur à chaque waypoint de manière cyclique toutes les ticks pour simuler un mouvement de caméra continu.
- La commande `/tstop` interrompt la téléportation et replace le joueur sous la carte tout en désactivant son vol.

## Fichiers de configuration
### plugin.yml
Contient la déclaration principale du plugin et les commandes disponibles :
- `generatewaypoints` : description de génération de waypoints (commande non implémentée dans le code).
- `tstart` : démarre la séquence de téléportation.
- `tstop` : arrête la séquence.

### saves.yml
Fichier généré automatiquement à l\'exécution. Il stocke pour chaque waypoint :
- `time` (string) : temps relatif sous la forme `Xmin Ysec Zms`.
- `x`, `y`, `z` (double) : coordonnées de téléportation.
- `yaw`, `pitch` (double) : orientation permettant de regarder vers le centre du cercle.

## Commandes
- `/tstart` – Lance la boucle de téléportation sur les waypoints générés.
- `/tstop` – Interrompt la boucle et réinitialise le joueur.

## Compilation / Installation
Le projet utilise **Maven**.
```bash
mvn package
```
Le fichier plugin résultant se trouve dans `target/SamplePlugin-1.0.jar`.

## Fichiers importants
- `SamplePlugin.java` : classe principale du plugin, gère la génération des waypoints et les commandes `tstart`/`tstop`.
- `SamplePluginEvents.java` : classe d\'événements (actuellement non utilisée).
- `plugin.yml` : déclaration du plugin et des commandes.
- `saves.yml` : stockage persistant des waypoints générés.

## Utilisation rapide
1. Compiler le plugin via Maven et placer `SamplePlugin-1.0.jar` dans le dossier `plugins/` de votre serveur Spigot.
2. Démarrer le serveur pour générer automatiquement `saves.yml` et les waypoints.
3. Utiliser `/tstart` pour démarrer le timelapse, `/tstop` pour l\'arrêter.

## Licence et contributions
Aucune licence n\'est spécifiée dans ce dépôt. Les contributions se font via Pull Request.
