/**
 * 
 */
package fr.n7.stl.block.ast.expression;

import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.tam.ast.impl.FragmentImpl;

/**
 * Abstract Syntax Tree node for a conditional expression.
 * @author Marc Pantel
 *
 */
public class ConditionalExpression implements Expression {

	/**
	 * AST node for the expression whose value is the condition for the conditional expression.
	 */
	protected Expression condition;
	
	/**
	 * AST node for the expression whose value is the then parameter for the conditional expression.
	 */
	protected Expression thenExpression;
	
	/**
	 * AST node for the expression whose value is the else parameter for the conditional expression.
	 */
	protected Expression elseExpression;
	
	/**
	 * Builds a binary expression Abstract Syntax Tree node from the left and right sub-expressions
	 * and the binary operation.
	 * @param _left : Expression for the left parameter.
	 * @param _operator : Binary Operator.
	 * @param _right : Expression for the right parameter.
	 */
	public ConditionalExpression(Expression _condition, Expression _then, Expression _else) {
		this.condition = _condition;
		this.thenExpression = _then;
		this.elseExpression = _else;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.expression.Expression#collect(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean collectAndBackwardResolve(HierarchicalScope<Declaration> scope) {
		if (this.elseExpression == null) {
			return this.condition.collectAndBackwardResolve(scope) && this.thenExpression.collectAndBackwardResolve(scope);
		}
		else {
			return this.condition.collectAndBackwardResolve(scope) && this.elseExpression.collectAndBackwardResolve(scope) && this.thenExpression.collectAndBackwardResolve(scope);
		}	
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.expression.Expression#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean fullResolve(HierarchicalScope<Declaration> scope) {
		if (this.elseExpression == null) {
			return this.condition.fullResolve(scope) && this.thenExpression.fullResolve(scope);
	
		}
		else {
			return this.condition.fullResolve(scope) && this.thenExpression.fullResolve(scope) && this.elseExpression.fullResolve(scope);
		}
	}

	/* (non-Jathrow new SemanticsUndefinedException( "Semantics getType is undefined in ConditionalExpression.");vadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "(" + this.condition + " ? " + this.thenExpression + " : " + this.elseExpression + ")";
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Expression#getType()
	 */
	@Override
	public Type getType() {
		if (this.elseExpression == null) {
			return thenExpression.getType();
		}
		else {
			return thenExpression.getType().merge(elseExpression.getType());
		}
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Expression#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory factory) {
		Fragment f = factory.createFragment();
		int id = factory.createLabelNumber();
		if (this.elseExpression == null) {
			f.add(factory.createJumpIf("endif" + id, 0));
			f.append(this.thenExpression.getCode(factory));
		}
		else {
			f.add(factory.createJumpIf("else" + id, 0));
			f.append(this.thenExpression.getCode(factory));
            f.add(factory.createJump("endif" + id));
            f.addSuffix("else" + id);
            f.append(this.elseExpression.getCode(factory));
		}
		f.addSuffix("endif" + id);
		return f;
	}

}
