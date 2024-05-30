
package fr.n7.stl.block.ast.classe;

import fr.n7.stl.block.ast.expression.Expression;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.type.PartialType;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.Pair;

public class AttributeDeclaration implements ClassElement {

	// Attributs de l'attribut de classe
       
	
	// Niveau d'accès de l'attribut             
    protected Etat etat;                    
    protected Type type;
    protected AccessRight accessRight;

    

	// Identifiant de l'attribut
    protected Pair<String, PartialType> id; 
    protected Expression valeur;            
    Register registre; 

	// Décalage mémoire                     
    int offset;                             
    
    // Constructeur principal 
    public AttributeDeclaration(Etat etat, Type type, Pair<String, PartialType> id, Expression valeur) {
    	this.etat = etat;
    	this.type = type;
    	this.id = id;
    	this.valeur = valeur;
    }
    
    // Constructeur alternatif avec seulement le type
	//Si l'utilisateur souhaite créer une instance de la classe sans spécifier tous les attributs 
    public AttributeDeclaration(Type type) {
    	this.type = type;
    }
    
	@Override
	public String getName() {
		return id.getLeft(); 
	}
	
	
	public AccessRight getAccessRight() {
        return accessRight ;
	}
	
	public void setAccessRight(AccessRight accessRight) {
        this.accessRight = accessRight;
        
	}
	
	@Override
	public Type getType() {
		return type; 
	}

	
	public Etat getEtat() {
		return etat; 
	}
	
	@Override
	public boolean collectAndBackwardResolve(HierarchicalScope<Declaration> scope) {
		
		if (scope.accepts(this)) {
			scope.register(this); 
			if (valeur != null) {
				
				return valeur.collectAndBackwardResolve(scope); 
			}
			return true;
		} else {
			System.out.println("Déjà défini : " + id.getLeft()); 
			return false;
		}
	}
	
	@Override
	public boolean fullResolve(HierarchicalScope<Declaration> scope) {
		if (valeur == null) {
			return type.resolve(scope); 
		}
		return type.resolve(scope) && valeur.fullResolve(scope); 
	}
	
	@Override
	public boolean checkType() {
		if (valeur == null) {
			return true;
		} else {
			if (type.compatibleWith(valeur.getType()) || valeur.getType().compatibleWith(type)) {
				return true; 
			} else {
				System.out.println("Type non compatible"); 
				return false;
			}
		}
	}
	
	@Override
	public int allocateMemory(Register register, int offset) {
		registre = register; 
		this.offset = offset; 
		return type.length(); 
	}
	
	@Override
	public Fragment getCode(TAMFactory factory) {
		
		Fragment resultat = factory.createFragment();		
		int typeLength = type.length();
		String nom_attribut = id.getLeft();

		if (valeur == null) {			
			resultat.add(factory.createPush(typeLength)); 
		} else {			
			Fragment valeurCode = valeur.getCode(factory);
			resultat.append(valeurCode);
			resultat.add(factory.createStore(registre, offset, typeLength)); 
		}
		resultat.addPrefix("debut:" + nom_attribut); 
		resultat.addSuffix("fin:" + nom_attribut);
		
		return resultat; 
	}
	
}
