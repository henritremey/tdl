/**
 * 
 */
package fr.n7.stl.block.ast.instruction;

import java.util.Optional;

import fr.n7.stl.block.ast.Block;
import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.expression.Expression;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.type.AtomicType;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.tam.ast.impl.FragmentImpl;

/**
 * Implementation of the Abstract Syntax Tree node for a conditional instruction.
 * @author Marc Pantel
 *
 */
public class Conditional implements Instruction {

	protected Expression condition;
	protected Block thenBranch;
	protected Block elseBranch;

	public Conditional(Expression _condition, Block _then, Block _else) {
		this.condition = _condition;
		this.thenBranch = _then;
		this.elseBranch = _else;
	}

	public Conditional(Expression _condition, Block _then) {
		this.condition = _condition;
		this.thenBranch = _then;
		this.elseBranch = null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "if (" + this.condition + " )" + this.thenBranch + ((this.elseBranch != null)?(" else " + this.elseBranch):"");
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#collect(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean collectAndBackwardResolve(HierarchicalScope<Declaration> scope) {
		if (this.elseBranch == null) {
			return this.condition.collectAndBackwardResolve(scope) && this.thenBranch.collect(scope);
		}
		else {
			return this.condition.collectAndBackwardResolve(scope) && this.thenBranch.collect(scope) && this.elseBranch.collect(scope);
		}
		
		
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.instruction.Instruction#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean fullResolve(HierarchicalScope<Declaration> scope) {
		if (this.elseBranch == null) {
			return this.condition.fullResolve(scope) && this.thenBranch.resolve(scope);
	
		}
		else {
			return this.condition.fullResolve(scope) && this.thenBranch.resolve(scope) && this.elseBranch.resolve(scope);
		}
	
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#checkType()
	 */
	@Override
	public boolean checkType() {
		if (elseBranch == null) {
			return condition.getType().compatibleWith(AtomicType.BooleanType) && thenBranch.checkType();
		}
		else {
			return condition.getType().compatibleWith(AtomicType.BooleanType) && thenBranch.checkType() && elseBranch.checkType();
		}
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#allocateMemory(fr.n7.stl.tam.ast.Register, int)
	 */
	@Override
	public int allocateMemory(Register register, int offset) {
		if (elseBranch == null) {
			this.thenBranch.allocateMemory(register, offset);
		}
		else {
			this.thenBranch.allocateMemory(register, offset);
			this.elseBranch.allocateMemory(register, offset);
		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Instruction#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory factory) {
		Fragment f = factory.createFragment();
		int id = factory.createLabelNumber();
		if (this.elseBranch == null) {
			f.add(factory.createJumpIf("endif" + id, 0));
			f.append(this.thenBranch.getCode(factory));
		}
		else {
			f.add(factory.createJumpIf("else" + id, 0));
			f.append(this.thenBranch.getCode(factory));
            f.add(factory.createJump("endif" + id));
            f.addSuffix("else" + id);
            f.append(this.elseBranch.getCode(factory));
		}
		f.addSuffix("endif" + id);
		return f;
		
	}

}
