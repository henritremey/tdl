/**
 * 
 */
package fr.n7.stl.block.ast.instruction.declaration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.n7.stl.block.ast.Block;
import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.expression.Expression;
import fr.n7.stl.block.ast.instruction.Instruction;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.scope.SymbolTable;
import fr.n7.stl.block.ast.type.AtomicType;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.Logger;

/**
 * Abstract Syntax Tree node for a function declaration.
 * @author Marc Pantel
 */
public class FunctionDeclaration implements Instruction, Declaration {

	/**
	 * Name of the function
	 */
	protected String name;
	
	/**
	 * AST node for the returned type of the function
	 */
	protected Type type;
	
	/**
	 * List of AST nodes for the formal parameters of the function
	 */
	protected List<ParameterDeclaration> parameters;
	
	/**
	 * AST node for the body of the function
	 */
	protected Block body;
	
	protected HierarchicalScope<Declaration> table;
	
	protected int offset;
	
	public static  List<Type> t_current = new ArrayList<Type>();
	
	/**
	 * @return the parameters
	 */
	public List<ParameterDeclaration> getParameters() {
		return parameters;
	}



	/**
	 * Builds an AST node for a function declaration
	 * @param _name : Name of the function
	 * @param _type : AST node for the returned type of the function
	 * @param _parameters : List of AST nodes for the formal parameters of the function
	 * @param _body : AST node for the body of the function
	 */
	public FunctionDeclaration(String _name, Type _type, List<ParameterDeclaration> _parameters, Block _body) {
		this.name = _name;
		this.type = _type;
		this.parameters = _parameters;
		this.body = _body;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String _result = this.type + " " + this.name + "( ";
		Iterator<ParameterDeclaration> _iter = this.parameters.iterator();
		if (_iter.hasNext()) {
			_result += _iter.next();
			while (_iter.hasNext()) {
				_result += " ," + _iter.next();
			}
		}
		return _result + " )" + this.body;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Declaration#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Declaration#getType()
	 */
	@Override
	public Type getType() {
		return this.type;
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#collect(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean collectAndBackwardResolve(HierarchicalScope<Declaration> scope) {
		if (((HierarchicalScope<Declaration>)scope).accepts(this)) {
            scope.register(this);
            SymbolTable tableFonction = new SymbolTable(scope);
            for (ParameterDeclaration parameter : this.parameters) {
            	tableFonction.register(parameter);
            }
            this.table = tableFonction;
            System.out.println("table : " + table.toString() + " oui elle ");
            return this.body.collect(this.table);
        } else {
            Logger.error("La fonction " + this.name + " est défini plusieurs fois");
            return false;
        }
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean fullResolve(HierarchicalScope<Declaration> scope) {
		return this.body.collect(this.table);
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#checkType()
	 */
	@Override
	public boolean checkType() {
		boolean res = true;
		this.t_current.add(this.type);
		System.out.println(this.t_current + " du functiondeclaration");
        for(ParameterDeclaration parameterDeclaration : this.parameters) {
            if (parameterDeclaration.getType().equalsTo(AtomicType.ErrorType)) {
            	res = false;
            	Logger.error(parameterDeclaration + " n'est pas compatible avec les autres paramètres.");
            }
        }
        return res;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#allocateMemory(fr.n7.stl.tam.ast.Register, int)
	 */
	@Override
	public int allocateMemory(Register register, int offset) {
		this.offset = 0;
		for (ParameterDeclaration parameterDeclaration : this.parameters) {
			this.offset += parameterDeclaration.getType().length();
		}
		this.body.allocateMemory(Register.LB, this.offset + 3);
		return 0;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory factory) {
		Fragment f = factory.createFragment();
		f.append(this.body.getCode(factory));
		f.addPrefix(this.name);
		if (this.type != AtomicType.VoidType){
            f.add(factory.createReturn(0, this.offset));
        }
		return f;
	}

}
