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
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.tam.ast.TAMInstruction;
import fr.n7.stl.tam.ast.impl.FragmentImpl;
import fr.n7.stl.util.Logger;



public class MethodDeclaration implements Declaration, ClassElement {
	protected SignatureDeclaration entete;
	
	protected Etat state; //final, static
	
	protected AccessRight right;
	
	protected Block body;
	
	protected HierarchicalScope<Declaration> table; //table qui hérite de la table de la classe
	
	protected int offset;
	
	public MethodDeclaration(Etat _state, SignatureDeclaration _signature, Block _corps) {
		this.state = _state;
		this.entete = _signature;
		this.body = _corps;
	}
	
	
	public String getName() {
		return this.entete.getName();
	}
	
	public Type getType() {
		return this.entete.getType();
	}
	
	public boolean collectAndBackwardResolve(HierarchicalScope<Declaration> scope) {
		String name = entete.getIdentifiant().getLeft();
		List<ParameterDeclaration> parameters = this.entete.getParameters();
		
		
		if (((HierarchicalScope<Declaration>)scope).accepts(this)) {
            scope.register(this);
            SymbolTable tableMethode = new SymbolTable(scope);
            
            if(parameters != null) {
            	for (ParameterDeclaration parameter : parameters) {
                	tableMethode.register(parameter);
                }
            }
            
            this.table = tableMethode;
            return this.body.collect(this.table);
        } else {
            Logger.error("La fonction " + name + " est défini plusieurs fois");
            return false;
        }
		
	
	}
	
	public boolean fullResolve(HierarchicalScope<Declaration> _scope) {
		return body.resolve(this.table);
	}
	
	public boolean checkType() {
		boolean result = true ;
		if(this.entete.getParameters() != null) {
			for(ParameterDeclaration p : entete.getParameters()) {
				if (p.getType().equalsTo(AtomicType.ErrorType)) {
	            	result = false;
	            	Logger.error(p + " n'est pas compatible avec les autres paramètres.");
	            }
			}
		}
		if(entete.getType().equalsTo(AtomicType.ErrorType)) {
			result = false;
		}
		return result;
	}
	
	public int allocateMemory(Register register, int offset) {
		this.offset = 0;
		for (ParameterDeclaration parameterDeclaration : this.entete.getParameters()) {
			this.offset += parameterDeclaration.getType().length();
		}
		this.body.allocateMemory(Register.LB, this.offset + 3);
		return 0;
	}
	
	public Fragment getCode(TAMFactory factory) {
		Fragment f = factory.createFragment();
		f.append(this.body.getCode(factory));
		f.addPrefix("begin" + this.entete.getName());
		if (this.entete.getType() != AtomicType.VoidType){
            f.add(factory.createReturn(0, this.offset));
        }
		
		return f;
	}

	public void setAccessRight(AccessRight right) {
		this.right = right;
	}
	public Object getAccessRight() {
		// TODO Auto-generated method stub
		return this.right;
	}
	
	
}