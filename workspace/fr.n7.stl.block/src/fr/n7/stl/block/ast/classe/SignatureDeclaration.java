package fr.n7.stl.block.ast.classe;


import java.util.ArrayList;
import java.util.List;

import fr.n7.stl.block.ast.Block;
import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.expression.Expression;
import fr.n7.stl.block.ast.instruction.Instruction;
import fr.n7.stl.block.ast.instruction.declaration.ParameterDeclaration;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.scope.SymbolTable;
import fr.n7.stl.block.ast.type.AtomicType;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.block.ast.type.PartialType;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.tam.ast.TAMInstruction;
import fr.n7.stl.tam.ast.impl.FragmentImpl;
import fr.n7.stl.util.Logger;
import fr.n7.stl.util.Pair;


public class SignatureDeclaration {
	protected Type type;
	
	protected Pair<String,PartialType> identifiant;
	
	protected List<ParameterDeclaration> parameters;
	
	public SignatureDeclaration(Type _type, Pair<String, PartialType> _identifiant, List<ParameterDeclaration> _parameters) {
		this.type = _type;
		this.parameters = _parameters;
		this.identifiant = _identifiant;
	}
	public Type getType() {
		return this.type;
	}
	
	public Pair<String,PartialType> getIdentifiant(){
		return this.identifiant;
	}
	
	public List<ParameterDeclaration> getParameters(){
		return this.parameters;
	}
	
	public String getName() {
		return this.identifiant.getLeft();
	}
	
	

}
