import java.lang.*;
import java.io.*;
import java.util.*;

public class boyerMooreHorspool {

	// Cette fonction lit le fichier fasta et sauvegarde la sequence génomique dans un String, sans les sauts de ligne \n.
	static String lireSequence(String nomFichier) throws IOException {
		File fichier = new File(nomFichier);
		StringBuilder description = new StringBuilder((int)fichier.length());
		StringBuilder sequence = new StringBuilder((int)fichier.length());
		Scanner scanner = new Scanner(fichier);
		int compteur = 0;

		try {
			while(scanner.hasNextLine()) {
				// Si on est à la première ligne, on ne sauvegarde pas celle-ci dans le String séquence, puisque ce n'est que la description du fichier.
				if (compteur == 0){    
					description.append(scanner.nextLine());
				} else {
					sequence.append(scanner.nextLine());
				}
				compteur++;
			}
			return sequence.toString();
		} finally {
			scanner.close();
		}
	}

	// Cette fonction fait, comme demandé dans le Bonus, une optimisation de l'alphabet pour que le tableau à remplir dans la phase
	// de pré-traitement dépende de |M| et non de|Σ|. Pour ce faire, on détermine quelles sont les lettres de l'alphabet initial qui
	// sont présentes dans le marqueur, puisque ce sont les seules qui nous intéressent pour la phase de pré-traitement. La fonction
	// retourne par la suite un nouvel alphabet, sous la forme d'un tableau de caractères.
	static char[] optimiserAlphabet(char[] alphabet, String marqueur) {
		int nbChars = 0;
		int j = 0;

		// On détermine d'abord le nombre de lettres distinctes dans le marqueur.
		for(int i=0; i < alphabet.length; i++) {
			if (marqueur.contains(Character.toString(alphabet[i]))) {
				nbChars++;
			}
		}

		// On initialise ensuite le tableau du nouvel alphabet à la bonne taille.
		char[] nouveauAlpha = new char[nbChars];

		// Puis on le remplit avec les lettres présentes dans le marqueur.
		for(int i=0; i < alphabet.length; i++) {
			if (marqueur.contains(Character.toString(alphabet[i]))) {
				nouveauAlpha[j] = alphabet[i];
				j++;
			}
		}

		return nouveauAlpha;
	}

	// Cette fonction construit le tableau de pré-traitement, une fois l'alphabet optimisé. Le temps de calcul de ce tableau prend un
	// temps linéaire par rapport à |M|, puisqu'on ne tient compte que des lettres présentes dans le marqueur M. Le temps de calcul
	// sera donc au maximum de |M| au carré.
	static int[][] initialiserTableau(char[] nouveauAlpha, String marqueur) {
		int[][] tableau = new int[nouveauAlpha.length][nouveauAlpha.length];
		char char1;
		char char2;
		int index1 = 0;
		int index2 = 0;

		// On initialise le tableau avec la valeur |M| + 1 dans toutes les cases, soit la valeur pour les occurences non trouvées.
		for(int i=0; i<nouveauAlpha.length; i++) {
			for(int j=0; j<nouveauAlpha.length; j++) {
				tableau[i][j] = marqueur.length() + 1;	
			}
		}

		// On parcourt ensuite le marqueur de droite à gauche, deux caractères à la fois, et on inscrit le décalage dans la bonne
		// position dans le tableau.
		for(int i=marqueur.length()-3; i >= 0; i--) {
			int j = i + 1;
			char1 = marqueur.charAt(i);
			char2 = marqueur.charAt(j);

			// Pour trouver le bon index dans le tableau selon le caractère, il faut d'abord trouver l'index de ce caractère dans
			// l'alphabet.
			for(int k=0; k<nouveauAlpha.length; k++) {
				if(char1 == nouveauAlpha[k]) {
					index1 = k;
				}
				if(char2 == nouveauAlpha[k]) {
					index2 = k;
				}
			}

			if(tableau[index1][index2] > -(i + 1 - marqueur.length())) {
				tableau[index1][index2] = -(i + 1 - marqueur.length());
			}
		}

	// On remplit finalement toutes les cases des occurennces non rencontrées dans la première colonne par |M|.
		for(int i=0; i<nouveauAlpha.length; i++) {
			if(tableau[i][0] == marqueur.length() + 1) {
				tableau[i][0] = marqueur.length();
			}
		}

		return tableau;
	}

