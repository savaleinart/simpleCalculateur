# Introduction
On veut faire un genre de calculatrice très basique. Le but c'est de montrer le concept donc on va considérer que:
- L'expression rentrée est valide donc on ne fera pas de vérification
- La calculatrice ne gère que les opérations de base (addition, soustraction, multiplication et division), pas les parenthèses, les nombres négatifs ou les opérations plus complexes comme les racines.
# Structure de donnée
Pour choisir une structure de données il faut prendre en compte le besoin et les contraintes associées. Ici, on a besoin de pouvoir gérer la priorité des opérations et comme ça reste un exemple simple il n'y a pas de contrainte particulière au niveau performance ou mémoire du moment que le résultat est exacte et est produit rapidement.

Les opérations simples sont composées d'un opérateur et de deux termes, et les opérations complexes peuvent êtres décomposées en une succession d'opérations simples.
On peut représenter une opération sous forme d'arbre, et en plus pour faire le calcul il suffit de remonter l'arbre ce qui permet de gérer la priorité des opérations.

Exemple: l'expression "3+2" devient un arbre avec comme nœud l'opérateur, et comme branches les deux termes de l'opération.
			
		+
	3		2
Pour une expression plus complexe comme "3+2-4" on peut décomposer étape par étape:

		-
	3+2	   4
  puis
	
			-
		+	    4
	3		2
Pour faire le calcul il suffit de partir du bas de l'arbre, et si il est construit correctement cela respecte la priorité opératoire (de gauche à droite, avec la multiplication prioritaire sur l'addition). Ici on prend donc l'opérateur le plus bas dans l'arbre, on fait le calcul avec ses deux branches, puis on utilise ce résultat pour faire le calcul à l'étage du dessus, et ainsi de suite jusqu'au résultat final.

# Le code
### Structure d'arbre
Dans la représentation sous forme d'arbre on a deux possibilité pour chaque nœud:
- un opérateur avec deux branches qui représente une opération
- un nombre qui du coup ne nécessite pas de calcul

Au niveau du code on a donc besoin de ces deux choses distinctes. Pour ma part, la classe __ComplexeExpression__ représente les nœuds de l'arbre qui sont des opérateurs, par conséquents dans cette classe on aura un opérateur, et deux branches qui seront soit des nombres, soit d'autres expressions.
J'ai aussi une classe __SimpleValue__ qui correspondra aux nœuds les plus bas dans l'arbre qui n'ont pas de branches: les nombres eux-mêmes. Cette classe aura juste un attribut 'value' pour al valeur du nombre.
Et parce que j'ai décidé d'utiliser Java qui est orienté objet et fortement typé, j'utilise une interface pour permettre de regrouper ces deux classes ensemble et définir leur comportement en imposant l'existence d'une méthode __solve()__ qui permettra de faire le calcul et qui sera implémentée différemment selon si l'objet qui utilise cette méthode est une expression ou un nombre.
### Le calcul
Partons d'abord du principe qu'on a déjà notre arbre tout bien construit. Maintenant il faut le parcourir pour effectuer le calcul, et pour ça on revient à la définition de notre structure de donnée.
Par définition, notre arbre est composé de nœuds avec des branches qui peuvent eux aussi êtres des sous arbres. Cette définition récursive est parfaite pour un parcours récursif !
Commençons par la partie simple, quand il n'y a pas de calcul à faire: Quand le nœud dont on veut faire le calcul est en bas de l'arbre car ce n'est qu'un nombre, il suffit de garder ce nombre (avec un parse si c'est une chaîne de caractère, pour ma part ce sera un double car le parsing sera fait au moment de la création de l'arbre).
```java
public double solve() {  
    return value;  
}
```
La partie simple c'est fait, maintenant on s'attaque à la partie plus compliquée: quand il y a un calcul à faire. Comme on part du principe que l'arbre est déjà construit, notre objet est déjà rempli avec tout ce qu'il faut dedans pour faire le calcul, il suffit juste de calculer en fonction de l'opérateur et des résultats des sous opérations.

