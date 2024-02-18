# histoGRAPH

*Outil d'analyse et de visualisation les logs de git*

![histoGRAPH img](https://loiseaubleu.s-ul.eu/O9LEY5VF)

-----------------

Vidéo de Présentation : [youtu.be/_rmvFj14Qnc](https://youtu.be/_rmvFj14Qnc)

-----------------

## Présentation

histoGRAPH est un outil qui sert à analyser les contributions des membres d'une équipe travaillant sur un même projet hébergé sur un dépôt git. Son but est d'aider les professeurs dans la notation individuelle d'étudiants travaillant en équipe.

histoGraph peut :

- calculer des données très utiles comme :
    - le nombre de lignes ajoutées / supprimées / modifiées.
    - le nombre de commits.
    - le nombre de merge commits.
- analyser les variations de ces données dans le temps
- afficher ces données dans des graphiques (histogrammes, camamberts, courbes etc.) via une page web auto-générée.


## Outils similaires déjà existants

- [gitstats](https://pypi.org/project/gitstats/)

## Informations techniques

- Les graphiques sont générés via une librairie externe ( [CanvasJS](https://canvasjs.com/) )

## Architecture

histoGRAPH contient les modules suivants :

- Merges Per Author _( Le nombre de merge par auteur )_
- Project Info _( Les informations essentiels sur le projet )_
- Contributors _( La liste des devloppeurs )_
- Languages Percentage _( Le pourcentage de chaque langue (de programmation) utiliser dans le projet )_
- Download Project _( Un panneau permanent de telechager les fichiers d'une branche )_
- Commits Per Author _( Le nombre de commits par auteur )_
- Commits Per Day _( Le nombre de commits par jour )_
- Branches List _( La liste des branches )_
- Number of Line Chnages Per Author _( Le nombre de ligne ajoutées et supprimées par utiliser )_

Ces modules peuvent être activés ou désactivés un par un via le fichier `config.yml`.

## Configuration
Pour plus d'information sur la manière de comment créer un token merci d'aller sur ce lien : [Acces Token](https://docs.gitlab.com/ee/user/profile/personal_access_tokens.html)

Le fichier de configuration `config.yml` se trouve dans le dossier histoGRAPH, ce dossier va être créé dans le dossier dans le quelle vous avez lancé le programme
### Config
```yaml
###############
#   General   #
###############
project_name: "name"
use_other_git_path: false #Si vous voulez utiliser le programme en dehors d'un dossier git, vous devez mettre ce champ à true, et dans le champ git_path le chemin d'accès vers le dossier git.
git_path: "" #Le chemin d'accès vers le dossier git. 

###############
#   Web Api   #
###############
use_web_api: false #Si vous voulez utiliser l'api, vous devez mettre ce champ à true
#   gihub gitlab bitbucket ....   #
service: "gitlab" #En ce moment le programme peut utiliser que l'api de gitlab
#   with http or https   #
url: "https://gaufre.informatique.univ-paris-diderot.fr" #Le lien de votre gitlab
project_id: 0000 #Peut-être trouver en dessous du nom du projet
private_repo: false #Il est conseiller de mettre true même si le projet n'est pas privée

##########################################
#                Security                #
#         If private_repo is true        #
#   (Or you can add directly on the url  #
#     and turn private_repo to false)    #
#                Exemple:                #
# url/?private_token=<your_access_token> #
##########################################
parameter: "private_token" ##merci de ne pas toucher, car que le service de gitlab est implémenté, pour le moment
#   Personal or project access token   #
token: "your_access_token" #Pour plus d'information sur la manière de comment créer un token merci d'aller sur ce lien : https://docs.gitlab.com/ee/user/profile/personal_access_tokens.html

#############################################################
#                          Plugins                          #
#         if html_output and json_output is on false        #
#              the plugins will be deactivated              #
#         You can find all chart type on this link:         #
# https://canvasjs.com/docs/charts/chart-options/data/type/ #
#############################################################
plugins:
  - countCommits:
      enable: true #Determine si le module est activer
      html_output: true #Si a true cree un html avec le graphique
      json_output: true #Si a true cree un json avec les informations du graphique
      chartTypes: #La liste des graphiques qui vont etre afficher
        - column
        - line
        - bar
        - area
        - spline
        - splineArea
        - stepLine
        - scatter
        - bubble
        - stackedColumn
        - stackedBar
        - stackedArea
        - pie
        - doughnut

```

## Utilisation

### Build le projet

1. clonez le dépôt distant ci-dessous :
   SSH
    ```
    git clone git@gaufre.informatique.univ-paris-diderot.fr:gargaun/histoGRAPH.git
    ```
   HTML
    ```
    git clone https://gaufre.informatique.univ-paris-diderot.fr/gargaun/histoGRAPH.git
    ```
2. Entrez dans le dossier du projet
    ```
    cd histoGRAPH
    ```
3. À l'UParis, uniquement si vous êtes sur un PC du SCRIPT, dans une des salles de TP :
    ```
    source SCRIPT/envsetup
    ```
   Cette commande va configurer la variable d'environnement `GRADLE_OPTS` pour que gradle utilise le proxy du SCRIPT afin de télécharger ses composants essentiels. Elle utilisera également un store de confiance custom pour télécharger.
4. Lancez gradle wrapper (cela téléchargera gradle et ses composants) :
    ```
    ./gradlew build
    ```
### Lancer le logiciel

On peut aussi lancer le programme directement via gradle, sans préalablement build :
```
./gradlew :cli:run
```

Pour lancer le programme après avoir build :
```
java -jar histoGRAPH-dev.jar
```


### Les Résultats
Les résultats se trouvent dans le dossier histoGRAPH, ce dossier va être créé dans le dossier dans le quelle vous avez lancé le programme

## Pour plus d'information
Vous pouvez visualiser la vidéo histoGRAPH-rapport.mp4 pour savoir davantage