	// Cette fonction opère l'algorithme Boyer-Moore-Horspool sur la séquence génomique. Elle retourne une tableau de String de
	// deux cases: dans la première on trouve le nombre d'occurences du marqueur dans la séquence, dans la deuxième un String qui
	// contient toutes les positions de départ de ces occurences.
	static String[] trouverOccurences(char[] nouveauAlpha, int[][] tableau, String sequence, String marqueur) {
		String[] resultats = new String[2];
		String positions = "";
		int nbOccurences = 0;
		int index1 = marqueur.length() - 1;
		int index2;
		char char1;
		char char2;
		int indexChar1 = 0;
		int indexChar2 = 0;
		int decalage = 0;

		while(index1 < sequence.length()) {
			// L'index1 représente la position à laquelle on est rendu dans la séquence. L'index2 représente la position du caractère
			// présentement étudié dans la séquence pour le comparer avec celui qui lui concorde dans le marqueur.
			index2 = index1;

			// On itère sur la séquence et le marqueur pour comparer chaque lettre de droite à gauche, en partant de index1 pour la séquence,
			// et du dernier caractère pour le marqueur.
			for(int i=marqueur.length()-1; i>=0; i--) {
				// S'il y a concordance entre les deux lettres, on passe à la prochaine.
				if((marqueur.charAt(i) == 'A' &&
				   (sequence.charAt(index2) == 'A' || sequence.charAt(index2) == 'R' || sequence.charAt(index2) == 'N' || sequence.charAt(index2) == 'W' || sequence.charAt(index2) == 'M')) ||
					(marqueur.charAt(i) == 'C' &&
					 (sequence.charAt(index2) == 'C' || sequence.charAt(index2) == 'Y' || sequence.charAt(index2) == 'N' || sequence.charAt(index2) == 'S' || sequence.charAt(index2) == 'M')) ||
					(marqueur.charAt(i) == 'G' &&
					 (sequence.charAt(index2) == 'G' || sequence.charAt(index2) == 'R' || sequence.charAt(index2) == 'N' || sequence.charAt(index2) == 'S' || sequence.charAt(index2) == 'K' || sequence.charAt(index2) == 'B')) ||
					(marqueur.charAt(i) == 'T' &&
					 (sequence.charAt(index2) == 'T' || sequence.charAt(index2) == 'Y' || sequence.charAt(index2) == 'N' || sequence.charAt(index2) == 'W' || sequence.charAt(index2) == 'K' || sequence.charAt(index2) == 'B'))) {
					index2--;
				// S'il y a concordance entre les deux lettres, et qu'on est rendu à la première lettre du marqueur, on a alors trouvé
				// une occurence. On incrémente donc le nombre d'occurences trouvées, et on store la position de départ de cette
				// occurence.
				if(i == 0) {
					nbOccurences++;
					positions += " " + Integer.toString(index2);
					char1 = sequence.charAt(index1 - 1);
					char2 = sequence.charAt(index1);

					for(int k=0; k<nouveauAlpha.length; k++) {
						if(char1 == nouveauAlpha[k]) {
							indexChar1 = k;
						}
						if(char2 == nouveauAlpha[k]) {
							indexChar2 = k;
						}
					}

					// Puis on détermine le décalage à appliquer, et on applique celui-ci sur l'index1 pour continuer à chercher
					// d'autres occurences.
					decalage = tableau[indexChar1][indexChar2];
					index1 += decalage - 1;
					break;
				}
				// Si deux lettres ne concordent pas, on détrmine tout de suite le décalage et on l'applique à l'index1.
			} else {
				char1 = sequence.charAt(index1 - 1);
				char2 = sequence.charAt(index1);

				for(int k=0; k<nouveauAlpha.length; k++) {
					if(char1 == nouveauAlpha[k]) {
						indexChar1 = k;
					}
					if(char2 == nouveauAlpha[k]) {
						indexChar2 = k;
					}
				}

				decalage = tableau[indexChar1][indexChar2];
				index1 += decalage - 1;
				break;
			}
		}
	}

	resultats[0] = Integer.toString(nbOccurences);
	resultats[1] = positions;
	return resultats;
}

public static void main (String[] args) throws IOException {
	if (args.length != 3){
		System.out.println("Veuillez entrer le bon nombre d'arguments");
	}else {
		String nomFichier = args[0];
		String marqueur = args[1];
		int flag = Integer.parseInt(args[2]);
		char[] alphabet = {'A','C','G','T','R','Y','N','W','S','M','K','B'};			
		String sequence = lireSequence(nomFichier);
		char[] nouveauAlpha = optimiserAlphabet(alphabet, marqueur);
		int[][] tableau = initialiserTableau(nouveauAlpha, marqueur);
		String[] resultats = trouverOccurences(nouveauAlpha, tableau, sequence, marqueur);

		System.out.println(resultats[0] + " occurences");
		if(flag == 1) {
			System.out.println("Positions :" + resultats[1]);
		}
	}
}
}