Prenons l'exemple d'une addition:
Le résultat lorsque les branches sont des nombres c'est juste nombre1 + nombre2, ça revient à appeler la fonction __solve()__ du nœud de type __SimpleValue__.
Lorsque les branches ne sont pas des nombres, on peut se ramener au cas où ce sont des nombres en appelant là aussi la fonction __solve()__ que l'on est en train de faire. 
C'est là que la récursivité entre en jeu, Notre fonction va donc s'appeler elle même jusqu'à tomber en bas de l'arbre où il n'y aura rien a calculer, puis remonter les résultats jusqu'au premier appel de la fonction:
```java
return leftMember.solve() + rightMember.solve();
```
En suivant le même principe avec un switch sur l'opérateur on peut gérer toutes les opérations:
```java
public double solve() {  
    return switch (operand) {  
        case "+" -> leftMember.solve() + rightMember.solve();  
        case "*" -> leftMember.solve() * rightMember.solve();  
        case "-" -> leftMember.solve() - rightMember.solve();  
        case "/" -> leftMember.solve() / rightMember.solve();  
        default -> throw new RuntimeException("Invalid operand");  
    };  
}
```
C'est pas mal, mais pour le moment faut créer l'arbre à la main pour que ça marche, donc la prochaine étape c'est de gérer la création de l'arbre.
### Générer l'arbre
On va devoir parse la chaîne de caractères entrée qui représente l'expression à calculer et en faire un arbre qu'on pourra envoyer au reste du code qui fera le calcul.
C'est une bonne occasion d'utiliser des regex toutes simples pour décomposer notre chaîne de caractères et en faire un arbre, mais c'est quoi une regex ? Pour faire très simple c'est un outil qui permet de faire de la reconnaissance de motif dans une chaîne de caractères, par exemple pour valider la forme d'un input utilisateur ou pour parse du texte.

Je ne vais pas rentrer dans le détail du fonctionnement des regex, mais dans l'idée voilà comment on va les utiliser:
Une regex va essayer de trouver un motif 'terme1' 'operateur' 'terme2' dans la chaîne de caractère, et construire un morceau de l'arbre qui correspond. Puis on recommence sur chaque branche qui sera aussi une chaîne de caractères, et ainsi de suite jusqu'à ce que le motif n'apparaisse plus (c'est à dire qu'on a fini de décomposer et qu'au niveau le plus bas c'est juste des nombres).

On ne peut pas faire ça n'importe comment cependant, pour respecter la priorité des opérations. On va d'abord commencer par chercher les opérateurs + et - qui ne sont pas prioritaires, en partant de la droite (ça peut paraître contre intuitif mais comme on fait le calcul en partant du bas de l'arbre, les nœuds que l'on construit en premier seront ceux traités en dernier). A chaque fois qu'on trouve l'opérateur on construit un bout de l'arbre et on continue.
Lorsqu'il n'y a plus aucune addition ou soustraction on utilise une autre regex pour trouver les multiplication et les divisions, et on continue la construction de l'arbre.

Exemple: Prenons l'expression "1+3\*4-9". 
D'abord la première regex va match "1+3\*4", "-" et "9" comme termes et opérateurs pour construire le premier étage de l'arbre. On continue ensuite à décomposer "1+3\*4" (car "9" c'est déjà un nombre alors plus besoin de passer dessus), la regex va match "1", "+" et "3\*4".
Ensuite il n'y a plus d'addition/soustraction donc on passe sur l'autre regex qui va match "3", "\*" et "4". L'arbre est maintenant correctement construit, la première opération effectuée sera "3\*4", puis "1+12" et enfin "13-9" ce qui donnera un résultat de 4.

# Conclusion
Bien choisir une structure de données conditionne la façon dont on approche un problème. Et aussi des fois les regex c'est cool :)
Pour aller plus loin on pourrait vérifier si ce que l'utilisateur tape est valide ou pas, rajouter des tests pour s'assurer qu'on casse pas tout à chaque changement, et gérer la priorité avec des parenthèses et des nombres négatifs.
